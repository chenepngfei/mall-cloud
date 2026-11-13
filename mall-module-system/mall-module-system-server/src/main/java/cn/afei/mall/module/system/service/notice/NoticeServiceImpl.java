package cn.afei.mall.module.system.service.notice;

import cn.afei.mall.framework.common.pojo.PageResult;
import cn.afei.mall.framework.common.util.object.BeanUtils;
import cn.afei.mall.module.system.controller.admin.notice.vo.NoticePageReqVO;
import cn.afei.mall.module.system.controller.admin.notice.vo.NoticeSaveReqVO;
import cn.afei.mall.module.system.dal.dataobject.notice.NoticeDO;
import cn.afei.mall.module.system.dal.jpa.notice.NoticeRepository;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.afei.mall.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.afei.mall.module.system.enums.ErrorCodeConstants.NOTICE_NOT_FOUND;

/**
 * 通知公告 Service 实现类
 *
 * @author 芋道源码
 */
@Service
public class NoticeServiceImpl implements NoticeService {

    @Resource
    private NoticeRepository noticeRepository;

    @Override
    public Long createNotice(NoticeSaveReqVO createReqVO) {
        NoticeDO notice = BeanUtils.toBean(createReqVO, NoticeDO.class);
        noticeRepository.save(notice);
        return notice.getId();
    }

    @Override
    public void updateNotice(NoticeSaveReqVO updateReqVO) {
        // 校验是否存在
        validateNoticeExists(updateReqVO.getId());
        // 更新通知公告
        NoticeDO updateObj = BeanUtils.toBean(updateReqVO, NoticeDO.class);
        noticeRepository.updateByIdSelective(updateObj);
    }

    @Override
    public void deleteNotice(Long id) {
        // 校验是否存在
        validateNoticeExists(id);
        // 删除通知公告
        noticeRepository.deleteByIdLogically(id);
    }

    @Override
    public void deleteNoticeList(List<Long> ids) {
        noticeRepository.deleteBatchIdsLogically(ids);
    }

    @Override
    public PageResult<NoticeDO> getNoticePage(NoticePageReqVO reqVO) {
        return noticeRepository.selectPage(reqVO);
    }

    @Override
    public NoticeDO getNotice(Long id) {
        return noticeRepository.selectById(id);
    }

    @VisibleForTesting
    public void validateNoticeExists(Long id) {
        if (id == null) {
            return;
        }
        NoticeDO notice = noticeRepository.selectById(id);
        if (notice == null) {
            throw exception(NOTICE_NOT_FOUND);
        }
    }

}
