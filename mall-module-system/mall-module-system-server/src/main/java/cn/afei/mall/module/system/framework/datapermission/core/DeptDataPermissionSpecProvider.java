package cn.afei.mall.module.system.framework.datapermission.core;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.afei.mall.framework.common.biz.system.permission.PermissionCommonApi;
import cn.afei.mall.framework.common.biz.system.permission.dto.DeptDataPermissionRespDTO;
import cn.afei.mall.framework.common.enums.UserTypeEnum;
import cn.afei.mall.framework.common.util.json.JsonUtils;
import cn.afei.mall.framework.datapermission.core.aop.DataPermissionContextHolder;
import cn.afei.mall.framework.security.core.LoginUser;
import cn.afei.mall.framework.security.core.util.SecurityFrameworkUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 1. 基于部门的数据权限 Specification 提供者 — 平移原 DeptDataPermissionRule（MP SQL 改写）的过滤行为
 *
 * 原注册表配置：system_users → dept_id（部门列）+ id（本人列）；system_dept → id（部门列）。
 * 用法（阶段7 在 AdminUserServiceImpl / DeptServiceImpl 的查询入口显式拼接）：
 *   spec.and(deptDataPermissionSpecProvider.userSpec())   // AdminUserDO 查询
 *   spec.and(deptDataPermissionSpecProvider.deptSpec())   // DeptDO 查询
 *
 * 行为与原实现一致：
 * 1. @DataPermission(enable=false) / DataPermissionUtils.executeIgnore → 不拼条件
 * 2. 无登录用户、或非管理员（ADMIN）用户 → 不拼条件
 * 3. 数据权限结果缓存在 LoginUser context，避免同一用户重复计算
 * 4. ALL → 不拼；部门集为空且不可见自己 → 恒假（对齐原 WHERE null = null 返回空数据）
 * 5. system_users：(dept_id IN (...) OR id = userId)；system_dept：id IN (...)
 *
 * @author 芋道源码
 */
@Slf4j
@Component
public class DeptDataPermissionSpecProvider {

    /**
     * LoginUser 的 Context 缓存 Key（与原 DeptDataPermissionRule 一致）
     */
    private static final String CONTEXT_KEY = "DeptDataPermissionRule";

    private final PermissionCommonApi permissionApi;

    public DeptDataPermissionSpecProvider(PermissionCommonApi permissionApi) {
        // Cloud 专属逻辑：优先使用本地的 PermissionApi 实现类，而不是 Feign 调用
        // 原因：在创建租户时，租户还没创建好，导致 Feign 调用获取数据权限时，报“租户不存在”的错误
        try {
            PermissionCommonApi local = SpringUtil.getBean("permissionApiImpl", PermissionCommonApi.class);
            if (local != null) {
                permissionApi = local;
            }
        } catch (Exception ignored) {
        }
        this.permissionApi = permissionApi;
    }

    /**
     * 2. AdminUserDO 查询的数据权限条件：(dept_id IN (...) OR id = userId)
     */
    public Specification<cn.afei.mall.module.system.dal.dataobject.user.AdminUserDO> userSpec() {
        return (root, query, cb) -> {
            DeptDataPermissionRespDTO permission = getDeptDataPermission();
            // 不生效场景：不拼条件
            if (skip(permission)) {
                return cb.conjunction();
            }
            // 无任何可见范围：恒假，返回空数据
            if (CollUtil.isEmpty(permission.getDeptIds()) && Boolean.FALSE.equals(permission.getSelf())) {
                return cb.disjunction();
            }
            jakarta.persistence.criteria.Predicate deptPredicate = CollUtil.isEmpty(permission.getDeptIds())
                    ? null : root.get("deptId").in(permission.getDeptIds());
            jakarta.persistence.criteria.Predicate selfPredicate = Boolean.FALSE.equals(permission.getSelf())
                    ? null : cb.equal(root.get("id"), SecurityFrameworkUtils.getLoginUserId());
            if (deptPredicate != null && selfPredicate != null) {
                return cb.or(deptPredicate, selfPredicate);
            }
            return deptPredicate != null ? deptPredicate
                    : selfPredicate != null ? selfPredicate : cb.disjunction();
        };
    }

    /**
     * 3. DeptDO 查询的数据权限条件：id IN (...)（部门表未配置本人列，仅按可见部门过滤）
     */
    public Specification<cn.afei.mall.module.system.dal.dataobject.dept.DeptDO> deptSpec() {
        return (root, query, cb) -> {
            DeptDataPermissionRespDTO permission = getDeptDataPermission();
            if (skip(permission)) {
                return cb.conjunction();
            }
            Set<Long> deptIds = permission.getDeptIds();
            if (CollUtil.isEmpty(deptIds)) {
                // 原行为：部门表无本人列，可见部门为空时恒假（返回空数据）
                return cb.disjunction();
            }
            return root.get("id").in(deptIds);
        };
    }

    /**
     * 4. 判断当前是否跳过数据权限（注解禁用 / 无登录用户 / 非管理员 / ALL 全量）
     */
    private boolean skip(DeptDataPermissionRespDTO permission) {
        // 情况一，@DataPermission(enable = false) 或 executeIgnore：不拼条件
        if (DataPermissionContextHolder.get() != null && !DataPermissionContextHolder.get().enable()) {
            return true;
        }
        // 情况二，ALL 可查看全部：无需拼接条件
        return permission != null && Boolean.TRUE.equals(permission.getAll());
    }

    /**
     * 5. 获取当前登录用户的数据权限（带 LoginUser context 缓存；无登录用户或非管理员返回 null 表示跳过）
     */
    private DeptDataPermissionRespDTO getDeptDataPermission() {
        // 只有有登陆用户的情况下，才进行数据权限的处理
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        if (loginUser == null) {
            return null;
        }
        // 只有管理员类型的用户，才进行数据权限的处理
        if (ObjectUtil.notEqual(loginUser.getUserType(), UserTypeEnum.ADMIN.getValue())) {
            return null;
        }
        // 从上下文中获取，避免重复计算
        DeptDataPermissionRespDTO deptDataPermission = loginUser.getContext(CONTEXT_KEY, DeptDataPermissionRespDTO.class);
        if (deptDataPermission == null) {
            deptDataPermission = permissionApi.getDeptDataPermission(loginUser.getId()).getCheckedData();
            if (deptDataPermission == null) {
                log.error("[getDeptDataPermission][LoginUser({}) 获取数据权限为 null]", JsonUtils.toJsonString(loginUser));
                throw new NullPointerException(String.format("LoginUser(%d) 未返回数据权限", loginUser.getId()));
            }
            loginUser.setContext(CONTEXT_KEY, deptDataPermission);
        }
        return deptDataPermission;
    }

}
