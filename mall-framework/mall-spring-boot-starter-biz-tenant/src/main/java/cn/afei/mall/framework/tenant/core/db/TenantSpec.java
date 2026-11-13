package cn.afei.mall.framework.tenant.core.db;

import cn.afei.mall.framework.tenant.core.context.TenantContextHolder;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

/**
 * 1. 租户过滤 Specification — tenant_id = 当前租户
 * <p>
 * 平移原 MP TenantLineInnerInterceptor 的 SQL 自动改写行为：
 * 1. 仅当实体含 tenantId 属性（TenantBaseDO 及补齐映射的实体）时拼接，非租户实体自动跳过（对齐原 ignoreTable）
 * 2. TenantContextHolder 处于忽略状态（@TenantIgnore / TenantUtils.executeIgnore）时不拼接
 * 3. 无租户上下文时抛异常（对齐原 getRequiredTenantId 的强制隔离语义，防数据泄漏）
 * <p>
 * 放在 biz-tenant 模块的原因：依赖 TenantContextHolder，而 starter-mybatis 不能反向依赖本模块。
 * 用法：spec.and(TenantSpec.tenantSpec())，与 JpaSpecUtil.logicDeleteSpec() 链式组合
 *
 * @author 芋道源码
 */
public final class TenantSpec {

    private TenantSpec() {
    }

    /**
     * 1. 构造当前租户的过滤条件
     */
    public static <T> Specification<T> tenantSpec() {
        return (root, query, cb) -> {
            // 情况一，全局忽略多租户（@TenantIgnore 方法注解 / TenantUtils.executeIgnore），对齐原 ignoreTable 首条判断
            if (TenantContextHolder.isIgnore()) {
                return cb.conjunction();
            }
            if (!hasAttribute(root, "tenantId")) {
                return cb.conjunction();
            }
            // 对齐原 TenantDatabaseInterceptor#getTenantId：无租户上下文直接抛异常（强制租户隔离，防数据泄漏）
            Long tenantId = TenantContextHolder.getRequiredTenantId();
            return cb.equal(root.get("tenantId"), tenantId);
        };
    }

    /**
     * 2. 判断实体是否包含指定属性（对非租户实体安全降级）
     */
    private static boolean hasAttribute(Root<?> root, String attr) {
        try {
            root.getModel().getAttribute(attr);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

}
