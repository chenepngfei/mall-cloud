package cn.afei.mall.framework.mybatis.config;

import cn.afei.mall.framework.mybatis.core.repository.TenantIdSupplier;
import cn.afei.mall.framework.mybatis.core.repository.TenantSupplierHolder;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * JPA 配置类：
 * 1. 桥接 TenantIdSupplier（biz-tenant 模块提供，若无租户体系则跳过租户过滤）
 * 2. 显式指定实体扫描包构建 EntityManagerFactory —— @EntityScan 在多模块 + fat jar 场景下
 *    未能注入持久化单元（实体列表为空），此处直接 setPackagesToScan 兜底，
 *    实体包取 mall.info.base-package（默认 cn.afei.mall）。
 * Repository 扫描与 BaseRepositoryXImpl 基类由各启动类的 @EnableJpaRepositories 声明
 *
 * @author 芋道源码
 */
@AutoConfiguration(beforeName = "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaConfiguration")
@EnableConfigurationProperties(JpaProperties.class)
public class MallJpaAutoConfiguration {

    @Autowired(required = false)
    private TenantIdSupplier tenantIdSupplier;

    /**
     * 1. 构建 EntityManagerFactory（实体包显式扫描）
     */
    @Bean
    @ConditionalOnMissingBean({LocalContainerEntityManagerFactoryBean.class, EntityManagerFactory.class})
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource,
                                                                      JpaProperties jpaProperties,
                                                                      Environment environment) {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource);
        emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        String basePackage = environment.getProperty("mall.info.base-package", "cn.afei.mall");
        emf.setPackagesToScan(basePackage);
        // spring.jpa.properties.hibernate.* 配置透传给 Hibernate
        Map<String, Object> properties = new HashMap<>(jpaProperties.getProperties());
        emf.setJpaPropertyMap(properties);
        return emf;
    }

    /**
     * 2. 注册租户桥接
     */
    @PostConstruct
    public void init() {
        if (tenantIdSupplier != null) {
            TenantSupplierHolder.init(tenantIdSupplier);
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public TenantIdSupplier defaultTenantIdSupplier() {
        return () -> null; // 无租户体系时的兜底：跳过租户过滤
    }

}
