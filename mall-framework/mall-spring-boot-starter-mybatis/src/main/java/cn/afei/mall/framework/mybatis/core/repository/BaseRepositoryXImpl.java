package cn.afei.mall.framework.mybatis.core.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * BaseRepositoryX 的实现基类 — 提供带【租户 + 逻辑删除】过滤的主键操作族，
 * 平移原 MyBatis Plus 拦截器对 selectById / deleteById 等方法的自动 SQL 改写行为：
 *
 * 1. selectById / selectBatchIds：WHERE id = ? AND deleted = false AND tenant_id = ?
 * 2. deleteByIdLogically / deleteBatchIdsLogically：UPDATE SET deleted = true WHERE id = ? AND deleted = false AND tenant_id = ?
 *    （只 SET deleted，不更新 update_time/updater，与原 MP @TableLogic 生成的 SQL 完全一致）
 *
 * @author 芋道源码
 */
@Transactional(readOnly = true)
public class BaseRepositoryXImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements BaseRepositoryX<T, ID> {

    private final EntityManager entityManager;
    private final Class<T> domainClass;

    public BaseRepositoryXImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
        this.domainClass = entityInformation.getJavaType();
    }

    @Override
    public T selectById(ID id) {
        return selectOne(buildBaseSpec((root, cb) -> cb.equal(root.get("id"), id)));
    }

    @Override
    public List<T> selectBatchIds(Iterable<ID> ids) {
        List<ID> idList = new ArrayList<>();
        ids.forEach(idList::add);
        if (idList.isEmpty()) {
            return List.of();
        }
        return selectList(buildBaseSpec((root, cb) -> root.get("id").in(idList)));
    }

    @Override
    @Transactional
    public void updateByIdSelective(T entity) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaUpdate<T> update = cb.createCriteriaUpdate(domainClass);
        Root<T> root = update.from(domainClass);
        Object idValue = null;
        // 遍历实体属性（含 BaseDO 继承字段），非 null 值参与更新（对齐 MP NOT_NULL 更新策略）
        for (java.lang.reflect.Field field : getAllFields(domainClass)) {
            field.setAccessible(true);
            Object value;
            try {
                value = field.get(entity);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("updateByIdSelectable 读取字段失败: " + field.getName(), e);
            }
            if ("id".equals(field.getName())) {
                idValue = value;
                continue;
            }
            if ("deleted".equals(field.getName())) {
                continue; // 逻辑删除标记不随业务更新
            }
            if (value != null) {
                update.set(root.get(field.getName()), value);
            }
        }
        if (idValue == null) {
            throw new IllegalArgumentException("updateByIdSelective 缺少主键 id");
        }
        // 平移原 MP MetaObjectHandler.updateFill：更新时间/更新人为空时填充
        if (hasAttribute(root, "updateTime")) {
            update.set(root.get("updateTime"), java.time.LocalDateTime.now());
        }
        Long loginUserId = cn.afei.mall.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId();
        if (loginUserId != null && hasAttribute(root, "updater")) {
            update.set(root.get("updater"), loginUserId.toString());
        }
        update.where(cb.equal(root.get("id"), idValue));
        entityManager.createQuery(update).executeUpdate();
    }

    /**
     * 收集类及其全部超类的字段（至 BaseDO 为止）
     */
    private static java.util.List<java.lang.reflect.Field> getAllFields(Class<?> clazz) {
        java.util.List<java.lang.reflect.Field> fields = new java.util.ArrayList<>();
        for (Class<?> c = clazz; c != null && c != Object.class; c = c.getSuperclass()) {
            fields.addAll(java.util.Arrays.asList(c.getDeclaredFields()));
        }
        return fields;
    }

    @Override
    @Transactional
    public void deleteByIdLogically(ID id) {
        executeLogicalDelete((root, cb) -> cb.and(
                cb.equal(root.get("id"), id), commonConditions(root, cb)));
    }

    @Override
    @Transactional
    public void deleteBatchIdsLogically(Iterable<ID> ids) {
        List<ID> idList = new ArrayList<>();
        ids.forEach(idList::add);
        if (idList.isEmpty()) {
            return;
        }
        executeLogicalDelete((root, cb) -> cb.and(
                root.get("id").in(idList), commonConditions(root, cb)));
    }

    // ========== 内部：条件构建 ==========

    /**
     * 1. 主键查询的通用 Specification：id 条件 + deleted=false + tenant（对齐原拦截器改写）
     */
    private org.springframework.data.jpa.domain.Specification<T> buildBaseSpec(
            java.util.function.BiFunction<Root<T>, CriteriaBuilder, Predicate> idCondition) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(idCondition.apply(root, cb));
            Predicate common = commonConditions(root, cb);
            if (common != null) {
                predicates.add(common);
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * 2. 公共过滤：deleted = false（实体含该属性时）+ tenant_id = 当前租户（实体含该属性且上下文有租户时）
     */
    private Predicate commonConditions(Root<T> root, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        if (hasAttribute(root, "deleted")) {
            predicates.add(cb.isFalse(root.get("deleted")));
        }
        Long tenantId = TenantSupplierHolder.getTenantId();
        if (tenantId != null && hasAttribute(root, "tenantId")) {
            predicates.add(cb.equal(root.get("tenantId"), tenantId));
        }
        return predicates.isEmpty() ? null : cb.and(predicates.toArray(new Predicate[0]));
    }

    /**
     * 3. 执行逻辑删除 UPDATE（仅 SET deleted = true，对齐原 MP 生成 SQL）
     */
    private void executeLogicalDelete(java.util.function.BiFunction<Root<T>, CriteriaBuilder, Predicate> conditions) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaUpdate<T> update = cb.createCriteriaUpdate(domainClass);
        Root<T> root = update.from(domainClass);
        update.set(root.get("deleted"), true);
        update.where(conditions.apply(root, cb));
        entityManager.createQuery(update).executeUpdate();
    }

    private boolean hasAttribute(Root<T> root, String attr) {
        try {
            root.getModel().getAttribute(attr);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

}
