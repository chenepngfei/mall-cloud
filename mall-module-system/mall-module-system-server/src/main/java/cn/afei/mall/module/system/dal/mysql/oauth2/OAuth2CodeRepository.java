package cn.afei.mall.module.system.dal.mysql.oauth2;

import cn.afei.mall.framework.jpa.core.repository.BaseRepositoryX;
import cn.afei.mall.module.system.dal.dataobject.oauth2.OAuth2CodeDO;

public interface OAuth2CodeRepository extends BaseRepositoryX<OAuth2CodeDO, Long> {

    default OAuth2CodeDO selectByCode(String code) {
        return selectOne(OAuth2CodeDO::getCode, code);
    }

}
