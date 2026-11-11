package cn.afei.mall.framework.tenant.core.db;

import cn.afei.mall.framework.jpa.core.hook.SessionHook;
import cn.afei.mall.framework.tenant.core.context.TenantContextHolder;
import org.hibernate.Session;

/**
 * 多租户的 Session 钩子：在 Repository 数据操作前，按租户上下文启用/禁用 Hibernate 租户过滤器
 *
 * 对应原 MyBatis Plus 的 TenantLineInnerInterceptor 的查询过滤能力：
 * 1. 上下文存在租户编号且未忽略时，启用 tenantFilter（查询自动拼接 tenant_id = ?）
 * 2. 忽略租户（@TenantIgnore / TenantUtils.execute(ignore)）或无租户上下文时，禁用过滤器
 */
public class TenantSessionHook implements SessionHook {

    @Override
    public void customize(Session session) {
        if (TenantContextHolder.isIgnore() || TenantContextHolder.getTenantId() == null) {
            session.disableFilter(TenantBaseDO.TENANT_FILTER_NAME);
        } else {
            session.enableFilter(TenantBaseDO.TENANT_FILTER_NAME)
                    .setParameter("tenantId", TenantContextHolder.getRequiredTenantId());
        }
    }

}
