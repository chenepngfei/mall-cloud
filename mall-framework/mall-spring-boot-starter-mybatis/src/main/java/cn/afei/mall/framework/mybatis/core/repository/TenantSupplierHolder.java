package cn.afei.mall.framework.mybatis.core.repository;

/**
 * 1. TenantIdSupplier 的静态持有器 — 供 BaseRepositoryXImpl 等 JDBC/JPA 层组件获取当前租户
 *
 * @author 芋道源码
 */
public final class TenantSupplierHolder {

    private static volatile TenantIdSupplier supplier;

    private TenantSupplierHolder() {
    }

    /**
     * 2. 注册供给实现（由自动配置调用一次）
     */
    public static void init(TenantIdSupplier s) {
        supplier = s;
    }

    /**
     * 3. 获取当前租户编号；未注册实现或忽略状态返回 null
     */
    public static Long getTenantId() {
        TenantIdSupplier s = supplier;
        return s == null ? null : s.get();
    }

}
