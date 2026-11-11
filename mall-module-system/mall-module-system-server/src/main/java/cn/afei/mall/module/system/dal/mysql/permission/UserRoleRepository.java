package cn.afei.mall.module.system.dal.mysql.permission;

import cn.afei.mall.framework.jpa.core.query.LambdaSpecifications;
import cn.afei.mall.framework.jpa.core.repository.BaseRepositoryX;
import cn.afei.mall.module.system.dal.dataobject.permission.UserRoleDO;

import java.util.Collection;
import java.util.List;

public interface UserRoleRepository extends BaseRepositoryX<UserRoleDO, Long> {

    default List<UserRoleDO> selectListByUserId(Long userId) {
        return selectList(UserRoleDO::getUserId, userId);
    }

    default void deleteListByUserIdAndRoleIdIds(Long userId, Collection<Long> roleIds) {
        delete(LambdaSpecifications.<UserRoleDO>of()
                .eq(UserRoleDO::getUserId, userId)
                .in(UserRoleDO::getRoleId, roleIds)
                .build());
    }

    default void deleteListByUserId(Long userId) {
        delete(UserRoleDO::getUserId, userId);
    }

    default void deleteListByRoleId(Long roleId) {
        delete(UserRoleDO::getRoleId, roleId);
    }

    default List<UserRoleDO> selectListByRoleIds(Collection<Long> roleIds) {
        return selectList(UserRoleDO::getRoleId, roleIds);
    }

}
