package cn.afei.mall.module.system.dal.dataobject.oauth2;

import cn.afei.mall.framework.common.enums.UserTypeEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Convert;
import cn.afei.mall.framework.mybatis.core.type.convert.JsonListStringConverter;
import cn.afei.mall.framework.tenant.core.db.TenantBaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * OAuth2 授权码 DO
 *
 * @author 芋道源码
 */
@Entity
@Table(name = "system_oauth2_code")
@Data
@EqualsAndHashCode(callSuper = true)
public class OAuth2CodeDO extends TenantBaseDO {

    /**
     * 编号，数据库递增
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 授权码
     */
    private String code;
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
     * 关联 {@link OAuth2ClientDO#getClientId()}
     */
    private String clientId;
    /**
     * 授权范围
     */
    @Convert(converter = JsonListStringConverter.class)
    private List<String> scopes;
    /**
     * 重定向地址
     */
    private String redirectUri;
    /**
     * 状态
     */
    private String state;
    /**
     * 过期时间
     */
    private LocalDateTime expiresTime;

}
