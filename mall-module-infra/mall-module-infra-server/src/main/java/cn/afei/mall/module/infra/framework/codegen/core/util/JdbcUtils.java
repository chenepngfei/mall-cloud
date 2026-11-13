package cn.afei.mall.module.infra.framework.codegen.core.util;

import com.baomidou.mybatisplus.annotation.DbType;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * JDBC 工具类（JPA 迁移后的轻量版 — 供 codegen 动态数据源使用，随 infra 模块放
 * 置：依赖 mybatis-plus-generator 的 DbType，starter 层不可见）
 *
 * @author 芋道源码
 */
public class JdbcUtils {

    /**
     * 1. 判断 URL 是否 SQL Server 数据库（SQLServer jdbc 非标准，参见 https://github.com/baomidou/mybatis-plus/issues/5419）
     */
    public static boolean isSQLServer(String url) {
        return url != null && url.toLowerCase().startsWith("jdbc:sqlserver:");
    }

    /**
     * 2. 获得 URL 对应的数据库类型（前缀映射；瀚高/openGauss/GaussDB/MogDB 归一为 PostgreSQL，与原实现一致）
     */
    public static DbType getDbType(String url) {
        if (url == null) {
            return DbType.OTHER;
        }
        String lower = url.toLowerCase();
        if (lower.startsWith("jdbc:mysql:") || lower.startsWith("jdbc:mariadb:")) {
            return DbType.MYSQL;
        }
        if (lower.startsWith("jdbc:postgresql:") || lower.startsWith("jdbc:highgo:")
                || lower.startsWith("jdbc:opengauss:") || lower.startsWith("jdbc:gaussdb:")
                || lower.startsWith("jdbc:mogdb:")) {
            return DbType.POSTGRE_SQL;
        }
        if (lower.startsWith("jdbc:oracle:")) {
            return DbType.ORACLE;
        }
        if (isSQLServer(url)) {
            return DbType.SQL_SERVER;
        }
        if (lower.startsWith("jdbc:dm:")) {
            return DbType.DM;
        }
        if (lower.startsWith("jdbc:kingbase")) {
            return DbType.KINGBASE_ES;
        }
        if (lower.startsWith("jdbc:h2:")) {
            return DbType.H2;
        }
        return DbType.OTHER;
    }

    /**
     * 3. 测试指定数据源配置是否可连接
     */
    public static boolean isConnectionOK(String url, String username, String password) {
        try (Connection ignored = DriverManager.getConnection(url, username, password)) {
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

}
