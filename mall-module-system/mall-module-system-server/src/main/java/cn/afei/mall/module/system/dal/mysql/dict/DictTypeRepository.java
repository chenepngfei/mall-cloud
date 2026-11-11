package cn.afei.mall.module.system.dal.mysql.dict;

import cn.afei.mall.framework.common.pojo.PageResult;
import cn.afei.mall.framework.jpa.core.query.LambdaSpecifications;
import cn.afei.mall.framework.jpa.core.repository.BaseRepositoryX;
import cn.afei.mall.module.system.controller.admin.dict.vo.type.DictTypePageReqVO;
import cn.afei.mall.module.system.dal.dataobject.dict.DictTypeDO;

import java.time.LocalDateTime;

public interface DictTypeRepository extends BaseRepositoryX<DictTypeDO, Long> {

    default PageResult<DictTypeDO> selectPage(DictTypePageReqVO reqVO) {
        return selectPage(reqVO, LambdaSpecifications.<DictTypeDO>of()
                .likeIfPresent(DictTypeDO::getName, reqVO.getName())
                .likeIfPresent(DictTypeDO::getType, reqVO.getType())
                .eqIfPresent(DictTypeDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(DictTypeDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(DictTypeDO::getId)
                .build());
    }

    default DictTypeDO selectByType(String type) {
        return selectOne(DictTypeDO::getType, type);
    }

    default DictTypeDO selectByName(String name) {
        return selectOne(DictTypeDO::getName, name);
    }

    /**
     * 标记删除字典类型：等价于原 MyBatis 的
     * {@code UPDATE system_dict_type SET deleted = 1, deleted_time = #{deletedTime} WHERE id = #{id}}
     */
    default void updateToDelete(Long id, LocalDateTime deletedTime) {
        DictTypeDO dictType = selectById(id);
        if (dictType == null) {
            return;
        }
        dictType.setDeleted(true);
        dictType.setDeletedTime(deletedTime);
        save(dictType);
    }

}
