package cn.afei.mall.module.system.dal.jpa.permission;

import cn.afei.mall.framework.common.pojo.PageResult;
import cn.afei.mall.framework.mybatis.core.repository.BaseRepositoryX;
import cn.afei.mall.framework.mybatis.core.repository.JpaSpecUtil;
import cn.afei.mall.framework.tenant.core.db.TenantSpec;
import cn.afei.mall.module.system.controller.admin.permission.vo.role.RolePageReqVO;
import cn.afei.mall.module.system.dal.dataobject.permission.RoleDO;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

/**
 * 角色 Repository（自原 RoleMapper 迁移）
 *
 * @author 芋道源码
 */
public interface RoleRepository extends BaseRepositoryX<RoleDO, Long> {

    default PageResult<RoleDO> selectPage(RolePageReqVO reqVO) {
        return selectPage(reqVO, Specification.<RoleDO>where(JpaSpecUtil.likeIfPresent("name", reqVO.getName()))
                .and(JpaSpecUtil.likeIfPresent("code", reqVO.getCode()))
                .and(JpaSpecUtil.eqIfPresent("status", reqVO.getStatus()))
                .and(JpaSpecUtil.betweenIfPresent("createTime", reqVO.getCreateTime()))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()),
                Sort.by(Sort.Direction.ASC, "sort"));
    }

    default RoleDO selectByName(String name) {
        return selectOne(Specification.<RoleDO>where(JpaSpecUtil.eq("name", name))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

    default RoleDO selectByCode(String code) {
        return selectOne(Specification.<RoleDO>where(JpaSpecUtil.eq("code", code))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

    default List<RoleDO> selectListByStatus(@Nullable Collection<Integer> statuses) {
        return selectList(Specification.<RoleDO>where(JpaSpecUtil.inIfPresent("status", statuses))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

}
