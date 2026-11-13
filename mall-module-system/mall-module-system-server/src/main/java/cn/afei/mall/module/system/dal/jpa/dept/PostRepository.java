package cn.afei.mall.module.system.dal.jpa.dept;

import cn.afei.mall.framework.common.pojo.PageResult;
import cn.afei.mall.framework.mybatis.core.repository.BaseRepositoryX;
import cn.afei.mall.framework.mybatis.core.repository.JpaSpecUtil;
import cn.afei.mall.framework.tenant.core.db.TenantSpec;
import cn.afei.mall.module.system.controller.admin.dept.vo.post.PostPageReqVO;
import cn.afei.mall.module.system.dal.dataobject.dept.PostDO;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;
import java.util.List;

/**
 * 岗位 Repository（自原 PostMapper 迁移）
 *
 * @author 芋道源码
 */
public interface PostRepository extends BaseRepositoryX<PostDO, Long> {

    default PageResult<PostDO> selectPage(PostPageReqVO reqVO) {
        return selectPage(reqVO, Specification.<PostDO>where(JpaSpecUtil.likeIfPresent("code", reqVO.getCode()))
                .and(JpaSpecUtil.likeIfPresent("name", reqVO.getName()))
                .and(JpaSpecUtil.eqIfPresent("status", reqVO.getStatus()))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()),
                Sort.by(Sort.Direction.DESC, "id"));
    }

    default PostDO selectByName(String name) {
        return selectOne(Specification.<PostDO>where(JpaSpecUtil.eq("name", name))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

    default PostDO selectByCode(String code) {
        return selectOne(Specification.<PostDO>where(JpaSpecUtil.eq("code", code))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

    default List<PostDO> selectList(Collection<Long> ids, Collection<Integer> statuses) {
        return selectList(Specification.<PostDO>where(JpaSpecUtil.inIfPresent("id", ids))
                .and(JpaSpecUtil.inIfPresent("status", statuses))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

}
