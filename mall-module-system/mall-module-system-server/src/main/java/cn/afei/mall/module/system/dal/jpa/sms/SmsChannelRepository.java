package cn.afei.mall.module.system.dal.jpa.sms;

import cn.afei.mall.framework.common.pojo.PageResult;
import cn.afei.mall.framework.mybatis.core.repository.BaseRepositoryX;
import cn.afei.mall.framework.mybatis.core.repository.JpaSpecUtil;
import cn.afei.mall.framework.tenant.core.db.TenantSpec;
import cn.afei.mall.module.system.controller.admin.sms.vo.channel.SmsChannelPageReqVO;
import cn.afei.mall.module.system.dal.dataobject.sms.SmsChannelDO;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

/**
 * 短信渠道 Repository（自原 SmsChannelMapper 迁移；@TenantIgnore 全局表，tenantSpec 自动跳过）
 *
 * @author 芋道源码
 */
public interface SmsChannelRepository extends BaseRepositoryX<SmsChannelDO, Long> {

    default PageResult<SmsChannelDO> selectPage(SmsChannelPageReqVO reqVO) {
        return selectPage(reqVO, Specification.<SmsChannelDO>where(JpaSpecUtil.likeIfPresent("signature", reqVO.getSignature()))
                .and(JpaSpecUtil.eqIfPresent("status", reqVO.getStatus()))
                .and(JpaSpecUtil.betweenIfPresent("createTime", reqVO.getCreateTime()))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()),
                Sort.by(Sort.Direction.DESC, "id"));
    }

    default SmsChannelDO selectByCode(String code) {
        return selectOne(Specification.<SmsChannelDO>where(JpaSpecUtil.eq("code", code))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

}
