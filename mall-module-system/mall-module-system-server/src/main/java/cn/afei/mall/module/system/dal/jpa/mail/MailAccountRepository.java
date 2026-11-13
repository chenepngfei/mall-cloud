package cn.afei.mall.module.system.dal.jpa.mail;

import cn.afei.mall.framework.common.pojo.PageResult;
import cn.afei.mall.framework.mybatis.core.repository.BaseRepositoryX;
import cn.afei.mall.framework.mybatis.core.repository.JpaSpecUtil;
import cn.afei.mall.framework.tenant.core.db.TenantSpec;
import cn.afei.mall.module.system.controller.admin.mail.vo.account.MailAccountPageReqVO;
import cn.afei.mall.module.system.dal.dataobject.mail.MailAccountDO;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

/**
 * 邮箱账号 Repository（自原 MailAccountMapper 迁移；@TenantIgnore 全局表，tenantSpec 自动跳过）
 *
 * @author 芋道源码
 */
public interface MailAccountRepository extends BaseRepositoryX<MailAccountDO, Long> {

    default PageResult<MailAccountDO> selectPage(MailAccountPageReqVO pageReqVO) {
        return selectPage(pageReqVO, Specification.<MailAccountDO>where(JpaSpecUtil.likeIfPresent("mail", pageReqVO.getMail()))
                .and(JpaSpecUtil.likeIfPresent("username", pageReqVO.getUsername()))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()),
                Sort.by(Sort.Direction.DESC, "id"));
    }

}
