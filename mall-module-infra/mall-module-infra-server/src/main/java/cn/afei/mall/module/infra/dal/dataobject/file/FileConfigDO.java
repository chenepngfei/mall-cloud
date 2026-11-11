package cn.afei.mall.module.infra.dal.dataobject.file;

import cn.afei.mall.framework.common.util.json.JsonUtils;
import cn.afei.mall.framework.jpa.core.dataobject.BaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import cn.afei.mall.framework.tenant.core.aop.TenantIgnore;
import cn.afei.mall.module.infra.framework.file.core.client.FileClientConfig;
import cn.afei.mall.module.infra.framework.file.core.client.FileClientConfigConverter;
import cn.afei.mall.module.infra.framework.file.core.enums.FileStorageEnum;
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


}
