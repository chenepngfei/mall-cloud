package cn.afei.mall.module.system.dal.jpa.permission;

import cn.afei.mall.framework.mybatis.core.repository.BaseRepositoryX;
import cn.afei.mall.framework.mybatis.core.repository.JpaSpecUtil;
import cn.afei.mall.framework.tenant.core.db.TenantSpec;
import cn.afei.mall.module.system.controller.admin.permission.vo.menu.MenuListReqVO;
import cn.afei.mall.module.system.dal.dataobject.permission.MenuDO;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

/**
 * 菜单 Repository（自原 MenuMapper 迁移；MenuDO 为 @TenantIgnore 全局表，tenantSpec 自动跳过）
 *
 * @author 芋道源码
 */
public interface MenuRepository extends BaseRepositoryX<MenuDO, Long> {

    default MenuDO selectByParentIdAndName(Long parentId, String name) {
        return selectOne(Specification.<MenuDO>where(JpaSpecUtil.eq("parentId", parentId))
                .and(JpaSpecUtil.eq("name", name))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

    default Long selectCountByParentId(Long parentId) {
        return selectCount(Specification.<MenuDO>where(JpaSpecUtil.eq("parentId", parentId))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

    default List<MenuDO> selectList(MenuListReqVO reqVO) {
        return selectList(Specification.<MenuDO>where(JpaSpecUtil.likeIfPresent("name", reqVO.getName()))
                .and(JpaSpecUtil.eqIfPresent("status", reqVO.getStatus()))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

    default List<MenuDO> selectListByPermission(String permission) {
        return selectList(Specification.<MenuDO>where(JpaSpecUtil.eq("permission", permission))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

    default MenuDO selectByComponentName(String componentName) {
        return selectOne(Specification.<MenuDO>where(JpaSpecUtil.eq("componentName", componentName))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

}
