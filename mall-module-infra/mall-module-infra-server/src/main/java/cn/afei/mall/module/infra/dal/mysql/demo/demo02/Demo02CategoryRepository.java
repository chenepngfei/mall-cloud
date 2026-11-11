package cn.afei.mall.module.infra.dal.mysql.demo.demo02;

import cn.afei.mall.framework.jpa.core.repository.BaseRepositoryX;
import cn.afei.mall.framework.jpa.core.query.LambdaSpecifications;
import cn.afei.mall.module.infra.controller.admin.demo.demo02.vo.Demo02CategoryListReqVO;
import cn.afei.mall.module.infra.dal.dataobject.demo.demo02.Demo02CategoryDO;

import java.util.List;

/**
 * 示例分类 Mapper
 *
 * @author 芋道源码
 */
public interface Demo02CategoryRepository extends BaseRepositoryX<Demo02CategoryDO, Long> {

    default List<Demo02CategoryDO> selectList(Demo02CategoryListReqVO reqVO) {
        return selectList(LambdaSpecifications.<Demo02CategoryDO>of()
                .likeIfPresent(Demo02CategoryDO::getName, reqVO.getName())
                .eqIfPresent(Demo02CategoryDO::getParentId, reqVO.getParentId())
                .betweenIfPresent(Demo02CategoryDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(Demo02CategoryDO::getId).build());
    }

    default Demo02CategoryDO selectByParentIdAndName(Long parentId, String name) {
        return selectOne(Demo02CategoryDO::getParentId, parentId, Demo02CategoryDO::getName, name);
    }

    default Long selectCountByParentId(Long parentId) {
        return selectCount(Demo02CategoryDO::getParentId, parentId);
    }

}