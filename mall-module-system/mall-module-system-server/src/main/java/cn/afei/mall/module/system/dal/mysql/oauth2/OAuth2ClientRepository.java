package cn.afei.mall.module.system.dal.mysql.oauth2;

import cn.afei.mall.framework.common.pojo.PageResult;
import cn.afei.mall.framework.jpa.core.query.LambdaSpecifications;
import cn.afei.mall.framework.jpa.core.repository.BaseRepositoryX;
import cn.afei.mall.module.system.controller.admin.oauth2.vo.client.OAuth2ClientPageReqVO;
import cn.afei.mall.module.system.dal.dataobject.oauth2.OAuth2ClientDO;


/**
 * OAuth2 客户端 Repository
 *
 * @author 芋道源码
 */
public interface OAuth2ClientRepository extends BaseRepositoryX<OAuth2ClientDO, Long> {

    default PageResult<OAuth2ClientDO> selectPage(OAuth2ClientPageReqVO reqVO) {
        return selectPage(reqVO, LambdaSpecifications.<OAuth2ClientDO>of()
                .likeIfPresent(OAuth2ClientDO::getName, reqVO.getName())
                .eqIfPresent(OAuth2ClientDO::getStatus, reqVO.getStatus())
                .orderByDesc(OAuth2ClientDO::getId)
                .build());
    }

    default OAuth2ClientDO selectByClientId(String clientId) {
        return selectOne(OAuth2ClientDO::getClientId, clientId);
    }

}
