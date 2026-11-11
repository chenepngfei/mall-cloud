package cn.afei.mall.module.infra.service.db;

import lombok.Data;

import java.util.List;

/**
 * 数据库表定义 DTO：基于 JDBC DatabaseMetaData 解析，替代原 MyBatis Plus Generator 的 TableInfo
 *
 * @author 芋道源码
 */
@Data
public class DatabaseTableDef {

    /**
     * 表名称
     */
    private String name;
    /**
     * 表描述
     */
    private String comment;
    /**
     * 是否有主键
     */
    private boolean havePrimaryKey;
    /**
     * 字段列表
     */
    private List<DatabaseColumnDef> fields;

}
