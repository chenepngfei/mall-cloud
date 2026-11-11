package cn.afei.mall.framework.jpa.core.util;

import cn.hutool.core.util.StrUtil;
import cn.afei.mall.framework.common.util.object.ObjectUtils;
import cn.afei.mall.framework.jpa.core.enums.DbType;

import java.util.Locale;

/**
 * JDBC 工具类（JPA 版）：基于 URL 识别数据库类型，供代码生成器等使用
 *
 * @author 芋道源码
 */
public class JdbcUtils {

    /**
     * 判断连接是否正确
     *
     * @param url      数据源连接
     * @param username 账号
     * @param password 密码
     * @return 是否正确
     */
    public static boolean isConnectionOK(String url, String username, String password) {
        try (java.sql.Connection ignored = java.sql.DriverManager.getConnection(url, username, password)) {
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * 获得 URL 对应的 DB 类型
     *
     * 特殊：以下原生驱动连接的数据库兼容 PostgreSQL，按 PostgreSQL 处理：
     * 1. 瀚高 HighGo（jdbc:highgo:）
     * 2. openGauss、MogDB（jdbc:opengauss:）
     * 3. 华为 GaussDB（jdbc:gaussdb:）
     *
     * @param url URL
     * @return DB 类型；无法识别时返回 null
     */
    public static DbType getDbType(String url) {
        if (StrUtil.isBlank(url)) {
            return null;
        }
        String lowerUrl = url.toLowerCase(Locale.ROOT);
        if (lowerUrl.contains(":mysql:") || lowerUrl.contains(":cobar:")) {
            return DbType.MYSQL;
        }
        if (lowerUrl.contains(":mariadb:")) {
            return DbType.MARIADB;
        }
        if (lowerUrl.contains(":oracle:") || lowerUrl.contains(":oci:")) {
            return DbType.ORACLE;
        }
        if (lowerUrl.contains(":postgresql:")) {
            return DbType.POSTGRE_SQL;
        }
        if (lowerUrl.contains(":sqlserver:") || lowerUrl.contains(":microsoft:")) {
            return DbType.SQL_SERVER;
        }
        if (lowerUrl.contains(":sqlserver2005:")) {
            return DbType.SQL_SERVER2005;
        }
        if (lowerUrl.contains(":dm:")) {
            return DbType.DM;
        }
        if (lowerUrl.contains(":kingbase:")) {
            return DbType.KINGBASE_ES;
        }
        if (lowerUrl.contains(":oceanbase:")) {
            return DbType.OCEAN_BASE;
        }
        if (lowerUrl.contains(":h2:")) {
            return DbType.H2;
        }
        if (lowerUrl.contains(":oscar:")) {
            return DbType.OSCAR;
        }
        if (lowerUrl.contains(":highgo:") || lowerUrl.contains(":opengauss:") || lowerUrl.contains(":gaussdb:")) {
            return DbType.POSTGRE_SQL; // 兼容 PostgreSQL
        }
        return null;
    }

    /**
     * 判断 JDBC 连接是否为 SQLServer 数据库
     *
     * @param url JDBC 连接
     * @return 是否为 SQLServer 数据库
     */
    public static boolean isSQLServer(String url) {
        return isSQLServer(getDbType(url));
    }

    /**
     * 判断 JDBC 连接是否为 SQLServer 数据库
     *
     * @param dbType DB 类型
     * @return 是否为 SQLServer 数据库
     */
    public static boolean isSQLServer(DbType dbType) {
        return ObjectUtils.equalsAny(dbType, DbType.SQL_SERVER, DbType.SQL_SERVER2005);
    }

}
