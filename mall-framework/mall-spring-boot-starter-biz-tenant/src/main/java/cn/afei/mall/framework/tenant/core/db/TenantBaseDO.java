package cn.afei.mall.framework.tenant.core.db;

import cn.afei.mall.framework.jpa.core.dataobject.BaseDO;
import cn.afei.mall.framework.tenant.core.context.TenantContextHolder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;

/**
 * 拓展多租户的 BaseDO 基类（JPA 版）
 *
 * 1. tenantId 字段：插入时通过 {@link PrePersist} 自动填充（对齐原 MyBatis 拦截器的 INSERT 注入）
 * 2. 租户过滤：通过 Hibernate {@link org.hibernate.annotations.Filter}（名称 {@link #TENANT_FILTER_NAME}）实现，
 *    由 {@link TenantSessionHook} 在 Repository 数据操作前按上下文自动启用/禁用，
 *    等价于原 MyBatis Plus 的 TenantLineInnerInterceptor
 *
 * 使用方式：租户实体继承本类，并在实体类上标注
 * {@code @Filter(name = TenantBaseDO.TENANT_FILTER_NAME, condition = "tenant_id = :tenantId", applyToLoadByKey = true)}
 * （@Filter 无法从 @MappedSuperclass 继承，必须逐实体标注）
 */
@MappedSuperclass
@Data
@EqualsAndHashCode(callSuper = true)
@FilterDef(name = TenantBaseDO.TENANT_FILTER_NAME,
        parameters = @ParamDef(name = "tenantId", type = Long.class))
public abstract class TenantBaseDO extends BaseDO {

    /**
     * 租户过滤器的名称
     */
    public static final String TENANT_FILTER_NAME = "tenantFilter";

    /**
     * 多租户编号
     */
    @Column(name = "tenant_id", nullable = false, updatable = false)
    private Long tenantId;

    /**
     * 插入时自动填充租户编号（对齐原 TenantLineInnerInterceptor 的 INSERT 注入）
     */
    @PrePersist
    void fillTenantId() {
        if (tenantId == null && !TenantContextHolder.isIgnore()) {
            tenantId = TenantContextHolder.getRequiredTenantId();
        }
    }

}
