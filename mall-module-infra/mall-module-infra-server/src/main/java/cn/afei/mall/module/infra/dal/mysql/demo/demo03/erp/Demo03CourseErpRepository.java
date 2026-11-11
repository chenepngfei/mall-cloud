package cn.afei.mall.module.infra.dal.mysql.demo.demo03.erp;

import cn.afei.mall.framework.common.pojo.PageParam;
import cn.afei.mall.framework.common.pojo.PageResult;
import cn.afei.mall.framework.jpa.core.repository.BaseRepositoryX;
import cn.afei.mall.framework.jpa.core.query.LambdaSpecifications;
import cn.afei.mall.module.infra.dal.dataobject.demo.demo03.Demo03CourseDO;

import java.util.List;

/**
 * 学生课程 Mapper
 *
 * @author 芋道源码
 */
public interface Demo03CourseErpRepository extends BaseRepositoryX<Demo03CourseDO, Long> {

    default PageResult<Demo03CourseDO> selectPage(PageParam reqVO, Long studentId) {
        return selectPage(reqVO, LambdaSpecifications.<Demo03CourseDO>of()
                .eq(Demo03CourseDO::getStudentId, studentId)
                .orderByDesc(Demo03CourseDO::getId).build());
    }

    default long deleteByStudentId(Long studentId) {
        return delete(Demo03CourseDO::getStudentId, studentId);
    }

    default long deleteByStudentIds(List<Long> studentIds) {
        return deleteBatch(Demo03CourseDO::getStudentId, studentIds);
    }

}
