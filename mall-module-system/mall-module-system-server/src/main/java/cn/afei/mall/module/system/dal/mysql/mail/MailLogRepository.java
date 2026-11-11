package cn.afei.mall.module.system.dal.mysql.mail;

import cn.hutool.core.util.StrUtil;
import cn.afei.mall.framework.common.pojo.PageResult;
import cn.afei.mall.framework.jpa.core.query.LambdaSpecifications;
import cn.afei.mall.framework.jpa.core.repository.BaseRepositoryX;
import cn.afei.mall.module.system.controller.admin.mail.vo.log.MailLogPageReqVO;
import cn.afei.mall.module.system.dal.dataobject.mail.MailLogDO;
import org.springframework.data.jpa.domain.Specification;

public interface MailLogRepository extends BaseRepositoryX<MailLogDO, Long> {

    default PageResult<MailLogDO> selectPage(MailLogPageReqVO reqVO) {
        Specification<MailLogDO> spec = LambdaSpecifications.<MailLogDO>of()
                .eqIfPresent(MailLogDO::getUserId, reqVO.getUserId())
                .eqIfPresent(MailLogDO::getUserType, reqVO.getUserType())
                .eqIfPresent(MailLogDO::getAccountId, reqVO.getAccountId())
                .eqIfPresent(MailLogDO::getTemplateId, reqVO.getTemplateId())
                .eqIfPresent(MailLogDO::getSendStatus, reqVO.getSendStatus())
                .betweenIfPresent(MailLogDO::getSendTime, reqVO.getSendTime())
                .orderByDesc(MailLogDO::getId)
                .build();
        // 等价于原 MyBatis 的 findInSet("to_mails")：to_mails 包含该邮箱（逗号分隔列表中的精确元素）
        // 跨数据库写法：concat(',', to_mails, ',') LIKE '%,mail,%'（MySQL / PostgreSQL 通用）
        if (StrUtil.isNotBlank(reqVO.getToMail())) {
            String toMail = reqVO.getToMail();
            spec = spec.and((root, query, cb) -> cb.like(
                    cb.concat(cb.concat(cb.literal(","), root.get("toMails")), cb.literal(",")),
                    "%," + toMail + ",%"));
        }
        return selectPage(reqVO, spec);
    }

}
