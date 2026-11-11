package cn.afei.mall.module.infra.service.db;

import lombok.Data;

/**
 * 数据库列定义 DTO：基于 JDBC DatabaseMetaData 解析，替代原 MyBatis Plus Generator 的 TableField
 *
 * @author 芋道源码
 */
@Data
public class DatabaseColumnDef {

    /**
     * 列名称
     */
    private String name;
    /**
     * 列描述
     */
    private String comment;
    /**
     * 是否为主键
     */
    private boolean keyFlag;
    /**
     * 是否允许空
     */
    private boolean nullable;
    /**
     * JDBC 类型名，如 VARCHAR、BIGINT，对应 {@link java.sql.JDBCType#getName()}
     */
    private String jdbcType;
    /**
     * Java 类型短名，如 Long、String、LocalDateTime
     */
    private String javaType;
    /**
     * Java 属性名（列名驼峰转换）
     */
    private String propertyName;

}
