package cn.afei.mall.module.infra.dal.dataobject.file;

import cn.afei.mall.framework.jpa.core.dataobject.BaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import cn.afei.mall.framework.tenant.core.aop.TenantIgnore;
import cn.afei.mall.module.infra.framework.file.core.client.db.DBFileClient;
import lombok.*;

/**
 * 文件内容表
 *
 * 专门用于存储 {@link DBFileClient} 的文件内容
 *
 * @author 芋道源码
 */
@Entity
@Table(name = "infra_file_content")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TenantIgnore
public class FileContentDO extends BaseDO {

    /**
     * 编号，数据库自增
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 配置编号
     *
     * 关联 {@link FileConfigDO#getId()}
     */
    private Long configId;
    /**
     * 路径，即文件名
     */
    private String path;
    /**
     * 文件内容
     */
    private byte[] content;

}
