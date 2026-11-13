package cn.afei.mall.module.system.dal.jpa.dept;

import cn.afei.mall.framework.mybatis.core.repository.BaseRepositoryX;
import cn.afei.mall.framework.mybatis.core.repository.JpaSpecUtil;
import cn.afei.mall.framework.tenant.core.context.TenantContextHolder;
import cn.afei.mall.framework.tenant.core.db.TenantSpec;
import cn.afei.mall.module.system.dal.dataobject.dept.UserPostDO;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

/**
 * 用户岗位关联 Repository（自原 UserPostMapper 迁移）
 *
 * @author 芋道源码
 */
public interface UserPostRepository extends BaseRepositoryX<UserPostDO, Long> {

    default List<UserPostDO> selectListByUserId(Long userId) {
        return selectList(Specification.<UserPostDO>where(JpaSpecUtil.eq("userId", userId))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

    default List<UserPostDO> selectListByPostIds(Collection<Long> postIds) {
        return selectList(Specification.<UserPostDO>where(JpaSpecUtil.in("postId", postIds))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

    /**
     * 逻辑删除（原 MP delete 经 @TableLogic 转为 UPDATE deleted=1，行为一致）
     */
    @Modifying
    @Query("UPDATE UserPostDO up SET up.deleted = true WHERE up.deleted = false AND up.userId = :userId AND up.postId IN :postIds AND up.tenantId = :tenantId")
    int deleteByUserIdAndPostId(@Param("userId") Long userId, @Param("postIds") Collection<Long> postIds, @Param("tenantId") Long tenantId);

    default void deleteByUserIdAndPostId(Long userId, Collection<Long> postIds) {
        deleteByUserIdAndPostId(userId, postIds, TenantContextHolder.getRequiredTenantId());
    }

    /**
     * 逻辑删除（按用户）
     */
    @Modifying
    @Query("UPDATE UserPostDO up SET up.deleted = true WHERE up.deleted = false AND up.userId = :userId AND up.tenantId = :tenantId")
    int deleteByUserId(@Param("userId") Long userId, @Param("tenantId") Long tenantId);

    default void deleteByUserId(Long userId) {
        deleteByUserId(userId, TenantContextHolder.getRequiredTenantId());
    }

}
