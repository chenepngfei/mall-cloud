package cn.afei.mall.framework.datapermission.config;

import cn.afei.mall.framework.datapermission.core.aop.DataPermissionAnnotationAdvisor;
import cn.afei.mall.framework.datapermission.core.rule.DataPermissionHookImpl;
import cn.afei.mall.framework.datapermission.core.rule.DataPermissionRule;
import cn.afei.mall.framework.datapermission.core.rule.DataPermissionRuleFactory;
import cn.afei.mall.framework.datapermission.core.rule.DataPermissionRuleFactoryImpl;
import cn.afei.mall.framework.jpa.core.hook.DataPermissionHook;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * 数据权限的自动配置类（JPA 版）
 *
 * 通过 {@link DataPermissionHook} 把规则追加到 Repository 的 Specification 查询，
 * 替代原 MyBatis Plus 的 DataPermissionInterceptor
 *
 * @author 芋道源码
 */
@AutoConfiguration
public class MallDataPermissionAutoConfiguration {

    @Bean
    public DataPermissionRuleFactory dataPermissionRuleFactory(List<DataPermissionRule> rules) {
        return new DataPermissionRuleFactoryImpl(rules);
    }

    @Bean
    public DataPermissionHook dataPermissionHook(DataPermissionRuleFactory ruleFactory) {
        return new DataPermissionHookImpl(ruleFactory);
    }

    @Bean
    public DataPermissionAnnotationAdvisor dataPermissionAnnotationAdvisor() {
        return new DataPermissionAnnotationAdvisor();
    }

}
