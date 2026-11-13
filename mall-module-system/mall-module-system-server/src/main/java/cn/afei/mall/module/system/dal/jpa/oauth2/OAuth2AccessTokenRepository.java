package cn.afei.mall.module.system.dal.jpa.oauth2;

import cn.afei.mall.framework.common.pojo.PageResult;
import cn.afei.mall.framework.mybatis.core.repository.BaseRepositoryX;
import cn.afei.mall.framework.mybatis.core.repository.JpaSpecUtil;
import cn.afei.mall.framework.tenant.core.db.TenantSpec;
import cn.afei.mall.module.system.controller.admin.oauth2.vo.token.OAuth2AccessTokenPageReqVO;
import cn.afei.mall.module.system.dal.dataobject.oauth2.OAuth2AccessTokenDO;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

import java.time.LocalDateTime;
import java.util.List;

/**
 * OAuth2 访问令牌 Repository（自原 OAuth2AccessTokenMapper 迁移）
 *
 * @author 芋道源码
 */
public interface OAuth2AccessTokenRepository extends BaseRepositoryX<OAuth2AccessTokenDO, Long> {

    default OAuth2AccessTokenDO selectByAccessToken(String accessToken) {
        return selectOne(Specification.<OAuth2AccessTokenDO>where(JpaSpecUtil.eq("accessToken", accessToken))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

    default List<OAuth2AccessTokenDO> selectListByRefreshToken(String refreshToken) {
        return selectList(Specification.<OAuth2AccessTokenDO>where(JpaSpecUtil.eq("refreshToken", refreshToken))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

    default PageResult<OAuth2AccessTokenDO> selectPage(OAuth2AccessTokenPageReqVO reqVO) {
        return selectPage(reqVO, Specification.<OAuth2AccessTokenDO>where(JpaSpecUtil.eqIfPresent("userId", reqVO.getUserId()))
                .and(JpaSpecUtil.eqIfPresent("userType", reqVO.getUserType()))
                .and(JpaSpecUtil.likeIfPresent("clientId", reqVO.getClientId()))
                .and((root, query, cb) -> cb.greaterThan(root.<LocalDateTime>get("expiresTime"), LocalDateTime.now()))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()),
                Sort.by(Sort.Direction.DESC, "id"));
    }

    default List<OAuth2AccessTokenDO> selectListByUserIdAndUserType(Long userId, Integer userType) {
        return selectList(Specification.<OAuth2AccessTokenDO>where(JpaSpecUtil.eq("userId", userId))
                .and(JpaSpecUtil.eq("userType", userType))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }


    /**
     * 物理删除早于过期时间的令牌（分批）— 原 @Delete 原生 SQL 的 PG 版（按主键子查询 LIMIT），
     * 仅供 Token 清理 Job 使用（原语义即物理删除、忽略租户）
     */
    @Modifying
    @Query(value = "DELETE FROM system_oauth2_access_token WHERE id IN (SELECT id FROM system_oauth2_access_token WHERE expires_time < :expiresTime LIMIT :limit)", nativeQuery = true)
    int deleteByExpiresTimeLt(@Param("expiresTime") LocalDateTime expiresTime, @Param("limit") Integer limit);

}
