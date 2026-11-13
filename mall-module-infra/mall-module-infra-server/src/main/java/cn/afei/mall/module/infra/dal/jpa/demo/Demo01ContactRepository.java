package cn.afei.mall.module.infra.dal.jpa.demo;

import cn.afei.mall.framework.common.pojo.PageResult;
import cn.afei.mall.framework.mybatis.core.repository.BaseRepositoryX;
import cn.afei.mall.framework.mybatis.core.repository.JpaSpecUtil;
import cn.afei.mall.framework.tenant.core.db.TenantSpec;
import cn.afei.mall.module.infra.controller.admin.demo.demo01.vo.Demo01ContactPageReqVO;
import cn.afei.mall.module.infra.dal.dataobject.demo.demo01.Demo01ContactDO;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

/**
 * 示例联系人 Repository（自原 Demo01ContactMapper 迁移；实体补齐 tenantId 映射，tenantSpec 生效）
 *
 * @author 芋道源码
 */
public interface Demo01ContactRepository extends BaseRepositoryX<Demo01ContactDO, Long> {

    default PageResult<Demo01ContactDO> selectPage(Demo01ContactPageReqVO reqVO) {
        return selectPage(reqVO, Specification.<Demo01ContactDO>where(JpaSpecUtil.likeIfPresent("name", reqVO.getName()))
                .and(JpaSpecUtil.eqIfPresent("sex", reqVO.getSex()))
                .and(JpaSpecUtil.betweenIfPresent("createTime", reqVO.getCreateTime()))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()),
                Sort.by(Sort.Direction.DESC, "id"));
    }

}
