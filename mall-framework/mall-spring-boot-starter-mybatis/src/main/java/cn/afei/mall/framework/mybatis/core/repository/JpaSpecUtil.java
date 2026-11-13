package cn.afei.mall.framework.mybatis.core.repository;

import cn.hutool.core.collection.CollUtil;
import jakarta.persistence.criteria.Expression;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;
import java.util.Objects;

/**
 * JPA Specification 动态查询工具类（对齐原 MP LambdaQueryWrapperX 的 IfPresent 语义族）
 *
 * 1. IfPresent 系列：值为 null / 空串 / 空集合时跳过条件，等价原 eqIfPresent/likeIfPresent 等
 * 2. 必传系列 eq/like/in：无条件拼接
 * 3. tenantSpec / logicDeleteSpec：平移原 MP TenantLineInnerInterceptor 与 @TableLogic 的自动过滤行为，
 *    所有列表、分页查询统一 Specification.where(基础条件).and(tenantSpec()).and(logicDeleteSpec()) 链式组合
 *
 * @author 芋道源码
 */
public final class JpaSpecUtil {

    private JpaSpecUtil() {
    }

    // ========== IfPresent 系列（动态条件，空值跳过） ==========

    /**
     * 1. 等值条件（值存在时拼接） — 等价原 eqIfPresent
     */
    public static <T> Specification<T> eqIfPresent(String attr, Object value) {
        return value == null ? null : eq(attr, value);
    }

    /**
     * 2. 模糊条件（值存在时拼接，LIKE %v%，不转义通配符，与原 MP 行为一致） — 等价原 likeIfPresent
     */
    public static <T> Specification<T> likeIfPresent(String attr, String value) {
        return value == null || value.isEmpty() ? null : like(attr, value);
    }

    /**
     * 3. 大于等于条件 — 等价原 geIfPresent
     */
    public static <T> Specification<T> geIfPresent(String attr, Object value) {
        return value == null ? null : (root, query, cb) -> cb.greaterThanOrEqualTo(root.get(attr), comparable(value));
    }

    /**
     * 4. 大于条件 — 等价原 gtIfPresent
     */
    public static <T> Specification<T> gtIfPresent(String attr, Object value) {
        return value == null ? null : (root, query, cb) -> cb.greaterThan(root.get(attr), comparable(value));
    }

    /**
     * 5. 小于等于条件 — 等价原 leIfPresent
     */
    public static <T> Specification<T> leIfPresent(String attr, Object value) {
        return value == null ? null : (root, query, cb) -> cb.lessThanOrEqualTo(root.get(attr), comparable(value));
    }

    /**
     * 6. 小于条件 — 等价原 ltIfPresent
     */
    public static <T> Specification<T> ltIfPresent(String attr, Object value) {
        return value == null ? null : (root, query, cb) -> cb.lessThan(root.get(attr), comparable(value));
    }

    /**
     * 7. IN 条件（集合非空时拼接） — 等价原 inIfPresent
     */
    public static <T> Specification<T> inIfPresent(String attr, Collection<?> values) {
        return CollUtil.isEmpty(values) ? null : in(attr, values);
    }

    /**
     * 8. 区间条件（[min, max] 数组，双空跳过、单边退化为 ge/le） — 等价原 betweenIfPresent
     */
    public static <T> Specification<T> betweenIfPresent(String attr, Object[] values) {
        if (values == null || values.length != 2) {
            return null;
        }
        Object min = values[0];
        Object max = values[1];
        if (min != null && max != null) {
            return (root, query, cb) -> cb.between(root.get(attr), comparable(min), comparable(max));
        }
        if (min != null) {
            return geIfPresent(attr, min);
        }
        return leIfPresent(attr, max);
    }

    // ========== 必传系列 ==========

    /**
     * 9. 等值条件（必传）
     */
    public static <T> Specification<T> eq(String attr, Object value) {
        return (root, query, cb) -> cb.equal(root.get(attr), value);
    }

    /**
     * 10. 模糊条件（必传，LIKE %v%）
     */
    public static <T> Specification<T> like(String attr, String value) {
        return (root, query, cb) -> cb.like(root.get(attr), "%" + value + "%");
    }

    /**
     * 11. IN 条件（必传）
     */
    public static <T> Specification<T> in(String attr, Collection<?> values) {
        return (root, query, cb) -> root.get(attr).in(values);
    }

    /**
     * 12. FIND_IN_SET 等价条件 — 逗号分隔列的精确元素匹配
     * <p>
     * 平移原 MyBatisUtils.findInSet 的 PostgreSQL 分支：strpos(',' || col || ',', ',' || v || ',') > 0，
     * 基于实体属性（root）取列，元素按英文逗号精确匹配（1 不会匹配 11）
     */
    public static <T> Specification<T> findInSet(String attr, String value) {
        return (root, query, cb) -> {
            if (value == null || value.isEmpty()) {
                return cb.conjunction();
            }
            Expression<String> column = root.get(attr);
            Expression<String> paddedColumn = cb.concat(cb.concat(cb.literal(","), column), cb.literal(","));
            Expression<String> paddedValue = cb.concat(cb.concat(cb.literal(","), cb.literal(value)), cb.literal(","));
            return cb.greaterThan(cb.function("strpos", Integer.class, paddedColumn, paddedValue), 0);
        };
    }

    // ========== 固定规格（平移原 MP 拦截器行为） ==========

    /**
     * 13. 逻辑删除条件 — deleted = false
     * <p>
     * 平移原 @TableLogic 自动过滤：所有查询默认不返回已删除数据
     * <p>
     * 注意：租户条件 tenantSpec 定义在 biz-tenant 模块的
     * {@code cn.afei.mall.framework.tenant.core.db.TenantSpec}（依赖方向：tenant 模块可依赖 mybatis 模块，反之不可）
     */
    public static <T> Specification<T> logicDeleteSpec() {
        return (root, query, cb) -> {
            if (!hasAttribute(root, "deleted")) {
                return cb.conjunction();
            }
            return cb.isFalse(root.get("deleted"));
        };
    }

    // ========== 辅助 ==========

    /**
     * 15. 判断实体是否包含指定属性（tenantSpec/logicDeleteSpec 对非租户/非 BaseDO 实体的安全降级）
     */
    private static boolean hasAttribute(jakarta.persistence.criteria.Root<?> root, String attr) {
        try {
            root.getModel().getAttribute(attr);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Comparable comparable(Object value) {
        return (Comparable) Objects.requireNonNull(value);
    }

}
