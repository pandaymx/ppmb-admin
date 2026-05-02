package top.ppmblszdp.system.application.service.dict.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.ppmblszdp.common.api.CommonResultCode;
import top.ppmblszdp.common.api.PageQuery;
import top.ppmblszdp.common.api.PageResult;
import top.ppmblszdp.common.exception.BusinessException;
import top.ppmblszdp.system.application.service.dict.DictApplicationService;
import top.ppmblszdp.system.domain.model.dict.entity.DictData;
import top.ppmblszdp.system.domain.model.dict.entity.DictType;
import top.ppmblszdp.system.domain.model.dict.repository.DictDataRepository;
import top.ppmblszdp.system.domain.model.dict.repository.DictTypeRepository;
import top.ppmblszdp.system.interfaces.web.dict.dto.CreateDictDataCommand;
import top.ppmblszdp.system.interfaces.web.dict.dto.CreateDictTypeCommand;
import top.ppmblszdp.system.interfaces.web.dict.dto.DictDataDto;
import top.ppmblszdp.system.interfaces.web.dict.dto.DictTypeDto;
import top.ppmblszdp.system.interfaces.web.dict.dto.UpdateDictDataCommand;
import top.ppmblszdp.system.interfaces.web.dict.dto.UpdateDictTypeCommand;

@Service
@RequiredArgsConstructor
public class DictApplicationServiceImpl implements DictApplicationService {

  private final DictTypeRepository dictTypeRepository;
  private final DictDataRepository dictDataRepository;
  private final top.ppmblszdp.system.application.assembler.DictAssembler dictAssembler;

  // Simple local cache for dict data
  private final Map<String, List<DictDataDto>> dictCache = new ConcurrentHashMap<>();

  private void clearCache(String dictType) {
    if (dictType != null) {
      dictCache.remove(dictType);
    }
  }

  // --- Dict Type ---

  @Override
  @Transactional(rollbackFor = Exception.class)
  public DictTypeDto createDictType(CreateDictTypeCommand command) {
    dictTypeRepository
        .findByDictType(command.getDictType())
        .ifPresent(
            t -> {
              throw new BusinessException(
                  org.springframework.http.HttpStatus.BAD_REQUEST,
                  CommonResultCode.PARAM_ERROR,
                  "字典类型已存在",
                  null);
            });

    DictType dictType =
        DictType.create(
            command.getDictName(),
            command.getDictType(),
            command.getSystemFlag(),
            command.getStatus(),
            command.getRemark());
    DictType saved = dictTypeRepository.save(dictType);
    return dictAssembler.toTypeDto(saved);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void updateDictType(Long id, UpdateDictTypeCommand command) {
    DictType dictType =
        dictTypeRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new BusinessException(
                        org.springframework.http.HttpStatus.BAD_REQUEST,
                        CommonResultCode.PARAM_ERROR,
                        "字典类型不存在",
                        null));

    if (command.getDictType() != null && !command.getDictType().equals(dictType.getDictType())) {
      dictTypeRepository
          .findByDictType(command.getDictType())
          .ifPresent(
              t -> {
                throw new BusinessException(
                    org.springframework.http.HttpStatus.BAD_REQUEST,
                    CommonResultCode.PARAM_ERROR,
                    "新字典类型已存在",
                    null);
              });
      clearCache(dictType.getDictType()); // Clear old cache
      clearCache(command.getDictType()); // Clear new cache just in case
      dictDataRepository.updateDictType(dictType.getDictType(), command.getDictType());
    } else {
      clearCache(dictType.getDictType());
    }

    dictType.updateInfo(
        command.getDictName(), command.getDictType(), command.getStatus(), command.getRemark());
    dictTypeRepository.save(dictType);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteDictType(Long id) {
    DictType dictType =
        dictTypeRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new BusinessException(
                        org.springframework.http.HttpStatus.BAD_REQUEST,
                        CommonResultCode.PARAM_ERROR,
                        "字典类型不存在",
                        null));

    if (dictType.isSystemFlag()) {
      throw new BusinessException(
          org.springframework.http.HttpStatus.BAD_REQUEST,
          CommonResultCode.PARAM_ERROR,
          "系统内置字典不可删除",
          null);
    }

    long count = dictDataRepository.countByParentId(id);
    if (count > 0) {
      throw new BusinessException(
          org.springframework.http.HttpStatus.BAD_REQUEST,
          CommonResultCode.PARAM_ERROR,
          "该字典类型下存在字典数据，不可删除",
          null);
    }

    clearCache(dictType.getDictType());
    dictTypeRepository.deleteById(id);
  }

  @Override
  public Optional<DictTypeDto> getDictTypeById(Long id) {
    return dictTypeRepository.findById(id).map(dictAssembler::toTypeDto);
  }

  @Override
  public PageResult<DictTypeDto> pageDictTypes(PageQuery pageQuery) {
    PageRequest request = PageRequest.of(pageQuery.pageNum() - 1, pageQuery.pageSize());
    Page<DictType> page = dictTypeRepository.findAll(request);
    List<DictTypeDto> dtos = dictAssembler.toTypeDtoList(page.getContent());
    return PageResult.of(page.getTotalElements(), dtos, pageQuery.pageNum(), pageQuery.pageSize());
  }

  // --- Dict Data ---

  @Override
  @Transactional(rollbackFor = Exception.class)
  public DictDataDto createDictData(CreateDictDataCommand command) {
    dictDataRepository
        .findByDictTypeAndDictValue(command.getDictType(), command.getDictValue())
        .ifPresent(
            d -> {
              throw new BusinessException(
                  org.springframework.http.HttpStatus.BAD_REQUEST,
                  CommonResultCode.PARAM_ERROR,
                  "当前字典类型下字典键值已存在",
                  null);
            });

    DictData data =
        DictData.create(
            command.getParentId(),
            command.getDictSort(),
            command.getDictLabel(),
            command.getDictValue(),
            command.getDictType(),
            command.getIsDefault(),
            command.getListClass(),
            command.getStatus(),
            command.getRemark());

    DictData saved = dictDataRepository.save(data);
    clearCache(command.getDictType());
    return dictAssembler.toDataDto(saved);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void updateDictData(Long id, UpdateDictDataCommand command) {
    DictData data =
        dictDataRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new BusinessException(
                        org.springframework.http.HttpStatus.BAD_REQUEST,
                        CommonResultCode.PARAM_ERROR,
                        "字典数据不存在",
                        null));

    boolean valueChanged =
        command.getDictValue() != null && !command.getDictValue().equals(data.getDictValue());
    boolean typeChanged =
        command.getDictType() != null && !command.getDictType().equals(data.getDictType());

    if (valueChanged || typeChanged) {
      String checkType = command.getDictType() != null ? command.getDictType() : data.getDictType();
      String checkValue =
          command.getDictValue() != null ? command.getDictValue() : data.getDictValue();
      dictDataRepository
          .findByDictTypeAndDictValue(checkType, checkValue)
          .ifPresent(
              d -> {
                throw new BusinessException(
                    org.springframework.http.HttpStatus.BAD_REQUEST,
                    CommonResultCode.PARAM_ERROR,
                    "当前字典类型下新字典键值已存在",
                    null);
              });
    }

    String oldType = data.getDictType();
    data.updateInfo(
        command.getDictSort(),
        command.getDictLabel(),
        command.getDictValue(),
        command.getDictType(),
        command.getIsDefault(),
        command.getListClass(),
        command.getStatus(),
        command.getRemark());

    dictDataRepository.save(data);

    clearCache(oldType);
    if (command.getDictType() != null && !oldType.equals(command.getDictType())) {
      clearCache(command.getDictType());
    }
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteDictData(Long id) {
    DictData data =
        dictDataRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new BusinessException(
                        org.springframework.http.HttpStatus.BAD_REQUEST,
                        CommonResultCode.PARAM_ERROR,
                        "字典数据不存在",
                        null));

    clearCache(data.getDictType());
    dictDataRepository.deleteById(id);
  }

  @Override
  public Optional<DictDataDto> getDictDataById(Long id) {
    return dictDataRepository.findById(id).map(dictAssembler::toDataDto);
  }

  @Override
  public PageResult<DictDataDto> pageDictData(String dictType, PageQuery pageQuery) {
    PageRequest request = PageRequest.of(pageQuery.pageNum() - 1, pageQuery.pageSize());
    Page<DictData> page = dictDataRepository.findByDictType(dictType, request);
    List<DictDataDto> dtos = dictAssembler.toDataDtoList(page.getContent());
    return PageResult.of(page.getTotalElements(), dtos, pageQuery.pageNum(), pageQuery.pageSize());
  }

  @Override
  public List<DictDataDto> getAvailableDictDataByType(String dictType) {
    return dictCache.computeIfAbsent(
        dictType,
        k -> {
          List<DictData> list =
              dictDataRepository.findByDictTypeAndStatusOrderByDictSortAsc(
                  k, 0); // 0 normally means normal/enabled
          return dictAssembler.toDataDtoList(list);
        });
  }

  // --- Converters ---

}
