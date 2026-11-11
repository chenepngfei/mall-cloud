package cn.afei.mall.module.system.dal.mysql.permission;

import cn.afei.mall.framework.jpa.core.query.LambdaSpecifications;
import cn.afei.mall.framework.jpa.core.repository.BaseRepositoryX;
import cn.afei.mall.module.system.dal.dataobject.permission.RoleMenuDO;

import java.util.Collection;
import java.util.List;

public interface RoleMenuRepository extends BaseRepositoryX<RoleMenuDO, Long> {

    default List<RoleMenuDO> selectListByRoleId(Long roleId) {
        return selectList(RoleMenuDO::getRoleId, roleId);
    }

    default List<RoleMenuDO> selectListByRoleId(Collection<Long> roleIds) {
        return selectList(RoleMenuDO::getRoleId, roleIds);
    }

    default List<RoleMenuDO> selectListByMenuId(Long menuId) {
        return selectList(RoleMenuDO::getMenuId, menuId);
    }

    default void deleteListByRoleIdAndMenuIds(Long roleId, Collection<Long> menuIds) {
        delete(LambdaSpecifications.<RoleMenuDO>of()
                .eq(RoleMenuDO::getRoleId, roleId)
                .in(RoleMenuDO::getMenuId, menuIds)
                .build());
    }

    default void deleteListByMenuId(Long menuId) {
        delete(RoleMenuDO::getMenuId, menuId);
    }

    default void deleteListByRoleId(Long roleId) {
        delete(RoleMenuDO::getRoleId, roleId);
    }

}
