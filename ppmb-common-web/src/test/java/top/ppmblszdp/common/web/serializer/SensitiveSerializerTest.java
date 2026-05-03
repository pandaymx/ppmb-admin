package top.ppmblszdp.common.web.serializer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.util.Collections;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import top.ppmblszdp.common.web.annotation.Sensitive;
import top.ppmblszdp.common.web.annotation.SensitiveStrategy;

class SensitiveSerializerTest {

  private SensitiveSerializer serializer;
  private JsonGenerator gen;
  private SerializerProvider prov;

  @BeforeEach
  void setUp() {
    serializer = new SensitiveSerializer();
    gen = mock(JsonGenerator.class);
    prov = mock(SerializerProvider.class);
    SecurityContextHolder.clearContext();
  }

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void testNullOrEmpty() throws IOException {
    serializer.serialize(null, gen, prov);
    verify(gen).writeString((String) null);

    serializer.serialize("", gen, prov);
    verify(gen).writeString("");
  }

  @Test
  void testNoPermissionMasksData() throws Exception {
    BeanProperty property = mock(BeanProperty.class);
    Sensitive sensitive = mock(Sensitive.class);
    when(sensitive.strategy()).thenReturn(SensitiveStrategy.PHONE);
    when(sensitive.permission()).thenReturn("sys:user:view");

    com.fasterxml.jackson.databind.JavaType javaType =
        mock(com.fasterxml.jackson.databind.JavaType.class);
    when(javaType.getRawClass()).thenReturn((Class) String.class);

    when(property.getType()).thenReturn(javaType);
    when(property.getAnnotation(Sensitive.class)).thenReturn(sensitive);

    JsonSerializer<?> contextualSerializer = serializer.createContextual(prov, property);
    assertNotNull(contextualSerializer);

    ((SensitiveSerializer) contextualSerializer).serialize("13812345678", gen, prov);
    verify(gen).writeString("138****5678");
  }

  @Test
  void testHasPermissionShowsData() throws Exception {
    SecurityContextHolder.getContext()
        .setAuthentication(
            new UsernamePasswordAuthenticationToken(
                "user",
                "pass",
                Collections.singleton(new SimpleGrantedAuthority("sys:user:view"))));

    BeanProperty property = mock(BeanProperty.class);
    Sensitive sensitive = mock(Sensitive.class);
    when(sensitive.strategy()).thenReturn(SensitiveStrategy.PHONE);
    when(sensitive.permission()).thenReturn("sys:user:view");

    com.fasterxml.jackson.databind.JavaType javaType =
        mock(com.fasterxml.jackson.databind.JavaType.class);
    when(javaType.getRawClass()).thenReturn((Class) String.class);

    when(property.getType()).thenReturn(javaType);
    when(property.getAnnotation(Sensitive.class)).thenReturn(sensitive);

    JsonSerializer<?> contextualSerializer = serializer.createContextual(prov, property);

    ((SensitiveSerializer) contextualSerializer).serialize("13812345678", gen, prov);
    verify(gen).writeString("13812345678");
  }

  @Test
  void testOtherStrategies() {
    assertEquals(
        "1234****5678", SensitiveStrategy.ID_CARD.getDesensitizer().apply("123456789012345678"));
    assertEquals("******", SensitiveStrategy.PASSWORD.getDesensitizer().apply("mysecret"));
    assertEquals("***", SensitiveStrategy.HIDE.getDesensitizer().apply("secretdata"));
    assertEquals("custom", SensitiveStrategy.CUSTOM.getDesensitizer().apply("custom"));
  }
}
