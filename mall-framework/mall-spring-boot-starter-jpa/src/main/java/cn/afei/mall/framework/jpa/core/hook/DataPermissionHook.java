package cn.afei.mall.framework.jpa.core.hook;

import org.springframework.data.jpa.domain.Specification;

/**
 * 数据权限钩子接口：Repository 执行 Specification 查询前，追加数据权限过滤条件
 *
 * 由 biz-data-permission starter 提供实现（基于 DataPermissionRuleFactory），
 * 通过 {@link DataPermissionHookRegistry} 注册，在 {@code BaseRepositoryImpl} 的查询入口被调用
 */
public interface DataPermissionHook {

    /**
     * 针对实体构建数据权限的过滤条件
     *
     * @param entityClass 实体类
     * @param <T>         实体类型
     * @return 数据权限 Specification；实体不受数据权限管控时返回 null
     */
    <T> Specification<T> buildSpecification(Class<T> entityClass);

}
