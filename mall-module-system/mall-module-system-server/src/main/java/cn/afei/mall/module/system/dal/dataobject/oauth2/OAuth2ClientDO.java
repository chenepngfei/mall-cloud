package cn.afei.mall.module.system.dal.dataobject.oauth2;

import cn.afei.mall.framework.common.enums.CommonStatusEnum;
import cn.afei.mall.framework.mybatis.core.dataobject.BaseDO;
import cn.afei.mall.framework.tenant.core.aop.TenantIgnore;
import cn.afei.mall.module.system.enums.oauth2.OAuth2GrantTypeEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Convert;
import cn.afei.mall.framework.mybatis.core.type.convert.JsonListStringConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * OAuth2 客户端 DO
 *
 * @author 芋道源码
 */
@Entity
@Table(name = "system_oauth2_client")
@Data
@EqualsAndHashCode(callSuper = true)
@TenantIgnore
public class OAuth2ClientDO extends BaseDO {

    /**
     * 编号，数据库自增
     *
     * 由于 SQL Server 在存储 String 主键有点问题，所以暂时使用 Long 类型
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 客户端编号
     */
    private String clientId;
    /**
     * 客户端密钥
     */
    private String secret;
    /**
     * 应用名
     */
    private String name;
    /**
     * 应用图标
     */
    private String logo;
    /**
     * 应用描述
     */
    private String description;
    /**
     * 状态
     *
     * 枚举 {@link CommonStatusEnum}
     */
    private Integer status;
    /**
     * 访问令牌的有效期
     */
    private Integer accessTokenValiditySeconds;
    /**
     * 刷新令牌的有效期
     */
    private Integer refreshTokenValiditySeconds;
    /**
     * 可重定向的 URI 地址
     */
    @Convert(converter = JsonListStringConverter.class)
    private List<String> redirectUris;
    /**
     * 授权类型（模式）
     *
     * 枚举 {@link OAuth2GrantTypeEnum}
     */
    @Convert(converter = JsonListStringConverter.class)
    private List<String> authorizedGrantTypes;
    /**
     * 授权范围
     */
    @Convert(converter = JsonListStringConverter.class)
    private List<String> scopes;
    /**
     * 自动授权的 Scope
     *
     * code 授权时，如果 scope 在这个范围内，则自动通过
     */
    @Convert(converter = JsonListStringConverter.class)
    private List<String> autoApproveScopes;
    /**
     * 权限
     */
    @Convert(converter = JsonListStringConverter.class)
    private List<String> authorities;
    /**
     * 资源
     */
    @Convert(converter = JsonListStringConverter.class)
    private List<String> resourceIds;
    /**
     * 附加信息，JSON 格式
     */
    private String additionalInformation;

}
