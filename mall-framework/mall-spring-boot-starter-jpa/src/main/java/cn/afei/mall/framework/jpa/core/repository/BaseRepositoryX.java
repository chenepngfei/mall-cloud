package cn.afei.mall.framework.jpa.core.repository;

import cn.hutool.core.collection.CollUtil;
import cn.afei.mall.framework.common.pojo.PageParam;
import cn.afei.mall.framework.common.pojo.PageResult;
import cn.afei.mall.framework.common.pojo.SortablePageParam;
import cn.afei.mall.framework.common.pojo.SortingField;
import cn.afei.mall.framework.common.util.object.PageUtils;
import cn.afei.mall.framework.jpa.core.query.LambdaSpecifications;
import cn.afei.mall.framework.jpa.core.query.SFunction;
import cn.afei.mall.framework.jpa.core.util.JpaPageUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;
import java.util.List;

/**
 * 在 Spring Data JPA 的 JpaRepository 的基础上拓展，提供更多的能力，对齐原 MyBatis Plus 的 {@code BaseMapperX}
 *
 * 1. {@link JpaRepository} 提供基础的 CRUD 能力
 * 2. {@link JpaSpecificationExecutor} 提供基于 {@link Specification} 的动态条件查询能力
 * 3. 方法名尽量与原 {@code BaseMapperX} 保持一致，降低业务模块的迁移成本
 */
@NoRepositoryBean
public interface BaseRepositoryX<T, ID> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

    // ========== 分页查询 ==========

    default PageResult<T> selectPage(PageParam pageParam, Specification<T> spec) {
        return selectPage(pageParam, (Collection<SortingField>) null, spec);
    }

    default PageResult<T> selectPage(SortablePageParam pageParam, Specification<T> spec) {
        return selectPage(pageParam, pageParam.getSortingFields(), spec);
    }

    default PageResult<T> selectPage(PageParam pageParam, Collection<SortingField> sortingFields, Specification<T> spec) {
        // 特殊：不分页，直接查询全部
        if (PageParam.PAGE_SIZE_NONE.equals(pageParam.getPageSize())) {
            List<T> list = findAll(spec);
            return PageUtils.buildPageResult(pageParam, list);
        }
        // Spring Data JPA 分页查询
        Page<T> page = findAll(spec, JpaPageUtils.buildPageable(pageParam,
                sortingFields != null ? List.copyOf(sortingFields) : null));
        return new PageResult<>(page.getContent(), page.getTotalElements());
    }

    // ========== 单条查询 ==========

    /**
     * 查询满足条件的一条记录
     *
     * @param spec 查询条件
     * @return 实体；不存在时返回 null；多于 1 条时抛出异常（与 MyBatis Plus 的 selectOne 行为一致）
     */
    default T selectOne(Specification<T> spec) {
        List<T> list = findAll(spec);
        if (list.size() > 1) {
            throw new org.springframework.dao.IncorrectResultSizeDataAccessException(1, list.size());
        }
        return CollUtil.getFirst(list);
    }

    default T selectOne(SFunction<T, ?> field, Object value) {
        return selectOne(LambdaSpecifications.<T>of().eq(field, value).build());
    }

    default T selectOne(SFunction<T, ?> field1, Object value1, SFunction<T, ?> field2, Object value2) {
        return selectOne(LambdaSpecifications.<T>of().eq(field1, value1).eq(field2, value2).build());
    }

    default T selectOne(SFunction<T, ?> field1, Object value1, SFunction<T, ?> field2, Object value2,
                        SFunction<T, ?> field3, Object value3) {
        return selectOne(LambdaSpecifications.<T>of().eq(field1, value1).eq(field2, value2).eq(field3, value3).build());
    }

    /**
     * 获取满足条件的第 1 条记录
     *
     * 目的：解决并发场景下，插入多条记录后，使用 selectOne 会报错的问题
     *
     * @param spec 查询条件
     * @return 实体
     */
    default T selectFirstOne(Specification<T> spec) {
        return CollUtil.getFirst(findAll(spec, PageRequest.of(0, 1)).getContent());
    }

    default T selectFirstOne(SFunction<T, ?> field, Object value) {
        return selectFirstOne(LambdaSpecifications.<T>of().eq(field, value).build());
    }

    default T selectFirstOne(SFunction<T, ?> field1, Object value1, SFunction<T, ?> field2, Object value2) {
        return selectFirstOne(LambdaSpecifications.<T>of().eq(field1, value1).eq(field2, value2).build());
    }

    /**
     * 获取满足条件的最新一条记录
     *
     * @param spec 查询条件
     * @return 最新一条；不存在返回 null
     */
    default T selectLastOne(Specification<T> spec) {
        return CollUtil.getLast(findAll(spec));
    }

    // ========== 列表查询 ==========

    default List<T> selectList() {
        return findAll();
    }

    default List<T> selectList(Specification<T> spec) {
        return findAll(spec);
    }

    default List<T> selectList(SFunction<T, ?> field, Object value) {
        return findAll(LambdaSpecifications.<T>of().eq(field, value).build());
    }

    default List<T> selectList(SFunction<T, ?> field, Collection<?> values) {
        if (CollUtil.isEmpty(values)) {
            return CollUtil.newArrayList();
        }
        return findAll(LambdaSpecifications.<T>of().in(field, values).build());
    }

    default List<T> selectList(SFunction<T, ?> field1, Object value1, SFunction<T, ?> field2, Object value2) {
        return findAll(LambdaSpecifications.<T>of().eq(field1, value1).eq(field2, value2).build());
    }

    /**
     * 获取满足条件的前 limit 条记录
     *
     * @param spec  查询条件
     * @param limit 条数，不能为负数；为 0 时不查询，直接返回空列表
     * @return 记录列表
     */
    default List<T> selectListLimitN(Specification<T> spec, int limit) {
        if (limit < 0) {
            throw new IllegalArgumentException("limit 不能为负数：" + limit);
        }
        if (limit == 0) {
            return CollUtil.newArrayList();
        }
        return findAll(spec, PageRequest.of(0, limit)).getContent();
    }

    // ========== 数量查询 ==========

    default Long selectCount() {
        return count();
    }

    default Long selectCount(Specification<T> spec) {
        return count(spec);
    }

    default Long selectCount(SFunction<T, ?> field, Object value) {
        return count(LambdaSpecifications.<T>of().eq(field, value).build());
    }

    // ========== 主键查询：对齐原 MyBatis Plus 的方法名 ==========

    /**
     * 按主键查询，不存在时返回 null（对齐 MP 的 selectById）
     */
    default T selectById(ID id) {
        return findById(id).orElse(null);
    }

    /**
     * 按主键批量查询（对齐 MP 的 selectBatchIds/selectByIds）
     */
    default List<T> selectByIds(Iterable<ID> ids) {
        return findAllById(ids);
    }

    // ========== 插入 ==========

    default T insert(T entity) {
        return save(entity);
    }

    /**
     * 批量插入
     *
     * @param entities 实体们
     * @return 插入后的实体列表
     */
    default List<T> insertBatch(Collection<T> entities) {
        return saveAll(entities);
    }

    /**
     * 选择性更新时，忽略的属性：id、审计字段、逻辑删除、租户
     */
    String[] IGNORED_UPDATE_PROPERTIES =
            {"id", "createTime", "updateTime", "creator", "updater", "deleted", "tenantId"};

    /**
     * 批量按 id 选择性更新，等价于 MP 的 {@code Db.updateBatchById}
     *
     * @param entities 携带待更新字段的实体们
     * @return 更新后的实体列表
     */
    default List<T> updateBatch(Collection<T> entities) {
        List<T> result = new java.util.ArrayList<>(entities.size());
        entities.forEach(entity -> result.add(updateById(entity)));
        return result;
    }

    /**
     * 按非空字段更新全部记录，等价于 MP 的 {@code updateBatch(T update)}（无条件的批量选择性更新）
     *
     * @param update 携带待更新字段的实体
     * @return 更新的记录数
     */
    default long updateBatch(T update) {
        List<T> list = findAll();
        for (T entity : list) {
            cn.hutool.core.bean.BeanUtil.copyProperties(update, entity, cn.hutool.core.bean.copier.CopyOptions.create()
                    .ignoreNullValue().setIgnoreProperties(IGNORED_UPDATE_PROPERTIES));
        }
        return saveAll(list).size();
    }

    // ========== 更新 ==========

    /**
     * 按 id 选择性更新：只更新非 null 的字段，等价于 MyBatis Plus 的 {@code updateById}
     *
     * 注意：会忽略 id、审计字段（createTime、updateTime、creator、updater）、deleted、tenantId
     *
     * @param entity 携带待更新字段的实体
     * @return 更新后的实体
     */
    T updateByIdSelective(T entity);

    /**
     * 按 id 选择性更新的别名，对齐 MP 的 updateById 方法名
     */
    default T updateById(T entity) {
        return updateByIdSelective(entity);
    }

    /**
     * 插入或更新，等价于 MP 的 {@code insertOrUpdate}：id 为空插入；id 非空时按 id 选择性更新，不存在则插入
     */
    T insertOrUpdate(T entity);

    // ========== 删除（走软删） ==========

    /**
     * 按条件删除：重写 {@link JpaSpecificationExecutor#delete(Specification)}，改为先查询再逐个删除，保证走软删（@SQLDelete）
     *
     * 注意：父接口的默认实现是 JPQL bulk delete，会绕过 @SQLDelete，因此必须重写
     *
     * @param spec 查询条件
     * @return 删除的记录数
     */
    @Override
    default long delete(Specification<T> spec) {
        List<T> list = findAll(spec);
        deleteAll(list);
        return list.size();
    }

    default long delete(SFunction<T, ?> field, Object value) {
        return delete(LambdaSpecifications.<T>of().eq(field, value).build());
    }

    default long deleteBatch(SFunction<T, ?> field, Collection<?> values) {
        if (CollUtil.isEmpty(values)) {
            return 0;
        }
        return delete(LambdaSpecifications.<T>of().in(field, values).build());
    }

    /**
     * 按主键批量删除（对齐 MP 的 deleteByIds），走软删
     */
    default void deleteByIds(Iterable<? extends ID> ids) {
        deleteAllById(ids);
    }

    // ========== 锁定读：需要在事务中调用 ==========

    /**
     * 获得满足条件的一条记录，并使用 FOR UPDATE 锁定
     *
     * @param spec 查询条件
     * @return 实体
     */
    T selectOneForUpdate(Specification<T> spec);

    /**
     * 获得满足条件的全部记录，并使用 FOR UPDATE 锁定
     *
     * @param spec 查询条件
     * @return 实体列表
     */
    List<T> selectListForUpdate(Specification<T> spec);

    /**
     * 获得指定 id 的记录，并使用 FOR UPDATE 锁定
     *
     * @param id 主键
     * @return 实体
     */
    T selectByIdForUpdate(ID id);

}
