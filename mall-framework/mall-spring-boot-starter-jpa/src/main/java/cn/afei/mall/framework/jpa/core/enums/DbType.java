package cn.afei.mall.framework.jpa.core.enums;

/**
 * 数据库类型枚举（JPA 版），枚举名与原 MyBatis Plus 的 DbType 保持一致，
 * 供代码生成器等按数据库类型生成差异化 SQL
 *
 * @author 芋道源码
 */
public enum DbType {

    H2,
    MYSQL,
    MARIADB,
    ORACLE,
    POSTGRE_SQL,
    SQL_SERVER,
    SQL_SERVER2005,
    DM,
    KINGBASE_ES,
    OCEAN_BASE,
    GAUSS_DB,
    OPENGAUSS,
    OSCAR

}
