package cn.afei.mall.framework.datapermission.config;

import cn.afei.mall.framework.datapermission.core.aop.DataPermissionAnnotationAdvisor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * 数据权限的自动配置类
 *
 * @author 芋道源码
 */
@AutoConfiguration
public class MallDataPermissionAutoConfiguration {

    // 数据权限的 SQL 改写体系（DataPermissionRule + DataPermissionRuleHandler + MP DataPermissionInterceptor）已随 MyBatis Plus 移除；
    // 等价能力由 system 模块的 DeptDataPermissionSpec（JPA Specification）提供，@DataPermission 注解与 AOP 上下文继续生效



    @Bean
    public DataPermissionAnnotationAdvisor dataPermissionAnnotationAdvisor() {
        return new DataPermissionAnnotationAdvisor();
    }

}
