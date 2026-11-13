package cn.afei.mall.framework.mybatis.core.type.convert;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * 6. 整数布尔字段转换器 — Boolean 与整数列（smallint/tinyint）互转
 * <p>
 * 平移原 IntegerBooleanTypeHandler 行为：写入 true→1、false→0；读取非 0 为 true、0 为 false、NULL 为 null
 * 用于 sms_code.used 等“列是 smallint、字段是 Boolean”的场景，避免 PG 不允许 boolean 与整数隐式转换
 *
 * @author 芋道源码
 */
@Converter
public class SmallintBooleanConverter implements AttributeConverter<Boolean, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Boolean attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute ? 1 : 0;
    }

    @Override
    public Boolean convertToEntityAttribute(Integer dbData) {
        if (dbData == null) {
            return null;
        }
        return dbData != 0;
    }

}
