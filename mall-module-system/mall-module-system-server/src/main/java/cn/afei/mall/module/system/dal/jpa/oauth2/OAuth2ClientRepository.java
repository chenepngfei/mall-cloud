package cn.afei.mall.module.system.dal.jpa.oauth2;

import cn.afei.mall.framework.common.pojo.PageResult;
import cn.afei.mall.framework.mybatis.core.repository.BaseRepositoryX;
import cn.afei.mall.framework.mybatis.core.repository.JpaSpecUtil;
import cn.afei.mall.framework.tenant.core.db.TenantSpec;
import cn.afei.mall.module.system.controller.admin.oauth2.vo.client.OAuth2ClientPageReqVO;
import cn.afei.mall.module.system.dal.dataobject.oauth2.OAuth2ClientDO;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

/**
 * OAuth2 客户端 Repository（自原 OAuth2ClientMapper 迁移；@TenantIgnore 全局表，tenantSpec 自动跳过）
 *
 * @author 芋道源码
 */
public interface OAuth2ClientRepository extends BaseRepositoryX<OAuth2ClientDO, Long> {

    default OAuth2ClientDO selectByClientId(String clientId) {
        return selectOne(Specification.<OAuth2ClientDO>where(JpaSpecUtil.eq("clientId", clientId))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

    default PageResult<OAuth2ClientDO> selectPage(OAuth2ClientPageReqVO reqVO) {
        return selectPage(reqVO, Specification.<OAuth2ClientDO>where(JpaSpecUtil.likeIfPresent("name", reqVO.getName()))
                .and(JpaSpecUtil.eqIfPresent("status", reqVO.getStatus()))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()),
                Sort.by(Sort.Direction.DESC, "id"));
    }

}
