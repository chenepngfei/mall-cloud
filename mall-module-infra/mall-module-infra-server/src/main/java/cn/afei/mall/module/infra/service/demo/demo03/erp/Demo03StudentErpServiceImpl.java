package cn.afei.mall.module.infra.service.demo.demo03.erp;

import cn.hutool.core.collection.CollUtil;
import cn.afei.mall.framework.common.pojo.PageParam;
import cn.afei.mall.framework.common.pojo.PageResult;
import cn.afei.mall.framework.common.util.object.BeanUtils;
import cn.afei.mall.module.infra.controller.admin.demo.demo03.erp.vo.Demo03StudentErpPageReqVO;
import cn.afei.mall.module.infra.controller.admin.demo.demo03.erp.vo.Demo03StudentErpSaveReqVO;
import cn.afei.mall.module.infra.dal.dataobject.demo.demo03.Demo03CourseDO;
import cn.afei.mall.module.infra.dal.dataobject.demo.demo03.Demo03GradeDO;
import cn.afei.mall.module.infra.dal.dataobject.demo.demo03.Demo03StudentDO;
import cn.afei.mall.module.infra.dal.jpa.demo.Demo03CourseRepository;
import cn.afei.mall.module.infra.dal.jpa.demo.Demo03GradeRepository;
import cn.afei.mall.module.infra.dal.jpa.demo.Demo03StudentRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.afei.mall.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.afei.mall.module.infra.enums.ErrorCodeConstants.*;

/**
 * 学生 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class Demo03StudentErpServiceImpl implements Demo03StudentErpService {

    @Resource
    private Demo03StudentRepository demo03StudentErpRepository;
    @Resource
    private Demo03CourseRepository demo03CourseErpRepository;
    @Resource
    private Demo03GradeRepository demo03GradeErpRepository;

    @Override
    public Long createDemo03Student(Demo03StudentErpSaveReqVO createReqVO) {
        // 插入
        Demo03StudentDO demo03Student = BeanUtils.toBean(createReqVO, Demo03StudentDO.class);
        demo03StudentErpRepository.save(demo03Student);
        // 返回
        return demo03Student.getId();
    }

    @Override
    public void updateDemo03Student(Demo03StudentErpSaveReqVO updateReqVO) {
        // 校验存在
        validateDemo03StudentExists(updateReqVO.getId());
        // 更新
        Demo03StudentDO updateObj = BeanUtils.toBean(updateReqVO, Demo03StudentDO.class);
        demo03StudentErpRepository.updateByIdSelective(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDemo03Student(Long id) {
        // 校验存在
        validateDemo03StudentExists(id);
        // 删除
        demo03StudentErpRepository.deleteByIdLogically(id);

        // 删除子表
        deleteDemo03CourseByStudentId(id);
        deleteDemo03GradeByStudentId(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDemo03StudentList(List<Long> ids) {
        // 校验存在
        validateDemo03StudentExists(ids);
        // 删除
        demo03StudentErpRepository.deleteBatchIdsLogically(ids);

        // 删除子表
        deleteDemo03CourseByStudentIds(ids);
        deleteDemo03GradeByStudentIds(ids);
    }

    private void validateDemo03StudentExists(List<Long> ids) {
        List<Demo03StudentDO> list = demo03StudentErpRepository.selectByIds(ids);
        if (CollUtil.isEmpty(list) || list.size() != ids.size()) {
            throw exception(DEMO03_STUDENT_NOT_EXISTS);
        }
    }

    private void validateDemo03StudentExists(Long id) {
        if (demo03StudentErpRepository.selectById(id) == null) {
            throw exception(DEMO03_STUDENT_NOT_EXISTS);
        }
    }

    @Override
    public Demo03StudentDO getDemo03Student(Long id) {
        return demo03StudentErpRepository.selectById(id);
    }

    @Override
    public PageResult<Demo03StudentDO> getDemo03StudentPage(Demo03StudentErpPageReqVO pageReqVO) {
        return demo03StudentErpRepository.selectPage(pageReqVO);
    }

    // ==================== 子表（学生课程） ====================

    @Override
    public PageResult<Demo03CourseDO> getDemo03CoursePage(PageParam pageReqVO, Long studentId) {
        return demo03CourseErpRepository.selectPage(pageReqVO, studentId);
    }

    @Override
    public Long createDemo03Course(Demo03CourseDO demo03Course) {
        demo03CourseErpRepository.save(demo03Course);
        return demo03Course.getId();
    }

    @Override
    public void updateDemo03Course(Demo03CourseDO demo03Course) {
        // 校验存在
        validateDemo03CourseExists(demo03Course.getId());
        // 更新
        demo03Course.clean();
        demo03CourseErpRepository.updateByIdSelective(demo03Course);
    }

    @Override
    public void deleteDemo03Course(Long id) {
        // 删除
        demo03CourseErpRepository.deleteByIdLogically(id);
    }

    @Override
    public void deleteDemo03CourseList(List<Long> ids) {
        // 删除
        demo03CourseErpRepository.deleteBatchIdsLogically(ids);
    }

    @Override
    public Demo03CourseDO getDemo03Course(Long id) {
        return demo03CourseErpRepository.selectById(id);
    }

    private void validateDemo03CourseExists(Long id) {
        if (demo03CourseErpRepository.selectById(id) == null) {
            throw exception(DEMO03_COURSE_NOT_EXISTS);
        }
    }

    private void deleteDemo03CourseByStudentId(Long studentId) {
        demo03CourseErpRepository.deleteByStudentId(studentId);
    }

    private void deleteDemo03CourseByStudentIds(List<Long> studentIds) {
        demo03CourseErpRepository.deleteByStudentIds(studentIds);
    }

    // ==================== 子表（学生班级） ====================

    @Override
    public PageResult<Demo03GradeDO> getDemo03GradePage(PageParam pageReqVO, Long studentId) {
        return demo03GradeErpRepository.selectPage(pageReqVO, studentId);
    }

    @Override
    public Long createDemo03Grade(Demo03GradeDO demo03Grade) {
        // 校验是否已经存在
        if (demo03GradeErpRepository.selectByStudentId(demo03Grade.getStudentId()) != null) {
            throw exception(DEMO03_GRADE_EXISTS);
        }
        // 插入
        demo03GradeErpRepository.save(demo03Grade);
        return demo03Grade.getId();
    }

    @Override
    public void updateDemo03Grade(Demo03GradeDO demo03Grade) {
        // 校验存在
        validateDemo03GradeExists(demo03Grade.getId());
        // 更新
        demo03Grade.clean();
        demo03GradeErpRepository.updateByIdSelective(demo03Grade);
    }

    @Override
    public void deleteDemo03Grade(Long id) {
        // 删除
        demo03GradeErpRepository.deleteByIdLogically(id);
    }

    @Override
    public void deleteDemo03GradeList(List<Long> ids) {
        // 删除
        demo03GradeErpRepository.deleteBatchIdsLogically(ids);
    }

    @Override
    public Demo03GradeDO getDemo03Grade(Long id) {
        return demo03GradeErpRepository.selectById(id);
    }

    private void validateDemo03GradeExists(Long id) {
        if (demo03GradeErpRepository.selectById(id) == null) {
            throw exception(DEMO03_GRADE_NOT_EXISTS);
        }
    }

    private void deleteDemo03GradeByStudentId(Long studentId) {
        demo03GradeErpRepository.deleteByStudentId(studentId);
    }

    private void deleteDemo03GradeByStudentIds(List<Long> studentIds) {
        demo03GradeErpRepository.deleteByStudentIds(studentIds);
    }

}