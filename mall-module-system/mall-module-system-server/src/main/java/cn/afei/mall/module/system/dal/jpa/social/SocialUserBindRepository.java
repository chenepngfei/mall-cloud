package cn.afei.mall.module.system.dal.jpa.social;

import cn.afei.mall.framework.mybatis.core.repository.BaseRepositoryX;
import cn.afei.mall.framework.mybatis.core.repository.JpaSpecUtil;
import cn.afei.mall.framework.tenant.core.context.TenantContextHolder;
import cn.afei.mall.framework.tenant.core.db.TenantSpec;
import cn.afei.mall.module.system.dal.dataobject.social.SocialUserBindDO;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 社交用户绑定 Repository（自原 SocialUserBindMapper 迁移；逻辑删除对齐原 MP @TableLogic 行为）
 *
 * @author 芋道源码
 */
public interface SocialUserBindRepository extends BaseRepositoryX<SocialUserBindDO, Long> {

    /**
     * 逻辑删除：按用户类型 + 用户 + 社交类型
     */
    @Modifying
    @Query("UPDATE SocialUserBindDO sb SET sb.deleted = true WHERE sb.deleted = false AND sb.userType = :userType AND sb.userId = :userId AND sb.socialType = :socialType AND sb.tenantId = :tenantId")
    int deleteByUserTypeAndUserIdAndSocialType(@Param("userType") Integer userType, @Param("userId") Long userId,
                                               @Param("socialType") Integer socialType, @Param("tenantId") Long tenantId);

    default void deleteByUserTypeAndUserIdAndSocialType(Integer userType, Long userId, Integer socialType) {
        deleteByUserTypeAndUserIdAndSocialType(userType, userId, socialType, TenantContextHolder.getRequiredTenantId());
    }

    /**
     * 逻辑删除：按用户类型 + 社交用户
     */
    @Modifying
    @Query("UPDATE SocialUserBindDO sb SET sb.deleted = true WHERE sb.deleted = false AND sb.userType = :userType AND sb.socialUserId = :socialUserId AND sb.tenantId = :tenantId")
    int deleteByUserTypeAndSocialUserId(@Param("userType") Integer userType, @Param("socialUserId") Long socialUserId, @Param("tenantId") Long tenantId);

    default void deleteByUserTypeAndSocialUserId(Integer userType, Long socialUserId) {
        deleteByUserTypeAndSocialUserId(userType, socialUserId, TenantContextHolder.getRequiredTenantId());
    }

    default SocialUserBindDO selectByUserTypeAndSocialUserId(Integer userType, Long socialUserId) {
        return selectOne(Specification.<SocialUserBindDO>where(JpaSpecUtil.eq("userType", userType))
                .and(JpaSpecUtil.eq("socialUserId", socialUserId))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

    default List<SocialUserBindDO> selectListByUserIdAndUserType(Long userId, Integer userType) {
        return selectList(Specification.<SocialUserBindDO>where(JpaSpecUtil.eq("userId", userId))
                .and(JpaSpecUtil.eq("userType", userType))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

    default SocialUserBindDO selectByUserIdAndUserTypeAndSocialType(Long userId, Integer userType, Integer socialType) {
        return selectOne(Specification.<SocialUserBindDO>where(JpaSpecUtil.eq("userId", userId))
                .and(JpaSpecUtil.eq("userType", userType))
                .and(JpaSpecUtil.eq("socialType", socialType))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

}
