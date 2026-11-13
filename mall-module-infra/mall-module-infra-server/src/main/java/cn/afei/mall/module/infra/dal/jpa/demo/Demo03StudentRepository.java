package cn.afei.mall.module.infra.dal.jpa.demo;

import cn.afei.mall.framework.common.pojo.PageResult;
import cn.afei.mall.framework.mybatis.core.repository.BaseRepositoryX;
import cn.afei.mall.framework.mybatis.core.repository.JpaSpecUtil;
import cn.afei.mall.framework.tenant.core.db.TenantSpec;
import cn.afei.mall.module.infra.controller.admin.demo.demo03.erp.vo.Demo03StudentErpPageReqVO;
import cn.afei.mall.module.infra.controller.admin.demo.demo03.inner.vo.Demo03StudentInnerPageReqVO;
import cn.afei.mall.module.infra.controller.admin.demo.demo03.normal.vo.Demo03StudentNormalPageReqVO;
import cn.afei.mall.module.infra.dal.dataobject.demo.demo03.Demo03StudentDO;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

/**
 * 示例学生 Repository（自原 Demo03Student 三套 Mapper（erp/inner/normal）合并迁移；
 * 三套 PageReqVO 查询条件一致，共享同一实现；实体补齐 tenantId 映射，tenantSpec 生效）
 *
 * @author 芋道源码
 */
public interface Demo03StudentRepository extends BaseRepositoryX<Demo03StudentDO, Long> {

    default PageResult<Demo03StudentDO> selectPage(Demo03StudentErpPageReqVO reqVO) {
        return selectPage(reqVO, baseSpec(reqVO.getName(), reqVO.getSex(), reqVO.getDescription(), reqVO.getCreateTime()),
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "id"));
    }

    default PageResult<Demo03StudentDO> selectPage(Demo03StudentInnerPageReqVO reqVO) {
        return selectPage(reqVO, baseSpec(reqVO.getName(), reqVO.getSex(), reqVO.getDescription(), reqVO.getCreateTime()),
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "id"));
    }

    default PageResult<Demo03StudentDO> selectPage(Demo03StudentNormalPageReqVO reqVO) {
        return selectPage(reqVO, baseSpec(reqVO.getName(), reqVO.getSex(), reqVO.getDescription(), reqVO.getCreateTime()),
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "id"));
    }

    private Specification<Demo03StudentDO> baseSpec(String name, Integer sex, String description, java.time.LocalDateTime[] createTime) {
        return Specification.<Demo03StudentDO>where(JpaSpecUtil.likeIfPresent("name", name))
                .and(JpaSpecUtil.eqIfPresent("sex", sex))
                .and(JpaSpecUtil.eqIfPresent("description", description))
                .and(JpaSpecUtil.betweenIfPresent("createTime", createTime))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec());
    }

}
