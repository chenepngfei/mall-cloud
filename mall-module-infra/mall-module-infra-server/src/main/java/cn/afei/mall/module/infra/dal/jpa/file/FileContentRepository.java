package cn.afei.mall.module.infra.dal.jpa.file;

import cn.afei.mall.framework.mybatis.core.repository.BaseRepositoryX;
import cn.afei.mall.framework.mybatis.core.repository.JpaSpecUtil;
import cn.afei.mall.framework.tenant.core.db.TenantSpec;
import cn.afei.mall.module.infra.dal.dataobject.file.FileContentDO;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 文件内容 Repository（自原 FileContentMapper 迁移；@TenantIgnore 全局表，tenantSpec 自动跳过）
 *
 * @author 芋道源码
 */
public interface FileContentRepository extends BaseRepositoryX<FileContentDO, Long> {

    default List<FileContentDO> selectListByConfigIdAndPath(Long configId, String path) {
        return selectList(Specification.<FileContentDO>where(JpaSpecUtil.eq("configId", configId))
                .and(JpaSpecUtil.eq("path", path))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

    /**
     * 逻辑删除：按配置 + 路径（原 MP delete → UPDATE deleted=1）
     */
    @Modifying
    @Query("UPDATE FileContentDO fc SET fc.deleted = true WHERE fc.deleted = false AND fc.configId = :configId AND fc.path = :path")
    int deleteByConfigIdAndPath(@Param("configId") Long configId, @Param("path") String path);

}
