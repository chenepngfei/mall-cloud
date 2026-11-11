package cn.afei.mall.module.system.dal.mysql.oauth2;

import cn.afei.mall.framework.jpa.core.query.LambdaSpecifications;
import cn.afei.mall.framework.jpa.core.repository.BaseRepositoryX;
import cn.afei.mall.framework.tenant.core.aop.TenantIgnore;
import cn.afei.mall.module.system.dal.dataobject.oauth2.OAuth2RefreshTokenDO;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

public interface OAuth2RefreshTokenRepository extends BaseRepositoryX<OAuth2RefreshTokenDO, Long> {

    default int deleteByRefreshToken(String refreshToken) {
        return (int) delete(LambdaSpecifications.<OAuth2RefreshTokenDO>of()
                .eq(OAuth2RefreshTokenDO::getRefreshToken, refreshToken)
                .build());
    }

    @TenantIgnore // 获取 token 的时候，需要忽略租户编号。原因是：一些场景下，可能不会传递 tenant-id 请求头，例如说文件上传、积木报表等等
    default OAuth2RefreshTokenDO selectByRefreshToken(String refreshToken) {
        return selectOne(OAuth2RefreshTokenDO::getRefreshToken, refreshToken);
    }

    /**
     * 物理删除指定过期时间之前的刷新令牌
     *
     * @param expiresTime 最大时间
     * @param limit       删除条数，防止一次删除太多
     * @return 删除条数
     */
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM system_oauth2_refresh_token WHERE expires_time < :expiresTime AND id IN (SELECT id FROM (SELECT id FROM system_oauth2_refresh_token WHERE expires_time < :expiresTime LIMIT :limit) tmp)",
            nativeQuery = true)
    Integer deleteByExpiresTimeLt(@Param("expiresTime") LocalDateTime expiresTime, @Param("limit") Integer limit);
}
