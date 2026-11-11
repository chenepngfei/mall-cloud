package cn.afei.mall.framework.jpa.core.hook;

import org.hibernate.Session;

/**
 * Session 钩子接口：在 Repository 执行数据操作前，对当前事务的 Hibernate Session 做定制
 *
 * 典型场景：多租户 starter 通过它启用 Hibernate @Filter（tenantFilter）
 *
 * 由 biz-tenant 等业务 starter 提供实现，通过 {@link SessionHookRegistry} 注册，
 * 在 {@code BaseRepositoryImpl} 的数据操作入口被调用
 */
public interface SessionHook {

    /**
     * 定制 Session，例如 enableFilter / disableFilter
     *
     * @param session 当前事务绑定的 Hibernate Session
     */
    void customize(Session session);

}
