package cn.afei.mall.module.system.dal.mysql.notify;

import cn.afei.mall.framework.common.pojo.PageResult;
import cn.afei.mall.framework.jpa.core.query.LambdaSpecifications;
import cn.afei.mall.framework.jpa.core.repository.BaseRepositoryX;
import cn.afei.mall.module.system.controller.admin.notify.vo.message.NotifyMessageMyPageReqVO;
import cn.afei.mall.module.system.controller.admin.notify.vo.message.NotifyMessagePageReqVO;
import cn.afei.mall.module.system.dal.dataobject.notify.NotifyMessageDO;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface NotifyMessageRepository extends BaseRepositoryX<NotifyMessageDO, Long> {

    default PageResult<NotifyMessageDO> selectPage(NotifyMessagePageReqVO reqVO) {
        return selectPage(reqVO, LambdaSpecifications.<NotifyMessageDO>of()
                .eqIfPresent(NotifyMessageDO::getUserId, reqVO.getUserId())
                .eqIfPresent(NotifyMessageDO::getUserType, reqVO.getUserType())
                .likeIfPresent(NotifyMessageDO::getTemplateCode, reqVO.getTemplateCode())
                .eqIfPresent(NotifyMessageDO::getTemplateType, reqVO.getTemplateType())
                .betweenIfPresent(NotifyMessageDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(NotifyMessageDO::getId)
                .build());
    }

    default PageResult<NotifyMessageDO> selectPage(NotifyMessageMyPageReqVO reqVO, Long userId, Integer userType) {
        return selectPage(reqVO, LambdaSpecifications.<NotifyMessageDO>of()
                .eqIfPresent(NotifyMessageDO::getReadStatus, reqVO.getReadStatus())
                .betweenIfPresent(NotifyMessageDO::getCreateTime, reqVO.getCreateTime())
                .eq(NotifyMessageDO::getUserId, userId)
                .eq(NotifyMessageDO::getUserType, userType)
                .orderByDesc(NotifyMessageDO::getId)
                .build());
    }

    default int updateListRead(Collection<Long> ids, Long userId, Integer userType) {
        List<NotifyMessageDO> list = selectList(LambdaSpecifications.<NotifyMessageDO>of()
                .in(NotifyMessageDO::getId, ids)
                .eq(NotifyMessageDO::getUserId, userId)
                .eq(NotifyMessageDO::getUserType, userType)
                .eq(NotifyMessageDO::getReadStatus, false)
                .build());
        list.forEach(message -> message.setReadStatus(true).setReadTime(LocalDateTime.now()));
        saveAll(list);
        return list.size();
    }

    default int updateListRead(Long userId, Integer userType) {
        List<NotifyMessageDO> list = selectList(LambdaSpecifications.<NotifyMessageDO>of()
                .eq(NotifyMessageDO::getUserId, userId)
                .eq(NotifyMessageDO::getUserType, userType)
                .eq(NotifyMessageDO::getReadStatus, false)
                .build());
        list.forEach(message -> message.setReadStatus(true).setReadTime(LocalDateTime.now()));
        saveAll(list);
        return list.size();
    }

    default List<NotifyMessageDO> selectUnreadListByUserIdAndUserType(Long userId, Integer userType, Integer size) {
        return selectListLimitN(LambdaSpecifications.<NotifyMessageDO>of()
                .eq(NotifyMessageDO::getUserId, userId)
                .eq(NotifyMessageDO::getUserType, userType)
                .eq(NotifyMessageDO::getReadStatus, false)
                .orderByDesc(NotifyMessageDO::getId)
                .build(), size);
    }

    default Long selectUnreadCountByUserIdAndUserType(Long userId, Integer userType) {
        return selectCount(LambdaSpecifications.<NotifyMessageDO>of()
                .eq(NotifyMessageDO::getReadStatus, false)
                .eq(NotifyMessageDO::getUserId, userId)
                .eq(NotifyMessageDO::getUserType, userType)
                .build());
    }

}
