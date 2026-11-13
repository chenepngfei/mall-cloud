package cn.afei.mall.module.system.dal.jpa.user;

import cn.afei.mall.framework.common.pojo.PageResult;
import cn.afei.mall.framework.mybatis.core.repository.BaseRepositoryX;
import cn.afei.mall.framework.mybatis.core.repository.JpaSpecUtil;
import cn.afei.mall.framework.tenant.core.db.TenantSpec;
import cn.afei.mall.module.system.controller.admin.user.vo.user.UserPageReqVO;
import cn.afei.mall.module.system.dal.dataobject.user.AdminUserDO;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;
import java.util.List;

/**
 * 管理后台用户 Repository（自原 AdminUserMapper 迁移）
 *
 * @author 芋道源码
 */
public interface AdminUserRepository extends BaseRepositoryX<AdminUserDO, Long> {

    default AdminUserDO selectByUsername(String username) {
        return selectOne(Specification.<AdminUserDO>where(JpaSpecUtil.eq("username", username))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

    default AdminUserDO selectByEmail(String email) {
        return selectOne(Specification.<AdminUserDO>where(JpaSpecUtil.eq("email", email))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

    default AdminUserDO selectByMobile(String mobile) {
        return selectOne(Specification.<AdminUserDO>where(JpaSpecUtil.eq("mobile", mobile))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

    default PageResult<AdminUserDO> selectPage(UserPageReqVO reqVO, Collection<Long> deptIds, Collection<Long> userIds) {
        return selectPage(reqVO, Specification.<AdminUserDO>where(JpaSpecUtil.likeIfPresent("username", reqVO.getUsername()))
                .and(JpaSpecUtil.likeIfPresent("mobile", reqVO.getMobile()))
                .and(JpaSpecUtil.eqIfPresent("status", reqVO.getStatus()))
                .and(JpaSpecUtil.betweenIfPresent("createTime", reqVO.getCreateTime()))
                .and(JpaSpecUtil.inIfPresent("deptId", deptIds))
                .and(JpaSpecUtil.inIfPresent("id", userIds))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()),
                Sort.by(Sort.Direction.DESC, "id"));
    }

    default List<AdminUserDO> selectListByNickname(String nickname) {
        return selectList(Specification.<AdminUserDO>where(JpaSpecUtil.like("nickname", nickname))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

    default List<AdminUserDO> selectListByStatus(Integer status) {
        return selectListByStatusAndDeptId(status, null);
    }

    default List<AdminUserDO> selectListByStatusAndDeptId(Integer status, Long deptId) {
        return selectList(Specification.<AdminUserDO>where(JpaSpecUtil.eq("status", status))
                .and(JpaSpecUtil.eqIfPresent("deptId", deptId))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

    default List<AdminUserDO> selectListByDeptIds(Collection<Long> deptIds) {
        return selectList(Specification.<AdminUserDO>where(JpaSpecUtil.in("deptId", deptIds))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

}
