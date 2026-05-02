package top.ppmblszdp.system.interfaces.web.dict;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.ppmblszdp.common.api.PageQuery;
import top.ppmblszdp.common.api.PageResult;
import top.ppmblszdp.common.api.Result;
import top.ppmblszdp.system.application.service.dict.DictApplicationService;
import top.ppmblszdp.system.interfaces.web.dict.dto.CreateDictDataCommand;
import top.ppmblszdp.system.interfaces.web.dict.dto.DictDataDto;
import top.ppmblszdp.system.interfaces.web.dict.dto.UpdateDictDataCommand;

@RestController
@RequestMapping("/dict-data")
@RequiredArgsConstructor
public class DictDataController {

  private final DictApplicationService dictApplicationService;

  @PostMapping
  public Result<DictDataDto> createDictData(@Valid @RequestBody CreateDictDataCommand command) {
    return Result.success(dictApplicationService.createDictData(command));
  }

  @PutMapping("/{id}")
  public Result<Void> updateDictData(
      @PathVariable Long id, @RequestBody UpdateDictDataCommand command) {
    dictApplicationService.updateDictData(id, command);
    return Result.success();
  }

  @DeleteMapping("/{id}")
  public Result<Void> deleteDictData(@PathVariable Long id) {
    dictApplicationService.deleteDictData(id);
    return Result.success();
  }

  @GetMapping("/{id}")
  public Result<DictDataDto> getDictDataById(@PathVariable Long id) {
    return dictApplicationService
        .getDictDataById(id)
        .map(Result::success)
        .orElseGet(Result::success);
  }

  @GetMapping("/page")
  public Result<PageResult<DictDataDto>> pageDictData(
      @RequestParam String dictType, PageQuery pageQuery) {
    return Result.success(dictApplicationService.pageDictData(dictType, pageQuery));
  }

  @GetMapping("/type/{dictType}")
  public Result<List<DictDataDto>> getDictDataByType(@PathVariable String dictType) {
    return Result.success(dictApplicationService.getAvailableDictDataByType(dictType));
  }
}
