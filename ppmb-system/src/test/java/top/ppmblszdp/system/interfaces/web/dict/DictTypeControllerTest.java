package top.ppmblszdp.system.interfaces.web.dict;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
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
import top.ppmblszdp.system.interfaces.web.dict.dto.CreateDictTypeCommand;
import top.ppmblszdp.system.interfaces.web.dict.dto.DictTypeDto;
import top.ppmblszdp.system.interfaces.web.dict.dto.UpdateDictTypeCommand;

@ExtendWith(MockitoExtension.class)
@DisplayName("字典类型控制器单元测试")
class DictTypeControllerTest {

  private MockMvc mockMvc;

  @Mock private DictApplicationService dictApplicationService;

  @InjectMocks private DictTypeController dictTypeController;

  private final ObjectMapper objectMapper = new ObjectMapper();

  private DictTypeDto dictTypeDto;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(dictTypeController).build();

    dictTypeDto = new DictTypeDto(1L, "用户状态", "user_status", "N", 0, "用户状态字典", null, null);
  }

  @Test
  @DisplayName("创建字典类型成功")
  void createDictType_success() throws Exception {
    CreateDictTypeCommand command = new CreateDictTypeCommand();
    command.setDictName("用户状态");
    command.setDictType("user_status");
    command.setSystemFlag("N");
    command.setStatus(0);
    command.setRemark("备注");

    when(dictApplicationService.createDictType(any(CreateDictTypeCommand.class)))
        .thenReturn(dictTypeDto);

    mockMvc
        .perform(
            post("/dict-types")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.dictType").value("user_status"))
        .andExpect(jsonPath("$.data.dictName").value("用户状态"));
  }

  @Test
  @DisplayName("创建字典类型失败-参数校验失败")
  void createDictType_validationFail() throws Exception {
    CreateDictTypeCommand command = new CreateDictTypeCommand();
    // dictName 和 dictType 为空，应该触发校验失败

    mockMvc
        .perform(
            post("/dict-types")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("更新字典类型成功")
  void updateDictType_success() throws Exception {
    UpdateDictTypeCommand command = new UpdateDictTypeCommand();
    command.setDictName("新名称");
    command.setStatus(1);

    doNothing()
        .when(dictApplicationService)
        .updateDictType(eq(1L), any(UpdateDictTypeCommand.class));

    mockMvc
        .perform(
            put("/dict-types/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("删除字典类型成功")
  void deleteDictType_success() throws Exception {
    doNothing().when(dictApplicationService).deleteDictType(1L);

    mockMvc.perform(delete("/dict-types/1")).andExpect(status().isOk());
  }

  @Test
  @DisplayName("根据ID获取字典类型成功")
  void getDictTypeById_success() throws Exception {
    when(dictApplicationService.getDictTypeById(1L)).thenReturn(Optional.of(dictTypeDto));

    mockMvc
        .perform(get("/dict-types/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.dictType").value("user_status"));
  }

  @Test
  @DisplayName("根据ID获取字典类型-不存在返回空")
  void getDictTypeById_notFound() throws Exception {
    when(dictApplicationService.getDictTypeById(999L)).thenReturn(Optional.empty());

    mockMvc.perform(get("/dict-types/999")).andExpect(status().isOk());
  }

  @Test
  @DisplayName("分页查询字典类型成功")
  void pageDictTypes_success() throws Exception {
    PageResult<DictTypeDto> pageResult = PageResult.of(1, Arrays.asList(dictTypeDto), 1, 10);

    when(dictApplicationService.pageDictTypes(any())).thenReturn(pageResult);

    mockMvc
        .perform(get("/dict-types/page").param("pageNum", "1").param("pageSize", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.total").value(1))
        .andExpect(jsonPath("$.data.list[0].dictType").value("user_status"));
  }
}
