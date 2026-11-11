package cn.afei.mall.framework.jpa.core.convert;

import cn.hutool.core.lang.Assert;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.extra.spring.SpringUtil;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * 字段加密的 AttributeConverter 实现类，基于 {@link AES} 实现，对应原 MyBatis 的 EncryptTypeHandler
 *
 * 可通过 mall.jpa.encryptor.password 配置项，设置密钥（兼容读取原 mybatis-plus.encryptor.password）
 */
@Converter
public class EncryptConverter implements AttributeConverter<String, String> {

    private static final String ENCRYPTOR_PROPERTY_NAME = "mall.jpa.encryptor.password";
    /**
     * 原配置项，兼容读取
     */
    private static final String LEGACY_ENCRYPTOR_PROPERTY_NAME = "mybatis-plus.encryptor.password";

    private static volatile AES aes;

    @Override
    public String convertToDatabaseColumn(String attribute) {
        return encrypt(attribute);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return decrypt(dbData);
    }

    private static String decrypt(String value) {
        if (value == null) {
            return null;
        }
        return getEncryptor().decryptStr(value);
    }

    public static String encrypt(String rawValue) {
        if (rawValue == null) {
            return null;
        }
        return getEncryptor().encryptBase64(rawValue);
    }

    private static AES getEncryptor() {
        if (aes != null) {
            return aes;
        }
        synchronized (EncryptConverter.class) {
            if (aes != null) {
                return aes;
            }
            // 构建 AES：优先读取新配置项，其次兼容原配置项
            String password = SpringUtil.getProperty(ENCRYPTOR_PROPERTY_NAME);
            if (password == null || password.isEmpty()) {
                password = SpringUtil.getProperty(LEGACY_ENCRYPTOR_PROPERTY_NAME);
            }
            Assert.notEmpty(password, "配置项({}) 不能为空", ENCRYPTOR_PROPERTY_NAME);
            aes = SecureUtil.aes(password.getBytes());
            return aes;
        }
    }

}
