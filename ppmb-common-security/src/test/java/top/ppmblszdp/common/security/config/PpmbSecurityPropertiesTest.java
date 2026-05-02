package top.ppmblszdp.common.security.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("安全配置属性测试")
class PpmbSecurityPropertiesTest {

  @Test
  @DisplayName("验证默认属性值")
  void defaultProperties() {
    PpmbSecurityProperties props = new PpmbSecurityProperties();
    assertFalse(props.isGatewayMode());
    assertEquals("Bearer ", props.getJwt().getPrefix());
    assertEquals("Authorization", props.getJwt().getHeaderName());
    assertEquals("X-User-ID", props.getHeader().getUserId());
    assertEquals("X-User-Name", props.getHeader().getUsername());
    assertEquals("X-User-Roles", props.getHeader().getRoles());
  }

  @Test
  @DisplayName("验证属性修改")
  void modifyProperties() {
    PpmbSecurityProperties props = new PpmbSecurityProperties();
    props.setGatewayMode(true);
    props.getJwt().setSecret("secret");
    props.getHeader().setUserId("Custom-User-ID");

    assertTrue(props.isGatewayMode());
    assertEquals("secret", props.getJwt().getSecret());
    assertEquals("Custom-User-ID", props.getHeader().getUserId());
  }
}
