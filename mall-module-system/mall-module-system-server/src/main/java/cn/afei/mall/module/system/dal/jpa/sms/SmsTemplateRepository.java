package cn.afei.mall.module.system.dal.jpa.sms;

import cn.afei.mall.framework.common.pojo.PageResult;
import cn.afei.mall.framework.mybatis.core.repository.BaseRepositoryX;
import cn.afei.mall.framework.mybatis.core.repository.JpaSpecUtil;
import cn.afei.mall.framework.tenant.core.db.TenantSpec;
import cn.afei.mall.module.system.controller.admin.sms.vo.template.SmsTemplatePageReqVO;
import cn.afei.mall.module.system.dal.dataobject.sms.SmsTemplateDO;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

/**
 * 短信模板 Repository（自原 SmsTemplateMapper 迁移；@TenantIgnore 全局表，tenantSpec 自动跳过）
 *
 * @author 芋道源码
 */
public interface SmsTemplateRepository extends BaseRepositoryX<SmsTemplateDO, Long> {

    default SmsTemplateDO selectByCode(String code) {
        return selectOne(Specification.<SmsTemplateDO>where(JpaSpecUtil.eq("code", code))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

    default PageResult<SmsTemplateDO> selectPage(SmsTemplatePageReqVO reqVO) {
        return selectPage(reqVO, Specification.<SmsTemplateDO>where(JpaSpecUtil.eqIfPresent("type", reqVO.getType()))
                .and(JpaSpecUtil.eqIfPresent("status", reqVO.getStatus()))
                .and(JpaSpecUtil.likeIfPresent("code", reqVO.getCode()))
                .and(JpaSpecUtil.likeIfPresent("content", reqVO.getContent()))
                .and(JpaSpecUtil.likeIfPresent("apiTemplateId", reqVO.getApiTemplateId()))
                .and(JpaSpecUtil.eqIfPresent("channelId", reqVO.getChannelId()))
                .and(JpaSpecUtil.betweenIfPresent("createTime", reqVO.getCreateTime()))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()),
                Sort.by(Sort.Direction.DESC, "id"));
    }

    default Long selectCountByChannelId(Long channelId) {
        return selectCount(Specification.<SmsTemplateDO>where(JpaSpecUtil.eq("channelId", channelId))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

    default List<SmsTemplateDO> selectListByStatus(Integer status) {
        return selectList(Specification.<SmsTemplateDO>where(JpaSpecUtil.eq("status", status))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

}
