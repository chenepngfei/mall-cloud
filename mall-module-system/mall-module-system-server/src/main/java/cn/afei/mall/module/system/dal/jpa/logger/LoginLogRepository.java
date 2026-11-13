package cn.afei.mall.module.system.dal.jpa.logger;

import cn.afei.mall.framework.common.pojo.PageResult;
import cn.afei.mall.framework.mybatis.core.repository.BaseRepositoryX;
import cn.afei.mall.framework.mybatis.core.repository.JpaSpecUtil;
import cn.afei.mall.framework.tenant.core.db.TenantSpec;
import cn.afei.mall.module.system.controller.admin.logger.vo.loginlog.LoginLogPageReqVO;
import cn.afei.mall.module.system.dal.dataobject.logger.LoginLogDO;
import cn.afei.mall.module.system.enums.logger.LoginResultEnum;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

/**
 * 登录日志 Repository（自原 LoginLogMapper 迁移；实体补齐 tenantId 映射，tenantSpec 生效）
 *
 * @author 芋道源码
 */
public interface LoginLogRepository extends BaseRepositoryX<LoginLogDO, Long> {

    default PageResult<LoginLogDO> selectPage(LoginLogPageReqVO reqVO) {
        Specification<LoginLogDO> spec = Specification.<LoginLogDO>where(JpaSpecUtil.likeIfPresent("userIp", reqVO.getUserIp()))
                .and(JpaSpecUtil.likeIfPresent("username", reqVO.getUsername()))
                .and(JpaSpecUtil.betweenIfPresent("createTime", reqVO.getCreateTime()));
        // 状态筛选：TRUE 成功（result = 0）、FALSE 失败（result > 0），与原 MP 条件分支一致
        if (Boolean.TRUE.equals(reqVO.getStatus())) {
            spec = spec.and(JpaSpecUtil.eq("result", LoginResultEnum.SUCCESS.getResult()));
        } else if (Boolean.FALSE.equals(reqVO.getStatus())) {
            spec = spec.and((root, query, cb) -> cb.greaterThan(root.<Integer>get("result"), LoginResultEnum.SUCCESS.getResult()));
        }
        return selectPage(reqVO, spec.and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()),
                Sort.by(Sort.Direction.DESC, "id"));
    }

}
