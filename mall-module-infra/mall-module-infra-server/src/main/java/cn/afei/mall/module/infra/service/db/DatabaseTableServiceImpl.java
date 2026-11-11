package cn.afei.mall.module.infra.service.db;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.afei.mall.module.infra.dal.dataobject.db.DataSourceConfigDO;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.JDBCType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 数据库表 Service 实现类：基于 JDBC DatabaseMetaData 解析表结构，
 * 替代原 MyBatis Plus Generator 的表结构解析
 *
 * @author 芋道源码
 */
@Service
public class DatabaseTableServiceImpl implements DatabaseTableService {

    /**
     * 排除的系统表：工作流、定时任务、Oracle 系统表、含 $ 的表
     */
    private static final Pattern EXCLUDE_TABLE_PATTERN = Pattern.compile(
            "^(?i)(ACT_[\\S\\s]+|QRTZ_[\\S\\s]+|FLW_[\\S\\s]+|act_[\\S\\s]+|qrtz_[\\S\\s]+|flw_[\\S\\s]+"
                    + "|IMPDP_[\\S\\s]+|ALL_[\\S\\s]+|HS_[\\S\\s]+|impdp_[\\S\\s]+|all_[\\S\\s]+|hs_[\\S\\s]+"
                    + "|[\\S\\s]+\\$[\\S\\s]+|[\\S\\s]+\\$)$");

    @Resource
    private DataSourceConfigService dataSourceConfigService;

    @Override
    public List<DatabaseTableDef> getTableList(Long dataSourceConfigId, String nameLike, String commentLike) {
        List<DatabaseTableDef> tables = getTableList0(dataSourceConfigId, null);
        return tables.stream().filter(table -> (StrUtil.isEmpty(nameLike) || table.getName().contains(nameLike))
                        && (StrUtil.isEmpty(commentLike) || table.getComment().contains(commentLike)))
                .collect(Collectors.toList());
    }

    @Override
    public DatabaseTableDef getTable(Long dataSourceConfigId, String name) {
        return CollUtil.getFirst(getTableList0(dataSourceConfigId, name));
    }

    private List<DatabaseTableDef> getTableList0(Long dataSourceConfigId, String name) {
        // 获得数据源配置
        DataSourceConfigDO config = dataSourceConfigService.getDataSourceConfig(dataSourceConfigId);
        Assert.notNull(config, "数据源({}) 不存在！", dataSourceConfigId);

        // 通过 JDBC 连接解析表结构
        List<DatabaseTableDef> tables = new ArrayList<>();
        Set<String> primaryKeys = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(config.getUrl(), config.getUsername(), config.getPassword())) {
            DatabaseMetaData metaData = connection.getMetaData();
            String catalog = connection.getCatalog();
            String schema = connection.getSchema();
            // 1. 解析表
            try (ResultSet rs = metaData.getTables(catalog, schema, StrUtil.isNotEmpty(name) ? name : "%", new String[]{"TABLE"})) {
                while (rs.next()) {
                    String tableName = rs.getString("TABLE_NAME");
                    // 忽略视图、系统表：工作流、定时任务前缀的表名等
                    if (EXCLUDE_TABLE_PATTERN.matcher(tableName).matches()) {
                        continue;
                    }
                    DatabaseTableDef table = new DatabaseTableDef();
                    table.setName(tableName);
                    table.setComment(rs.getString("REMARKS"));
                    table.setFields(new ArrayList<>());
                    tables.add(table);
                }
            }
            // 2. 解析每张表的列与主键
            for (DatabaseTableDef table : tables) {
                primaryKeys.clear();
                try (ResultSet pkRs = metaData.getPrimaryKeys(catalog, schema, table.getName())) {
                    while (pkRs.next()) {
                        primaryKeys.add(pkRs.getString("COLUMN_NAME"));
                    }
                }
                table.setHavePrimaryKey(!primaryKeys.isEmpty());
                try (ResultSet rs = metaData.getColumns(catalog, schema, table.getName(), "%")) {
                    while (rs.next()) {
                        DatabaseColumnDef column = new DatabaseColumnDef();
                        column.setName(rs.getString("COLUMN_NAME"));
                        column.setComment(rs.getString("REMARKS"));
                        column.setNullable("YES".equalsIgnoreCase(rs.getString("IS_NULLABLE")));
                        int dataType = rs.getInt("DATA_TYPE");
                        column.setJdbcType(toJdbcTypeName(dataType));
                        column.setJavaType(toJavaTypeName(dataType));
                        column.setPropertyName(toPropertyName(column.getName()));
                        column.setKeyFlag(primaryKeys.contains(column.getName()));
                        table.getFields().add(column);
                    }
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("解析数据源(" + dataSourceConfigId + ")的表结构失败：" + e.getMessage(), e);
        }
        // 按名字排序
        tables.sort(Comparator.comparing(DatabaseTableDef::getName));
        return tables;
    }

    /**
     * JDBC 类型编码转 JDBC 类型名，对齐原 MyBatis Plus 的 JdbcType 枚举名
     */
    private static String toJdbcTypeName(int dataType) {
        try {
            return JDBCType.valueOf(dataType).getName();
        } catch (IllegalArgumentException e) {
            return String.valueOf(dataType);
        }
    }

    /**
     * JDBC 类型编码转 Java 类型短名，对齐原 MyBatis Plus Generator（DateType.TIME_PACK）的默认映射
     */
    private static String toJavaTypeName(int dataType) {
        switch (dataType) {
            case Types.BIT:
            case Types.BOOLEAN:
                return Boolean.class.getSimpleName();
            case Types.TINYINT:
            case Types.SMALLINT:
            case Types.INTEGER:
                return Integer.class.getSimpleName();
            case Types.BIGINT:
                return Long.class.getSimpleName();
            case Types.FLOAT:
            case Types.REAL:
            case Types.DOUBLE:
                return Double.class.getSimpleName();
            case Types.DECIMAL:
            case Types.NUMERIC:
                return BigDecimal.class.getSimpleName();
            case Types.CHAR:
            case Types.VARCHAR:
            case Types.LONGVARCHAR:
            case Types.NCHAR:
            case Types.NVARCHAR:
            case Types.LONGNVARCHAR:
            case Types.CLOB:
            case Types.NCLOB:
                return String.class.getSimpleName();
            case Types.DATE:
                return LocalDate.class.getSimpleName();
            case Types.TIME:
                return LocalTime.class.getSimpleName();
            case Types.TIMESTAMP:
            case Types.TIMESTAMP_WITH_TIMEZONE:
                return LocalDateTime.class.getSimpleName();
            case Types.BINARY:
            case Types.VARBINARY:
            case Types.LONGVARBINARY:
            case Types.BLOB:
                return "byte[]";
            default:
                return String.class.getSimpleName();
        }
    }

    /**
     * 列名转 Java 属性名：下划线转驼峰
     */
    private static String toPropertyName(String columnName) {
        StringBuilder result = new StringBuilder();
        boolean upperNext = false;
        for (char ch : columnName.toLowerCase(Locale.ROOT).toCharArray()) {
            if (ch == '_') {
                upperNext = true;
            } else if (upperNext) {
                result.append(Character.toUpperCase(ch));
                upperNext = false;
            } else {
                result.append(ch);
            }
        }
        return result.toString();
    }

}
