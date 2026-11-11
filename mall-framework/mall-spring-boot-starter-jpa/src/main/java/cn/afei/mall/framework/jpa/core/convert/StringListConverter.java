package cn.afei.mall.framework.jpa.core.convert;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.List;

/**
 * List&lt;String&gt; 的类型转换器实现类，对应数据库的 varchar 类型，逗号分隔
 */
@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {

    private static final String COMMA = ",";

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        if (attribute == null) {
            return null;
        }
        return CollUtil.join(attribute, COMMA);
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return StrUtil.splitTrim(dbData, COMMA);
    }

}
