package top.ppmblszdp.system.interfaces.web.dept;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import top.ppmblszdp.system.application.service.dept.DepartmentApplicationService;
import top.ppmblszdp.system.interfaces.web.dept.dto.DepartmentDto;

@ExtendWith(MockitoExtension.class)
@DisplayName("部门管理接口测试")
class DepartmentControllerTest {

  private MockMvc mockMvc;

  @Mock private DepartmentApplicationService departmentService;

  @InjectMocks private DepartmentController departmentController;

  private final ObjectMapper objectMapper = new ObjectMapper();

  private DepartmentDto departmentDto;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(departmentController).build();

    departmentDto =
        new DepartmentDto(
            1L, null, "IT Department", "IT01", "IT", "it@example.com", "123456", 1L, 1, 0);
  }

  @Test
  @DisplayName("创建部门")
  void createDepartment() throws Exception {
    when(departmentService.createDepartment(any(DepartmentDto.class))).thenReturn(departmentDto);

    mockMvc
        .perform(
            post("/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(departmentDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.deptName").value("IT Department"));
  }

  @Test
  @DisplayName("根据 ID 获取部门")
  void getDepartmentById() throws Exception {
    when(departmentService.getDepartmentById(1L)).thenReturn(Optional.of(departmentDto));

    mockMvc
        .perform(get("/departments/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.deptName").value("IT Department"));
  }

  @Test
  @DisplayName("获取所有部门")
  void getAllDepartments() throws Exception {
    when(departmentService.getAllDepartments()).thenReturn(Arrays.asList(departmentDto));

    mockMvc
        .perform(get("/departments"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data[0].id").value(1))
        .andExpect(jsonPath("$.data[0].deptName").value("IT Department"));
  }

  @Test
  @DisplayName("更新部门")
  void updateDepartment() throws Exception {
    when(departmentService.updateDepartment(eq(1L), any(DepartmentDto.class)))
        .thenReturn(departmentDto);

    mockMvc
        .perform(
            put("/departments/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(departmentDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.deptName").value("IT Department"));
  }

  @Test
  @DisplayName("删除部门")
  void deleteDepartment() throws Exception {
    doNothing().when(departmentService).deleteDepartment(1L);

    mockMvc.perform(delete("/departments/1")).andExpect(status().isOk());
  }
}
