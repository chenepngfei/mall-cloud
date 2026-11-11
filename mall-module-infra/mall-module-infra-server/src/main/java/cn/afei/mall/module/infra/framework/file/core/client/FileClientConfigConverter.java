package cn.afei.mall.module.infra.framework.file.core.client;

import cn.hutool.core.util.StrUtil;
import cn.afei.mall.framework.common.util.json.JsonUtils;
import cn.afei.mall.module.infra.framework.file.core.client.db.DBFileClientConfig;
import cn.afei.mall.module.infra.framework.file.core.client.ftp.FtpFileClientConfig;
import cn.afei.mall.module.infra.framework.file.core.client.local.LocalFileClientConfig;
import cn.afei.mall.module.infra.framework.file.core.client.s3.S3FileClientConfig;
import cn.afei.mall.module.infra.framework.file.core.client.sftp.SftpFileClientConfig;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * {@link FileClientConfig} 的 AttributeConverter，对应原 MyBatis 的 FileClientConfigTypeHandler
 *
 * @author 芋道源码
 */
@Converter
public class FileClientConfigConverter implements AttributeConverter<FileClientConfig, String> {

    @Override
    public String convertToDatabaseColumn(FileClientConfig attribute) {
        return JsonUtils.toJsonString(attribute);
    }

    @Override
    public FileClientConfig convertToEntityAttribute(String json) {
        FileClientConfig config = JsonUtils.parseObjectQuietly(json, new TypeReference<>() {
        });
        if (config != null) {
            return config;
        }
        // 兼容老版本的包路径
        String className = JsonUtils.parseObject(json, "@class", String.class);
        className = StrUtil.subAfter(className, ".", true);
        switch (className) {
            case "DBFileClientConfig":
                return JsonUtils.parseObject2(json, DBFileClientConfig.class);
            case "FtpFileClientConfig":
                return JsonUtils.parseObject2(json, FtpFileClientConfig.class);
            case "LocalFileClientConfig":
                return JsonUtils.parseObject2(json, LocalFileClientConfig.class);
            case "SftpFileClientConfig":
                return JsonUtils.parseObject2(json, SftpFileClientConfig.class);
            case "S3FileClientConfig":
                return JsonUtils.parseObject2(json, S3FileClientConfig.class);
            default:
                throw new IllegalArgumentException("未知的 FileClientConfig 类型：" + json);
        }
    }

}
