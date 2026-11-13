package cn.afei.mall.module.infra.dal.jpa.file;

import cn.afei.mall.framework.common.pojo.PageResult;
import cn.afei.mall.framework.mybatis.core.repository.BaseRepositoryX;
import cn.afei.mall.framework.mybatis.core.repository.JpaSpecUtil;
import cn.afei.mall.framework.tenant.core.db.TenantSpec;
import cn.afei.mall.module.infra.controller.admin.file.vo.config.FileConfigPageReqVO;
import cn.afei.mall.module.infra.dal.dataobject.file.FileConfigDO;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * 文件配置 Repository（自原 FileConfigMapper 迁移；@TenantIgnore 全局表，tenantSpec 自动跳过）
 *
 * @author 芋道源码
 */
public interface FileConfigRepository extends BaseRepositoryX<FileConfigDO, Long> {

    default FileConfigDO selectByMaster() {
        return selectOne(Specification.<FileConfigDO>where(JpaSpecUtil.eq("master", true))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

    default PageResult<FileConfigDO> selectPage(FileConfigPageReqVO reqVO) {
        return selectPage(reqVO, Specification.<FileConfigDO>where(JpaSpecUtil.likeIfPresent("name", reqVO.getName()))
                .and(JpaSpecUtil.eqIfPresent("storage", reqVO.getStorage()))
                .and(JpaSpecUtil.betweenIfPresent("createTime", reqVO.getCreateTime()))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()),
                Sort.by(Sort.Direction.DESC, "id"));
    }


    /**
     * 将所有主配置置为非 master（原 MP update(entity, wrapper) 的平移，仅 SET master）
     */
    @Modifying
    @Query("UPDATE FileConfigDO fc SET fc.master = false WHERE fc.master = true AND fc.deleted = false")
    int updateMasterToFalse();

}
