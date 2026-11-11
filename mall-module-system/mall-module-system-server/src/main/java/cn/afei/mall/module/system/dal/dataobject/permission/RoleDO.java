package cn.afei.mall.module.system.dal.dataobject.permission;

import cn.afei.mall.framework.common.enums.CommonStatusEnum;
import cn.afei.mall.framework.jpa.core.convert.LongSetConverter;
import cn.afei.mall.framework.tenant.core.db.TenantBaseDO;
import cn.afei.mall.module.system.enums.permission.DataScopeEnum;
import cn.afei.mall.module.system.enums.permission.RoleTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Filter;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Set;

/**
 * 角色 DO
 *
 * @author ruoyi
 */
@Entity
@Table(name = "system_role")
@Filter(name = TenantBaseDO.TENANT_FILTER_NAME, condition = "tenant_id = :tenantId")
@Data
@EqualsAndHashCode(callSuper = true)
public class RoleDO extends TenantBaseDO {

    /**
     * 角色ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 角色名称
     */
    private String name;
    /**
     * 角色标识
     *
     * 枚举
     */
    private String code;
    /**
     * 角色排序
     */
    private Integer sort;
    /**
     * 角色状态
     *
     * 枚举 {@link CommonStatusEnum}
     */
    private Integer status;
    /**
     * 角色类型
     *
     * 枚举 {@link RoleTypeEnum}
     */
    private Integer type;
    /**
     * 备注
     */
    private String remark;

    /**
     * 数据范围
     *
     * 枚举 {@link DataScopeEnum}
     */
    private Integer dataScope;
    /**
     * 数据范围(指定部门数组)
     *
     * 适用于 {@link #dataScope} 的值为 {@link DataScopeEnum#DEPT_CUSTOM} 时
     */
    @Convert(converter = LongSetConverter.class)
    private Set<Long> dataScopeDeptIds;

}
