package cn.afei.mall.module.system.dal.jpa.dict;

import cn.afei.mall.framework.common.pojo.PageResult;
import cn.afei.mall.framework.mybatis.core.repository.BaseRepositoryX;
import cn.afei.mall.framework.mybatis.core.repository.JpaSpecUtil;
import cn.afei.mall.framework.tenant.core.db.TenantSpec;
import cn.afei.mall.module.system.controller.admin.dict.vo.type.DictTypePageReqVO;
import cn.afei.mall.module.system.dal.dataobject.dict.DictTypeDO;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

/**
 * 字典类型 Repository（自原 DictTypeMapper 迁移；DictTypeDO 为 @TenantIgnore 全局表，tenantSpec 自动跳过）
 *
 * @author 芋道源码
 */
public interface DictTypeRepository extends BaseRepositoryX<DictTypeDO, Long> {

    default PageResult<DictTypeDO> selectPage(DictTypePageReqVO reqVO) {
        return selectPage(reqVO, Specification.<DictTypeDO>where(JpaSpecUtil.likeIfPresent("name", reqVO.getName()))
                .and(JpaSpecUtil.likeIfPresent("type", reqVO.getType()))
                .and(JpaSpecUtil.eqIfPresent("status", reqVO.getStatus()))
                .and(JpaSpecUtil.betweenIfPresent("createTime", reqVO.getCreateTime()))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()),
                Sort.by(Sort.Direction.DESC, "id"));
    }

    default DictTypeDO selectByType(String type) {
        return selectOne(Specification.<DictTypeDO>where(JpaSpecUtil.eq("type", type))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

    default DictTypeDO selectByName(String name) {
        return selectOne(Specification.<DictTypeDO>where(JpaSpecUtil.eq("name", name))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

    /**
     * 逻辑删除并记录删除时间（原 @Update 原生 SQL 平移；deleted_time 列实体未映射，走 nativeQuery）
     */
    @Modifying
    @Query(value = "UPDATE system_dict_type SET deleted = true, deleted_time = :deletedTime WHERE id = :id AND deleted = false", nativeQuery = true)
    void updateToDelete(@Param("id") Long id, @Param("deletedTime") LocalDateTime deletedTime);

}
