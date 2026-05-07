package top.ppmblszdp.system.interfaces.web.dict;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import top.ppmblszdp.common.api.PageResult;
import top.ppmblszdp.system.application.service.dict.DictApplicationService;
import top.ppmblszdp.system.interfaces.web.dict.dto.CreateDictDataCommand;
import top.ppmblszdp.system.interfaces.web.dict.dto.DictDataDto;
import top.ppmblszdp.system.interfaces.web.dict.dto.UpdateDictDataCommand;

@ExtendWith(MockitoExtension.class)
@DisplayName("字典数据控制器单元测试")
class DictDataControllerTest {

  private MockMvc mockMvc;

  @Mock private DictApplicationService dictApplicationService;

  @InjectMocks private DictDataController dictDataController;

  private final ObjectMapper objectMapper = new ObjectMapper();

  private DictDataDto dictDataDto;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(dictDataController).build();

    dictDataDto =
        new DictDataDto(1L, 1L, 1, "启用", "1", "user_status", "N", "success", 0, "启用状态", null);
  }

  @Test
  @DisplayName("创建字典数据成功")
  void createDictData_success() throws Exception {
    CreateDictDataCommand command = new CreateDictDataCommand();
    command.setParentId(1L);
    command.setDictSort(1);
    command.setDictLabel("启用");
    command.setDictValue("1");
    command.setDictType("user_status");
    command.setIsDefault("N");
    command.setStatus(0);
    command.setRemark("备注");

    when(dictApplicationService.createDictData(any(CreateDictDataCommand.class)))
        .thenReturn(dictDataDto);

    mockMvc
        .perform(
            post("/dict-data")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.dictLabel").value("启用"))
        .andExpect(jsonPath("$.data.dictValue").value("1"));
  }

  @Test
  @DisplayName("创建字典数据失败-参数校验失败")
  void createDictData_validationFail() throws Exception {
    CreateDictDataCommand command = new CreateDictDataCommand();
    // 缺少必填字段

    mockMvc
        .perform(
            post("/dict-data")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("更新字典数据成功")
  void updateDictData_success() throws Exception {
    UpdateDictDataCommand command = new UpdateDictDataCommand();
    command.setDictLabel("新标签");
    command.setStatus(1);

    doNothing()
        .when(dictApplicationService)
        .updateDictData(eq(1L), any(UpdateDictDataCommand.class));

    mockMvc
        .perform(
            put("/dict-data/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("删除字典数据成功")
  void deleteDictData_success() throws Exception {
    doNothing().when(dictApplicationService).deleteDictData(1L);

    mockMvc.perform(delete("/dict-data/1")).andExpect(status().isOk());
  }

  @Test
  @DisplayName("根据ID获取字典数据成功")
  void getDictDataById_success() throws Exception {
    when(dictApplicationService.getDictDataById(1L)).thenReturn(Optional.of(dictDataDto));

    mockMvc
        .perform(get("/dict-data/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.dictLabel").value("启用"));
  }

  @Test
  @DisplayName("根据ID获取字典数据-不存在返回空")
  void getDictDataById_notFound() throws Exception {
    when(dictApplicationService.getDictDataById(999L)).thenReturn(Optional.empty());

    mockMvc.perform(get("/dict-data/999")).andExpect(status().isOk());
  }

  @Test
  @DisplayName("分页查询字典数据成功")
  void pageDictData_success() throws Exception {
    PageResult<DictDataDto> pageResult = PageResult.of(1, Arrays.asList(dictDataDto), 1, 10);

    when(dictApplicationService.pageDictData(eq("user_status"), any())).thenReturn(pageResult);

    mockMvc
        .perform(
            get("/dict-data/page")
                .param("dictType", "user_status")
                .param("pageNum", "1")
                .param("pageSize", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.total").value(1))
        .andExpect(jsonPath("$.data.list[0].dictValue").value("1"));
  }

  @Test
  @DisplayName("根据字典类型获取可用数据列表成功")
  void getDictDataByType_success() throws Exception {
    when(dictApplicationService.getAvailableDictDataByType("user_status"))
        .thenReturn(Arrays.asList(dictDataDto));

    mockMvc
        .perform(get("/dict-data/type/user_status"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data[0].dictLabel").value("启用"))
        .andExpect(jsonPath("$.data[0].dictValue").value("1"));
  }

  @Test
  @DisplayName("根据字典类型获取可用数据列表-空结果")
  void getDictDataByType_empty() throws Exception {
    when(dictApplicationService.getAvailableDictDataByType("empty_type"))
        .thenReturn(Collections.emptyList());

    mockMvc
        .perform(get("/dict-data/type/empty_type"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data").isEmpty());
  }
}
