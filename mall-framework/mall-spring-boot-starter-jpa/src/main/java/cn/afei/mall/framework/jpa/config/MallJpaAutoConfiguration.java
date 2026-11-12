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
     * 【临时诊断】通过 -Dmall.jpa.readOnlyDiagnostic=true 激活，包装 DataSource
     * 打印所有 Connection.setReadOnly 调用栈。问题定位后删除。
     */
    @Bean
    public static org.springframework.beans.factory.config.BeanPostProcessor readOnlyDiagnosticBpp() {
        return new org.springframework.beans.factory.config.BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) {
                if (Boolean.getBoolean("mall.jpa.readOnlyDiagnostic")
                        && bean instanceof JpaTransactionManager) {
                    System.out.println("[READONLY-DIAG] TM bean '" + beanName + "' -> " + bean
                            + " dialect=" + ((JpaTransactionManager) bean).getJpaDialect());
                }
                if (Boolean.getBoolean("mall.jpa.readOnlyDiagnostic")
                        && bean instanceof javax.sql.DataSource ds
                        && !beanName.contains("readOnlyDiagnostic")) {
                    System.out.println("[READONLY-DIAG] wrapping DataSource bean: " + beanName);
                    return new ReadOnlyDiagnosticDataSource(ds);
                }
                return bean;
            }
        };
    }

    /**
     * 自定义 JPA 事务管理器：HibernateJpaDialect.setPrepareConnection(false)，
     * 禁止 Spring 把只读事务标志下发到 JDBC 连接。
     *
     * 原因：PgJDBC 的 setReadOnly(true) 在 autocommit 下执行的是会话级的
     * "SET SESSION CHARACTERISTICS AS TRANSACTION READ ONLY"，连接回池后若未复位，
     * 该连接上的所有后续写操作都会报 "cannot execute INSERT in a read-only transaction"。
     * MySQL 驱动的 readOnly 只是提示，因此 MP 时代无感。只读语义仍由
     * Hibernate 的 FlushMode.MANUAL 保证，不丢失。
     *
     * 注意：不能通过 TransactionManagerCustomizer 实现——Boot 自动装配的
     * JpaTransactionManager 与 Spring Data 实际使用的事务管理器可能不是同一实例。
     */
    @Bean
    public JpaTransactionManager transactionManager(jakarta.persistence.EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager(entityManagerFactory);
        HibernateJpaDialect jpaDialect = new HibernateJpaDialect() {
            @Override
            public String toString() {
                return "MallHibernateJpaDialect(prepareConnection=false)";
            }
        };
        jpaDialect.setPrepareConnection(false);
        transactionManager.setJpaDialect(jpaDialect);
        return transactionManager;
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

