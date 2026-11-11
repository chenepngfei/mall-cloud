package cn.afei.mall.module.infra.dal.mysql.demo.demo03.inner;

import cn.afei.mall.framework.jpa.core.repository.BaseRepositoryX;
import cn.afei.mall.module.infra.dal.dataobject.demo.demo03.Demo03CourseDO;

import java.util.List;

/**
 * 学生课程 Mapper
 *
 * @author 芋道源码
 */
public interface Demo03CourseInnerRepository extends BaseRepositoryX<Demo03CourseDO, Long> {

    default List<Demo03CourseDO> selectListByStudentId(Long studentId) {
        return selectList(Demo03CourseDO::getStudentId, studentId);
    }

    default long deleteByStudentId(Long studentId) {
        return delete(Demo03CourseDO::getStudentId, studentId);
    }

    default long deleteByStudentIds(List<Long> studentIds) {
        return deleteBatch(Demo03CourseDO::getStudentId, studentIds);
    }

}
