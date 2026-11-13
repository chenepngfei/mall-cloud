package cn.afei.mall.module.infra.service.demo.demo03.normal;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.afei.mall.framework.common.pojo.PageResult;
import cn.afei.mall.framework.common.util.object.BeanUtils;
import cn.afei.mall.module.infra.controller.admin.demo.demo03.normal.vo.Demo03StudentNormalPageReqVO;
import cn.afei.mall.module.infra.controller.admin.demo.demo03.normal.vo.Demo03StudentNormalSaveReqVO;
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
import static cn.afei.mall.framework.common.util.collection.CollectionUtils.convertList;
import static cn.afei.mall.framework.common.util.collection.CollectionUtils.diffList;
import static cn.afei.mall.module.infra.enums.ErrorCodeConstants.DEMO03_STUDENT_NOT_EXISTS;

/**
 * 学生 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class Demo03StudentNormalServiceImpl implements Demo03StudentNormalService {

    @Resource
    private Demo03StudentRepository demo03StudentNormalRepository;
    @Resource
    private Demo03CourseRepository demo03CourseNormalRepository;
    @Resource
    private Demo03GradeRepository demo03GradeNormalRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createDemo03Student(Demo03StudentNormalSaveReqVO createReqVO) {
        // 插入
        Demo03StudentDO demo03Student = BeanUtils.toBean(createReqVO, Demo03StudentDO.class);
        demo03StudentNormalRepository.save(demo03Student);

        // 插入子表
        createDemo03CourseList(demo03Student.getId(), createReqVO.getDemo03Courses());
        createDemo03Grade(demo03Student.getId(), createReqVO.getDemo03Grade());
        // 返回
        return demo03Student.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDemo03Student(Demo03StudentNormalSaveReqVO updateReqVO) {
        // 校验存在
        validateDemo03StudentExists(updateReqVO.getId());
        // 更新
        Demo03StudentDO updateObj = BeanUtils.toBean(updateReqVO, Demo03StudentDO.class);
        demo03StudentNormalRepository.updateByIdSelective(updateObj);

        // 更新子表
        updateDemo03CourseList(updateReqVO.getId(), updateReqVO.getDemo03Courses());
        updateDemo03Grade(updateReqVO.getId(), updateReqVO.getDemo03Grade());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDemo03Student(Long id) {
        // 校验存在
        validateDemo03StudentExists(id);
        // 删除
        demo03StudentNormalRepository.deleteByIdLogically(id);

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
        demo03StudentNormalRepository.deleteBatchIdsLogically(ids);

        // 删除子表
        deleteDemo03CourseByStudentIds(ids);
        deleteDemo03GradeByStudentIds(ids);
    }

    private void validateDemo03StudentExists(List<Long> ids) {
        List<Demo03StudentDO> list = demo03StudentNormalRepository.selectByIds(ids);
        if (CollUtil.isEmpty(list) || list.size() != ids.size()) {
            throw exception(DEMO03_STUDENT_NOT_EXISTS);
        }
    }

    private void validateDemo03StudentExists(Long id) {
        if (demo03StudentNormalRepository.selectById(id) == null) {
            throw exception(DEMO03_STUDENT_NOT_EXISTS);
        }
    }

    @Override
    public Demo03StudentDO getDemo03Student(Long id) {
        return demo03StudentNormalRepository.selectById(id);
    }

    @Override
    public PageResult<Demo03StudentDO> getDemo03StudentPage(Demo03StudentNormalPageReqVO pageReqVO) {
        return demo03StudentNormalRepository.selectPage(pageReqVO);
    }

    // ==================== 子表（学生课程） ====================

    @Override
    public List<Demo03CourseDO> getDemo03CourseListByStudentId(Long studentId) {
        return demo03CourseNormalRepository.selectListByStudentId(studentId);
    }

    private void createDemo03CourseList(Long studentId, List<Demo03CourseDO> list) {
        list.forEach(o -> o.setStudentId(studentId).clean());
        demo03CourseNormalRepository.saveAll(list);
    }

    private void updateDemo03CourseList(Long studentId, List<Demo03CourseDO> list) {
        list.forEach(o -> o.setStudentId(studentId).clean());
        List<Demo03CourseDO> oldList = demo03CourseNormalRepository.selectListByStudentId(studentId);
        List<List<Demo03CourseDO>> diffList = diffList(oldList, list, (oldVal, newVal) -> {
            boolean same = ObjectUtil.equal(oldVal.getId(), newVal.getId());
            if (same) {
                newVal.setId(oldVal.getId());
            }
            return same;
        });

        // 第二步，批量添加、修改、删除
        if (CollUtil.isNotEmpty(diffList.get(0))) {
            demo03CourseNormalRepository.saveAll(diffList.get(0));
        }
        if (CollUtil.isNotEmpty(diffList.get(1))) {
            demo03CourseNormalRepository.updateBatchSelective(diffList.get(1));
        }
        if (CollUtil.isNotEmpty(diffList.get(2))) {
            demo03CourseNormalRepository.deleteBatchIdsLogically(convertList(diffList.get(2), Demo03CourseDO::getId));
        }
    }

    private void deleteDemo03CourseByStudentId(Long studentId) {
        demo03CourseNormalRepository.deleteByStudentId(studentId);
    }

    private void deleteDemo03CourseByStudentIds(List<Long> studentIds) {
        demo03CourseNormalRepository.deleteByStudentIds(studentIds);
    }

    // ==================== 子表（学生班级） ====================

    @Override
    public Demo03GradeDO getDemo03GradeByStudentId(Long studentId) {
        return demo03GradeNormalRepository.selectByStudentId(studentId);
    }

    private void createDemo03Grade(Long studentId, Demo03GradeDO demo03Grade) {
        if (demo03Grade == null) {
            return;
        }
        demo03Grade.setStudentId(studentId);
        demo03GradeNormalRepository.save(demo03Grade);
    }

    private void updateDemo03Grade(Long studentId, Demo03GradeDO demo03Grade) {
        if (demo03Grade == null) {
            return;
        }
        demo03Grade.setStudentId(studentId).clean();
        demo03GradeNormalRepository.save(demo03Grade);
    }

    private void deleteDemo03GradeByStudentId(Long studentId) {
        demo03GradeNormalRepository.deleteByStudentId(studentId);
    }

    private void deleteDemo03GradeByStudentIds(List<Long> studentIds) {
        demo03GradeNormalRepository.deleteByStudentIds(studentIds);
    }

}