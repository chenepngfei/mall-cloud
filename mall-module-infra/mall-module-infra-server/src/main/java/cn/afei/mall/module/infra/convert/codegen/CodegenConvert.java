package cn.afei.mall.module.infra.convert.codegen;

import cn.afei.mall.framework.common.util.collection.CollectionUtils;
import cn.afei.mall.framework.common.util.object.BeanUtils;
import cn.afei.mall.module.infra.controller.admin.codegen.vo.CodegenDetailRespVO;
import cn.afei.mall.module.infra.controller.admin.codegen.vo.CodegenPreviewRespVO;
import cn.afei.mall.module.infra.controller.admin.codegen.vo.column.CodegenColumnRespVO;
import cn.afei.mall.module.infra.controller.admin.codegen.vo.table.CodegenTableRespVO;
import cn.afei.mall.module.infra.dal.dataobject.codegen.CodegenColumnDO;
import cn.afei.mall.module.infra.dal.dataobject.codegen.CodegenTableDO;
import cn.afei.mall.module.infra.service.db.DatabaseColumnDef;
import cn.afei.mall.module.infra.service.db.DatabaseTableDef;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;

@Mapper
public interface CodegenConvert {

    CodegenConvert INSTANCE = Mappers.getMapper(CodegenConvert.class);

    // ========== DatabaseTableDef 相关 ==========

    @Mappings({
            @Mapping(source = "name", target = "tableName"),
            @Mapping(source = "comment", target = "tableComment"),
    })
    CodegenTableDO convert(DatabaseTableDef bean);

    List<CodegenColumnDO> convertList(List<DatabaseColumnDef> list);

    @Mappings({
            @Mapping(source = "name", target = "columnName"),
            @Mapping(source = "jdbcType", target = "dataType"),
            @Mapping(source = "comment", target = "columnComment"),
            @Mapping(source = "keyFlag", target = "primaryKey"),
            @Mapping(source = "propertyName", target = "javaField"),
    })
    CodegenColumnDO convert(DatabaseColumnDef bean);

    // ========== 其它 ==========

    default CodegenDetailRespVO convert(CodegenTableDO table, List<CodegenColumnDO> columns) {
        CodegenDetailRespVO respVO = new CodegenDetailRespVO();
        respVO.setTable(BeanUtils.toBean(table, CodegenTableRespVO.class));
        respVO.setColumns(BeanUtils.toBean(columns, CodegenColumnRespVO.class));
        return respVO;
    }

    default List<CodegenPreviewRespVO> convert(Map<String, String> codes) {
        return CollectionUtils.convertList(codes.entrySet(),
                entry -> new CodegenPreviewRespVO().setFilePath(entry.getKey()).setCode(entry.getValue()));
    }

}
