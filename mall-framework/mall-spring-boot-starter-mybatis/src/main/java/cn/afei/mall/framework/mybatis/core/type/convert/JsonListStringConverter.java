package cn.afei.mall.framework.mybatis.core.type.convert;

import cn.afei.mall.framework.common.util.json.JsonUtils;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.List;

/**
 * 1. JSON List&lt;String&gt; 字段转换器 — List 以 JSON 数组文本（如 ["a","b"]）存入 varchar 列
 * <p>
 * 平移原 MP JacksonTypeHandler 行为：序列化/反序列化均走项目统一 ObjectMapper（支持 LocalDateTime）
 *
 * @author 芋道源码
 */
@Converter
public class JsonListStringConverter implements AttributeConverter<List<String>, String> {

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        return JsonUtils.toJsonString(attribute);
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        return JsonUtils.parseArray(dbData, String.class);
    }

}
