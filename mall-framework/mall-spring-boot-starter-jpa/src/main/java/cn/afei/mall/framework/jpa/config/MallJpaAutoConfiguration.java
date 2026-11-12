package cn.afei.mall.framework.jpa.config;

import cn.afei.mall.framework.jpa.core.hook.DataPermissionHook;
import cn.afei.mall.framework.jpa.core.hook.DataPermissionHookRegistry;
import cn.afei.mall.framework.jpa.core.hook.SessionHook;
import cn.afei.mall.framework.jpa.core.hook.SessionHookRegistry;
import cn.afei.mall.framework.jpa.core.repository.BaseRepositoryImpl;
import cn.afei.mall.framework.security.core.util.SecurityFrameworkUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;

import java.util.List;
import java.util.Optional;

/**
 * JPA 配置类，替代原 {@code MallMybatisAutoConfiguration}
 *
 * 1. 扫描 ${mall.info.base-package} 下的所有 Repository 接口，统一使用 {@link BaseRepositoryImpl} 作为实现基类
 * 2. 启用 JPA Auditing，自动填充 createTime、updateTime、creator、updater 字段
 * 3. 注册 Session 钩子（多租户 Filter）与数据权限钩子（由 biz-tenant、biz-data-permission 提供）
 */
@AutoConfiguration
@EnableJpaRepositories(basePackages = "${mall.info.base-package:cn.afei.mall}",
        repositoryBaseClass = BaseRepositoryImpl.class)
@EnableJpaAuditing(auditorAwareRef = "loginUserAuditorAware")
public class MallJpaAutoConfiguration {

    /**
     * 审计填充：从 Security 上下文获取当前登录用户，作为 creator、updater
     */
    @Bean
    public AuditorAware<String> loginUserAuditorAware() {
        return () -> Optional.ofNullable(SecurityFrameworkUtils.getLoginUserId())
                .map(String::valueOf);
    }

    /**
     * 禁止 Spring 把只读事务标志下发到 JDBC 连接（prepareConnection=false）。
     *
     * 原因：MySQL 驱动的 readOnly 只是提示、不强制；PostgreSQL 会真正以
     * "BEGIN READ ONLY" 开启事务并拒绝写入。dynamic-datasource 与 Spring 各自
     * 管理一份连接包装，事务异常回滚后池中连接的 readOnly 标志可能不被复位，
     * 导致后续借到该连接的写操作报 "cannot execute INSERT in a read-only transaction"。
     * 只读语义仍由 Hibernate 的 FlushMode.MANUAL 保证，不丢失。
     */
    @Bean
    public TransactionManagerCustomizer<JpaTransactionManager> jpaTransactionManagerCustomizer() {
        return transactionManager -> {
            if (transactionManager.getJpaDialect() instanceof HibernateJpaDialect hibernateDialect) {
                hibernateDialect.setPrepareConnection(false);
            }
        };
    }

    /**
     * 在所有单例初始化后，把 biz-tenant 等提供的 Session 钩子注册到静态注册中心
     */
    @Bean
    public SmartInitializingSingleton sessionHookInitializer(ObjectProvider<SessionHook> sessionHooks) {
        // 在所有单例初始化后再取值，避免提前实例化 biz-tenant 的钩子 Bean
        return () -> SessionHookRegistry.setHooks(sessionHooks.stream().toList());
    }

    /**
     * 在所有单例初始化后，把 biz-data-permission 提供的数据权限钩子注册到静态注册中心
     */
    @Bean
    public SmartInitializingSingleton dataPermissionHookInitializer(ObjectProvider<DataPermissionHook> dataPermissionHook) {
        return () -> dataPermissionHook.ifAvailable(DataPermissionHookRegistry::setHook);
    }

}

