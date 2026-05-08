package top.ppmblszdp.system.application.service.dict.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import top.ppmblszdp.common.api.PageQuery;
import top.ppmblszdp.common.api.PageResult;
import top.ppmblszdp.common.exception.BusinessException;
import top.ppmblszdp.system.application.assembler.DictAssembler;
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

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("字典应用服务单元测试")
class DictApplicationServiceImplTest {

  @Mock private DictTypeRepository dictTypeRepository;
  @Mock private DictDataRepository dictDataRepository;
  @Mock private DictAssembler dictAssembler;

  @InjectMocks private DictApplicationServiceImpl dictService;

  private DictType dictType;
  private DictData dictData;

  @BeforeEach
  void setUp() {
    dictType = DictType.create("用户状态", "user_status", "N", 0, "用户状态字典");
    setEntityId(dictType, 1L);

    dictData = DictData.create(1L, 1, "启用", "1", "user_status", "N", "success", 0, "启用状态");
    setEntityId(dictData, 1L);
  }

  private void setEntityId(Object entity, Long id) {
    if (entity instanceof top.ppmblszdp.common.domain.entity.BaseEntity baseEntity) {
      top.ppmblszdp.common.domain.entity.EntityTestUtils.setId(baseEntity, id);
    }
  }

  @Test
  @DisplayName("创建字典类型成功")
  void createDictType_success() {
    CreateDictTypeCommand command = new CreateDictTypeCommand();
    command.setDictName("用户状态");
    command.setDictType("user_status");
    command.setSystemFlag("N");
    command.setStatus(0);
    command.setRemark("备注");

    when(dictTypeRepository.findByDictType("user_status")).thenReturn(Optional.empty());
    when(dictTypeRepository.save(any(DictType.class))).thenReturn(dictType);
    DictTypeDto dummyDto = new DictTypeDto(1L, "用户状态", "user_status", "N", 0, "备注", null, null);
    when(dictAssembler.toTypeDto(any(DictType.class))).thenReturn(dummyDto);

    DictTypeDto result = dictService.createDictType(command);

    assertNotNull(result);
    assertEquals("用户状态", result.dictName());
    assertEquals("user_status", result.dictType());
    verify(dictTypeRepository).save(any(DictType.class));
  }

  @Test
  @DisplayName("创建字典类型失败-类型已存在")
  void createDictType_duplicateType() {
    CreateDictTypeCommand command = new CreateDictTypeCommand();
    command.setDictType("user_status");

    when(dictTypeRepository.findByDictType("user_status")).thenReturn(Optional.of(dictType));

    BusinessException exception =
        assertThrows(BusinessException.class, () -> dictService.createDictType(command));

    assertTrue(exception.getMessage().contains("字典类型已存在"));
  }

  @org.junit.jupiter.params.ParameterizedTest
  @org.junit.jupiter.params.provider.CsvSource({
    "user_status, false, 不修改类型编码",
    "null, false, 类型为null",
    "new_user_status, true, 修改类型编码"
  })
  @DisplayName("更新字典类型成功")
  void updateDictType_success(String newType, boolean expectDataUpdate, String unusedScenario) {
    UpdateDictTypeCommand command = new UpdateDictTypeCommand();
    command.setDictName("用户状态");
    command.setDictType("null".equals(newType) ? null : newType);
    command.setStatus(0);
    command.setRemark("备注");

    when(dictTypeRepository.findById(1L)).thenReturn(Optional.of(dictType));
    if (expectDataUpdate) {
      when(dictTypeRepository.findByDictType(newType)).thenReturn(Optional.empty());
    }

    dictService.updateDictType(1L, command);

    if (expectDataUpdate) {
      verify(dictDataRepository).updateDictType("user_status", newType);
    }
    verify(dictTypeRepository).save(any(DictType.class));
  }

  @Test
  @DisplayName("更新字典类型失败-字典类型不存在")
  void updateDictType_notFound() {
    UpdateDictTypeCommand command = new UpdateDictTypeCommand();

    when(dictTypeRepository.findById(1L)).thenReturn(Optional.empty());

    BusinessException exception =
        assertThrows(BusinessException.class, () -> dictService.updateDictType(1L, command));

    assertTrue(exception.getMessage().contains("字典类型不存在"));
  }

  @Test
  @DisplayName("更新字典类型失败-新类型已存在")
  void updateDictType_newTypeExists() {
    UpdateDictTypeCommand command = new UpdateDictTypeCommand();
    command.setDictType("existing_type");

    when(dictTypeRepository.findById(1L)).thenReturn(Optional.of(dictType));
    when(dictTypeRepository.findByDictType("existing_type"))
        .thenReturn(Optional.of(DictType.create("其他", "existing_type", "N", 0, null)));

    BusinessException exception =
        assertThrows(BusinessException.class, () -> dictService.updateDictType(1L, command));

    assertTrue(exception.getMessage().contains("新字典类型已存在"));
  }

  @Test
  @DisplayName("删除字典类型成功")
  void deleteDictType_success() {
    when(dictTypeRepository.findById(1L)).thenReturn(Optional.of(dictType));
    when(dictDataRepository.countByParentId(1L)).thenReturn(0L);

    dictService.deleteDictType(1L);

    verify(dictTypeRepository).deleteById(1L);
  }

  @Test
  @DisplayName("删除字典类型失败-字典类型不存在")
  void deleteDictType_notFound() {
    when(dictTypeRepository.findById(1L)).thenReturn(Optional.empty());

    BusinessException exception =
        assertThrows(BusinessException.class, () -> dictService.deleteDictType(1L));

    assertTrue(exception.getMessage().contains("字典类型不存在"));
  }

  @Test
  @DisplayName("删除字典类型失败-系统内置字典")
  void deleteDictType_systemFlag() {
    DictType systemDict = DictType.create("系统字典", "sys_dict", "Y", 0, null);
    setEntityId(systemDict, 1L);

    when(dictTypeRepository.findById(1L)).thenReturn(Optional.of(systemDict));

    BusinessException exception =
        assertThrows(BusinessException.class, () -> dictService.deleteDictType(1L));

    assertTrue(exception.getMessage().contains("系统内置字典不可删除"));
  }

  @Test
  @DisplayName("删除字典类型失败-存在关联数据")
  void deleteDictType_hasData() {
    when(dictTypeRepository.findById(1L)).thenReturn(Optional.of(dictType));
    when(dictDataRepository.countByParentId(1L)).thenReturn(5L);

    BusinessException exception =
        assertThrows(BusinessException.class, () -> dictService.deleteDictType(1L));

    assertTrue(exception.getMessage().contains("该字典类型下存在字典数据"));
  }

  @Test
  @DisplayName("根据ID获取字典类型成功")
  void getDictTypeById_success() {
    when(dictTypeRepository.findById(1L)).thenReturn(Optional.of(dictType));
    DictTypeDto dummyDto = new DictTypeDto(1L, "用户状态", "user_status", "N", 0, "备注", null, null);
    when(dictAssembler.toTypeDto(any(DictType.class))).thenReturn(dummyDto);

    Optional<DictTypeDto> result = dictService.getDictTypeById(1L);

    assertTrue(result.isPresent());
    assertEquals("user_status", result.get().dictType());
  }

  @Test
  @DisplayName("根据ID获取字典类型-不存在返回空")
  void getDictTypeById_notFound() {
    when(dictTypeRepository.findById(1L)).thenReturn(Optional.empty());

    Optional<DictTypeDto> result = dictService.getDictTypeById(1L);

    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("分页查询字典类型成功")
  void pageDictTypes_success() {
    final PageQuery pageQuery = new PageQuery(1, 10);
    Page<DictType> page = new PageImpl<>(Arrays.asList(dictType));

    when(dictTypeRepository.findAll(any(Pageable.class))).thenReturn(page);
    DictTypeDto dummyDto = new DictTypeDto(1L, "用户状态", "user_status", "N", 0, "备注", null, null);
    when(dictAssembler.toTypeDtoList(any())).thenReturn(Arrays.asList(dummyDto));

    PageResult<DictTypeDto> result = dictService.pageDictTypes(pageQuery);

    assertNotNull(result);
    assertEquals(1, result.total());
    assertEquals(1, result.list().size());
  }

  @Test
  @DisplayName("创建字典数据成功")
  void createDictData_success() {
    CreateDictDataCommand command = new CreateDictDataCommand();
    command.setParentId(1L);
    command.setDictSort(1);
    command.setDictLabel("启用");
    command.setDictValue("1");
    command.setDictType("user_status");
    command.setIsDefault("N");
    command.setListClass("success");
    command.setStatus(0);
    command.setRemark("启用状态");

    when(dictDataRepository.findByDictTypeAndDictValue("user_status", "1"))
        .thenReturn(Optional.empty());
    when(dictDataRepository.save(any(DictData.class))).thenReturn(dictData);
    DictDataDto dummyDto =
        new DictDataDto(1L, 1L, 1, "启用", "1", "user_status", "N", "success", 0, "备注", null);
    when(dictAssembler.toDataDto(any(DictData.class))).thenReturn(dummyDto);

    DictDataDto result = dictService.createDictData(command);

    assertNotNull(result);
    assertEquals("启用", result.dictLabel());
    assertEquals("1", result.dictValue());
    verify(dictDataRepository).save(any(DictData.class));
  }

  @Test
  @DisplayName("创建字典数据失败-键值已存在")
  void createDictData_duplicateValue() {
    CreateDictDataCommand command = new CreateDictDataCommand();
    command.setDictType("user_status");
    command.setDictValue("1");

    when(dictDataRepository.findByDictTypeAndDictValue("user_status", "1"))
        .thenReturn(Optional.of(dictData));

    BusinessException exception =
        assertThrows(BusinessException.class, () -> dictService.createDictData(command));

    assertTrue(exception.getMessage().contains("当前字典类型下字典键值已存在"));
  }

  @Test
  @DisplayName("更新字典数据成功-不修改键值和类型")
  void updateDictData_success_noChange() {
    UpdateDictDataCommand command = new UpdateDictDataCommand();
    command.setDictLabel("新标签");
    command.setDictSort(2);

    when(dictDataRepository.findById(1L)).thenReturn(Optional.of(dictData));

    dictService.updateDictData(1L, command);

    verify(dictDataRepository).save(any(DictData.class));
  }

  @Test
  @DisplayName("更新字典数据成功-修改类型但新类型与旧类型相同")
  void updateDictData_success_sameType() {
    UpdateDictDataCommand command = new UpdateDictDataCommand();
    command.setDictType("user_status");
    command.setDictLabel("新标签");

    when(dictDataRepository.findById(1L)).thenReturn(Optional.of(dictData));

    dictService.updateDictData(1L, command);

    verify(dictDataRepository).save(any(DictData.class));
  }

  @org.junit.jupiter.params.ParameterizedTest
  @org.junit.jupiter.params.provider.CsvSource({
    "2, new_type, 同时修改键值和类型",
    "2, user_status, 仅修改键值",
    "1, new_type, 仅修改类型"
  })
  @DisplayName("更新字典数据成功-修改键值或类型")
  void updateDictData_success_parameterized(
      String newValue, String newType, String unusedScenario) {
    UpdateDictDataCommand command = new UpdateDictDataCommand();
    command.setDictValue(newValue);
    command.setDictType(newType);

    when(dictDataRepository.findById(1L)).thenReturn(Optional.of(dictData));
    if (!"1".equals(newValue) || !"user_status".equals(newType)) {
      when(dictDataRepository.findByDictTypeAndDictValue(newType, newValue))
          .thenReturn(Optional.empty());
    }

    dictService.updateDictData(1L, command);

    verify(dictDataRepository).save(any(DictData.class));
  }

  @Test
  @DisplayName("更新字典数据失败-字典数据不存在")
  void updateDictData_notFound() {
    UpdateDictDataCommand command = new UpdateDictDataCommand();

    when(dictDataRepository.findById(1L)).thenReturn(Optional.empty());

    BusinessException exception =
        assertThrows(BusinessException.class, () -> dictService.updateDictData(1L, command));

    assertTrue(exception.getMessage().contains("字典数据不存在"));
  }

  @Test
  @DisplayName("更新字典数据失败-新键值已存在")
  void updateDictData_newValueExists() {
    UpdateDictDataCommand command = new UpdateDictDataCommand();
    command.setDictValue("existing_value");

    when(dictDataRepository.findById(1L)).thenReturn(Optional.of(dictData));
    when(dictDataRepository.findByDictTypeAndDictValue("user_status", "existing_value"))
        .thenReturn(
            Optional.of(
                DictData.create(1L, 2, "其他", "existing_value", "user_status", "N", null, 0, null)));

    BusinessException exception =
        assertThrows(BusinessException.class, () -> dictService.updateDictData(1L, command));

    assertTrue(exception.getMessage().contains("当前字典类型下新字典键值已存在"));
  }

  @Test
  @DisplayName("删除字典数据成功")
  void deleteDictData_success() {
    when(dictDataRepository.findById(1L)).thenReturn(Optional.of(dictData));

    dictService.deleteDictData(1L);

    verify(dictDataRepository).deleteById(1L);
  }

  @Test
  @DisplayName("删除字典数据失败-字典数据不存在")
  void deleteDictData_notFound() {
    when(dictDataRepository.findById(1L)).thenReturn(Optional.empty());

    BusinessException exception =
        assertThrows(BusinessException.class, () -> dictService.deleteDictData(1L));

    assertTrue(exception.getMessage().contains("字典数据不存在"));
  }

  @Test
  @DisplayName("根据ID获取字典数据成功")
  void getDictDataById_success() {
    when(dictDataRepository.findById(1L)).thenReturn(Optional.of(dictData));
    DictDataDto dummyDto =
        new DictDataDto(1L, 1L, 1, "启用", "1", "user_status", "N", "success", 0, "备注", null);
    when(dictAssembler.toDataDto(any(DictData.class))).thenReturn(dummyDto);

    Optional<DictDataDto> result = dictService.getDictDataById(1L);

    assertTrue(result.isPresent());
    assertEquals("启用", result.get().dictLabel());
  }

  @Test
  @DisplayName("根据ID获取字典数据-不存在返回空")
  void getDictDataById_notFound() {
    when(dictDataRepository.findById(1L)).thenReturn(Optional.empty());

    Optional<DictDataDto> result = dictService.getDictDataById(1L);

    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("分页查询字典数据成功")
  void pageDictData_success() {
    final PageQuery pageQuery = new PageQuery(1, 10);
    Page<DictData> page = new PageImpl<>(Arrays.asList(dictData));

    when(dictDataRepository.findByDictType(eq("user_status"), any(Pageable.class)))
        .thenReturn(page);
    DictDataDto dummyDto =
        new DictDataDto(1L, 1L, 1, "启用", "1", "user_status", "N", "success", 0, "备注", null);
    when(dictAssembler.toDataDtoList(any())).thenReturn(java.util.Arrays.asList(dummyDto));

    PageResult<DictDataDto> result = dictService.pageDictData("user_status", pageQuery);

    assertNotNull(result);
    assertEquals(1, result.total());
    assertEquals(1, result.list().size());
  }

  @Test
  @DisplayName("根据类型获取可用字典数据-从数据库查询")
  void getAvailableDictDataByType_fromDb() {
    when(dictDataRepository.findByDictTypeAndStatusOrderByDictSortAsc("user_status", 0))
        .thenReturn(Arrays.asList(dictData));
    DictDataDto dummyDto =
        new DictDataDto(1L, 1L, 1, "启用", "1", "user_status", "N", "success", 0, "备注", null);
    when(dictAssembler.toDataDtoList(any())).thenReturn(java.util.Arrays.asList(dummyDto));

    List<DictDataDto> result = dictService.getAvailableDictDataByType("user_status");

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("启用", result.get(0).dictLabel());
  }

  @Test
  @DisplayName("根据类型获取可用字典数据-从缓存获取")
  void getAvailableDictDataByType_fromCache() {
    when(dictDataRepository.findByDictTypeAndStatusOrderByDictSortAsc("user_status", 0))
        .thenReturn(Arrays.asList(dictData));
    DictDataDto dummyDto =
        new DictDataDto(1L, 1L, 1, "启用", "1", "user_status", "N", "success", 0, "备注", null);
    when(dictAssembler.toDataDtoList(any())).thenReturn(java.util.Arrays.asList(dummyDto));

    List<DictDataDto> result1 = dictService.getAvailableDictDataByType("user_status");
    assertNotNull(result1);

    List<DictDataDto> result2 = dictService.getAvailableDictDataByType("user_status");
    assertNotNull(result2);

    verify(dictDataRepository, times(1))
        .findByDictTypeAndStatusOrderByDictSortAsc("user_status", 0);
  }

  @Test
  @DisplayName("根据类型获取可用字典数据-空结果")
  void getAvailableDictDataByType_empty() {
    when(dictDataRepository.findByDictTypeAndStatusOrderByDictSortAsc("empty_type", 0))
        .thenReturn(Collections.emptyList());
    when(dictAssembler.toDataDtoList(any())).thenReturn(Collections.emptyList());

    List<DictDataDto> result = dictService.getAvailableDictDataByType("empty_type");

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("更新字典数据成功-仅修改键值且类型为null")
  void updateDictData_success_changeValueOnly_nullType() {
    UpdateDictDataCommand command = new UpdateDictDataCommand();
    command.setDictValue("2");
    command.setDictType(null);

    when(dictDataRepository.findById(1L)).thenReturn(Optional.of(dictData));
    when(dictDataRepository.findByDictTypeAndDictValue("user_status", "2"))
        .thenReturn(Optional.empty());

    dictService.updateDictData(1L, command);

    verify(dictDataRepository).save(any(DictData.class));
  }

  @Test
  @DisplayName("更新字典数据成功-键值和类型都未改变（显式设置相同值）")
  void updateDictData_success_allSame() {

    UpdateDictDataCommand command = new UpdateDictDataCommand();
    command.setDictValue("1"); // Current value
    command.setDictType("user_status"); // Current type
    command.setDictLabel("新标签");

    when(dictDataRepository.findById(1L)).thenReturn(Optional.of(dictData));

    dictService.updateDictData(1L, command);

    verify(dictDataRepository).save(any(DictData.class));
  }
}
