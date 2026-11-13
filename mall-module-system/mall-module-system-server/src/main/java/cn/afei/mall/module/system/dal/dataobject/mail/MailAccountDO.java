package cn.afei.mall.module.system.dal.dataobject.mail;

import cn.afei.mall.framework.mybatis.core.dataobject.BaseDO;
import cn.afei.mall.framework.tenant.core.aop.TenantIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 邮箱账号 DO
 *
 * 用途：配置发送邮箱的账号
 *
 * @author wangjingyi
 * @since 2022-03-21
 */
@Entity
@Table(name = "system_mail_account")
@Data
@EqualsAndHashCode(callSuper = true)
@TenantIgnore
public class MailAccountDO extends BaseDO {

    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 邮箱
     */
    private String mail;

    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * SMTP 服务器域名
     */
    private String host;
    /**
     * SMTP 服务器端口
     */
    private Integer port;
    /**
     * 是否开启 SSL
     */
    private Boolean sslEnable;
    /**
     * 是否开启 STARTTLS
     */
    private Boolean starttlsEnable;

}
