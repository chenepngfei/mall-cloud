package cn.afei.mall.module.system.dal.mysql.mail;

import cn.afei.mall.framework.common.pojo.PageResult;
import cn.afei.mall.framework.jpa.core.query.LambdaSpecifications;
import cn.afei.mall.framework.jpa.core.repository.BaseRepositoryX;
import cn.afei.mall.module.system.controller.admin.mail.vo.account.MailAccountPageReqVO;
import cn.afei.mall.module.system.dal.dataobject.mail.MailAccountDO;

public interface MailAccountRepository extends BaseRepositoryX<MailAccountDO, Long> {

    default PageResult<MailAccountDO> selectPage(MailAccountPageReqVO pageReqVO) {
        return selectPage(pageReqVO, LambdaSpecifications.<MailAccountDO>of()
                .likeIfPresent(MailAccountDO::getMail, pageReqVO.getMail())
                .likeIfPresent(MailAccountDO::getUsername, pageReqVO.getUsername())
                .orderByDesc(MailAccountDO::getId)
                .build());
    }

}
