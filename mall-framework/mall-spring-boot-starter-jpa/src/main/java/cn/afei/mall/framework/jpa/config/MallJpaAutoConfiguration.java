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
@EnableJpaRepositories(basePackages = "${mall.info.base-package}",
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

