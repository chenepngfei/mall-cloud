package cn.afei.mall.module.system.dal.dataobject.notify;

import cn.afei.mall.framework.common.enums.UserTypeEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Convert;
import cn.afei.mall.framework.mybatis.core.type.convert.JsonMapConverter;
import cn.afei.mall.framework.tenant.core.db.TenantBaseDO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 站内信 DO
 *
 * @author xrcoder
 */
@Entity
@Table(name = "system_notify_message")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotifyMessageDO extends TenantBaseDO {

    /**
     * 站内信编号，自增
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 用户编号
     *
     * 关联 MemberUserDO 的 id 字段、或者 AdminUserDO 的 id 字段
     */
    private Long userId;
    /**
     * 用户类型
     *
     * 枚举 {@link UserTypeEnum}
     */
    private Integer userType;

    // ========= 模板相关字段 =========

    /**
     * 模版编号
     *
     * 关联 {@link NotifyTemplateDO#getId()}
     */
    private Long templateId;
    /**
     * 模版编码
     *
     * 关联 {@link NotifyTemplateDO#getCode()}
     */
    private String templateCode;
    /**
     * 模版类型
     *
     * 冗余 {@link NotifyTemplateDO#getType()}
     */
    private Integer templateType;
    /**
     * 模版发送人名称
     *
     * 冗余 {@link NotifyTemplateDO#getNickname()}
     */
    private String templateNickname;
    /**
     * 模版内容
     *
     * 基于 {@link NotifyTemplateDO#getContent()} 格式化后的内容
     */
    private String templateContent;
    /**
     * 模版参数
     *
     * 基于 {@link NotifyTemplateDO#getParams()} 输入后的参数
     */
    @Convert(converter = JsonMapConverter.class)
    private Map<String, Object> templateParams;

    // ========= 读取相关字段 =========

    /**
     * 是否已读
     */
    private Boolean readStatus;
    /**
     * 阅读时间
     */
    private LocalDateTime readTime;

}
