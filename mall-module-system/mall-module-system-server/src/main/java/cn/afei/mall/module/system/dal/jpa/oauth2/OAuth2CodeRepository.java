package cn.afei.mall.module.system.dal.jpa.oauth2;

import cn.afei.mall.framework.mybatis.core.repository.BaseRepositoryX;
import cn.afei.mall.framework.mybatis.core.repository.JpaSpecUtil;
import cn.afei.mall.framework.tenant.core.db.TenantSpec;
import cn.afei.mall.module.system.dal.dataobject.oauth2.OAuth2CodeDO;
import org.springframework.data.jpa.domain.Specification;

/**
 * OAuth2 授权码 Repository（自原 OAuth2CodeMapper 迁移；实体补齐 tenantId 映射，tenantSpec 生效）
 *
 * @author 芋道源码
 */
public interface OAuth2CodeRepository extends BaseRepositoryX<OAuth2CodeDO, Long> {

    default OAuth2CodeDO selectByCode(String code) {
        return selectOne(Specification.<OAuth2CodeDO>where(JpaSpecUtil.eq("code", code))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

}
