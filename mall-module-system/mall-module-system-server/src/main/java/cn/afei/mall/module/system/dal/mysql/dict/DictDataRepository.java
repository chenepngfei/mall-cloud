package cn.afei.mall.module.system.dal.mysql.dict;

import cn.afei.mall.framework.common.pojo.PageResult;
import cn.afei.mall.framework.jpa.core.query.LambdaSpecifications;
import cn.afei.mall.framework.jpa.core.repository.BaseRepositoryX;
import cn.afei.mall.module.system.controller.admin.dict.vo.data.DictDataPageReqVO;
import cn.afei.mall.module.system.dal.dataobject.dict.DictDataDO;

import java.util.Collection;
import java.util.List;

public interface DictDataRepository extends BaseRepositoryX<DictDataDO, Long> {

    default DictDataDO selectByDictTypeAndValue(String dictType, String value) {
        return selectOne(DictDataDO::getDictType, dictType, DictDataDO::getValue, value);
    }

    default DictDataDO selectByDictTypeAndLabel(String dictType, String label) {
        return selectOne(DictDataDO::getDictType, dictType, DictDataDO::getLabel, label);
    }

    default List<DictDataDO> selectByDictTypeAndValues(String dictType, Collection<String> values) {
        return selectList(LambdaSpecifications.<DictDataDO>of()
                .eq(DictDataDO::getDictType, dictType)
                .in(DictDataDO::getValue, values)
                .build());
    }

    default long selectCountByDictType(String dictType) {
        return selectCount(DictDataDO::getDictType, dictType);
    }

    default PageResult<DictDataDO> selectPage(DictDataPageReqVO reqVO) {
        return selectPage(reqVO, LambdaSpecifications.<DictDataDO>of()
                .likeIfPresent(DictDataDO::getLabel, reqVO.getLabel())
                .eqIfPresent(DictDataDO::getDictType, reqVO.getDictType())
                .eqIfPresent(DictDataDO::getStatus, reqVO.getStatus())
                .orderByDesc(DictDataDO::getDictType)
                .orderByDesc(DictDataDO::getSort)
                .build());
    }

    default List<DictDataDO> selectListByStatusAndDictType(Integer status, String dictType) {
        return selectList(LambdaSpecifications.<DictDataDO>of()
                .eqIfPresent(DictDataDO::getStatus, status)
                .eqIfPresent(DictDataDO::getDictType, dictType)
                .build());
    }

}
