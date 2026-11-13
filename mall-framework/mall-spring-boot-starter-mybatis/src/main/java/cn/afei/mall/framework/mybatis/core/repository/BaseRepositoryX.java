package cn.afei.mall.framework.mybatis.core.repository;

import cn.afei.mall.framework.common.pojo.PageParam;
import cn.afei.mall.framework.common.pojo.PageResult;
import cn.afei.mall.framework.common.pojo.SortablePageParam;
import cn.afei.mall.framework.common.pojo.SortingField;
import cn.afei.mall.framework.common.util.object.PageUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import java.util.Collection;
import java.util.List;

/**
 * JPA Repository 通用基类 — 平移原 BaseMapperX 的查询能力面（Specification 版）
 *
 * 1. 分页语义与原 MP 完全一致：pageNo 从 1 起、PAGE_SIZE_NONE(-1) 不分页查全部、超出末页返回空列表且 total 保留
 * 2. selectOne 多行结果抛异常，对齐原 MP selectOne(wrapper) 的 TooManyResults 行为
 * 3. 排序字段使用实体属性名（驼峰），由 Hibernate 命名策略映射列，不硬写表名
 *
 * @author 芋道源码
 */
@NoRepositoryBean
public interface BaseRepositoryX<T, ID> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

    // ========== 分页查询 ==========

    /**
     * 1. 分页查询（无动态排序）
     */
    default PageResult<T> selectPage(PageParam pageParam, Specification<T> spec) {
        return selectPage(pageParam, spec, Sort.unsorted());
    }

    /**
     * 2. 分页查询（SortablePageParam，支持前端动态排序字段）
     */
    default PageResult<T> selectPage(SortablePageParam pageParam, Specification<T> spec) {
        return selectPage(pageParam, spec, buildSort(pageParam.getSortingFields()));
    }

    /**
     * 3. 分页查询（指定排序）
     */
    default PageResult<T> selectPage(PageParam pageParam, Specification<T> spec, Sort sort) {
        // 特殊：不分页，直接查询全部（对齐原 BaseMapperX 的 PAGE_SIZE_NONE 语义）
        if (PageParam.PAGE_SIZE_NONE.equals(pageParam.getPageSize())) {
            List<T> list = findAll(spec, sort);
            return PageUtils.buildPageResult(pageParam, list);
        }
        Pageable pageable = PageRequest.of(pageParam.getPageNo() - 1, pageParam.getPageSize(), sort);
        Page<T> page = findAll(spec, pageable);
        return new PageResult<>(page.getContent(), page.getTotalElements());
    }

    // ========== 列表查询 ==========

    /**
     * 4. 列表查询
     */
    default List<T> selectList(Specification<T> spec) {
        return findAll(spec);
    }

    /**
     * 5. 列表查询（指定排序）
     */
    default List<T> selectList(Specification<T> spec, Sort sort) {
        return findAll(spec, sort);
    }

    // ========== 单条查询 ==========

    /**
     * 6. 单条查询；无结果返回 null，多行结果抛异常（对齐原 MP selectOne 行为）
     */
    default T selectOne(Specification<T> spec) {
        List<T> list = findAll(spec);
        if (list.isEmpty()) {
            return null;
        }
        if (list.size() > 1) {
            throw new IncorrectResultSizeDataAccessException(1, list.size());
        }
        return list.get(0);
    }

    /**
     * 7. 排序后取第一条（LIMIT 1）；无结果返回 null — 对齐原 selectFirstOne(Wrapper) / selectLastOne(Wrapper) 语义
     */
    default T selectFirstOne(Specification<T> spec, Sort sort) {
        List<T> list = findAll(spec, PageRequest.of(0, 1, sort)).getContent();
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * 8. 取前 limit 条（LIMIT n）— 对齐原 selectListLimitN；limit 为 0 返回空列表
     */
    default List<T> selectListLimitN(Specification<T> spec, Sort sort, int limit) {
        if (limit < 0) {
            throw new IllegalArgumentException("limit 不能为负数：" + limit);
        }
        if (limit == 0) {
            return List.of();
        }
        return findAll(spec, PageRequest.of(0, limit, sort)).getContent();
    }

    // ========== 全量查询 ==========

    /**
     * 9. 全表查询（带租户 + 逻辑删除过滤）— 对齐原 MP selectList() 无参调用被拦截器改写后的行为
     */
    default List<T> selectAll() {
        return selectList((Specification<T>) (root, query, cb) -> cb.conjunction());
    }

    // ========== 计数查询 ==========

    /**
     * 9.5 全表计数（带租户 + 逻辑删除过滤）— 对齐原 MP selectCount() 无参调用
     */
    default Long selectCount() {
        return count((Specification<T>) (root, query, cb) -> cb.conjunction());
    }

    /**
     * 10. 计数查询
     */
    default Long selectCount(Specification<T> spec) {
        return count(spec);
    }

    // ========== 主键操作（带租户 + 逻辑删除过滤，对齐原 MP 拦截器行为） ==========

    /**
     * 11. 按主键查询（WHERE id = ? AND deleted = false AND tenant_id = ?）— 对齐原 MP selectById 被拦截器改写后的行为
     */
    T selectById(ID id);

    /**
     * 11.5 按主键集合查询（selectBatchIds 别名，对齐原 MP selectByIds 调用名）
     */
    default List<T> selectByIds(Iterable<ID> ids) {
        return selectBatchIds(ids);
    }

    /**
     * 12. 按主键集合查询（带租户 + 逻辑删除过滤）— 对齐原 MP selectBatchIds
     */
    List<T> selectBatchIds(Iterable<ID> ids);

    /**
     * 12.5 按主键选择性更新 — 平移原 MP updateById + DefaultDBFieldHandler 行为：
     * 只更新非 null 字段（NOT_NULL 策略），并自动填充 updateTime/updater
     */
    void updateByIdSelective(T entity);

    /**
     * 12.6 批量选择性更新 — 平移原 MP updateBatch（循环 updateById 非空更新）
     */
    default void updateBatchSelective(Iterable<T> entities) {
        for (T entity : entities) {
            updateByIdSelective(entity);
        }
    }

    /**
     * 13. 按主键逻辑删除（UPDATE SET deleted = true WHERE id = ? AND deleted = false AND tenant_id = ?）
     * 仅 SET deleted，不更新 update_time/updater，与原 MP @TableLogic 生成 SQL 一致
     */
    void deleteByIdLogically(ID id);

    /**
     * 14. 按主键集合逻辑删除 — 对齐原 MP deleteBatchIds
     */
    void deleteBatchIdsLogically(Iterable<ID> ids);

    // ========== 辅助 ==========

    /**
     * 15. SortingField 集合转 Sort（空安全；字段为实体属性名，由 Hibernate 蛇形映射列）
     */
    static Sort buildSort(Collection<SortingField> sortingFields) {
        if (sortingFields == null || sortingFields.isEmpty()) {
            return Sort.unsorted();
        }
        Sort sort = Sort.unsorted();
        for (SortingField field : sortingFields) {
            Sort.Direction direction = SortingField.ORDER_DESC.equalsIgnoreCase(field.getOrder())
                    ? Sort.Direction.DESC : Sort.Direction.ASC;
            sort = sort.and(Sort.by(direction, field.getField()));
        }
        return sort;
    }

}
