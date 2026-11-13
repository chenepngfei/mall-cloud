package cn.afei.mall.module.system.dal.jpa.social;

import cn.afei.mall.framework.common.pojo.PageResult;
import cn.afei.mall.framework.mybatis.core.repository.BaseRepositoryX;
import cn.afei.mall.framework.mybatis.core.repository.JpaSpecUtil;
import cn.afei.mall.framework.tenant.core.db.TenantSpec;
import cn.afei.mall.module.system.controller.admin.social.vo.client.SocialClientPageReqVO;
import cn.afei.mall.module.system.dal.dataobject.social.SocialClientDO;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

/**
 * 社交客户端 Repository（自原 SocialClientMapper 迁移）
 *
 * @author 芋道源码
 */
public interface SocialClientRepository extends BaseRepositoryX<SocialClientDO, Long> {

    default SocialClientDO selectBySocialTypeAndUserType(Integer socialType, Integer userType) {
        return selectOne(Specification.<SocialClientDO>where(JpaSpecUtil.eq("socialType", socialType))
                .and(JpaSpecUtil.eq("userType", userType))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

    default PageResult<SocialClientDO> selectPage(SocialClientPageReqVO reqVO) {
        return selectPage(reqVO, Specification.<SocialClientDO>where(JpaSpecUtil.likeIfPresent("name", reqVO.getName()))
                .and(JpaSpecUtil.eqIfPresent("socialType", reqVO.getSocialType()))
                .and(JpaSpecUtil.eqIfPresent("userType", reqVO.getUserType()))
                .and(JpaSpecUtil.likeIfPresent("clientId", reqVO.getClientId()))
                .and(JpaSpecUtil.eqIfPresent("status", reqVO.getStatus()))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()),
                Sort.by(Sort.Direction.DESC, "id"));
    }

}
