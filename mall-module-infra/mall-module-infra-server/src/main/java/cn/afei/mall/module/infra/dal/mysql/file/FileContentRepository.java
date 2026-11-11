package cn.afei.mall.module.infra.dal.mysql.file;

import cn.afei.mall.module.infra.dal.dataobject.file.FileContentDO;
import cn.afei.mall.framework.jpa.core.query.LambdaSpecifications;
import cn.afei.mall.framework.jpa.core.repository.BaseRepositoryX;
import java.util.List;

public interface FileContentRepository extends BaseRepositoryX<FileContentDO, Long> {

    default void deleteByConfigIdAndPath(Long configId, String path) {
        this.delete(LambdaSpecifications.<FileContentDO>of()
                .eq(FileContentDO::getConfigId, configId)
                .eq(FileContentDO::getPath, path).build());
    }

    default List<FileContentDO> selectListByConfigIdAndPath(Long configId, String path) {
        return selectList(LambdaSpecifications.<FileContentDO>of()
                .eq(FileContentDO::getConfigId, configId)
                .eq(FileContentDO::getPath, path).build());
    }

}
