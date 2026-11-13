package cn.afei.mall.module.infra.dal.jpa.file;

import cn.afei.mall.framework.common.pojo.PageResult;
import cn.afei.mall.framework.mybatis.core.repository.BaseRepositoryX;
import cn.afei.mall.framework.mybatis.core.repository.JpaSpecUtil;
import cn.afei.mall.framework.tenant.core.db.TenantSpec;
import cn.afei.mall.module.infra.controller.admin.file.vo.file.FilePageReqVO;
import cn.afei.mall.module.infra.dal.dataobject.file.FileDO;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

/**
 * 文件 Repository（自原 FileMapper 迁移；@TenantIgnore 全局表，tenantSpec 自动跳过）
 *
 * @author 芋道源码
 */
public interface FileRepository extends BaseRepositoryX<FileDO, Long> {

    default PageResult<FileDO> selectPage(FilePageReqVO reqVO) {
        return selectPage(reqVO, Specification.<FileDO>where(JpaSpecUtil.likeIfPresent("path", reqVO.getPath()))
                .and(JpaSpecUtil.likeIfPresent("type", reqVO.getType()))
                .and(JpaSpecUtil.betweenIfPresent("createTime", reqVO.getCreateTime()))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()),
                Sort.by(Sort.Direction.DESC, "id"));
    }

    /**
     * 取同配置同路径的最新一条（原 selectLastOne + orderByAsc(id)，等价反转为 id 降序取第一条）
     */
    default FileDO selectLatestByConfigIdAndPath(Long configId, String path) {
        return selectFirstOne(Specification.<FileDO>where(JpaSpecUtil.eq("configId", configId))
                .and(JpaSpecUtil.eq("path", path))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()),
                Sort.by(Sort.Direction.DESC, "id"));
    }

}
