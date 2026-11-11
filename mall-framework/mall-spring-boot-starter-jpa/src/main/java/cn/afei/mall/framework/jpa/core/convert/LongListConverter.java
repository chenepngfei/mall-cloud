package cn.afei.mall.framework.jpa.core.convert;

import cn.hutool.core.collection.CollUtil;
import cn.afei.mall.framework.common.util.string.StrUtils;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.List;

/**
 * List&lt;Long&gt; 的类型转换器实现类，对应数据库的 varchar 类型，逗号分隔
 */
@Converter
public class LongListConverter implements AttributeConverter<List<Long>, String> {

    private static final String COMMA = ",";

    @Override
    public String convertToDatabaseColumn(List<Long> attribute) {
        if (attribute == null) {
            return null;
        }
        return CollUtil.join(attribute, COMMA);
    }

    @Override
    public List<Long> convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return StrUtils.splitToLong(dbData, COMMA);
    }

}
