package cn.afei.mall.module.infra.dal.jpa.demo;

import cn.afei.mall.framework.common.pojo.PageParam;
import cn.afei.mall.framework.common.pojo.PageResult;
import cn.afei.mall.framework.mybatis.core.repository.BaseRepositoryX;
import cn.afei.mall.framework.mybatis.core.repository.JpaSpecUtil;
import cn.afei.mall.framework.mybatis.core.repository.JpaSpecUtil;
import cn.afei.mall.framework.tenant.core.context.TenantContextHolder;
import cn.afei.mall.framework.tenant.core.db.TenantSpec;
import cn.afei.mall.framework.common.pojo.PageParam;
import cn.afei.mall.framework.common.pojo.PageResult;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import cn.afei.mall.module.infra.dal.dataobject.demo.demo03.Demo03GradeDO;
import cn.afei.mall.framework.common.pojo.PageParam;
import cn.afei.mall.framework.common.pojo.PageResult;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 示例班级 Repository（自原 Demo03Grade 三套 Mapper 合并迁移；实体补齐 tenantId 映射）
 *
 * @author 芋道源码
 */
public interface Demo03GradeRepository extends BaseRepositoryX<Demo03GradeDO, Long> {

    /**
     * 逻辑删除：按学生编号（原 MP delete → UPDATE deleted=1，含租户条件）
     */
    @Modifying
    @Query("UPDATE Demo03GradeDO g SET g.deleted = true WHERE g.deleted = false AND g.studentId = :studentId AND g.tenantId = :tenantId")
    int deleteByStudentId(@Param("studentId") Long studentId, @Param("tenantId") Long tenantId);

    default int deleteByStudentId(Long studentId) {
        return deleteByStudentId(studentId, TenantContextHolder.getRequiredTenantId());
    }

    /**
     * 逻辑删除：按学生编号集合
     */
    @Modifying
    @Query("UPDATE Demo03GradeDO g SET g.deleted = true WHERE g.deleted = false AND g.studentId IN :studentIds AND g.tenantId = :tenantId")
    int deleteByStudentIds(@Param("studentIds") java.util.List<Long> studentIds, @Param("tenantId") Long tenantId);

    default int deleteByStudentIds(java.util.List<Long> studentIds) {
        return deleteByStudentIds(studentIds, TenantContextHolder.getRequiredTenantId());
    }

    default Demo03GradeDO selectByStudentId(Long studentId) {
        return selectOne(Specification.<Demo03GradeDO>where(JpaSpecUtil.eq("studentId", studentId))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()));
    }

    /**
     * 按学生分页查询子表（原 Erp 版 Mapper 的 selectPage(PageParam, studentId)）
     */
    default PageResult<Demo03GradeDO> selectPage(PageParam pageParam, Long studentId) {
        return selectPage(pageParam, Specification.<Demo03GradeDO>where(JpaSpecUtil.eq("studentId", studentId))
                .and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()),
                Sort.by(Sort.Direction.DESC, "id"));
    }
}