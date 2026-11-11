package cn.afei.mall.module.system.dal.mysql.notice;

import cn.afei.mall.framework.common.pojo.PageResult;
import cn.afei.mall.framework.jpa.core.query.LambdaSpecifications;
import cn.afei.mall.framework.jpa.core.repository.BaseRepositoryX;
import cn.afei.mall.module.system.controller.admin.notice.vo.NoticePageReqVO;
import cn.afei.mall.module.system.dal.dataobject.notice.NoticeDO;

public interface NoticeRepository extends BaseRepositoryX<NoticeDO, Long> {

    default PageResult<NoticeDO> selectPage(NoticePageReqVO reqVO) {
        return selectPage(reqVO, LambdaSpecifications.<NoticeDO>of()
                .likeIfPresent(NoticeDO::getTitle, reqVO.getTitle())
                .eqIfPresent(NoticeDO::getStatus, reqVO.getStatus())
                .orderByDesc(NoticeDO::getId)
                .build());
    }

}
