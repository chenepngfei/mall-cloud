package cn.afei.mall.module.infra.dal.dataobject.db;

import cn.afei.mall.framework.mybatis.core.dataobject.BaseDO;
import cn.afei.mall.framework.tenant.core.aop.TenantIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Convert;
import cn.afei.mall.framework.mybatis.core.type.convert.EncryptStringConverter;
import lombok.Data;

/**
 * 数据源配置
 *
 * @author 芋道源码
 */
@Entity
@Table(name = "infra_data_source_config")
@Data
@TenantIgnore
public class DataSourceConfigDO extends BaseDO {

    /**
     * 主键编号 - Master 数据源
     */
    public static final Long ID_MASTER = 0L;

    /**
     * 主键编号
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 连接名
     */
    private String name;

    /**
     * 数据源连接
     */
    private String url;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    @Convert(converter = EncryptStringConverter.class)
    private String password;

}
