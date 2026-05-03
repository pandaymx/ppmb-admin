package top.ppmblszdp.common.doc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("OpenApiConfiguration 配置类测试")
class OpenApiConfigurationTest {

  @Test
  @DisplayName("验证 OpenAPI Bean 成功创建并包含正确信息")
  void shouldCreateOpenApiBean() {
    OpenApiConfiguration configuration = new OpenApiConfiguration();
    OpenAPI openApi = configuration.customOpenApi();

    assertNotNull(openApi);
    assertEquals("PPMB API", openApi.getInfo().getTitle());
    assertEquals("1.0.0", openApi.getInfo().getVersion());
    assertNotNull(openApi.getComponents().getSecuritySchemes().get("bearerAuth"));
  }
}
