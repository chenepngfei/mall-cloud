package cn.afei.mall.module.infra.dal.jpa.logger;

import cn.afei.mall.framework.common.pojo.PageResult;
import cn.afei.mall.framework.mybatis.core.repository.BaseRepositoryX;
import cn.afei.mall.framework.mybatis.core.repository.JpaSpecUtil;
import cn.afei.mall.framework.tenant.core.db.TenantSpec;
import cn.afei.mall.module.infra.controller.admin.logger.vo.apierrorlog.ApiErrorLogPageReqVO;
import cn.afei.mall.module.infra.dal.dataobject.logger.ApiErrorLogDO;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

/**
 * API 错误日志 Repository（自原 ApiErrorLogMapper 迁移；实体补齐 tenantId 映射，tenantSpec 生效）
 *
 * @author 芋道源码
 */
public interface ApiErrorLogRepository extends BaseRepositoryX<ApiErrorLogDO, Long> {

    default PageResult<ApiErrorLogDO> selectPage(ApiErrorLogPageReqVO reqVO) {
        return selectPage(reqVO, Specification.<ApiErrorLogDO>where(JpaSpecUtil.eqIfPresent("userId", reqVO.getUserId()))
                .and(JpaSpecUtil.eqIfPresent("userType", reqVO.getUserType()))
                .and(JpaSpecUtil.eqIfPresent("applicationName", reqVO.getApplicationName()))
                .and(JpaSpecUtil.likeIfPresent("requestUrl", reqVO.getRequestUrl()))
                .and(JpaSpecUtil.betweenIfPresent("exceptionTime", reqVO.getExceptionTime()))
                .and(JpaSpecUtil.eqIfPresent("processStatus", reqVO.getProcessStatus()))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()),
                Sort.by(Sort.Direction.DESC, "id"));
    }

    /**
     * 物理删除早于阈值的日志（分批）— 原 @Delete 原生 SQL 的 PG 版：
     * PG 的 DELETE 不支持 LIMIT，按主键子查询选出一批（与原 SqlDialectUtils 的 PG 分支一致）。
     * 仅供日志清理 Job 使用（原语义即物理删除、忽略租户）
     */
    @Modifying
    @Query(value = "DELETE FROM infra_api_error_log WHERE id IN (SELECT id FROM infra_api_error_log WHERE create_time < :createTime LIMIT :limit)", nativeQuery = true)
    int deleteBatchByLessThanCreateTime(@Param("createTime") LocalDateTime createTime, @Param("limit") Integer limit);

}
