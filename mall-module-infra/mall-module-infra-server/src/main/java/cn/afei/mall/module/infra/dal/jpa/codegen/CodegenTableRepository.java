package cn.afei.mall.module.infra.dal.jpa.codegen;

import cn.afei.mall.framework.common.pojo.PageResult;
import cn.afei.mall.framework.mybatis.core.repository.BaseRepositoryX;
import cn.afei.mall.framework.mybatis.core.repository.JpaSpecUtil;
import cn.afei.mall.framework.tenant.core.db.TenantSpec;
import cn.afei.mall.module.infra.controller.admin.codegen.vo.table.CodegenTablePageReqVO;
import cn.afei.mall.module.infra.dal.dataobject.codegen.CodegenTableDO;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

/**
 * 代码生成表 Repository（自原 CodegenTableMapper 迁移；@TenantIgnore 全局表，tenantSpec 自动跳过）
 *
 * @author 芋道源码
 */
public interface CodegenTableRepository extends BaseRepositoryX<CodegenTableDO, Long> {

    default CodegenTableDO selectByTableNameAndDataSourceConfigId(String tableName, Long dataSourceConfigId) {
        return selectOne(Specification.<CodegenTableDO>where(JpaSpecUtil.eq("tableName", tableName))
                .and(JpaSpecUtil.eq("dataSourceConfigId", dataSourceConfigId))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

    default PageResult<CodegenTableDO> selectPage(CodegenTablePageReqVO pageReqVO) {
        return selectPage(pageReqVO, Specification.<CodegenTableDO>where(JpaSpecUtil.likeIfPresent("tableName", pageReqVO.getTableName()))
                .and(JpaSpecUtil.likeIfPresent("tableComment", pageReqVO.getTableComment()))
                .and(JpaSpecUtil.likeIfPresent("className", pageReqVO.getClassName()))
                .and(JpaSpecUtil.betweenIfPresent("createTime", pageReqVO.getCreateTime()))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()),
                Sort.by(Sort.Direction.DESC, "updateTime"));
    }

    default List<CodegenTableDO> selectListByDataSourceConfigId(Long dataSourceConfigId) {
        return selectList(Specification.<CodegenTableDO>where(JpaSpecUtil.eq("dataSourceConfigId", dataSourceConfigId))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

    default List<CodegenTableDO> selectListByTemplateTypeAndMasterTableId(Integer templateType, Long masterTableId) {
        return selectList(Specification.<CodegenTableDO>where(JpaSpecUtil.eq("templateType", templateType))
                .and(JpaSpecUtil.eq("masterTableId", masterTableId))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

}
