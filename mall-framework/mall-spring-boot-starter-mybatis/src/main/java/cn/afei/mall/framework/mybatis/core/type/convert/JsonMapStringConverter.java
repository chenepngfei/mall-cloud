package cn.afei.mall.framework.mybatis.core.type.convert;

import cn.afei.mall.framework.common.util.json.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Map;

/**
 * 4. JSON Map&lt;String, String&gt; 字段转换器 — Map 以 JSON 对象文本存入 varchar 列
 * <p>
 * 平移原 MP JacksonTypeHandler 行为，用于 OAuth2 accessToken 的 userInfo 等字段
 *
 * @author 芋道源码
 */
@Converter
public class JsonMapStringConverter implements AttributeConverter<Map<String, String>, String> {

    private static final TypeReference<Map<String, String>> TYPE = new TypeReference<>() {
    };

    @Override
    public String convertToDatabaseColumn(Map<String, String> attribute) {
        return JsonUtils.toJsonString(attribute);
    }

    @Override
    public Map<String, String> convertToEntityAttribute(String dbData) {
        return JsonUtils.parseObject(dbData, TYPE);
    }

}
