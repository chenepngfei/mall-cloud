package cn.afei.mall.framework.mybatis.core.dataobject;

import cn.afei.mall.framework.security.core.util.SecurityFrameworkUtils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Data;
import org.dromara.core.trans.vo.TransPojo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 基础实体对象（JPA 版）
 *
 * 1. 为什么实现 {@link TransPojo} 接口？
 *    因为使用 Easy-Trans TransType.SIMPLE 模式，集成查询结果的 VO/DO 数据翻译
 * 2. 创建时间/更新时间/创建者/更新者的自动填充：
 *    原 MyBatis Plus DefaultDBFieldHandler（MetaObjectHandler）平移为 {@link @PrePersist}/{@link @PreUpdate}
 * 3. deleted 兜底为 false：PG 列 NOT NULL 且无默认值，JPA 插入必须显式赋值（对齐原 MP @TableLogic 默认未删语义）
 *
 * @author 芋道源码
 */
@Data
@JsonIgnoreProperties(value = "transMap") // 由于 Easy-Trans 会添加 transMap 属性，避免 Jackson 在 Spring Cache 反序列化报错
@MappedSuperclass
public abstract class BaseDO implements Serializable, TransPojo {

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 最后更新时间
     */
    private LocalDateTime updateTime;
    /**
     * 创建者，目前使用 SysUser 的 id 编号
     *
     * 使用 String 类型的原因是，未来可能会存在非数值的情况，留好拓展性。
     */
    private String creator;
    /**
     * 更新者，目前使用 SysUser 的 id 编号
     *
     * 使用 String 类型的原因是，未来可能会存在非数值的情况，留好拓展性。
     */
    private String updater;
    /**
     * 是否删除
     *
     * 不加 JPA 删除注解，逻辑删除条件统一由 Specification 拼接（deleted = false），不依赖框架注解
     */
    private Boolean deleted;

    @PrePersist
    public void prePersist() {
        LocalDateTime current = LocalDateTime.now();
        // 创建时间为空，则以当前时间为插入时间
        if (Objects.isNull(createTime)) {
            createTime = current;
        }
        // 更新时间为空，则以当前时间为更新时间
        if (Objects.isNull(updateTime)) {
            updateTime = current;
        }
        // deleted 兜底：未删除
        if (Objects.isNull(deleted)) {
            deleted = Boolean.FALSE;
        }

        Long userId = SecurityFrameworkUtils.getLoginUserId();
        // 当前登录用户不为空，创建人为空，则当前登录用户为创建人
        if (Objects.nonNull(userId) && Objects.isNull(creator)) {
            creator = userId.toString();
        }
        // 当前登录用户不为空，更新人为空，则当前登录用户为更新人
        if (Objects.nonNull(userId) && Objects.isNull(updater)) {
            updater = userId.toString();
        }
    }

    @PreUpdate
    public void preUpdate() {
        // 更新时间为空，则以当前时间为更新时间
        if (Objects.isNull(updateTime)) {
            updateTime = LocalDateTime.now();
        }
        // 当前登录用户不为空，更新人为空，则当前登录用户为更新人
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        if (Objects.nonNull(userId) && Objects.isNull(updater)) {
            updater = userId.toString();
        }
    }

    /**
     * 把 creator、createTime、updateTime、updater 都清空，避免前端直接传递 creator 之类的字段，直接就被更新了
     */
    public void clean(){
        this.creator = null;
        this.createTime = null;
        this.updater = null;
        this.updateTime = null;
    }

}
