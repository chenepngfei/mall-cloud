package cn.afei.mall.framework.jpa.config;

import cn.afei.mall.framework.jpa.core.repository.BaseRepositoryImpl;
import cn.afei.mall.framework.security.core.util.SecurityFrameworkUtils;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Optional;

/**
 * JPA 配置类，替代原 {@code MallMybatisAutoConfiguration}
 *
 * 1. 扫描 ${mall.info.base-package} 下的所有 Repository 接口，统一使用 {@link BaseRepositoryImpl} 作为实现基类
 * 2. 启用 JPA Auditing，自动填充 createTime、updateTime、creator、updater 字段
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

}
