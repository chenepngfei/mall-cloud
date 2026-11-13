package cn.afei.mall.framework.mybatis.core.type.convert;

import cn.hutool.core.lang.Assert;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.extra.spring.SpringUtil;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * 7. 字段加密转换器 — String 字段 AES 加密后以 Base64 文本存库
 * <p>
 * 平移原 EncryptTypeHandler 行为（hutool AES + encryptBase64/decryptStr，密钥懒加载静态缓存），
 * 密钥配置项自 mybatis-plus.encryptor.password 迁移为 mall.db.encryptor.password，保证存量密文可解
 * 用于 infra_data_source_config.password 等敏感字段
 *
 * @author 芋道源码
 */
@Converter
public class EncryptStringConverter implements AttributeConverter<String, String> {

    /**
     * 加解密秘钥的配置项
     */
    private static final String ENCRYPTOR_PROPERTY_NAME = "mall.db.encryptor.password";

    /**
     * AES 实例；懒加载静态缓存（同原 TypeHandler）
     */
    private static AES aes;

    @Override
    public String convertToDatabaseColumn(String attribute) {
        return encrypt(attribute);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return decrypt(dbData);
    }

    public static String encrypt(String rawValue) {
        if (rawValue == null) {
            return null;
        }
        return getEncryptor().encryptBase64(rawValue);
    }

    private static String decrypt(String value) {
        if (value == null) {
            return null;
        }
        return getEncryptor().decryptStr(value);
    }

    private static AES getEncryptor() {
        if (aes != null) {
            return aes;
        }
        // 构建 AES
        String password = SpringUtil.getProperty(ENCRYPTOR_PROPERTY_NAME);
        Assert.notEmpty(password, "配置项({}) 不能为空", ENCRYPTOR_PROPERTY_NAME);
        aes = SecureUtil.aes(password.getBytes());
        return aes;
    }

}
