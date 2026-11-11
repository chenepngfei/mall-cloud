package cn.afei.mall.module.system.service.logger;

import cn.afei.mall.framework.common.pojo.PageResult;
import cn.afei.mall.framework.common.util.object.BeanUtils;
import cn.afei.mall.framework.common.biz.system.logger.dto.OperateLogCreateReqDTO;
import cn.afei.mall.module.system.api.logger.dto.OperateLogPageReqDTO;
import cn.afei.mall.module.system.controller.admin.logger.vo.operatelog.OperateLogPageReqVO;
import cn.afei.mall.module.system.dal.dataobject.logger.OperateLogDO;
import cn.afei.mall.module.system.dal.mysql.logger.OperateLogRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * 操作日志 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
@Slf4j
public class OperateLogServiceImpl implements OperateLogService {

    @Resource
    private OperateLogRepository operateLogRepository;

    @Override
    public void createOperateLog(OperateLogCreateReqDTO createReqDTO) {
        OperateLogDO log = BeanUtils.toBean(createReqDTO, OperateLogDO.class);
        operateLogRepository.insert(log);
    }

    @Override
    public OperateLogDO getOperateLog(Long id) {
        return operateLogRepository.selectById(id);
    }

    @Override
    public PageResult<OperateLogDO> getOperateLogPage(OperateLogPageReqVO pageReqVO) {
        return operateLogRepository.selectPage(pageReqVO);
    }

    @Override
    public PageResult<OperateLogDO> getOperateLogPage(OperateLogPageReqDTO pageReqDTO) {
        return operateLogRepository.selectPage(pageReqDTO);
    }

}
