package cn.afei.mall.module.infra.dal.mysql.demo.demo03.erp;

import cn.afei.mall.framework.common.pojo.PageResult;
import cn.afei.mall.framework.jpa.core.repository.BaseRepositoryX;
import cn.afei.mall.framework.jpa.core.query.LambdaSpecifications;
import cn.afei.mall.module.infra.controller.admin.demo.demo03.erp.vo.Demo03StudentErpPageReqVO;
import cn.afei.mall.module.infra.dal.dataobject.demo.demo03.Demo03StudentDO;

/**
 * 学生 Mapper
 *
 * @author 芋道源码
 */
public interface Demo03StudentErpRepository extends BaseRepositoryX<Demo03StudentDO, Long> {

    default PageResult<Demo03StudentDO> selectPage(Demo03StudentErpPageReqVO reqVO) {
        return selectPage(reqVO, LambdaSpecifications.<Demo03StudentDO>of()
                .likeIfPresent(Demo03StudentDO::getName, reqVO.getName())
                .eqIfPresent(Demo03StudentDO::getSex, reqVO.getSex())
                .eqIfPresent(Demo03StudentDO::getDescription, reqVO.getDescription())
                .betweenIfPresent(Demo03StudentDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(Demo03StudentDO::getId).build());
    }

}