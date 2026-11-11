package cn.afei.mall.module.infra.dal.dataobject.demo.demo03;

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
 * 学生课程 DO
 *
 * @author 芋道源码
 */
@Entity
@Table(name = "mall_demo03_course")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Demo03CourseDO extends BaseDO {

    /**
     * 编号
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 学生编号
     */
    private Long studentId;
    /**
     * 名字
     */
    private String name;
    /**
     * 分数
     */
    private Integer score;

}