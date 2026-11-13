package cn.afei.mall.module.system.dal.jpa.notify;

import cn.afei.mall.framework.common.pojo.PageResult;
import cn.afei.mall.framework.mybatis.core.repository.BaseRepositoryX;
import cn.afei.mall.framework.mybatis.core.repository.JpaSpecUtil;
import cn.afei.mall.framework.tenant.core.db.TenantSpec;
import cn.afei.mall.module.system.controller.admin.notify.vo.template.NotifyTemplatePageReqVO;
import cn.afei.mall.module.system.dal.dataobject.notify.NotifyTemplateDO;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

/**
 * 站内信模板 Repository（自原 NotifyTemplateMapper 迁移；@TenantIgnore 全局表，tenantSpec 自动跳过）
 *
 * @author 芋道源码
 */
public interface NotifyTemplateRepository extends BaseRepositoryX<NotifyTemplateDO, Long> {

    default NotifyTemplateDO selectByCode(String code) {
        return selectOne(Specification.<NotifyTemplateDO>where(JpaSpecUtil.eq("code", code))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

    default PageResult<NotifyTemplateDO> selectPage(NotifyTemplatePageReqVO reqVO) {
        return selectPage(reqVO, Specification.<NotifyTemplateDO>where(JpaSpecUtil.likeIfPresent("code", reqVO.getCode()))
                .and(JpaSpecUtil.likeIfPresent("name", reqVO.getName()))
                .and(JpaSpecUtil.eqIfPresent("status", reqVO.getStatus()))
                .and(JpaSpecUtil.betweenIfPresent("createTime", reqVO.getCreateTime()))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()),
                Sort.by(Sort.Direction.DESC, "id"));
    }

    default List<NotifyTemplateDO> selectListByStatus(Integer status) {
        return selectList(Specification.<NotifyTemplateDO>where(JpaSpecUtil.eq("status", status))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

}
