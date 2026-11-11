package cn.afei.mall.module.infra.dal.mysql.demo.demo01;

import cn.afei.mall.framework.common.pojo.PageResult;
import cn.afei.mall.framework.jpa.core.repository.BaseRepositoryX;
import cn.afei.mall.framework.jpa.core.query.LambdaSpecifications;
import cn.afei.mall.module.infra.controller.admin.demo.demo01.vo.Demo01ContactPageReqVO;
import cn.afei.mall.module.infra.dal.dataobject.demo.demo01.Demo01ContactDO;

/**
 * 示例联系人 Mapper
 *
 * @author 芋道源码
 */
public interface Demo01ContactRepository extends BaseRepositoryX<Demo01ContactDO, Long> {

    default PageResult<Demo01ContactDO> selectPage(Demo01ContactPageReqVO reqVO) {
        return selectPage(reqVO, LambdaSpecifications.<Demo01ContactDO>of()
                .likeIfPresent(Demo01ContactDO::getName, reqVO.getName())
                .eqIfPresent(Demo01ContactDO::getSex, reqVO.getSex())
                .betweenIfPresent(Demo01ContactDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(Demo01ContactDO::getId).build());
    }

}