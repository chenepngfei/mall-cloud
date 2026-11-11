package cn.afei.mall.module.system.dal.convert;

import cn.afei.mall.framework.common.util.json.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Map;

/**
 * Map&lt;String, Object&gt; 的类型转换器实现类，对应数据库的 JSON 字符串类型
 *
 * 对齐原 MyBatis Plus 的 JacksonTypeHandler 的存储格式
 */
@Converter
public class ObjectMapConverter implements AttributeConverter<Map<String, Object>, String> {

    private static final TypeReference<Map<String, Object>> TYPE = new TypeReference<>() {
    };

    @Override
    public String convertToDatabaseColumn(Map<String, Object> attribute) {
        if (attribute == null) {
            return null;
        }
        return JsonUtils.toJsonString(attribute);
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return JsonUtils.parseObject(dbData, TYPE);
    }

}
