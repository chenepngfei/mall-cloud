package cn.afei.mall.module.infra.dal.dataobject.file;

import cn.hutool.core.util.StrUtil;
import cn.afei.mall.framework.common.util.json.JsonUtils;
import cn.afei.mall.framework.mybatis.core.dataobject.BaseDO;
import cn.afei.mall.framework.tenant.core.aop.TenantIgnore;
import cn.afei.mall.module.infra.framework.file.core.client.FileClientConfig;
import cn.afei.mall.module.infra.framework.file.core.client.db.DBFileClientConfig;
import cn.afei.mall.module.infra.framework.file.core.client.ftp.FtpFileClientConfig;
import cn.afei.mall.module.infra.framework.file.core.client.local.LocalFileClientConfig;
import cn.afei.mall.module.infra.framework.file.core.client.s3.S3FileClientConfig;
import cn.afei.mall.module.infra.framework.file.core.client.sftp.SftpFileClientConfig;
import cn.afei.mall.module.infra.framework.file.core.enums.FileStorageEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Convert;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.*;


/**
 * 文件配置表
 *
 * @author 芋道源码
 */
@Entity
@Table(name = "infra_file_config")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TenantIgnore
public class FileConfigDO extends BaseDO {

    /**
     * 配置编号，数据库自增
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 配置名
     */
    private String name;
    /**
     * 存储器
     *
     * 枚举 {@link FileStorageEnum}
     */
    private Integer storage;
    /**
     * 备注
     */
    private String remark;
    /**
     * 是否为主配置
     *
     * 由于我们可以配置多个文件配置，默认情况下，使用主配置进行文件的上传
     */
    private Boolean master;

    /**
     * 支付渠道配置
     */
    @Convert(converter = FileClientConfigConverter.class)
    private FileClientConfig config;

    /**
     * FileClientConfig 字段转换器 — JSON 文本存 varchar 列（平移原 MP TypeHandler，含老版本包路径兼容）
     */
    public static class FileClientConfigConverter implements AttributeConverter<FileClientConfig, String> {

        @Override
        public String convertToDatabaseColumn(FileClientConfig attribute) {
            return JsonUtils.toJsonString(attribute);
        }

        @Override
        public FileClientConfig convertToEntityAttribute(String json) {
            FileClientConfig config = JsonUtils.parseObjectQuietly(json, new TypeReference<>() {});
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

}
