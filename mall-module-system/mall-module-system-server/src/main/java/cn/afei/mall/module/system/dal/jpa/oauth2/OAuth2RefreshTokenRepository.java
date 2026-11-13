package cn.afei.mall.module.system.dal.jpa.oauth2;

import cn.afei.mall.framework.mybatis.core.repository.BaseRepositoryX;
import cn.afei.mall.framework.mybatis.core.repository.JpaSpecUtil;
import cn.afei.mall.framework.tenant.core.context.TenantContextHolder;
import cn.afei.mall.framework.tenant.core.db.TenantSpec;
import cn.afei.mall.module.system.dal.dataobject.oauth2.OAuth2RefreshTokenDO;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * OAuth2 刷新令牌 Repository（自原 OAuth2RefreshTokenMapper 迁移）
 *
 * @author 芋道源码
 */
public interface OAuth2RefreshTokenRepository extends BaseRepositoryX<OAuth2RefreshTokenDO, Long> {

    /**
     * 逻辑删除：按刷新令牌（原 MP delete → UPDATE deleted=1，含租户条件）
     */
    @Modifying
    @Query("UPDATE OAuth2RefreshTokenDO rt SET rt.deleted = true WHERE rt.deleted = false AND rt.refreshToken = :refreshToken AND rt.tenantId = :tenantId")
    int deleteByRefreshToken(@Param("refreshToken") String refreshToken, @Param("tenantId") Long tenantId);

    default int deleteByRefreshToken(String refreshToken) {
        return deleteByRefreshToken(refreshToken, TenantContextHolder.getRequiredTenantId());
    }

    default OAuth2RefreshTokenDO selectByRefreshToken(String refreshToken) {
        return selectOne(Specification.<OAuth2RefreshTokenDO>where(JpaSpecUtil.eq("refreshToken", refreshToken))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }


    /**
     * 物理删除早于过期时间的令牌（分批）— 原 @Delete 原生 SQL 的 PG 版（按主键子查询 LIMIT），
     * 仅供 Token 清理 Job 使用（原语义即物理删除、忽略租户）
     */
    @Modifying
    @Query(value = "DELETE FROM system_oauth2_refresh_token WHERE id IN (SELECT id FROM system_oauth2_refresh_token WHERE expires_time < :expiresTime LIMIT :limit)", nativeQuery = true)
    int deleteByExpiresTimeLt(@Param("expiresTime") LocalDateTime expiresTime, @Param("limit") Integer limit);

}
