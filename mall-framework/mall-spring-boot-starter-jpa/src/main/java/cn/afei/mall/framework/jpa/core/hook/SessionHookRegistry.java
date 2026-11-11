package cn.afei.mall.framework.jpa.core.hook;

import jakarta.persistence.EntityManager;
import org.hibernate.Session;

import java.util.Collections;
import java.util.List;

/**
 * {@link SessionHook} 的静态注册中心
 *
 * 为什么用静态注册？因为 Spring Data 的 Repository 基类实例（BaseRepositoryImpl）不是 Spring Bean，
 * 无法自动注入，只能通过静态字段传递；由 {@code MallJpaAutoConfiguration} 在启动完成前注册
 */
public final class SessionHookRegistry {

    private static volatile List<SessionHook> hooks = Collections.emptyList();

    private SessionHookRegistry() {
    }

    public static void setHooks(List<SessionHook> sessionHooks) {
        hooks = sessionHooks != null ? List.copyOf(sessionHooks) : Collections.emptyList();
    }

    /**
     * 对当前 EntityManager 绑定的 Session 应用全部钩子
     *
     * @param entityManager Repository 使用的 EntityManager（事务内为事务绑定的 Session）
     */
    public static void applyHooks(EntityManager entityManager) {
        if (hooks.isEmpty()) {
            return;
        }
        Session session;
        try {
            session = entityManager.unwrap(Session.class);
        } catch (Exception e) {
            // 无绑定的 Session（例如启动期无事务），跳过
            return;
        }
        for (SessionHook hook : hooks) {
            hook.customize(session);
        }
    }

}
