package cn.afei.mall.module.system.dal.jpa.notice;

import cn.afei.mall.framework.common.pojo.PageResult;
import cn.afei.mall.framework.mybatis.core.repository.BaseRepositoryX;
import cn.afei.mall.framework.mybatis.core.repository.JpaSpecUtil;
import cn.afei.mall.framework.tenant.core.db.TenantSpec;
import cn.afei.mall.module.system.controller.admin.notice.vo.NoticePageReqVO;
import cn.afei.mall.module.system.dal.dataobject.notice.NoticeDO;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

/**
 * 通知公告 Repository（自原 NoticeMapper 迁移；实体补齐 tenantId 映射，tenantSpec 生效）
 *
 * @author 芋道源码
 */
public interface NoticeRepository extends BaseRepositoryX<NoticeDO, Long> {

    default PageResult<NoticeDO> selectPage(NoticePageReqVO reqVO) {
        return selectPage(reqVO, Specification.<NoticeDO>where(JpaSpecUtil.likeIfPresent("title", reqVO.getTitle()))
                .and(JpaSpecUtil.eqIfPresent("status", reqVO.getStatus()))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()),
                Sort.by(Sort.Direction.DESC, "id"));
    }

}
