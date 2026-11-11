package cn.afei.mall.framework.jpa.core.query;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.afei.mall.framework.common.util.collection.ArrayUtils;
import org.springframework.util.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

/**
 * 拓展 Spring Data JPA 的 {@link Specification} 构建器，主要增加如下功能：
 *
 * 1. 拼接条件的方法，增加 xxxIfPresent 方法，用于判断值不存在的时候，不要拼接到条件中
 * 2. 链式调用风格对齐原 MyBatis Plus 的 {@code LambdaQueryWrapperX}
 *
 * @param <T> 数据类型
 */
public class LambdaSpecifications<T> {

    /**
     * 条件列表：每个元素接收 (path, cb) 返回一个 Predicate
     */
    private final List<BiFunction<Path<T>, CriteriaBuilder, Predicate>> conditions = new ArrayList<>();
    /**
     * 排序列表：[0] 属性名，[1] 方向（asc/desc）
     */
    private final List<String[]> orders = new ArrayList<>();

    public static <T> LambdaSpecifications<T> of() {
        return new LambdaSpecifications<>();
    }

    // ========== xxxIfPresent 方法：值为空时，不拼接条件 ==========

    public LambdaSpecifications<T> eqIfPresent(SFunction<T, ?> column, Object val) {
        if (ObjectUtil.isNotEmpty(val)) {
            conditions.add((path, cb) -> cb.equal(path.get(propertyName(column)), val));
        }
        return this;
    }

    public LambdaSpecifications<T> neIfPresent(SFunction<T, ?> column, Object val) {
        if (ObjectUtil.isNotEmpty(val)) {
            conditions.add((path, cb) -> cb.notEqual(path.get(propertyName(column)), val));
        }
        return this;
    }

    public LambdaSpecifications<T> likeIfPresent(SFunction<T, ?> column, String val) {
        if (StringUtils.hasText(val)) {
            conditions.add((path, cb) -> cb.like(path.<String>get(propertyName(column)), "%" + val + "%"));
        }
        return this;
    }

    public LambdaSpecifications<T> likeRightIfPresent(SFunction<T, ?> column, String val) {
        if (StringUtils.hasText(val)) {
            conditions.add((path, cb) -> cb.like(path.<String>get(propertyName(column)), val + "%"));
        }
        return this;
    }

    public LambdaSpecifications<T> gtIfPresent(SFunction<T, ?> column, Object val) {
        if (val != null) {
            conditions.add((path, cb) -> cb.greaterThan(path.<Comparable>get(propertyName(column)), (Comparable) val));
        }
        return this;
    }

    public LambdaSpecifications<T> geIfPresent(SFunction<T, ?> column, Object val) {
        if (val != null) {
            conditions.add((path, cb) -> cb.greaterThanOrEqualTo(path.<Comparable>get(propertyName(column)), (Comparable) val));
        }
        return this;
    }

    public LambdaSpecifications<T> ltIfPresent(SFunction<T, ?> column, Object val) {
        if (val != null) {
            conditions.add((path, cb) -> cb.lessThan(path.<Comparable>get(propertyName(column)), (Comparable) val));
        }
        return this;
    }

    public LambdaSpecifications<T> leIfPresent(SFunction<T, ?> column, Object val) {
        if (val != null) {
            conditions.add((path, cb) -> cb.lessThanOrEqualTo(path.<Comparable>get(propertyName(column)), (Comparable) val));
        }
        return this;
    }

    public LambdaSpecifications<T> inIfPresent(SFunction<T, ?> column, Collection<?> values) {
        if (ObjectUtil.isAllNotEmpty(values) && !ArrayUtil.isEmpty(values)) {
            conditions.add((path, cb) -> path.get(propertyName(column)).in(values));
        }
        return this;
    }

    public LambdaSpecifications<T> inIfPresent(SFunction<T, ?> column, Object... values) {
        if (ObjectUtil.isAllNotEmpty(values) && !ArrayUtil.isEmpty(values)) {
            conditions.add((path, cb) -> path.get(propertyName(column)).in(Arrays.asList(values)));
        }
        return this;
    }

    public LambdaSpecifications<T> betweenIfPresent(SFunction<T, ?> column, Object val1, Object val2) {
        if (val1 != null && val2 != null) {
            conditions.add((path, cb) -> cb.between(path.<Comparable>get(propertyName(column)), (Comparable) val1, (Comparable) val2));
        } else if (val1 != null) {
            return geIfPresent(column, val1);
        } else if (val2 != null) {
            return leIfPresent(column, val2);
        }
        return this;
    }

    public LambdaSpecifications<T> betweenIfPresent(SFunction<T, ?> column, Object[] values) {
        Object val1 = ArrayUtils.get(values, 0);
        Object val2 = ArrayUtils.get(values, 1);
        return betweenIfPresent(column, val1, val2);
    }

    /**
     * 拼接两个闭区间的重叠条件
     *
     * <p>记录区间与查询区间重叠的条件为：记录开始值小于等于查询结束值，
     * 且记录结束值大于等于查询开始值。不能使用单字段 {@code BETWEEN} 代替，
     * 否则会漏掉完整覆盖查询区间的记录。</p>
     *
     * @param beginColumn 记录开始值字段
     * @param endColumn   记录结束值字段
     * @param times       查询闭区间
     * @return 查询条件
     */
    public LambdaSpecifications<T> betweenIfPresent(
            SFunction<T, ?> beginColumn, SFunction<T, ?> endColumn, LocalDateTime[] times) {
        return leIfPresent(beginColumn, ArrayUtils.get(times, 1))
                .geIfPresent(endColumn, ArrayUtils.get(times, 0));
    }

    // ========== 普通（非 IfPresent）条件 ==========

    public LambdaSpecifications<T> eq(SFunction<T, ?> column, Object val) {
        conditions.add((path, cb) -> cb.equal(path.get(propertyName(column)), val));
        return this;
    }

    public LambdaSpecifications<T> like(SFunction<T, ?> column, String val) {
        if (val != null) {
            conditions.add((path, cb) -> cb.like(path.<String>get(propertyName(column)), "%" + val + "%"));
        }
        return this;
    }

    public LambdaSpecifications<T> ne(SFunction<T, ?> column, Object val) {
        conditions.add((path, cb) -> cb.notEqual(path.get(propertyName(column)), val));
        return this;
    }

    public LambdaSpecifications<T> in(SFunction<T, ?> column, Collection<?> values) {
        if (ObjectUtil.isAllNotEmpty(values) && !ArrayUtil.isEmpty(values)) {
            conditions.add((path, cb) -> path.get(propertyName(column)).in(values));
        }
        return this;
    }

    public LambdaSpecifications<T> isNull(SFunction<T, ?> column) {
        conditions.add((path, cb) -> cb.isNull(path.get(propertyName(column))));
        return this;
    }

    public LambdaSpecifications<T> isNotNull(SFunction<T, ?> column) {
        conditions.add((path, cb) -> cb.isNotNull(path.get(propertyName(column))));
        return this;
    }

    // ========== 排序 ==========

    public LambdaSpecifications<T> orderByAsc(SFunction<T, ?> column) {
        orders.add(new String[]{propertyName(column), "asc"});
        return this;
    }

    public LambdaSpecifications<T> orderByDesc(SFunction<T, ?> column) {
        orders.add(new String[]{propertyName(column), "desc"});
        return this;
    }

    // ========== 构建 ==========

    public Specification<T> build() {
        return (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>(conditions.size());
            for (BiFunction<Path<T>, CriteriaBuilder, Predicate> condition : conditions) {
                predicates.add(condition.apply(root, cb));
            }
            if (!orders.isEmpty() && query != null) {
                List<Order> orderList = new ArrayList<>(orders.size());
                for (String[] order : orders) {
                    if ("asc".equals(order[1])) {
                        orderList.add(cb.asc(root.get(order[0])));
                    } else {
                        orderList.add(cb.desc(root.get(order[0])));
                    }
                }
                query.orderBy(orderList);
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    // ========== Lambda 解析属性名 ==========

    /**
     * 解析 Lambda 表达式对应的实体属性名
     *
     * 例如 AdminUserDO::getUsername 解析为 username
     *
     * @param column 字段的 Lambda 表达式
     * @return 属性名
     */
    public static <T> String propertyName(SFunction<T, ?> column) {
        Objects.requireNonNull(column, "Lambda 表达式不能为空");
        try {
            Method writeReplace = column.getClass().getDeclaredMethod("writeReplace");
            writeReplace.setAccessible(true);
            SerializedLambda lambda = (SerializedLambda) writeReplace.invoke(column);
            String methodName = lambda.getImplMethodName(); // 如 getUsername、isDeleted
            String fieldName;
            if (methodName.startsWith("get") && methodName.length() > 3) {
                fieldName = methodName.substring(3);
            } else if (methodName.startsWith("is") && methodName.length() > 2) {
                fieldName = methodName.substring(2);
            } else {
                throw new IllegalArgumentException("无法解析的方法名：" + methodName + "，请使用 XxxDO::getXxx 形式的 Lambda");
            }
            // 首字母小写；前两个字母都大写时保持原样（如 getURL → URL）
            if (fieldName.length() == 1 || Character.isUpperCase(fieldName.charAt(1))) {
                return fieldName;
            }
            return Character.toLowerCase(fieldName.charAt(0)) + fieldName.substring(1);
        } catch (Exception e) {
            throw new IllegalArgumentException("解析 Lambda 表达式失败，请使用 XxxDO::getXxx 形式的 Lambda", e);
        }
    }

}
