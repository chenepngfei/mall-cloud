package cn.afei.mall.module.infra.dal.dataobject.config;

import cn.afei.mall.framework.jpa.core.dataobject.BaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import cn.afei.mall.framework.tenant.core.aop.TenantIgnore;
import cn.afei.mall.module.infra.enums.config.ConfigTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 参数配置表
 *
 * @author 芋道源码
 */
@Entity
@Table(name = "infra_config")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TenantIgnore
public class ConfigDO extends BaseDO {

    /**
     * 参数主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 参数分类
     */
    private String category;
    /**
     * 参数名称
     */
    private String name;
    /**
     * 参数键名
     *
     * 支持多 DB 类型时，无法直接使用 key + @Column(name = "config_key") 来实现转换，原因是 "config_key" AS key 而存在报错
     */
    private String configKey;
    /**
     * 参数键值
     */
    private String value;
    /**
     * 参数类型
     *
     * 枚举 {@link ConfigTypeEnum}
     */
    private Integer type;
    /**
     * 是否可见
     *
     * 不可见的参数，一般是敏感参数，前端不可获取
     */
    private Boolean visible;
    /**
     * 备注
     */
    private String remark;

}
