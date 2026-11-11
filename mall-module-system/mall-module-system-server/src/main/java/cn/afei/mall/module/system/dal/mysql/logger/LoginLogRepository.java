package cn.afei.mall.module.system.dal.mysql.logger;

import cn.afei.mall.framework.common.pojo.PageResult;
import cn.afei.mall.framework.jpa.core.query.LambdaSpecifications;
import cn.afei.mall.framework.jpa.core.repository.BaseRepositoryX;
import cn.afei.mall.module.system.controller.admin.logger.vo.loginlog.LoginLogPageReqVO;
import cn.afei.mall.module.system.dal.dataobject.logger.LoginLogDO;
import cn.afei.mall.module.system.enums.logger.LoginResultEnum;

public interface LoginLogRepository extends BaseRepositoryX<LoginLogDO, Long> {

    default PageResult<LoginLogDO> selectPage(LoginLogPageReqVO reqVO) {
        LambdaSpecifications<LoginLogDO> query = LambdaSpecifications.<LoginLogDO>of()
                .likeIfPresent(LoginLogDO::getUserIp, reqVO.getUserIp())
                .likeIfPresent(LoginLogDO::getUsername, reqVO.getUsername())
                .betweenIfPresent(LoginLogDO::getCreateTime, reqVO.getCreateTime());
        if (Boolean.TRUE.equals(reqVO.getStatus())) {
            query.eq(LoginLogDO::getResult, LoginResultEnum.SUCCESS.getResult());
        } else if (Boolean.FALSE.equals(reqVO.getStatus())) {
            query.gtIfPresent(LoginLogDO::getResult, LoginResultEnum.SUCCESS.getResult());
        }
        query.orderByDesc(LoginLogDO::getId); // 降序
        return selectPage(reqVO, query.build());
    }

}
