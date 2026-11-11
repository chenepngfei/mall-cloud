package cn.afei.mall.module.system.dal.mysql.oauth2;

import cn.afei.mall.framework.common.pojo.PageResult;
import cn.afei.mall.framework.jpa.core.query.LambdaSpecifications;
import cn.afei.mall.framework.jpa.core.repository.BaseRepositoryX;
import cn.afei.mall.framework.tenant.core.aop.TenantIgnore;
import cn.afei.mall.module.system.controller.admin.oauth2.vo.token.OAuth2AccessTokenPageReqVO;
import cn.afei.mall.module.system.dal.dataobject.oauth2.OAuth2AccessTokenDO;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface OAuth2AccessTokenRepository extends BaseRepositoryX<OAuth2AccessTokenDO, Long> {

    @TenantIgnore // 获取 token 的时候，需要忽略租户编号。原因是：一些场景下，可能不会传递 tenant-id 请求头，例如说文件上传、积木报表等等
    default OAuth2AccessTokenDO selectByAccessToken(String accessToken) {
        return selectOne(OAuth2AccessTokenDO::getAccessToken, accessToken);
    }

    default List<OAuth2AccessTokenDO> selectListByRefreshToken(String refreshToken) {
        return selectList(OAuth2AccessTokenDO::getRefreshToken, refreshToken);
    }

    default PageResult<OAuth2AccessTokenDO> selectPage(OAuth2AccessTokenPageReqVO reqVO) {
        return selectPage(reqVO, LambdaSpecifications.<OAuth2AccessTokenDO>of()
                .eqIfPresent(OAuth2AccessTokenDO::getUserId, reqVO.getUserId())
                .eqIfPresent(OAuth2AccessTokenDO::getUserType, reqVO.getUserType())
                .likeIfPresent(OAuth2AccessTokenDO::getClientId, reqVO.getClientId())
                .gtIfPresent(OAuth2AccessTokenDO::getExpiresTime, LocalDateTime.now())
                .orderByDesc(OAuth2AccessTokenDO::getId)
                .build());
    }

    default List<OAuth2AccessTokenDO> selectListByUserIdAndUserType(Long userId, Integer userType) {
        return selectList(OAuth2AccessTokenDO::getUserId, userId,
                OAuth2AccessTokenDO::getUserType, userType);
    }

    /**
     * 物理删除指定过期时间之前的访问令牌
     *
     * @param expiresTime 最大时间
     * @param limit       删除条数，防止一次删除太多
     * @return 删除条数
     */
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM system_oauth2_access_token WHERE expires_time < :expiresTime AND id IN (SELECT id FROM (SELECT id FROM system_oauth2_access_token WHERE expires_time < :expiresTime LIMIT :limit) tmp)",
            nativeQuery = true)
    Integer deleteByExpiresTimeLt(@Param("expiresTime") LocalDateTime expiresTime, @Param("limit") Integer limit);

}
