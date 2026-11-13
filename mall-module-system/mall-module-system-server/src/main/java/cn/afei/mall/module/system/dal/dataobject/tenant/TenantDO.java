package cn.afei.mall.module.system.dal.dataobject.tenant;

import cn.afei.mall.framework.common.enums.CommonStatusEnum;
import cn.afei.mall.framework.mybatis.core.dataobject.BaseDO;
import cn.afei.mall.framework.tenant.core.aop.TenantIgnore;
import cn.afei.mall.module.system.dal.dataobject.user.AdminUserDO;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Convert;
import cn.afei.mall.framework.mybatis.core.type.convert.CommaStringListConverter;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 租户 DO
 *
 * @author 芋道源码
 */
@Entity
@Table(name = "system_tenant")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TenantIgnore
public class TenantDO extends BaseDO {

    /**
     * 套餐编号 - 系统
     */
    public static final Long PACKAGE_ID_SYSTEM = 0L;

    /**
     * 租户编号，自增
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 租户名，唯一
     */
    private String name;
    /**
     * 联系人的用户编号
     *
     * 关联 {@link AdminUserDO#getId()}
     */
    private Long contactUserId;
    /**
     * 联系人
     */
    private String contactName;
    /**
     * 联系手机
     */
    private String contactMobile;
    /**
     * 租户状态
     *
     * 枚举 {@link CommonStatusEnum}
     */
    private Integer status;
    /**
     * 绑定域名列表
     *
     * 1. 考虑到对微信小程序的兼容，也允许传递 appid
     * 2. 为什么是数组，考虑到管理后台、会员前台都有独立的域名，又或者多个管理后台
     */
    @Convert(converter = CommaStringListConverter.class)
    private List<String> websites;
    /**
     * 租户套餐编号
     *
     * 关联 {@link TenantPackageDO#getId()}
     * 特殊逻辑：系统内置租户，不使用套餐，暂时使用 {@link #PACKAGE_ID_SYSTEM} 标识
     */
    private Long packageId;
    /**
     * 过期时间
     */
    private LocalDateTime expireTime;
    /**
     * 账号数量
     */
    private Integer accountCount;

}
