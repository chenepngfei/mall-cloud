package cn.afei.mall.module.system.dal.mysql.dept;

import cn.afei.mall.framework.jpa.core.query.LambdaSpecifications;
import cn.afei.mall.framework.jpa.core.repository.BaseRepositoryX;
import cn.afei.mall.module.system.controller.admin.dept.vo.dept.DeptListReqVO;
import cn.afei.mall.module.system.dal.dataobject.dept.DeptDO;

import java.util.Collection;
import java.util.List;

public interface DeptRepository extends BaseRepositoryX<DeptDO, Long> {

    default List<DeptDO> selectList(DeptListReqVO reqVO) {
        return selectList(LambdaSpecifications.<DeptDO>of()
                .likeIfPresent(DeptDO::getName, reqVO.getName())
                .eqIfPresent(DeptDO::getStatus, reqVO.getStatus())
                .build());
    }

    default DeptDO selectByParentIdAndName(Long parentId, String name) {
        return selectOne(DeptDO::getParentId, parentId, DeptDO::getName, name);
    }

    default Long selectCountByParentId(Long parentId) {
        return selectCount(DeptDO::getParentId, parentId);
    }

    default List<DeptDO> selectListByParentId(Collection<Long> parentIds) {
        return selectList(DeptDO::getParentId, parentIds);
    }

    default List<DeptDO> selectListByLeaderUserId(Long id) {
        return selectList(DeptDO::getLeaderUserId, id);
    }

}
