package cn.afei.mall.module.system.dal.dataobject.oauth2;

import cn.afei.mall.framework.common.enums.UserTypeEnum;
import cn.afei.mall.framework.tenant.core.db.TenantBaseDO;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Convert;
import cn.afei.mall.framework.mybatis.core.type.convert.JsonListStringConverter;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * OAuth2 刷新令牌
 *
 * @author 芋道源码
 */
@Entity
@Table(name = "system_oauth2_refresh_token")
// 由于 Oracle 的 SEQ 的名字长度有限制，所以就先用 system_oauth2_access_token_seq 吧，反正也没啥问题
@Data
public class OAuth2RefreshTokenDO extends TenantBaseDO {

    /**
     * 编号，数据库字典
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 刷新令牌
     */
    private String refreshToken;
    /**
     * 用户编号
     */
    private Long userId;
    /**
     * 用户类型
     *
     * 枚举 {@link UserTypeEnum}
     */
    private Integer userType;
    /**
     * 客户端编号
     *
     * 关联 {@link OAuth2ClientDO#getId()}
     */
    private String clientId;
    /**
     * 授权范围
     */
    @Convert(converter = JsonListStringConverter.class)
    private List<String> scopes;
    /**
     * 过期时间
     */
    private LocalDateTime expiresTime;

}
