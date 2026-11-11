package cn.afei.mall.module.system.dal.mysql.tenant;

import cn.afei.mall.framework.common.pojo.PageResult;
import cn.afei.mall.framework.jpa.core.query.LambdaSpecifications;
import cn.afei.mall.framework.jpa.core.repository.BaseRepositoryX;
import cn.afei.mall.module.system.controller.admin.tenant.vo.tenant.TenantPageReqVO;
import cn.afei.mall.module.system.dal.dataobject.tenant.TenantDO;

import java.util.List;

public interface TenantRepository extends BaseRepositoryX<TenantDO, Long> {

    default PageResult<TenantDO> selectPage(TenantPageReqVO reqVO) {
        return selectPage(reqVO, LambdaSpecifications.<TenantDO>of()
                .likeIfPresent(TenantDO::getName, reqVO.getName())
                .likeIfPresent(TenantDO::getContactName, reqVO.getContactName())
                .likeIfPresent(TenantDO::getContactMobile, reqVO.getContactMobile())
                .eqIfPresent(TenantDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(TenantDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(TenantDO::getId)
                .build());
    }

    default TenantDO selectByName(String name) {
        return selectOne(TenantDO::getName, name);
    }

    /**
     * 查询绑定了指定域名的租户列表
     *
     * 等价于原 MyBatis Plus 的 findInSet 写法，websites 字段是以逗号分隔的域名列表；
     * 跨数据库写法：concat(',', websites, ',') LIKE '%,website,%'（MySQL / PostgreSQL 通用）
     */
    default List<TenantDO> selectListByWebsite(String website) {
        return selectList((root, query, cb) -> cb.like(
                cb.concat(cb.concat(cb.literal(","), root.get("websites")), cb.literal(",")),
                "%," + website + ",%"));
    }

    default Long selectCountByPackageId(Long packageId) {
        return selectCount(TenantDO::getPackageId, packageId);
    }

    default List<TenantDO> selectListByPackageId(Long packageId) {
        return selectList(TenantDO::getPackageId, packageId);
    }

    default List<TenantDO> selectListByStatus(Integer status) {
        return selectList(TenantDO::getStatus, status);
    }

}
