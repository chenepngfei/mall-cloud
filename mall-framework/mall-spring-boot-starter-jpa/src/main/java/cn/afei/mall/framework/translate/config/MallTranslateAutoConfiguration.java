package cn.afei.mall.framework.translate.config;

import cn.afei.mall.framework.translate.core.TranslateUtils;
import org.dromara.trans.service.impl.TransService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

// 不能声明 @AutoConfiguration(after = TransServiceConfig.class)：
// 该约束会让 Boot 排序器把 TransServiceConfig 提到 EasyTransJPA3Config 之前，
// 导致 SimpleTransService 因 @ConditionalOnBean(SimpleTransDiver) 不满足而缺失。
// 因此这里通过 ObjectProvider 延迟获取，避免依赖自动配置顺序。
@AutoConfiguration
public class MallTranslateAutoConfiguration {

    @Bean
    @SuppressWarnings("InstantiationOfUtilityClass")
    public TranslateUtils translateUtils(ObjectProvider<TransService> transServiceProvider) {
        TransService transService = transServiceProvider.getIfAvailable();
        if (transService != null) {
            TranslateUtils.init(transService);
        }
        return new TranslateUtils();
    }

}
