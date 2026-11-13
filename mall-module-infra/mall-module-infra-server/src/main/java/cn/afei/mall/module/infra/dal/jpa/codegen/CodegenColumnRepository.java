package cn.afei.mall.module.infra.dal.jpa.codegen;

import cn.afei.mall.framework.mybatis.core.repository.BaseRepositoryX;
import cn.afei.mall.framework.mybatis.core.repository.JpaSpecUtil;
import cn.afei.mall.framework.tenant.core.db.TenantSpec;
import cn.afei.mall.module.infra.dal.dataobject.codegen.CodegenColumnDO;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

/**
 * 代码生成字段 Repository（自原 CodegenColumnMapper 迁移；@TenantIgnore 全局表，tenantSpec 自动跳过）
 *
 * @author 芋道源码
 */
public interface CodegenColumnRepository extends BaseRepositoryX<CodegenColumnDO, Long> {

    default List<CodegenColumnDO> selectListByTableId(Long tableId) {
        return selectList(Specification.<CodegenColumnDO>where(JpaSpecUtil.eq("tableId", tableId))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()),
                Sort.by(Sort.Direction.ASC, "ordinalPosition"));
    }

    /**
     * 逻辑删除：按表编号
     */
    @Modifying
    @Query("UPDATE CodegenColumnDO cc SET cc.deleted = true WHERE cc.deleted = false AND cc.tableId = :tableId")
    int deleteListByTableId(@Param("tableId") Long tableId);

    /**
     * 逻辑删除：按表编号集合
     */
    @Modifying
    @Query("UPDATE CodegenColumnDO cc SET cc.deleted = true WHERE cc.deleted = false AND cc.tableId IN :tableIds")
    int deleteListByTableId(@Param("tableIds") Collection<Long> tableIds);

}
