package cn.afei.mall.framework.datapermission.core.rule.dept;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.afei.mall.framework.common.biz.system.permission.PermissionCommonApi;
import cn.afei.mall.framework.common.enums.UserTypeEnum;
import cn.afei.mall.framework.common.util.json.JsonUtils;
import cn.afei.mall.framework.datapermission.core.rule.DataPermissionRule;
import cn.afei.mall.framework.security.core.LoginUser;
import cn.afei.mall.framework.security.core.util.SecurityFrameworkUtils;
import cn.afei.mall.framework.common.biz.system.permission.dto.DeptDataPermissionRespDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 基于部门的 {@link DataPermissionRule} 数据权限规则实现（JPA 版）
 *
 * 注意，使用 DeptDataPermissionRule 时，需要保证表中有 dept_id 部门编号的字段，可自定义。
 *
 * 实际业务场景下，会存在一个经典的问题？当用户修改部门时，冗余的 dept_id 是否需要修改？
 * 1. 一般情况下，dept_id 不进行修改，则会导致用户看不到之前的数据。【mall-server 采用该方案】
 * 2. 部分情况下，希望该用户还是能看到之前的数据，则有两种方式解决：【需要你改造该 DeptDataPermissionRule 的实现代码】
 *  1）编写洗数据的脚本，将 dept_id 修改成新部门的编号；【建议】
 *      最终过滤条件是 WHERE dept_id = ?
 *  2）洗数据的话，可能涉及的数据量较大，也可以使用 user_id 进行过滤的方式，此时需要获取到 dept_id 对应的所有 user_id 用户编号；
 *      最终过滤条件是 WHERE user_id IN (?, ?, ? ...)
 *  3）想要保证原 dept_id 和 user_id 都可以看的到，此时使用 dept_id 和 user_id 一起过滤；
 *      最终过滤条件是 WHERE dept_id = ? OR user_id IN (?, ?, ? ...)
 *
 * @author 芋道源码
 */
@AllArgsConstructor
@Slf4j
public class DeptDataPermissionRule implements DataPermissionRule {

    /**
     * LoginUser 的 Context 缓存 Key
     */
    protected static final String CONTEXT_KEY = DeptDataPermissionRule.class.getSimpleName();

    private static final String DEPT_PROPERTY_NAME = "deptId";
    private static final String USER_PROPERTY_NAME = "userId";

    private final PermissionCommonApi permissionApi;

    /**
     * 基于部门的实体属性配置
     * 一般情况下，每个实体的部门编号属性是 deptId，通过该配置自定义。
     *
     * key：实体类
     * value：属性名
     */
    private final Map<Class<?>, String> deptColumns = new HashMap<>();
    /**
     * 基于用户的实体属性配置
     * 一般情况下，每个实体的用户编号属性是 userId，通过该配置自定义。
     *
     * key：实体类
     * value：属性名
     */
    private final Map<Class<?>, String> userColumns = new HashMap<>();
    /**
     * 所有实体类，是 {@link #deptColumns} 和 {@link #userColumns} 的合集
     */
    private final Set<Class<?>> ENTITY_CLASSES = new HashSet<>();

    @Override
    public Set<Class<?>> getEntityClasses() {
        return ENTITY_CLASSES;
    }

    @Override
    public Predicate getPredicate(Class<?> entityClass, Root<?> root, CriteriaBuilder criteriaBuilder) {
        // 只有有登陆用户的情况下，才进行数据权限的处理
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        if (loginUser == null) {
            return null;
        }
        // 只有管理员类型的用户，才进行数据权限的处理
        if (ObjectUtil.notEqual(loginUser.getUserType(), UserTypeEnum.ADMIN.getValue())) {
            return null;
        }

        // 获得数据权限
        DeptDataPermissionRespDTO deptDataPermission = loginUser.getContext(CONTEXT_KEY, DeptDataPermissionRespDTO.class);
        // 从上下文中拿不到，则调用逻辑进行获取
        if (deptDataPermission == null) {
            deptDataPermission = permissionApi.getDeptDataPermission(loginUser.getId()).getCheckedData();
            if (deptDataPermission == null) {
                log.error("[getPredicate][LoginUser({}) 获取数据权限为 null]", JsonUtils.toJsonString(loginUser));
                throw new NullPointerException(String.format("LoginUser(%d) 实体(%s) 未返回数据权限",
                        loginUser.getId(), entityClass.getSimpleName()));
            }
            // 添加到上下文中，避免重复计算
            loginUser.setContext(CONTEXT_KEY, deptDataPermission);
        }

        // 情况一，如果是 ALL 可查看全部，则无需拼接条件
        if (deptDataPermission.getAll()) {
            return null;
        }

        // 情况二，即不能查看部门，又不能查看自己，则说明 100% 无权限
        if (CollUtil.isEmpty(deptDataPermission.getDeptIds())
                && Boolean.FALSE.equals(deptDataPermission.getSelf())) {
            return criteriaBuilder.disjunction(); // 恒为 false，可以保证返回的数据为空
        }

        // 情况三，拼接 Dept 和 User 的条件，最后组合
        Predicate deptPredicate = buildDeptPredicate(entityClass, root, criteriaBuilder, deptDataPermission.getDeptIds());
        Predicate userPredicate = buildUserPredicate(entityClass, root, criteriaBuilder, deptDataPermission.getSelf(), loginUser.getId());
        if (deptPredicate == null && userPredicate == null) {
            // TODO 芋艿：获得不到条件的时候，暂时不抛出异常，而是不返回数据
            log.warn("[getPredicate][LoginUser({}) 实体({}) DeptDataPermission({}) 构建的条件为空]",
                    JsonUtils.toJsonString(loginUser), entityClass.getSimpleName(), JsonUtils.toJsonString(deptDataPermission));
            return criteriaBuilder.disjunction(); // 恒为 false，可以保证返回的数据为空
        }
        if (deptPredicate == null) {
            return userPredicate;
        }
        if (userPredicate == null) {
            return deptPredicate;
        }
        // 目前，如果有指定部门 + 可查看自己，采用 OR 条件。即，WHERE (dept_id IN ? OR user_id = ?)
        return criteriaBuilder.or(deptPredicate, userPredicate);
    }

    private Predicate buildDeptPredicate(Class<?> entityClass, Root<?> root, CriteriaBuilder criteriaBuilder, Set<Long> deptIds) {
        // 如果不存在配置，则无需作为条件
        String propertyName = deptColumns.get(entityClass);
        if (propertyName == null) {
            return null;
        }
        // 如果为空，则无条件
        if (CollUtil.isEmpty(deptIds)) {
            return null;
        }
        // 拼接条件
        return root.get(propertyName).in(deptIds);
    }

    private Predicate buildUserPredicate(Class<?> entityClass, Root<?> root, CriteriaBuilder criteriaBuilder, Boolean self, Long userId) {
        // 如果不查看自己，则无需作为条件
        if (Boolean.FALSE.equals(self)) {
            return null;
        }
        String propertyName = userColumns.get(entityClass);
        if (propertyName == null) {
            return null;
        }
        // 拼接条件
        return criteriaBuilder.equal(root.get(propertyName), userId);
    }

    // ==================== 添加配置 ====================

    public void addDeptColumn(Class<?> entityClass) {
        addDeptColumn(entityClass, DEPT_PROPERTY_NAME);
    }

    public void addDeptColumn(Class<?> entityClass, String propertyName) {
        deptColumns.put(entityClass, propertyName);
        ENTITY_CLASSES.add(entityClass);
    }

    public void addUserColumn(Class<?> entityClass) {
        addUserColumn(entityClass, USER_PROPERTY_NAME);
    }

    public void addUserColumn(Class<?> entityClass, String propertyName) {
        userColumns.put(entityClass, propertyName);
        ENTITY_CLASSES.add(entityClass);
    }

}
