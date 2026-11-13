package cn.afei.mall.module.system.dal.jpa.tenant;

import cn.afei.mall.framework.common.pojo.PageResult;
import cn.afei.mall.framework.mybatis.core.repository.BaseRepositoryX;
import cn.afei.mall.framework.mybatis.core.repository.JpaSpecUtil;
import cn.afei.mall.framework.tenant.core.db.TenantSpec;
import cn.afei.mall.module.system.controller.admin.tenant.vo.tenant.TenantPageReqVO;
import cn.afei.mall.module.system.dal.dataobject.tenant.TenantDO;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

/**
 * 租户 Repository（自原 TenantMapper 迁移；TenantDO 为 @TenantIgnore 全局表，tenantSpec 自动跳过）
 *
 * @author 芋道源码
 */
public interface TenantRepository extends BaseRepositoryX<TenantDO, Long> {

    default PageResult<TenantDO> selectPage(TenantPageReqVO reqVO) {
        return selectPage(reqVO, Specification.<TenantDO>where(JpaSpecUtil.likeIfPresent("name", reqVO.getName()))
                .and(JpaSpecUtil.likeIfPresent("contactName", reqVO.getContactName()))
                .and(JpaSpecUtil.likeIfPresent("contactMobile", reqVO.getContactMobile()))
                .and(JpaSpecUtil.eqIfPresent("status", reqVO.getStatus()))
                .and(JpaSpecUtil.betweenIfPresent("createTime", reqVO.getCreateTime()))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()),
                Sort.by(Sort.Direction.DESC, "id"));
    }

    default TenantDO selectByName(String name) {
        return selectOne(Specification.<TenantDO>where(JpaSpecUtil.eq("name", name))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

    /**
     * 按站点域名精确匹配（原 FIND_IN_SET(website, websites) 的 PG 等价：strpos 元素精确匹配）
     */
    default List<TenantDO> selectListByWebsite(String website) {
        return selectList(Specification.<TenantDO>where(JpaSpecUtil.findInSet("websites", website))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

    default Long selectCountByPackageId(Long packageId) {
        return selectCount(Specification.<TenantDO>where(JpaSpecUtil.eq("packageId", packageId))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

    default List<TenantDO> selectListByPackageId(Long packageId) {
        return selectList(Specification.<TenantDO>where(JpaSpecUtil.eq("packageId", packageId))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

    default List<TenantDO> selectListByStatus(Integer status) {
        return selectList(Specification.<TenantDO>where(JpaSpecUtil.eq("status", status))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

}
