package cn.afei.mall.module.infra.dal.jpa.logger;

import cn.afei.mall.framework.common.pojo.PageResult;
import cn.afei.mall.framework.mybatis.core.repository.BaseRepositoryX;
import cn.afei.mall.framework.mybatis.core.repository.JpaSpecUtil;
import cn.afei.mall.framework.tenant.core.db.TenantSpec;
import cn.afei.mall.module.infra.controller.admin.logger.vo.apiaccesslog.ApiAccessLogPageReqVO;
import cn.afei.mall.module.infra.dal.dataobject.logger.ApiAccessLogDO;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

/**
 * API 访问日志 Repository（自原 ApiAccessLogMapper 迁移；实体补齐 tenantId 映射，tenantSpec 生效）
 *
 * @author 芋道源码
 */
public interface ApiAccessLogRepository extends BaseRepositoryX<ApiAccessLogDO, Long> {

    default PageResult<ApiAccessLogDO> selectPage(ApiAccessLogPageReqVO reqVO) {
        return selectPage(reqVO, Specification.<ApiAccessLogDO>where(JpaSpecUtil.eqIfPresent("userId", reqVO.getUserId()))
                .and(JpaSpecUtil.eqIfPresent("userType", reqVO.getUserType()))
                .and(JpaSpecUtil.eqIfPresent("applicationName", reqVO.getApplicationName()))
                .and(JpaSpecUtil.likeIfPresent("requestUrl", reqVO.getRequestUrl()))
                .and(JpaSpecUtil.betweenIfPresent("beginTime", reqVO.getBeginTime()))
                .and(JpaSpecUtil.geIfPresent("duration", reqVO.getDuration()))
                .and(JpaSpecUtil.eqIfPresent("resultCode", reqVO.getResultCode()))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()),
                Sort.by(Sort.Direction.DESC, "id"));
    }

    /**
     * 物理删除早于阈值的日志（分批）— 原 @Delete 原生 SQL 的 PG 版（按主键子查询 LIMIT）
     */
    @Modifying
    @Query(value = "DELETE FROM infra_api_access_log WHERE id IN (SELECT id FROM infra_api_access_log WHERE create_time < :createTime LIMIT :limit)", nativeQuery = true)
    int deleteBatchByLessThanCreateTime(@Param("createTime") LocalDateTime createTime, @Param("limit") Integer limit);

}
