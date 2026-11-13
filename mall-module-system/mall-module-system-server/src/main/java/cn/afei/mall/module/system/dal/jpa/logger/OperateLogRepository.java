package cn.afei.mall.module.system.dal.jpa.logger;

import cn.afei.mall.framework.common.pojo.PageResult;
import cn.afei.mall.framework.mybatis.core.repository.BaseRepositoryX;
import cn.afei.mall.framework.mybatis.core.repository.JpaSpecUtil;
import cn.afei.mall.framework.tenant.core.db.TenantSpec;
import cn.afei.mall.module.system.controller.admin.logger.vo.operatelog.OperateLogPageReqVO;
import cn.afei.mall.module.system.dal.dataobject.logger.OperateLogDO;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

/**
 * 操作日志 Repository（自原 OperateLogMapper 迁移；实体补齐 tenantId 映射，tenantSpec 生效）
 *
 * @author 芋道源码
 */
public interface OperateLogRepository extends BaseRepositoryX<OperateLogDO, Long> {

    default PageResult<OperateLogDO> selectPage(OperateLogPageReqVO pageReqDTO) {
        return selectPage(pageReqDTO, Specification.<OperateLogDO>where(JpaSpecUtil.eqIfPresent("userId", pageReqDTO.getUserId()))
                .and(JpaSpecUtil.eqIfPresent("bizId", pageReqDTO.getBizId()))
                .and(JpaSpecUtil.likeIfPresent("type", pageReqDTO.getType()))
                .and(JpaSpecUtil.likeIfPresent("subType", pageReqDTO.getSubType()))
                .and(JpaSpecUtil.likeIfPresent("action", pageReqDTO.getAction()))
                .and(JpaSpecUtil.betweenIfPresent("createTime", pageReqDTO.getCreateTime()))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()),
                Sort.by(Sort.Direction.DESC, "id"));
    }

    default PageResult<OperateLogDO> selectPage(cn.afei.mall.module.system.api.logger.dto.OperateLogPageReqDTO pageReqDTO) {
        return selectPage(pageReqDTO, Specification.<OperateLogDO>where(JpaSpecUtil.eqIfPresent("type", pageReqDTO.getType()))
                .and(JpaSpecUtil.eqIfPresent("bizId", pageReqDTO.getBizId()))
                .and(JpaSpecUtil.eqIfPresent("userId", pageReqDTO.getUserId()))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()),
                Sort.by(Sort.Direction.DESC, "id"));
    }

}
