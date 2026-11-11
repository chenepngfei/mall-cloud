package cn.afei.mall.module.infra.dal.mysql.logger;

import cn.afei.mall.framework.common.pojo.PageResult;
import cn.afei.mall.framework.jpa.core.repository.BaseRepositoryX;
import cn.afei.mall.framework.jpa.core.query.LambdaSpecifications;
import cn.afei.mall.module.infra.controller.admin.logger.vo.apiaccesslog.ApiAccessLogPageReqVO;
import cn.afei.mall.module.infra.dal.dataobject.logger.ApiAccessLogDO;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

/**
 * API 访问日志 Repository
 *
 * @author 芋道源码
 */
public interface ApiAccessLogRepository extends BaseRepositoryX<ApiAccessLogDO, Long> {

    default PageResult<ApiAccessLogDO> selectPage(ApiAccessLogPageReqVO reqVO) {
        return selectPage(reqVO, LambdaSpecifications.<ApiAccessLogDO>of()
                .eqIfPresent(ApiAccessLogDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ApiAccessLogDO::getUserType, reqVO.getUserType())
                .eqIfPresent(ApiAccessLogDO::getApplicationName, reqVO.getApplicationName())
                .likeIfPresent(ApiAccessLogDO::getRequestUrl, reqVO.getRequestUrl())
                .betweenIfPresent(ApiAccessLogDO::getBeginTime, reqVO.getBeginTime())
                .geIfPresent(ApiAccessLogDO::getDuration, reqVO.getDuration())
                .eqIfPresent(ApiAccessLogDO::getResultCode, reqVO.getResultCode())
                .orderByDesc(ApiAccessLogDO::getId).build());
    }

    /**
     * 物理删除指定时间之前的日志（MySQL 语法，保持原 SqlDialectUtils 的物理批量删除语义）
     *
     * @param createTime 最大时间
     * @param limit      删除条数，防止一次删除太多
     * @return 删除条数
     */
    @Modifying
    @Query(value = "DELETE FROM infra_api_access_log WHERE create_time < :createTime AND id IN (SELECT id FROM (SELECT id FROM infra_api_access_log WHERE create_time < :createTime LIMIT :limit) tmp)", nativeQuery = true)
    int deleteByCreateTimeLt(@Param("createTime") LocalDateTime createTime, @Param("limit") Integer limit);

}
