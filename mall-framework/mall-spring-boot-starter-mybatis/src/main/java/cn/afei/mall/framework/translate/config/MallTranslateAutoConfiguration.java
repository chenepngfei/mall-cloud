package cn.afei.mall.framework.translate.config;

import cn.afei.mall.framework.translate.core.TranslateUtils;
import org.dromara.trans.config.TransServiceConfig;
import org.dromara.trans.service.impl.TransService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;

// 解决 Easy-Trans 自动配置顺序问题，详见 https://t.zsxq.com/nX3vi
// 原 after 含 EasyTransMybatisPlusConfig（easy-trans-mybatis-plus-extend），JPA 迁移后依赖已移除，仅保留核心配置排序
@AutoConfiguration(after = {
        TransServiceConfig.class
})
public class MallTranslateAutoConfiguration {

    @Bean
    @ConditionalOnBean(TransService.class)
    @SuppressWarnings("InstantiationOfUtilityClass")
    public TranslateUtils translateUtils(TransService transService) {
        TranslateUtils.init(transService);
        return new TranslateUtils();
    }

}
