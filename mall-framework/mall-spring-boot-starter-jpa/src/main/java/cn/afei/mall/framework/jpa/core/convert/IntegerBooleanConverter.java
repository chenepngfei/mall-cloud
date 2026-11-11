package cn.afei.mall.framework.jpa.core.convert;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Boolean 的类型转换器实现类，对应数据库的整数类型（tinyint、smallint、int2、number 等）
 *
 * 写入时 true 存 1、false 存 0；读取时非 0 为 true、0 为 false、NULL 为 null。
 * 用于数据库列是整数、而 Java 字段是 Boolean 的场景，避免 PostgreSQL 等数据库不允许 boolean 与整数隐式转换。
 *
 * 注意：仅按字段使用，不要注册为全局转换器，否则会影响真正的 boolean / bit 列
 */
@Converter
public class IntegerBooleanConverter implements AttributeConverter<Boolean, Integer> {

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
