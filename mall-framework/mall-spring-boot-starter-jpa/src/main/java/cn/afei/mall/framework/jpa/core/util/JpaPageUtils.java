package cn.afei.mall.framework.jpa.core.util;

import cn.afei.mall.framework.common.pojo.PageParam;
import cn.afei.mall.framework.common.pojo.SortingField;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA 分页工具类，实现 {@link PageParam} 与 Spring Data 的 {@link Pageable} 互转
 */
public class JpaPageUtils {

    /**
     * 构建 Pageable：页码从 1 开始转换为从 0 开始
     *
     * @param pageParam 分页参数
     * @return Pageable
     */
    public static Pageable buildPageable(PageParam pageParam) {
        return PageRequest.of(pageParam.getPageNo() - 1, pageParam.getPageSize());
    }

    /**
     * 构建 Pageable，带排序
     *
     * @param pageParam     分页参数
     * @param sortingFields 排序字段列表
     * @return Pageable
     */
    public static Pageable buildPageable(PageParam pageParam, List<SortingField> sortingFields) {
        return PageRequest.of(pageParam.getPageNo() - 1, pageParam.getPageSize(), buildSort(sortingFields));
    }

    /**
     * 构建前 limit 条的 Pageable，不查询总数
     *
     * @param limit 条数
     * @param sort  排序
     * @return Pageable
     */
    public static Pageable buildLimitPageable(int limit, Sort sort) {
        return PageRequest.of(0, limit, sort != null ? sort : Sort.unsorted());
    }

    /**
     * 构建 Sort
     *
     * @param sortingFields 排序字段列表（field 为 Java 属性名）
     * @return Sort
     */
    public static Sort buildSort(List<SortingField> sortingFields) {
        if (sortingFields == null || sortingFields.isEmpty()) {
            return Sort.unsorted();
        }
        List<Sort.Order> orders = new ArrayList<>(sortingFields.size());
        for (SortingField field : sortingFields) {
            if (SortingField.ORDER_ASC.equalsIgnoreCase(field.getOrder())) {
                orders.add(Sort.Order.asc(field.getField()));
            } else {
                orders.add(Sort.Order.desc(field.getField()));
            }
        }
        return Sort.by(orders);
    }

}
