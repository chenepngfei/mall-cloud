package cn.afei.mall.framework.datapermission.core.rule;

import cn.hutool.core.collection.CollUtil;
import cn.afei.mall.framework.jpa.core.hook.DataPermissionHook;
import cn.afei.mall.framework.security.core.util.SecurityFrameworkUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

/**
 * 数据权限钩子实现：把 {@link DataPermissionRule} 的规则，转换为 Specification 追加到 Repository 查询
 *
 * 对应原 MyBatis Plus 的 {@code DataPermissionRuleHandler}（MultiDataPermissionHandler）
 *
 * @author 芋道源码
 */
@RequiredArgsConstructor
public class DataPermissionHookImpl implements DataPermissionHook {

    private final DataPermissionRuleFactory ruleFactory;

    @Override
    public <T> Specification<T> buildSpecification(Class<T> entityClass) {
        // 特殊：跨租户访问时，跳过数据权限（对齐原 DataPermissionRuleHandler 的 skipPermissionCheck）
        if (SecurityFrameworkUtils.skipPermissionCheck()) {
            return null;
        }
        // 获得当前生效的数据权限规则（含 @DataPermission 注解的开启/关闭、规则过滤等上下文逻辑）
        List<DataPermissionRule> rules = ruleFactory.getDataPermissionRule(null);
        if (CollUtil.isEmpty(rules)) {
            return null;
        }
        // 拼接命中的规则（多个规则 AND 组合）
        Specification<T> result = null;
        for (DataPermissionRule rule : rules) {
            if (!rule.getEntityClasses().contains(entityClass)) {
                continue;
            }
            Specification<T> specification = (root, query, criteriaBuilder) ->
                    rule.getPredicate(entityClass, root, criteriaBuilder);
            result = result == null ? specification : result.and(specification);
        }
        return result;
    }

}
