package cn.afei.mall.module.system.dal.dataobject.tenant;

import cn.afei.mall.framework.common.enums.CommonStatusEnum;
import cn.afei.mall.framework.mybatis.core.dataobject.BaseDO;
import cn.afei.mall.framework.tenant.core.aop.TenantIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Convert;
import cn.afei.mall.framework.mybatis.core.type.convert.JsonSetLongConverter;
import lombok.*;

import java.util.Set;

/**
 * 租户套餐 DO
 *
 * @author 芋道源码
 */
@Entity
@Table(name = "system_tenant_package")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TenantIgnore
public class TenantPackageDO extends BaseDO {

    /**
     * 套餐编号，自增
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 套餐名，唯一
     */
    private String name;
    /**
     * 租户套餐状态
     *
     * 枚举 {@link CommonStatusEnum}
     */
    private Integer status;
    /**
     * 备注
     */
    private String remark;
    /**
     * 关联的菜单编号
     */
    @Convert(converter = JsonSetLongConverter.class)
    private Set<Long> menuIds;

}
