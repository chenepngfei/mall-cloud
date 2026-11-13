package cn.afei.mall.module.system.dal.jpa.permission;

import cn.afei.mall.framework.mybatis.core.repository.BaseRepositoryX;
import cn.afei.mall.framework.mybatis.core.repository.JpaSpecUtil;
import cn.afei.mall.framework.tenant.core.context.TenantContextHolder;
import cn.afei.mall.framework.tenant.core.db.TenantSpec;
import cn.afei.mall.module.system.dal.dataobject.permission.UserRoleDO;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

/**
 * 用户角色关联 Repository（自原 UserRoleMapper 迁移；逻辑删除对齐原 MP @TableLogic 行为）
 *
 * @author 芋道源码
 */
public interface UserRoleRepository extends BaseRepositoryX<UserRoleDO, Long> {

    default List<UserRoleDO> selectListByUserId(Long userId) {
        return selectList(Specification.<UserRoleDO>where(JpaSpecUtil.eq("userId", userId))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

    default List<UserRoleDO> selectListByRoleIds(Collection<Long> roleIds) {
        return selectList(Specification.<UserRoleDO>where(JpaSpecUtil.in("roleId", roleIds))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

    /**
     * 逻辑删除：按用户 + 角色集合
     */
    @Modifying
    @Query("UPDATE UserRoleDO ur SET ur.deleted = true WHERE ur.deleted = false AND ur.userId = :userId AND ur.roleId IN :roleIds AND ur.tenantId = :tenantId")
    int deleteListByUserIdAndRoleIdIds(@Param("userId") Long userId, @Param("roleIds") Collection<Long> roleIds, @Param("tenantId") Long tenantId);

    default void deleteListByUserIdAndRoleIdIds(Long userId, Collection<Long> roleIds) {
        deleteListByUserIdAndRoleIdIds(userId, roleIds, TenantContextHolder.getRequiredTenantId());
    }

    /**
     * 逻辑删除：按用户
     */
    @Modifying
    @Query("UPDATE UserRoleDO ur SET ur.deleted = true WHERE ur.deleted = false AND ur.userId = :userId AND ur.tenantId = :tenantId")
    int deleteListByUserId(@Param("userId") Long userId, @Param("tenantId") Long tenantId);

    default void deleteListByUserId(Long userId) {
        deleteListByUserId(userId, TenantContextHolder.getRequiredTenantId());
    }

    /**
     * 逻辑删除：按角色
     */
    @Modifying
    @Query("UPDATE UserRoleDO ur SET ur.deleted = true WHERE ur.deleted = false AND ur.roleId = :roleId AND ur.tenantId = :tenantId")
    int deleteListByRoleId(@Param("roleId") Long roleId, @Param("tenantId") Long tenantId);

    default void deleteListByRoleId(Long roleId) {
        deleteListByRoleId(roleId, TenantContextHolder.getRequiredTenantId());
    }

}
