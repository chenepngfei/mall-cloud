package cn.afei.mall.module.infra.dal.mysql.demo.demo03.erp;

import cn.afei.mall.framework.common.pojo.PageParam;
import cn.afei.mall.framework.common.pojo.PageResult;
import cn.afei.mall.framework.jpa.core.repository.BaseRepositoryX;
import cn.afei.mall.framework.jpa.core.query.LambdaSpecifications;
import cn.afei.mall.module.infra.dal.dataobject.demo.demo03.Demo03GradeDO;

import java.util.List;

/**
 * 学生班级 Mapper
 *
 * @author 芋道源码
 */
public interface Demo03GradeErpRepository extends BaseRepositoryX<Demo03GradeDO, Long> {

    default PageResult<Demo03GradeDO> selectPage(PageParam reqVO, Long studentId) {
        return selectPage(reqVO, LambdaSpecifications.<Demo03GradeDO>of()
                .eq(Demo03GradeDO::getStudentId, studentId)
                .orderByDesc(Demo03GradeDO::getId).build());
    }

    default Demo03GradeDO selectByStudentId(Long studentId) {
        return selectOne(Demo03GradeDO::getStudentId, studentId);
    }

    default long deleteByStudentId(Long studentId) {
        return delete(Demo03GradeDO::getStudentId, studentId);
    }

    default long deleteByStudentIds(List<Long> studentIds) {
        return deleteBatch(Demo03GradeDO::getStudentId, studentIds);
    }

}
