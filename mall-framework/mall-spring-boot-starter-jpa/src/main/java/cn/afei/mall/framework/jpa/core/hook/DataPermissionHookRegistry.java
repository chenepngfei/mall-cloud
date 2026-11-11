package cn.afei.mall.framework.jpa.core.hook;

import org.springframework.data.jpa.domain.Specification;

/**
 * {@link DataPermissionHook} 的静态注册中心
 *
 * 为什么用静态注册？因为 Spring Data 的 Repository 基类实例（BaseRepositoryImpl）不是 Spring Bean，
 * 无法自动注入，只能通过静态字段传递；由 {@code MallJpaAutoConfiguration} 在启动完成前注册
 */
public final class DataPermissionHookRegistry {

    private static volatile DataPermissionHook hook;

    private DataPermissionHookRegistry() {
    }

    public static void setHook(DataPermissionHook dataPermissionHook) {
        hook = dataPermissionHook;
    }

    /**
     * 针对实体构建数据权限过滤条件
     *
     * @param entityClass 实体类
     * @param <T>         实体类型
     * @return 数据权限 Specification；未注册钩子或实体不受管控时返回 null
     */
    public static <T> Specification<T> buildSpecification(Class<T> entityClass) {
        DataPermissionHook current = hook;
        if (current == null) {
            return null;
        }
        return current.buildSpecification(entityClass);
    }

}
