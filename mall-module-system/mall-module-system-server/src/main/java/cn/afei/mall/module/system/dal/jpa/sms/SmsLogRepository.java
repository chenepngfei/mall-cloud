package cn.afei.mall.module.system.dal.jpa.sms;

import cn.afei.mall.framework.common.pojo.PageResult;
import cn.afei.mall.framework.mybatis.core.repository.BaseRepositoryX;
import cn.afei.mall.framework.mybatis.core.repository.JpaSpecUtil;
import cn.afei.mall.framework.tenant.core.db.TenantSpec;
import cn.afei.mall.module.system.controller.admin.sms.vo.log.SmsLogPageReqVO;
import cn.afei.mall.module.system.dal.dataobject.sms.SmsLogDO;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

/**
 * 短信日志 Repository（自原 SmsLogMapper 迁移；@TenantIgnore 全局表，tenantSpec 自动跳过）
 *
 * @author 芋道源码
 */
public interface SmsLogRepository extends BaseRepositoryX<SmsLogDO, Long> {

    default SmsLogDO selectByApiSerialNo(String apiSerialNo) {
        return selectOne(Specification.<SmsLogDO>where(JpaSpecUtil.eq("apiSerialNo", apiSerialNo))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

    default PageResult<SmsLogDO> selectPage(SmsLogPageReqVO reqVO) {
        return selectPage(reqVO, Specification.<SmsLogDO>where(JpaSpecUtil.eqIfPresent("channelId", reqVO.getChannelId()))
                .and(JpaSpecUtil.eqIfPresent("templateId", reqVO.getTemplateId()))
                .and(JpaSpecUtil.likeIfPresent("mobile", reqVO.getMobile()))
                .and(JpaSpecUtil.eqIfPresent("sendStatus", reqVO.getSendStatus()))
                .and(JpaSpecUtil.betweenIfPresent("sendTime", reqVO.getSendTime()))
                .and(JpaSpecUtil.eqIfPresent("receiveStatus", reqVO.getReceiveStatus()))
                .and(JpaSpecUtil.betweenIfPresent("receiveTime", reqVO.getReceiveTime()))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()),
                Sort.by(Sort.Direction.DESC, "id"));
    }

}
