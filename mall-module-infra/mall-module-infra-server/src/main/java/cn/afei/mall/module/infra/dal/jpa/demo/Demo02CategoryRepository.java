package cn.afei.mall.module.infra.dal.jpa.demo;

import cn.afei.mall.framework.mybatis.core.repository.BaseRepositoryX;
import cn.afei.mall.framework.mybatis.core.repository.JpaSpecUtil;
import cn.afei.mall.framework.tenant.core.db.TenantSpec;
import cn.afei.mall.module.infra.controller.admin.demo.demo02.vo.Demo02CategoryListReqVO;
import cn.afei.mall.module.infra.dal.dataobject.demo.demo02.Demo02CategoryDO;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

/**
 * 示例分类 Repository（自原 Demo02CategoryMapper 迁移；实体补齐 tenantId 映射，tenantSpec 生效）
 *
 * @author 芋道源码
 */
public interface Demo02CategoryRepository extends BaseRepositoryX<Demo02CategoryDO, Long> {

    default List<Demo02CategoryDO> selectList(Demo02CategoryListReqVO reqVO) {
        return selectList(Specification.<Demo02CategoryDO>where(JpaSpecUtil.likeIfPresent("name", reqVO.getName()))
                .and(JpaSpecUtil.eqIfPresent("parentId", reqVO.getParentId()))
                .and(JpaSpecUtil.betweenIfPresent("createTime", reqVO.getCreateTime()))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()),
                Sort.by(Sort.Direction.DESC, "id"));
    }

    default Demo02CategoryDO selectByParentIdAndName(Long parentId, String name) {
        return selectOne(Specification.<Demo02CategoryDO>where(JpaSpecUtil.eq("parentId", parentId))
                .and(JpaSpecUtil.eq("name", name))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

    default Long selectCountByParentId(Long parentId) {
        return selectCount(Specification.<Demo02CategoryDO>where(JpaSpecUtil.eq("parentId", parentId))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

}
