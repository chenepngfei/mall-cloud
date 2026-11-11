package cn.afei.mall.module.system.dal.mysql.sms;

import cn.afei.mall.framework.common.pojo.PageResult;
import cn.afei.mall.framework.jpa.core.query.LambdaSpecifications;
import cn.afei.mall.framework.jpa.core.repository.BaseRepositoryX;
import cn.afei.mall.module.system.controller.admin.sms.vo.channel.SmsChannelPageReqVO;
import cn.afei.mall.module.system.dal.dataobject.sms.SmsChannelDO;

public interface SmsChannelRepository extends BaseRepositoryX<SmsChannelDO, Long> {

    default PageResult<SmsChannelDO> selectPage(SmsChannelPageReqVO reqVO) {
        return selectPage(reqVO, LambdaSpecifications.<SmsChannelDO>of()
                .likeIfPresent(SmsChannelDO::getSignature, reqVO.getSignature())
                .eqIfPresent(SmsChannelDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(SmsChannelDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(SmsChannelDO::getId)
                .build());
    }

    default SmsChannelDO selectByCode(String code) {
        return selectOne(SmsChannelDO::getCode, code);
    }

}
