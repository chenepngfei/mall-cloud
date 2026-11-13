package cn.afei.mall.framework.tenant.core.db;

import cn.afei.mall.framework.mybatis.core.dataobject.BaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 拓展多租户的 BaseDO 基类（JPA 版）
 *
 * tenant_id 的写入由租户 EntityListener / Service 层负责（原 MP TenantLineInnerInterceptor 对 INSERT 自动改写），
 * 查询过滤由 tenantSpec()（Specification）统一拼接，行为与原 SQL 改写保持一致
 *
 * @author 芋道源码
 */
@Data
@EqualsAndHashCode(callSuper = true)
@MappedSuperclass
public abstract class TenantBaseDO extends BaseDO {

    /**
     * 多租户编号
     */
    @Column(name = "tenant_id")
    private Long tenantId;

}
