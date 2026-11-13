package cn.afei.mall.module.system.dal.jpa.mail;

import cn.afei.mall.framework.common.pojo.PageResult;
import cn.afei.mall.framework.mybatis.core.repository.BaseRepositoryX;
import cn.afei.mall.framework.mybatis.core.repository.JpaSpecUtil;
import cn.afei.mall.framework.tenant.core.db.TenantSpec;
import cn.afei.mall.module.system.controller.admin.mail.vo.log.MailLogPageReqVO;
import cn.afei.mall.module.system.dal.dataobject.mail.MailLogDO;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

/**
 * 邮件日志 Repository（自原 MailLogMapper 迁移；@TenantIgnore 全局表，tenantSpec 自动跳过）
 *
 * @author 芋道源码
 */
public interface MailLogRepository extends BaseRepositoryX<MailLogDO, Long> {

    default PageResult<MailLogDO> selectPage(MailLogPageReqVO reqVO) {
        return selectPage(reqVO, Specification.<MailLogDO>where(JpaSpecUtil.eqIfPresent("userId", reqVO.getUserId()))
                .and(JpaSpecUtil.eqIfPresent("userType", reqVO.getUserType()))
                .and(JpaSpecUtil.eqIfPresent("accountId", reqVO.getAccountId()))
                .and(JpaSpecUtil.eqIfPresent("templateId", reqVO.getTemplateId()))
                .and(JpaSpecUtil.eqIfPresent("sendStatus", reqVO.getSendStatus()))
                .and(JpaSpecUtil.betweenIfPresent("sendTime", reqVO.getSendTime()))
                .and(JpaSpecUtil.findInSet("toMails", reqVO.getToMail())) // 原 FIND_IN_SET(to_mails) 的 PG 等价
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()),
                Sort.by(Sort.Direction.DESC, "id"));
    }

}
