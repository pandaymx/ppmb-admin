package top.ppmblszdp.system.interfaces.web.dict;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.ppmblszdp.common.api.PageQuery;
import top.ppmblszdp.common.api.PageResult;
import top.ppmblszdp.common.api.Result;
import top.ppmblszdp.system.application.service.dict.DictApplicationService;
import top.ppmblszdp.system.interfaces.web.dict.dto.CreateDictTypeCommand;
import top.ppmblszdp.system.interfaces.web.dict.dto.DictTypeDto;
import top.ppmblszdp.system.interfaces.web.dict.dto.UpdateDictTypeCommand;

@RestController
@RequestMapping("/dict-types")
@RequiredArgsConstructor
public class DictTypeController {

  private final DictApplicationService dictApplicationService;

  @PostMapping
  public Result<DictTypeDto> createDictType(@Valid @RequestBody CreateDictTypeCommand command) {
    return Result.success(dictApplicationService.createDictType(command));
  }

  @PutMapping("/{id}")
  public Result<Void> updateDictType(
      @PathVariable Long id, @RequestBody UpdateDictTypeCommand command) {
    dictApplicationService.updateDictType(id, command);
    return Result.success();
  }

  @DeleteMapping("/{id}")
  public Result<Void> deleteDictType(@PathVariable Long id) {
    dictApplicationService.deleteDictType(id);
    return Result.success();
  }

  @GetMapping("/{id}")
  public Result<DictTypeDto> getDictTypeById(@PathVariable Long id) {
    return dictApplicationService
        .getDictTypeById(id)
        .map(Result::success)
        .orElseGet(Result::success);
  }

  @GetMapping("/page")
  public Result<PageResult<DictTypeDto>> pageDictTypes(PageQuery pageQuery) {
    return Result.success(dictApplicationService.pageDictTypes(pageQuery));
  }
}
