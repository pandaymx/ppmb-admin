package top.ppmblszdp.system.controller;

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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import top.ppmblszdp.system.domain.entity.Department;
import top.ppmblszdp.system.service.DepartmentService;

@ExtendWith(MockitoExtension.class)
public class DepartmentControllerTest {

  private MockMvc mockMvc;

  @Mock private DepartmentService departmentService;

  @InjectMocks private DepartmentController departmentController;

  private ObjectMapper objectMapper = new ObjectMapper();

  private Department department;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(departmentController).build();

    department = new Department();
    department.setId(1L);
    department.setDeptName("IT Department");
    department.setDeptCode("IT01");
    department.setStatus(0);
  }

  @Test
  void createDepartment() throws Exception {
    when(departmentService.createDepartment(any(Department.class))).thenReturn(department);

    mockMvc
        .perform(
            post("/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(department)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.deptName").value("IT Department"));
  }

  @Test
  void getDepartmentById() throws Exception {
    when(departmentService.getDepartmentById(1L)).thenReturn(Optional.of(department));

    mockMvc
        .perform(get("/departments/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.deptName").value("IT Department"));
  }

  @Test
  void getAllDepartments() throws Exception {
    when(departmentService.getAllDepartments()).thenReturn(Arrays.asList(department));

    mockMvc
        .perform(get("/departments"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].deptName").value("IT Department"));
  }

  @Test
  void updateDepartment() throws Exception {
    when(departmentService.updateDepartment(eq(1L), any(Department.class))).thenReturn(department);

    mockMvc
        .perform(
            put("/departments/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(department)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.deptName").value("IT Department"));
  }

  @Test
  void deleteDepartment() throws Exception {
    doNothing().when(departmentService).deleteDepartment(1L);

    mockMvc.perform(delete("/departments/1")).andExpect(status().isNoContent());
  }
}
