package cn.afei.mall.framework.mybatis.core.type.convert;

import cn.afei.mall.framework.common.util.json.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Set;

/**
 * 2. JSON Set&lt;Long&gt; 字段转换器 — Set 以 JSON 数组文本（如 [1,2]）存入 varchar 列
 * <p>
 * 平移原 MP JacksonTypeHandler 行为，用于 post_ids、menu_ids、data_scope_dept_ids 等字段
 *
 * @author 芋道源码
 */
@Converter
public class JsonSetLongConverter implements AttributeConverter<Set<Long>, String> {

    private static final TypeReference<Set<Long>> TYPE = new TypeReference<>() {
    };

    @Override
    public String convertToDatabaseColumn(Set<Long> attribute) {
        return JsonUtils.toJsonString(attribute);
    }

    @Override
    public Set<Long> convertToEntityAttribute(String dbData) {
        return JsonUtils.parseObject(dbData, TYPE);
    }

}
