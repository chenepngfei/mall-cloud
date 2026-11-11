package cn.afei.mall.module.infra.dal.mysql.codegen;

import cn.afei.mall.framework.jpa.core.repository.BaseRepositoryX;
import cn.afei.mall.framework.jpa.core.query.LambdaSpecifications;
import cn.afei.mall.module.infra.dal.dataobject.codegen.CodegenColumnDO;

import java.util.Collection;
import java.util.List;

public interface CodegenColumnRepository extends BaseRepositoryX<CodegenColumnDO, Long> {

    default List<CodegenColumnDO> selectListByTableId(Long tableId) {
        return selectList(LambdaSpecifications.<CodegenColumnDO>of()
                .eq(CodegenColumnDO::getTableId, tableId)
                .orderByAsc(CodegenColumnDO::getOrdinalPosition).build());
    }

    default void deleteListByTableId(Long tableId) {
        delete(CodegenColumnDO::getTableId, tableId);
    }

    default void deleteListByTableId(Collection<Long> tableIds) {
        delete(LambdaSpecifications.<CodegenColumnDO>of()
               .in(CodegenColumnDO::getTableId, tableIds).build());
    }

}
