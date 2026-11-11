package cn.afei.mall.module.infra.dal.mysql.demo.demo03.inner;

import cn.afei.mall.framework.jpa.core.repository.BaseRepositoryX;
import cn.afei.mall.module.infra.dal.dataobject.demo.demo03.Demo03GradeDO;

import java.util.List;

/**
 * 学生班级 Mapper
 *
 * @author 芋道源码
 */
public interface Demo03GradeInnerRepository extends BaseRepositoryX<Demo03GradeDO, Long> {

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
