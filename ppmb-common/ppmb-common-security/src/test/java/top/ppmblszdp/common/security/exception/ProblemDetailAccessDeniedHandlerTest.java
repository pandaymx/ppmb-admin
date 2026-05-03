package top.ppmblszdp.common.security.exception;

import static org.mockito.Mockito.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import tools.jackson.databind.ObjectMapper;

@DisplayName("AccessDeniedHandler 测试")
class ProblemDetailAccessDeniedHandlerTest {

  private ProblemDetailAccessDeniedHandler handler;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
    handler = new ProblemDetailAccessDeniedHandler(objectMapper);
  }

  @Test
  @DisplayName("处理 AccessDeniedException 并返回 ProblemDetail 格式")
  void handle() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    AccessDeniedException exception = new AccessDeniedException("Access denied");

    when(request.getRequestURI()).thenReturn("/test");
    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    handler.handle(request, response, exception);

    verify(response).setStatus(HttpStatus.FORBIDDEN.value());
    verify(response).setContentType("application/problem+json");
    verify(response).setCharacterEncoding("UTF-8");
  }
}
