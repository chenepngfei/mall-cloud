package cn.afei.mall.framework.jpa.core.query;

import java.io.Serializable;
import java.util.function.Function;

/**
 * 支持 Lambda 表达式的函数，对应 MyBatis Plus 的 com.baomidou.mybatisplus.core.toolkit.support.SFunction
 *
 * 通过实现 {@link Serializable}，可以被解析出方法名（如 getUsername），进而推导出属性名（username）
 *
 * @param <T> 实体类型
 * @param <R> 属性类型
 */
@FunctionalInterface
public interface SFunction<T, R> extends Serializable, Function<T, R> {
}
