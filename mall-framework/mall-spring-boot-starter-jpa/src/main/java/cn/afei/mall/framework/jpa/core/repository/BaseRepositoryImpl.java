package cn.afei.mall.framework.jpa.core.repository;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import cn.afei.mall.framework.jpa.core.hook.DataPermissionHookRegistry;
import cn.afei.mall.framework.jpa.core.hook.SessionHookRegistry;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

/**
 * {@link BaseRepositoryX} 的实现类，作为所有 Repository 的统一基类
 *
 * 通过 {@code @EnableJpaRepositories(repositoryBaseClass = BaseRepositoryImpl.class)} 生效
 *
 * 数据操作入口统一做两件事（对齐原 MyBatis 拦截器的能力）：
 * 1. 应用 Session 钩子（多租户：启用/禁用 Hibernate tenantFilter），见 {@link SessionHookRegistry}
 * 2. Specification 查询前追加数据权限过滤条件，见 {@link DataPermissionHookRegistry}
 *
 * 注意：绕过本类的查询（EntityManager 直用、@Query、derived query）不会经过上述钩子，
 * 编码规约要求业务代码统一走 BaseRepositoryX 的方法
 */
@Transactional(readOnly = true)
public class BaseRepositoryImpl<T, ID> extends SimpleJpaRepository<T, ID> implements BaseRepositoryX<T, ID> {

    /**
     * 选择性更新时，忽略的属性：id、审计字段、逻辑删除、租户
     */
    private static final String[] IGNORED_PROPERTIES =
            {"id", "createTime", "updateTime", "creator", "updater", "deleted", "tenantId"};

    private final JpaEntityInformation<T, ?> entityInformation;
    private final EntityManager entityManager;

    public BaseRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityInformation = entityInformation;
        this.entityManager = entityManager;
    }

    // ========== 钩子应用 ==========

    private void applySessionHooks() {
        SessionHookRegistry.applyHooks(entityManager);
    }

    private Specification<T> withDataPermission(Specification<T> spec) {
        Specification<T> dataPermissionSpec = DataPermissionHookRegistry.buildSpecification(getDomainClass());
        if (dataPermissionSpec == null) {
            return spec;
        }
        // Specification 的 and 组合是 null 安全的
        return dataPermissionSpec.and(spec);
    }

    // ========== 查询：覆写以应用钩子 ==========

    @Override
    public List<T> findAll() {
        applySessionHooks();
        return super.findAll(withDataPermission(null));
    }

    @Override
    public List<T> findAll(Sort sort) {
        applySessionHooks();
        return super.findAll(withDataPermission(null), sort);
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        applySessionHooks();
        return super.findAll(withDataPermission(null), pageable);
    }

    @Override
    public List<T> findAll(Specification<T> spec) {
        applySessionHooks();
        return super.findAll(withDataPermission(spec));
    }

    @Override
    public List<T> findAll(Specification<T> spec, Sort sort) {
        applySessionHooks();
        return super.findAll(withDataPermission(spec), sort);
    }

    @Override
    public Page<T> findAll(Specification<T> spec, Pageable pageable) {
        applySessionHooks();
        return super.findAll(withDataPermission(spec), pageable);
    }

    @Override
    public Optional<T> findById(ID id) {
        applySessionHooks();
        // 数据权限场景下，按主键查询也走 Specification，保证与列表查询口径一致
        Specification<T> dataPermissionSpec = DataPermissionHookRegistry.buildSpecification(getDomainClass());
        if (dataPermissionSpec != null) {
            Specification<T> spec = dataPermissionSpec.and(
                    (root, query, cb) -> cb.equal(root.get("id"), id));
            return super.findAll(spec).stream().findFirst();
        }
        return super.findById(id);
    }

    @Override
    public List<T> findAllById(Iterable<ID> ids) {
        applySessionHooks();
        Specification<T> dataPermissionSpec = DataPermissionHookRegistry.buildSpecification(getDomainClass());
        if (dataPermissionSpec != null) {
            return super.findAll(dataPermissionSpec.and(
                    (root, query, cb) -> root.get("id").in(ids)));
        }
        return super.findAllById(ids);
    }

    @Override
    public long count() {
        applySessionHooks();
        return super.count(withDataPermission(null));
    }

    @Override
    public long count(Specification<T> spec) {
        applySessionHooks();
        return super.count(withDataPermission(spec));
    }

    @Override
    public boolean existsById(ID id) {
        applySessionHooks();
        return super.existsById(id);
    }

    // ========== 写入：覆写以应用 Session 钩子 ==========

    @Override
    @Transactional
    public <S extends T> S save(S entity) {
        applySessionHooks();
        return super.save(entity);
    }

    @Override
    @Transactional
    public <S extends T> List<S> saveAll(Iterable<S> entities) {
        applySessionHooks();
        return super.saveAll(entities);
    }

    @Override
    @Transactional
    public void deleteById(ID id) {
        applySessionHooks();
        super.deleteById(id);
    }

    @Override
    @Transactional
    public void delete(T entity) {
        applySessionHooks();
        super.delete(entity);
    }

    @Override
    @Transactional
    public void deleteAllById(Iterable<? extends ID> ids) {
        applySessionHooks();
        super.deleteAllById(ids);
    }

    @Override
    @Transactional
    public void deleteAll(Iterable<? extends T> entities) {
        applySessionHooks();
        super.deleteAll(entities);
    }

    // ========== BaseRepositoryX 扩展方法的实现 ==========

    @Override
    @Transactional
    @SuppressWarnings("unchecked")
    public T updateByIdSelective(T entity) {
        applySessionHooks();
        ID id = (ID) entityInformation.getId(entity);
        if (id == null) {
            throw new IllegalArgumentException("选择性更新的实体 id 不能为空");
        }
        T existing = (T) entityManager.find(getDomainClass(), id);
        if (existing == null) {
            throw new IllegalArgumentException("选择性更新的实体不存在：" + getDomainClass().getSimpleName() + "#" + id);
        }
        // 只拷贝非 null 的业务字段
        BeanUtil.copyProperties(entity, existing, CopyOptions.create()
                .ignoreNullValue().setIgnoreProperties(IGNORED_PROPERTIES));
        return save(existing);
    }

    @Override
    @Transactional
    @SuppressWarnings("unchecked")
    public T insertOrUpdate(T entity) {
        applySessionHooks();
        ID id = (ID) entityInformation.getId(entity);
        if (id == null) {
            return save(entity);
        }
        T existing = (T) entityManager.find(getDomainClass(), id);
        if (existing == null) {
            return save(entity);
        }
        BeanUtil.copyProperties(entity, existing, CopyOptions.create()
                .ignoreNullValue().setIgnoreProperties(IGNORED_PROPERTIES));
        return save(existing);
    }

    @Override
    @Transactional
    public T selectOneForUpdate(Specification<T> spec) {
        List<T> list = selectListForUpdate(spec);
        if (list.size() > 1) {
            throw new IncorrectResultSizeDataAccessException(1, list.size());
        }
        return CollUtil.getFirst(list);
    }

    @Override
    @Transactional
    @SuppressWarnings("unchecked")
    public List<T> selectListForUpdate(Specification<T> spec) {
        applySessionHooks();
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> cq = (CriteriaQuery<T>) cb.createQuery(getDomainClass());
        Root<T> root = cq.from(getDomainClass());
        Specification<T> effectiveSpec = withDataPermission(spec);
        if (effectiveSpec != null) {
            Predicate predicate = effectiveSpec.toPredicate(root, cq, cb);
            if (predicate != null) {
                cq.where(predicate);
            }
        }
        return entityManager.createQuery(cq)
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .getResultList();
    }

    @Override
    @Transactional
    public T selectByIdForUpdate(ID id) {
        applySessionHooks();
        return entityManager.find(getDomainClass(), id, LockModeType.PESSIMISTIC_WRITE);
    }

}
