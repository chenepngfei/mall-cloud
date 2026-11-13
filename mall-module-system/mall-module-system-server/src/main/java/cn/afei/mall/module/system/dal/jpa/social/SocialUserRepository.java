package cn.afei.mall.module.system.dal.jpa.social;

import cn.afei.mall.framework.common.pojo.PageResult;
import cn.afei.mall.framework.mybatis.core.repository.BaseRepositoryX;
import cn.afei.mall.framework.mybatis.core.repository.JpaSpecUtil;
import cn.afei.mall.framework.tenant.core.db.TenantSpec;
import cn.afei.mall.module.system.controller.admin.social.vo.user.SocialUserPageReqVO;
import cn.afei.mall.module.system.dal.dataobject.social.SocialUserDO;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

/**
 * 社交用户 Repository（自原 SocialUserMapper 迁移；实体补齐 tenantId 映射，tenantSpec 生效）
 *
 * @author 芋道源码
 */
public interface SocialUserRepository extends BaseRepositoryX<SocialUserDO, Long> {

    default SocialUserDO selectByTypeAndCodeAnState(Integer type, String code, String state) {
        return selectOne(Specification.<SocialUserDO>where(JpaSpecUtil.eq("type", type))
                .and(JpaSpecUtil.eq("code", code))
                .and(JpaSpecUtil.eq("state", state))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

    default SocialUserDO selectByTypeAndOpenid(Integer type, String openid) {
        return selectFirstOne(Specification.<SocialUserDO>where(JpaSpecUtil.eq("type", type))
                .and(JpaSpecUtil.eq("openid", openid))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()),
                Sort.by(Sort.Direction.ASC, "id")); // 原 selectFirstOne(field1, v1, field2, v2) 无显式排序，取自然序第一条
    }

    default PageResult<SocialUserDO> selectPage(SocialUserPageReqVO reqVO) {
        return selectPage(reqVO, Specification.<SocialUserDO>where(JpaSpecUtil.eqIfPresent("type", reqVO.getType()))
                .and(JpaSpecUtil.likeIfPresent("nickname", reqVO.getNickname()))
                .and(JpaSpecUtil.likeIfPresent("openid", reqVO.getOpenid()))
                .and(JpaSpecUtil.betweenIfPresent("createTime", reqVO.getCreateTime()))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()),
                Sort.by(Sort.Direction.DESC, "id"));
    }

}
