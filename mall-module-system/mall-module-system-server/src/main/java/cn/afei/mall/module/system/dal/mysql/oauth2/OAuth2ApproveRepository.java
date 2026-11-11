package cn.afei.mall.module.system.dal.mysql.oauth2;

import cn.afei.mall.framework.jpa.core.query.LambdaSpecifications;
import cn.afei.mall.framework.jpa.core.repository.BaseRepositoryX;
import cn.afei.mall.module.system.dal.dataobject.oauth2.OAuth2ApproveDO;

import java.util.List;

public interface OAuth2ApproveRepository extends BaseRepositoryX<OAuth2ApproveDO, Long> {

    /**
     * 按（用户 + 用户类型 + 客户端 + 授权范围）更新批准信息
     *
     * 对齐原 MyBatis Plus 的 update(entity, wrapper)：查询满足条件的记录，更新对应的字段
     *
     * @param updateObj 携带待更新字段的实体
     * @return 更新的记录数
     */
    default int update(OAuth2ApproveDO updateObj) {
        List<OAuth2ApproveDO> approveDOs = selectList(LambdaSpecifications.<OAuth2ApproveDO>of()
                .eq(OAuth2ApproveDO::getUserId, updateObj.getUserId())
                .eq(OAuth2ApproveDO::getUserType, updateObj.getUserType())
                .eq(OAuth2ApproveDO::getClientId, updateObj.getClientId())
                .eq(OAuth2ApproveDO::getScope, updateObj.getScope())
                .build());
        approveDOs.forEach(approveDO -> {
            approveDO.setApproved(updateObj.getApproved());
            approveDO.setExpiresTime(updateObj.getExpiresTime());
        });
        insertBatch(approveDOs);
        return approveDOs.size();
    }

    default List<OAuth2ApproveDO> selectListByUserIdAndUserTypeAndClientId(Long userId, Integer userType, String clientId) {
        return selectList(LambdaSpecifications.<OAuth2ApproveDO>of()
                .eq(OAuth2ApproveDO::getUserId, userId)
                .eq(OAuth2ApproveDO::getUserType, userType)
                .eq(OAuth2ApproveDO::getClientId, clientId)
                .build());
    }

}
