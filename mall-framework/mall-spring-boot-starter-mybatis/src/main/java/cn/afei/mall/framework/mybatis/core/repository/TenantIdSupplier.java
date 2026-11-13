package cn.afei.mall.framework.mybatis.core.repository;

/**
 * 1. 租户编号供给接口 — 由 biz-tenant 模块的自动配置注册实现，解开 starter-mybatis 不能反向依赖 tenant 模块的问题
 *
 * 返回 null 表示当前无需租户过滤（忽略状态 / 无租户体系）
 *
 * @author 芋道源码
 */
@FunctionalInterface
public interface TenantIdSupplier {

    /**
     * 2. 获取当前租户编号；null 表示跳过租户过滤
     */
    Long get();

}
