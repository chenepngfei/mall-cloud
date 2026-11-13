package cn.afei.mall.module.system.dal.jpa.notify;

import cn.afei.mall.framework.common.pojo.PageResult;
import cn.afei.mall.framework.mybatis.core.repository.BaseRepositoryX;
import cn.afei.mall.framework.mybatis.core.repository.JpaSpecUtil;
import cn.afei.mall.framework.tenant.core.context.TenantContextHolder;
import cn.afei.mall.framework.tenant.core.db.TenantSpec;
import cn.afei.mall.module.system.controller.admin.notify.vo.message.NotifyMessagePageReqVO;
import cn.afei.mall.module.system.controller.admin.notify.vo.message.NotifyMessageMyPageReqVO;
import cn.afei.mall.module.system.dal.dataobject.notify.NotifyMessageDO;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * 站内信消息 Repository（自原 NotifyMessageMapper 迁移；实体补齐 tenantId 映射，tenantSpec 生效）
 *
 * @author 芋道源码
 */
public interface NotifyMessageRepository extends BaseRepositoryX<NotifyMessageDO, Long> {

    default PageResult<NotifyMessageDO> selectPage(NotifyMessagePageReqVO reqVO) {
        return selectPage(reqVO, Specification.<NotifyMessageDO>where(JpaSpecUtil.eqIfPresent("userId", reqVO.getUserId()))
                .and(JpaSpecUtil.eqIfPresent("userType", reqVO.getUserType()))
                .and(JpaSpecUtil.likeIfPresent("templateCode", reqVO.getTemplateCode()))
                .and(JpaSpecUtil.eqIfPresent("templateType", reqVO.getTemplateType()))
                .and(JpaSpecUtil.betweenIfPresent("createTime", reqVO.getCreateTime()))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()),
                Sort.by(Sort.Direction.DESC, "id"));
    }

    default PageResult<NotifyMessageDO> selectPage(NotifyMessageMyPageReqVO reqVO, Long userId, Integer userType) {
        return selectPage(reqVO, Specification.<NotifyMessageDO>where(JpaSpecUtil.eqIfPresent("readStatus", reqVO.getReadStatus()))
                .and(JpaSpecUtil.betweenIfPresent("createTime", reqVO.getCreateTime()))
                .and(JpaSpecUtil.eq("userId", userId))
                .and(JpaSpecUtil.eq("userType", userType))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()),
                Sort.by(Sort.Direction.DESC, "id"));
    }

    /**
     * 标记已读：按消息集合（原 MP update(entity, wrapper) → JPQL 批量更新，语义一致）
     */
    @Modifying
    @Query("UPDATE NotifyMessageDO nm SET nm.readStatus = true, nm.readTime = :readTime WHERE nm.deleted = false AND nm.id IN :ids AND nm.userId = :userId AND nm.userType = :userType AND nm.readStatus = false AND nm.tenantId = :tenantId")
    int updateListRead(@Param("ids") Collection<Long> ids, @Param("userId") Long userId, @Param("userType") Integer userType,
                       @Param("readTime") LocalDateTime readTime, @Param("tenantId") Long tenantId);

    default int updateListRead(Collection<Long> ids, Long userId, Integer userType) {
        return updateListRead(ids, userId, userType, LocalDateTime.now(), TenantContextHolder.getRequiredTenantId());
    }

    /**
     * 标记已读：全部未读
     */
    @Modifying
    @Query("UPDATE NotifyMessageDO nm SET nm.readStatus = true, nm.readTime = :readTime WHERE nm.deleted = false AND nm.userId = :userId AND nm.userType = :userType AND nm.readStatus = false AND nm.tenantId = :tenantId")
    int updateListRead(@Param("userId") Long userId, @Param("userType") Integer userType,
                       @Param("readTime") LocalDateTime readTime, @Param("tenantId") Long tenantId);

    default int updateListRead(Long userId, Integer userType) {
        return updateListRead(userId, userType, LocalDateTime.now(), TenantContextHolder.getRequiredTenantId());
    }

    default List<NotifyMessageDO> selectUnreadListByUserIdAndUserType(Long userId, Integer userType, Integer size) {
        return selectListLimitN(Specification.<NotifyMessageDO>where(JpaSpecUtil.eq("userId", userId))
                .and(JpaSpecUtil.eq("userType", userType))
                .and(JpaSpecUtil.eq("readStatus", false))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()),
                Sort.by(Sort.Direction.DESC, "id"), size);
    }

    default Long selectUnreadCountByUserIdAndUserType(Long userId, Integer userType) {
        return selectCount(Specification.<NotifyMessageDO>where(JpaSpecUtil.eq("readStatus", false))
                .and(JpaSpecUtil.eq("userId", userId))
                .and(JpaSpecUtil.eq("userType", userType))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

}
