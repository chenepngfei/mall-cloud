package cn.afei.mall.module.infra.dal.jpa.config;

import cn.afei.mall.framework.common.pojo.PageResult;
import cn.afei.mall.framework.mybatis.core.repository.BaseRepositoryX;
import cn.afei.mall.framework.mybatis.core.repository.JpaSpecUtil;
import cn.afei.mall.framework.tenant.core.db.TenantSpec;
import cn.afei.mall.module.infra.controller.admin.config.vo.ConfigPageReqVO;
import cn.afei.mall.module.infra.dal.dataobject.config.ConfigDO;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

/**
 * 参数配置 Repository（自原 ConfigMapper 迁移；@TenantIgnore 全局表，tenantSpec 自动跳过）
 *
 * @author 芋道源码
 */
public interface ConfigRepository extends BaseRepositoryX<ConfigDO, Long> {

    default ConfigDO selectByKey(String key) {
        return selectOne(Specification.<ConfigDO>where(JpaSpecUtil.eq("configKey", key))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

    default PageResult<ConfigDO> selectPage(ConfigPageReqVO reqVO) {
        return selectPage(reqVO, Specification.<ConfigDO>where(JpaSpecUtil.likeIfPresent("name", reqVO.getName()))
                .and(JpaSpecUtil.likeIfPresent("configKey", reqVO.getKey()))
                .and(JpaSpecUtil.eqIfPresent("type", reqVO.getType()))
                .and(JpaSpecUtil.betweenIfPresent("createTime", reqVO.getCreateTime()))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()),
                Sort.by(Sort.Direction.DESC, "id"));
    }

}
