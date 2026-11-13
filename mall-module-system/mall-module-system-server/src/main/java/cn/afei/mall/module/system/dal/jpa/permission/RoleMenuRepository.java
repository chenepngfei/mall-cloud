package cn.afei.mall.module.system.dal.jpa.permission;

import cn.afei.mall.framework.mybatis.core.repository.BaseRepositoryX;
import cn.afei.mall.framework.mybatis.core.repository.JpaSpecUtil;
import cn.afei.mall.framework.tenant.core.context.TenantContextHolder;
import cn.afei.mall.framework.tenant.core.db.TenantSpec;
import cn.afei.mall.module.system.dal.dataobject.permission.RoleMenuDO;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

/**
 * 角色菜单关联 Repository（自原 RoleMenuMapper 迁移；逻辑删除对齐原 MP @TableLogic 行为）
 *
 * @author 芋道源码
 */
public interface RoleMenuRepository extends BaseRepositoryX<RoleMenuDO, Long> {

    default List<RoleMenuDO> selectListByRoleId(Long roleId) {
        return selectList(Specification.<RoleMenuDO>where(JpaSpecUtil.eq("roleId", roleId))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

    default List<RoleMenuDO> selectListByRoleId(Collection<Long> roleIds) {
        return selectList(Specification.<RoleMenuDO>where(JpaSpecUtil.in("roleId", roleIds))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

    default List<RoleMenuDO> selectListByMenuId(Long menuId) {
        return selectList(Specification.<RoleMenuDO>where(JpaSpecUtil.eq("menuId", menuId))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

    /**
     * 逻辑删除：按角色 + 菜单集合（原 MP delete → UPDATE deleted=1，含租户条件，行为一致）
     */
    @Modifying
    @Query("UPDATE RoleMenuDO rm SET rm.deleted = true WHERE rm.deleted = false AND rm.roleId = :roleId AND rm.menuId IN :menuIds AND rm.tenantId = :tenantId")
    int deleteListByRoleIdAndMenuIds(@Param("roleId") Long roleId, @Param("menuIds") Collection<Long> menuIds, @Param("tenantId") Long tenantId);

    default void deleteListByRoleIdAndMenuIds(Long roleId, Collection<Long> menuIds) {
        deleteListByRoleIdAndMenuIds(roleId, menuIds, TenantContextHolder.getRequiredTenantId());
    }

    /**
     * 逻辑删除：按菜单
     */
    @Modifying
    @Query("UPDATE RoleMenuDO rm SET rm.deleted = true WHERE rm.deleted = false AND rm.menuId = :menuId AND rm.tenantId = :tenantId")
    int deleteListByMenuId(@Param("menuId") Long menuId, @Param("tenantId") Long tenantId);

    default void deleteListByMenuId(Long menuId) {
        deleteListByMenuId(menuId, TenantContextHolder.getRequiredTenantId());
    }

    /**
     * 逻辑删除：按角色
     */
    @Modifying
    @Query("UPDATE RoleMenuDO rm SET rm.deleted = true WHERE rm.deleted = false AND rm.roleId = :roleId AND rm.tenantId = :tenantId")
    int deleteListByRoleId(@Param("roleId") Long roleId, @Param("tenantId") Long tenantId);

    default void deleteListByRoleId(Long roleId) {
        deleteListByRoleId(roleId, TenantContextHolder.getRequiredTenantId());
    }

}
