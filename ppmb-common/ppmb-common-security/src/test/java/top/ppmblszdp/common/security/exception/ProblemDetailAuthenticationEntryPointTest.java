package top.ppmblszdp.common.security.exception;

import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;

@DisplayName("AuthenticationEntryPoint 测试")
class ProblemDetailAuthenticationEntryPointTest {

  private ProblemDetailAuthenticationEntryPoint entryPoint;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
    entryPoint = new ProblemDetailAuthenticationEntryPoint(objectMapper);
  }

  @Test
  @DisplayName("处理 AuthenticationException 并返回 ProblemDetail 格式")
  void commence() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    AuthenticationException exception = mock(AuthenticationException.class);
    when(exception.getMessage()).thenReturn("Unauthorized");

    when(request.getRequestURI()).thenReturn("/test");
    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    entryPoint.commence(request, response, exception);

    verify(response).setStatus(HttpStatus.UNAUTHORIZED.value());
    verify(response).setContentType("application/problem+json");
    verify(response).setCharacterEncoding("UTF-8");
  }
}
