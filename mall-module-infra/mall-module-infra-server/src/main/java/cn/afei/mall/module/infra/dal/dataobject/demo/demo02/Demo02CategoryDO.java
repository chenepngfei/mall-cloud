package cn.afei.mall.module.infra.dal.dataobject.demo.demo02;

import cn.afei.mall.framework.jpa.core.dataobject.BaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.*;

/**
 * 示例分类 DO
 *
 * @author 芋道源码
 */
@Entity
@Table(name = "mall_demo02_category")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Demo02CategoryDO extends BaseDO {

    public static final Long PARENT_ID_ROOT = 0L;

    /**
     * 编号
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 名字
     */
    private String name;
    /**
     * 父级编号
     */
    private Long parentId;

}