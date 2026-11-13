package cn.afei.mall.module.system.dal.jpa.dict;

import cn.afei.mall.framework.common.pojo.PageResult;
import cn.afei.mall.framework.mybatis.core.repository.BaseRepositoryX;
import cn.afei.mall.framework.mybatis.core.repository.JpaSpecUtil;
import cn.afei.mall.framework.tenant.core.db.TenantSpec;
import cn.afei.mall.module.system.controller.admin.dict.vo.data.DictDataPageReqVO;
import cn.afei.mall.module.system.dal.dataobject.dict.DictDataDO;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;
import java.util.List;

/**
 * 字典数据 Repository（自原 DictDataMapper 迁移；DictDataDO 为 @TenantIgnore 全局表，tenantSpec 自动跳过）
 *
 * @author 芋道源码
 */
public interface DictDataRepository extends BaseRepositoryX<DictDataDO, Long> {

    default DictDataDO selectByDictTypeAndValue(String dictType, String value) {
        return selectOne(Specification.<DictDataDO>where(JpaSpecUtil.eq("dictType", dictType))
                .and(JpaSpecUtil.eq("value", value))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

    default DictDataDO selectByDictTypeAndLabel(String dictType, String label) {
        return selectOne(Specification.<DictDataDO>where(JpaSpecUtil.eq("dictType", dictType))
                .and(JpaSpecUtil.eq("label", label))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

    default List<DictDataDO> selectByDictTypeAndValues(String dictType, Collection<String> values) {
        return selectList(Specification.<DictDataDO>where(JpaSpecUtil.eq("dictType", dictType))
                .and(JpaSpecUtil.in("value", values))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

    default long selectCountByDictType(String dictType) {
        return selectCount(Specification.<DictDataDO>where(JpaSpecUtil.eq("dictType", dictType))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

    default List<DictDataDO> selectListByStatusAndDictType(Integer status, String dictType) {
        return selectList(Specification.<DictDataDO>where(JpaSpecUtil.eqIfPresent("status", status))
                .and(JpaSpecUtil.eqIfPresent("dictType", dictType))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

    default PageResult<DictDataDO> selectPage(DictDataPageReqVO reqVO) {
        return selectPage(reqVO, Specification.<DictDataDO>where(JpaSpecUtil.likeIfPresent("label", reqVO.getLabel()))
                .and(JpaSpecUtil.eqIfPresent("dictType", reqVO.getDictType()))
                .and(JpaSpecUtil.eqIfPresent("status", reqVO.getStatus()))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()),
                Sort.by(Sort.Direction.DESC, "dictType", "sort"));
    }

}
