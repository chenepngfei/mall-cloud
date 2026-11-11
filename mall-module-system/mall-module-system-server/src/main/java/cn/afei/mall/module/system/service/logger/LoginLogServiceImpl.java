package cn.afei.mall.module.system.service.logger;

import cn.afei.mall.framework.common.pojo.PageResult;
import cn.afei.mall.framework.common.util.object.BeanUtils;
import cn.afei.mall.module.system.api.logger.dto.LoginLogCreateReqDTO;
import cn.afei.mall.module.system.controller.admin.logger.vo.loginlog.LoginLogPageReqVO;
import cn.afei.mall.module.system.dal.dataobject.logger.LoginLogDO;
import cn.afei.mall.module.system.dal.mysql.logger.LoginLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;

/**
 * 登录日志 Service 实现
 */
@Service
@Validated
public class LoginLogServiceImpl implements LoginLogService {

    @Resource
    private LoginLogRepository loginLogRepository;

    @Override
    public LoginLogDO getLoginLog(Long id) {
        return loginLogRepository.selectById(id);
    }

    @Override
    public PageResult<LoginLogDO> getLoginLogPage(LoginLogPageReqVO pageReqVO) {
        return loginLogRepository.selectPage(pageReqVO);
    }

    @Override
    public void createLoginLog(LoginLogCreateReqDTO reqDTO) {
        LoginLogDO loginLog = BeanUtils.toBean(reqDTO, LoginLogDO.class);
        loginLogRepository.insert(loginLog);
    }

}
