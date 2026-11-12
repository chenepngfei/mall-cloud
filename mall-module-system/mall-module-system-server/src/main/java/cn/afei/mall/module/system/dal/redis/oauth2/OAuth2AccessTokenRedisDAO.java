package cn.afei.mall.module.system.dal.redis.oauth2;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.afei.mall.framework.common.util.collection.CollectionUtils;
import cn.afei.mall.framework.common.util.json.JsonUtils;
import cn.afei.mall.module.system.dal.dataobject.oauth2.OAuth2AccessTokenDO;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static cn.afei.mall.module.system.dal.redis.RedisKeyConstants.OAUTH2_ACCESS_TOKEN;

/**
 * {@link OAuth2AccessTokenDO} 的 RedisDAO
 *
 * @author 芋道源码
 */
@Repository
public class OAuth2AccessTokenRedisDAO {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public OAuth2AccessTokenDO get(String accessToken) {
        String redisKey = formatKey(accessToken);
        return JsonUtils.parseObject(stringRedisTemplate.opsForValue().get(redisKey), OAuth2AccessTokenDO.class);
    }

    public void set(OAuth2AccessTokenDO accessTokenDO) {
        String redisKey = formatKey(accessTokenDO.getAccessToken());
        // 清理多余字段，避免缓存。
        // 注意：必须在【副本】上清理，不能修改原实体——JPA 下它是持久化上下文中的受管实体，
        // 直接置空字段会触发 Hibernate 脏检查，commit 时生成全量 UPDATE 把 deleted=null 写库，违反非空约束
        OAuth2AccessTokenDO cacheDO = new OAuth2AccessTokenDO()
                .setId(accessTokenDO.getId()).setAccessToken(accessTokenDO.getAccessToken())
                .setRefreshToken(accessTokenDO.getRefreshToken()).setUserId(accessTokenDO.getUserId())
                .setUserType(accessTokenDO.getUserType()).setUserInfo(accessTokenDO.getUserInfo())
                .setClientId(accessTokenDO.getClientId()).setScopes(accessTokenDO.getScopes())
                .setExpiresTime(accessTokenDO.getExpiresTime());
        cacheDO.setTenantId(accessTokenDO.getTenantId());
        long time = LocalDateTimeUtil.between(LocalDateTime.now(), cacheDO.getExpiresTime(), ChronoUnit.SECONDS);
        if (time > 0) {
            stringRedisTemplate.opsForValue().set(redisKey, JsonUtils.toJsonString(cacheDO), time, TimeUnit.SECONDS);
        }
    }

    public void delete(String accessToken) {
        String redisKey = formatKey(accessToken);
        stringRedisTemplate.delete(redisKey);
    }

    public void deleteList(Collection<String> accessTokens) {
        List<String> redisKeys = CollectionUtils.convertList(accessTokens, OAuth2AccessTokenRedisDAO::formatKey);
        stringRedisTemplate.delete(redisKeys);
    }

    private static String formatKey(String accessToken) {
        return String.format(OAUTH2_ACCESS_TOKEN, accessToken);
    }

}
