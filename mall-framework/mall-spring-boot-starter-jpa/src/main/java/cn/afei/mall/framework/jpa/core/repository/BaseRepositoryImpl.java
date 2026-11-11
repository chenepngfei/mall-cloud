package cn.afei.mall.framework.jpa.core.repository;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
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

/**
 * {@link BaseRepositoryX} 的实现类，作为所有 Repository 的统一基类
 *
 * 通过 {@code @EnableJpaRepositories(repositoryBaseClass = BaseRepositoryImpl.class)} 生效
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

    @Override
    @Transactional
    @SuppressWarnings("unchecked")
    public T updateByIdSelective(T entity) {
        ID id = (ID) entityInformation.getId(entity);
        if (id == null) {
            throw new IllegalArgumentException("选择性更新的实体 id 不能为空");
        }
        @SuppressWarnings("unchecked")
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
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> cq = (CriteriaQuery<T>) cb.createQuery(getDomainClass());
        Root<T> root = cq.from(getDomainClass());
        if (spec != null) {
            Predicate predicate = spec.toPredicate(root, cq, cb);
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
        return entityManager.find(getDomainClass(), id, LockModeType.PESSIMISTIC_WRITE);
    }

}
