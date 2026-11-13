package cn.afei.mall.module.system.dal.jpa.dept;

import cn.afei.mall.framework.mybatis.core.repository.BaseRepositoryX;
import cn.afei.mall.framework.mybatis.core.repository.JpaSpecUtil;
import cn.afei.mall.framework.tenant.core.db.TenantSpec;
import cn.afei.mall.module.system.controller.admin.dept.vo.dept.DeptListReqVO;
import cn.afei.mall.module.system.dal.dataobject.dept.DeptDO;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;
import java.util.List;

/**
 * 部门 Repository（自原 DeptMapper 迁移，查询语义与 MyBatis Plus 版一致）
 *
 * @author 芋道源码
 */
public interface DeptRepository extends BaseRepositoryX<DeptDO, Long> {

    default List<DeptDO> selectList(DeptListReqVO reqVO) {
        return selectList(Specification.<DeptDO>where(JpaSpecUtil.likeIfPresent("name", reqVO.getName()))
                .and(JpaSpecUtil.eqIfPresent("status", reqVO.getStatus()))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

    default DeptDO selectByParentIdAndName(Long parentId, String name) {
        return selectOne(Specification.<DeptDO>where(JpaSpecUtil.eq("parentId", parentId))
                .and(JpaSpecUtil.eq("name", name))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

    default Long selectCountByParentId(Long parentId) {
        return selectCount(Specification.<DeptDO>where(JpaSpecUtil.eq("parentId", parentId))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

    default List<DeptDO> selectListByParentId(Collection<Long> parentIds) {
        return selectList(Specification.<DeptDO>where(JpaSpecUtil.in("parentId", parentIds))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

    default List<DeptDO> selectListByLeaderUserId(Long id) {
        return selectList(Specification.<DeptDO>where(JpaSpecUtil.eq("leaderUserId", id))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

}
