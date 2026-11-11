package cn.afei.mall.framework.datapermission.core.rule;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.Set;

/**
 * 数据权限规则接口
 * 通过实现接口，自定义数据规则。例如说，
 *
 * JPA 版：返回 jakarta Criteria 的 {@link Predicate}，追加到 Repository 的 Specification 查询中，
 * 替代原 MyBatis Plus 基于 JSqlParser 的 SQL 重写
 *
 * @author 芋道源码
 */
public interface DataPermissionRule {

    /**
     * 返回需要生效的实体类数组
     *
     * @return 实体类数组
     */
    Set<Class<?>> getEntityClasses();

    /**
     * 针对实体生成数据权限的过滤条件
     *
     * @param entityClass     实体类
     * @param root            查询根
     * @param criteriaBuilder 条件构建器
     * @return 过滤条件；返回 null 表示无需过滤（例如 ALL 权限）
     */
    Predicate getPredicate(Class<?> entityClass, Root<?> root, CriteriaBuilder criteriaBuilder);

}
