package cn.afei.mall.module.system.dal.jpa.oauth2;

import cn.afei.mall.framework.mybatis.core.repository.BaseRepositoryX;
import cn.afei.mall.framework.mybatis.core.repository.JpaSpecUtil;
import cn.afei.mall.framework.tenant.core.db.TenantSpec;
import cn.afei.mall.module.system.dal.dataobject.oauth2.OAuth2ApproveDO;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

/**
 * OAuth2 批准 Repository（自原 OAuth2ApproveMapper 迁移）
 * <p>
 * 原 update(entity, wrapper) 按 userId+userType+clientId+scope 更新非空字段，
 * 迁移为 Service 层 select-then-save 模式（查询条件由下方方法提供），行为等价
 *
 * @author 芋道源码
 */
public interface OAuth2ApproveRepository extends BaseRepositoryX<OAuth2ApproveDO, Long> {

    default OAuth2ApproveDO selectByUserIdAndUserTypeAndClientIdAndScope(Long userId, Integer userType, String clientId, String scope) {
        return selectOne(Specification.<OAuth2ApproveDO>where(JpaSpecUtil.eq("userId", userId))
                .and(JpaSpecUtil.eq("userType", userType))
                .and(JpaSpecUtil.eq("clientId", clientId))
                .and(JpaSpecUtil.eq("scope", scope))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

    default List<OAuth2ApproveDO> selectListByUserIdAndUserTypeAndClientId(Long userId, Integer userType, String clientId) {
        return selectList(Specification.<OAuth2ApproveDO>where(JpaSpecUtil.eq("userId", userId))
                .and(JpaSpecUtil.eq("userType", userType))
                .and(JpaSpecUtil.eq("clientId", clientId))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

}
