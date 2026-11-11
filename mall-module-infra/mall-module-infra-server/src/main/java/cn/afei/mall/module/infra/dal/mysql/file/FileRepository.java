package cn.afei.mall.module.infra.dal.mysql.file;

import cn.afei.mall.framework.common.pojo.PageResult;
import cn.afei.mall.framework.jpa.core.repository.BaseRepositoryX;
import cn.afei.mall.framework.jpa.core.query.LambdaSpecifications;
import cn.afei.mall.module.infra.controller.admin.file.vo.file.FilePageReqVO;
import cn.afei.mall.module.infra.dal.dataobject.file.FileDO;

/**
 * 文件操作 Mapper
 *
 * @author 芋道源码
 */
public interface FileRepository extends BaseRepositoryX<FileDO, Long> {

    default PageResult<FileDO> selectPage(FilePageReqVO reqVO) {
        return selectPage(reqVO, LambdaSpecifications.<FileDO>of()
                .likeIfPresent(FileDO::getPath, reqVO.getPath())
                .likeIfPresent(FileDO::getType, reqVO.getType())
                .betweenIfPresent(FileDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(FileDO::getId).build());
    }

    default FileDO selectLatestByConfigIdAndPath(Long configId, String path) {
        return selectLastOne(LambdaSpecifications.<FileDO>of()
                .eq(FileDO::getConfigId, configId)
                .eq(FileDO::getPath, path)
                .orderByAsc(FileDO::getId).build());
    }

}
