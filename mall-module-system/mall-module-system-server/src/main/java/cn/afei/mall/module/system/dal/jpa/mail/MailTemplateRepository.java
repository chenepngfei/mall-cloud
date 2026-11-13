package cn.afei.mall.module.system.dal.jpa.mail;

import cn.afei.mall.framework.mybatis.core.repository.BaseRepositoryX;
import cn.afei.mall.framework.common.pojo.PageResult;
import cn.afei.mall.framework.mybatis.core.repository.JpaSpecUtil;
import cn.afei.mall.framework.tenant.core.db.TenantSpec;
import cn.afei.mall.module.system.controller.admin.mail.vo.template.MailTemplatePageReqVO;
import cn.afei.mall.module.system.dal.dataobject.mail.MailTemplateDO;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

/**
 * 邮件模板 Repository（自原 MailTemplateMapper 迁移；@TenantIgnore 全局表，tenantSpec 自动跳过）
 *
 * @author 芋道源码
 */
public interface MailTemplateRepository extends BaseRepositoryX<MailTemplateDO, Long> {

    default Long selectCountByAccountId(Long accountId) {
        return selectCount(Specification.<MailTemplateDO>where(JpaSpecUtil.eq("accountId", accountId))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

    default MailTemplateDO selectByCode(String code) {
        return selectOne(Specification.<MailTemplateDO>where(JpaSpecUtil.eq("code", code))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

    default PageResult<MailTemplateDO> selectPage(MailTemplatePageReqVO pageReqVO) {
        return selectPage(pageReqVO, Specification.<MailTemplateDO>where(JpaSpecUtil.eqIfPresent("name", pageReqVO.getName()))
                .and(JpaSpecUtil.eqIfPresent("status", pageReqVO.getStatus()))
                .and(JpaSpecUtil.likeIfPresent("code", pageReqVO.getCode()))
                .and(JpaSpecUtil.eqIfPresent("accountId", pageReqVO.getAccountId()))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()),
                Sort.by(Sort.Direction.DESC, "id"));
    }

    default List<MailTemplateDO> selectListByStatus(Integer status) {
        return selectList(Specification.<MailTemplateDO>where(JpaSpecUtil.eq("status", status))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

}
