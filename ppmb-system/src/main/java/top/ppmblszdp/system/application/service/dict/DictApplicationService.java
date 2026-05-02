package top.ppmblszdp.system.application.service.dict;

import java.util.List;
import java.util.Optional;
import top.ppmblszdp.common.api.PageQuery;
import top.ppmblszdp.common.api.PageResult;
import top.ppmblszdp.system.interfaces.web.dict.dto.CreateDictDataCommand;
import top.ppmblszdp.system.interfaces.web.dict.dto.CreateDictTypeCommand;
import top.ppmblszdp.system.interfaces.web.dict.dto.DictDataDto;
import top.ppmblszdp.system.interfaces.web.dict.dto.DictTypeDto;
import top.ppmblszdp.system.interfaces.web.dict.dto.UpdateDictDataCommand;
import top.ppmblszdp.system.interfaces.web.dict.dto.UpdateDictTypeCommand;

public interface DictApplicationService {
  // Dict Type
  DictTypeDto createDictType(CreateDictTypeCommand command);

  void updateDictType(Long id, UpdateDictTypeCommand command);

  void deleteDictType(Long id);

  Optional<DictTypeDto> getDictTypeById(Long id);

  PageResult<DictTypeDto> pageDictTypes(PageQuery pageQuery);

  // Dict Data
  DictDataDto createDictData(CreateDictDataCommand command);

  void updateDictData(Long id, UpdateDictDataCommand command);

  void deleteDictData(Long id);

  Optional<DictDataDto> getDictDataById(Long id);

  PageResult<DictDataDto> pageDictData(String dictType, PageQuery pageQuery);

  // Cache specific
  List<DictDataDto> getAvailableDictDataByType(String dictType);
}
