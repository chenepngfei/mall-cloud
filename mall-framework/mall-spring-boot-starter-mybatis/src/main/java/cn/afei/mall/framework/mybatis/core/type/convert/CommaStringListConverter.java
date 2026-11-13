package cn.afei.mall.framework.mybatis.core.type.convert;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.List;

/**
 * 5. 逗号分隔 List&lt;String&gt; 字段转换器 — List 以英文逗号拼接文本（如 a,b,c）存入 varchar 列
 * <p>
 * 平移原 StringListTypeHandler 行为：写入 join(",")，读取 splitTrim(",")
 * 用于 tenant.websites、mail_log.to_mails 等逗号分隔列
 *
 * @author 芋道源码
 */
@Converter
public class CommaStringListConverter implements AttributeConverter<List<String>, String> {

    private static final String COMMA = ",";

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
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
