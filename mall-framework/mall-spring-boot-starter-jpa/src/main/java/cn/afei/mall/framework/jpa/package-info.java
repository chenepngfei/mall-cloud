/**
 * JPA（Spring Data JPA + Hibernate）拓展，提供基础实体、通用 Repository、Specification 构建器、字段转换器等能力
 *
 * 1. core.dataobject 包：BaseDO 基础实体，提供审计填充、逻辑删除
 * 2. core.repository 包：BaseRepositoryX 通用 Repository，方法名对齐原 MyBatis Plus 的 BaseMapperX
 * 3. core.query 包：LambdaSpecifications 动态条件构建器，对齐原 LambdaQueryWrapperX
 * 4. core.convert 包：字段转换器，对应原 MyBatis 的 TypeHandler
 */
package cn.afei.mall.framework.jpa;
