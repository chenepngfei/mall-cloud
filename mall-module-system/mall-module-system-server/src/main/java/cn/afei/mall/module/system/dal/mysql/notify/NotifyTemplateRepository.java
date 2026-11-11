package cn.afei.mall.module.system.dal.mysql.notify;

import cn.afei.mall.framework.common.pojo.PageResult;
import cn.afei.mall.framework.jpa.core.query.LambdaSpecifications;
import cn.afei.mall.framework.jpa.core.repository.BaseRepositoryX;
import cn.afei.mall.module.system.controller.admin.notify.vo.template.NotifyTemplatePageReqVO;
import cn.afei.mall.module.system.dal.dataobject.notify.NotifyTemplateDO;

import java.util.List;

public interface NotifyTemplateRepository extends BaseRepositoryX<NotifyTemplateDO, Long> {

    default NotifyTemplateDO selectByCode(String code) {
        return selectOne(NotifyTemplateDO::getCode, code);
    }

    default PageResult<NotifyTemplateDO> selectPage(NotifyTemplatePageReqVO reqVO) {
        return selectPage(reqVO, LambdaSpecifications.<NotifyTemplateDO>of()
                .likeIfPresent(NotifyTemplateDO::getCode, reqVO.getCode())
                .likeIfPresent(NotifyTemplateDO::getName, reqVO.getName())
                .eqIfPresent(NotifyTemplateDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(NotifyTemplateDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(NotifyTemplateDO::getId)
                .build());
    }

    default List<NotifyTemplateDO> selectListByStatus(Integer status) {
        return selectList(NotifyTemplateDO::getStatus, status);
    }

}
