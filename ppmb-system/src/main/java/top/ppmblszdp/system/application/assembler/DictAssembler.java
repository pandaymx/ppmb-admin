package top.ppmblszdp.system.application.assembler;

import java.util.List;
import org.mapstruct.Mapper;
import top.ppmblszdp.system.domain.model.dict.entity.DictData;
import top.ppmblszdp.system.domain.model.dict.entity.DictType;
import top.ppmblszdp.system.interfaces.web.dict.dto.DictDataDto;
import top.ppmblszdp.system.interfaces.web.dict.dto.DictTypeDto;

@Mapper(componentModel = "spring")
public interface DictAssembler {

  DictTypeDto toTypeDto(DictType dictType);

  List<DictTypeDto> toTypeDtoList(List<DictType> dictTypes);

  DictDataDto toDataDto(DictData dictData);

  List<DictDataDto> toDataDtoList(List<DictData> dictDataList);
}
