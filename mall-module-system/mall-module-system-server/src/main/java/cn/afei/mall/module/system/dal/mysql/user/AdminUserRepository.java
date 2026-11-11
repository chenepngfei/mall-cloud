package cn.afei.mall.module.system.dal.mysql.user;

import cn.afei.mall.framework.common.pojo.PageResult;
import cn.afei.mall.framework.jpa.core.query.LambdaSpecifications;
import cn.afei.mall.framework.jpa.core.repository.BaseRepositoryX;
import cn.afei.mall.module.system.controller.admin.user.vo.user.UserPageReqVO;
import cn.afei.mall.module.system.dal.dataobject.user.AdminUserDO;

import java.util.Collection;
import java.util.List;

public interface AdminUserRepository extends BaseRepositoryX<AdminUserDO, Long> {

    default AdminUserDO selectByUsername(String username) {
        return selectOne(AdminUserDO::getUsername, username);
    }

    default AdminUserDO selectByEmail(String email) {
        return selectOne(AdminUserDO::getEmail, email);
    }

    default AdminUserDO selectByMobile(String mobile) {
        return selectOne(AdminUserDO::getMobile, mobile);
    }

    default PageResult<AdminUserDO> selectPage(UserPageReqVO reqVO, Collection<Long> deptIds, Collection<Long> userIds) {
        return selectPage(reqVO, LambdaSpecifications.<AdminUserDO>of()
                .likeIfPresent(AdminUserDO::getUsername, reqVO.getUsername())
                .likeIfPresent(AdminUserDO::getMobile, reqVO.getMobile())
                .eqIfPresent(AdminUserDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(AdminUserDO::getCreateTime, reqVO.getCreateTime())
                .inIfPresent(AdminUserDO::getDeptId, deptIds)
                .inIfPresent(AdminUserDO::getId, userIds)
                .orderByDesc(AdminUserDO::getId)
                .build());
    }

    default List<AdminUserDO> selectListByNickname(String nickname) {
        return selectList(LambdaSpecifications.<AdminUserDO>of().like(AdminUserDO::getNickname, nickname).build());
    }

    default List<AdminUserDO> selectListByStatus(Integer status) {
        return selectListByStatusAndDeptId(status, null);
    }

    default List<AdminUserDO> selectListByStatusAndDeptId(Integer status, Long deptId) {
        return selectList(LambdaSpecifications.<AdminUserDO>of()
                .eq(AdminUserDO::getStatus, status)
                .eqIfPresent(AdminUserDO::getDeptId, deptId)
                .build());
    }

    default List<AdminUserDO> selectListByDeptIds(Collection<Long> deptIds) {
        return selectList(AdminUserDO::getDeptId, deptIds);
    }

}
