package cn.afei.mall.framework.jpa.core.dataobject;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.dromara.core.trans.vo.TransPojo;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 基础实体对象（JPA 版）
 *
 * 1. 审计字段（createTime、updateTime、creator、updater）通过 Spring Data JPA Auditing 自动填充，
 *    creator/updater 来源于 {@link cn.afei.mall.framework.jpa.config.MallJpaAutoConfiguration} 的 AuditorAware
 * 2. 逻辑删除通过 {@link SQLDelete} + {@link SQLRestriction} 实现，等价于 MyBatis Plus 的 {@code @TableLogic}：
 *    加载、JPQL、Criteria 查询自动过滤 deleted = 0，{@code deleteById} 会执行 UPDATE 而非 DELETE
 *
 * 为什么实现 {@link TransPojo} 接口？
 * 因为使用 Easy-Trans TransType.SIMPLE 模式，集成 JPA 查询
 *
 * 注意：子类不要使用 derived delete（deleteByXxx）与 JPQL bulk delete/update，它们会绕过软删与过滤器
 */
@MappedSuperclass
@Data
@JsonIgnoreProperties(value = "transMap") // 由于 Easy-Trans 会添加 transMap 属性，避免 Jackson 在 Spring Cache 反序列化报错
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE {h-table} SET deleted = 1 WHERE id = ?")
@SQLRestriction("deleted = 0")
public abstract class BaseDO implements Serializable, TransPojo {

    /**
     * 创建时间
     */
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createTime;
    /**
     * 最后更新时间
     */
    @LastModifiedDate
    private LocalDateTime updateTime;
    /**
     * 创建者，目前使用 SysUser 的 id 编号
     *
     * 使用 String 类型的原因是，未来可能会存在非数值的情况，留好拓展性。
     */
    @CreatedBy
    @Column(updatable = false, length = 64)
    private String creator;
    /**
     * 更新者，目前使用 SysUser 的 id 编号
     *
     * 使用 String 类型的原因是，未来可能会存在非数值的情况，留好拓展性。
     */
    @LastModifiedBy
    @Column(length = 64)
    private String updater;
    /**
     * 是否删除
     */
    @Column(nullable = false)
    private Boolean deleted = Boolean.FALSE;

    /**
     * 把 creator、createTime、updateTime、updater 都清空，避免前端直接传递 creator 之类的字段，直接就被更新了
     */
    public void clean() {
        this.creator = null;
        this.createTime = null;
        this.updater = null;
        this.updateTime = null;
    }

}
