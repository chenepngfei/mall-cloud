package cn.afei.mall.framework.mybatis.core.type.convert;

import cn.afei.mall.framework.common.util.json.JsonUtils;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Map;

/**
 * 3. JSON Map&lt;String, Object&gt; 字段转换器 — Map 以 JSON 对象文本存入 varchar 列
 * <p>
 * 平移原 MP JacksonTypeHandler 行为，用于 template_params 等字段
 *
 * @author 芋道源码
 */
@Converter
public class JsonMapConverter implements AttributeConverter<Map<String, Object>, String> {

    @Override
    public String convertToDatabaseColumn(Map<String, Object> attribute) {
        return JsonUtils.toJsonString(attribute);
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String dbData) {
        return JsonUtils.parseMap(dbData);
    }

}
