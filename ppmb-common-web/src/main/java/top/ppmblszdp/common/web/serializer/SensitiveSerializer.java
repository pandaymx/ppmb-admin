package top.ppmblszdp.common.web.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import java.io.IOException;
import java.util.Objects;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import top.ppmblszdp.common.web.annotation.Sensitive;
import top.ppmblszdp.common.web.annotation.SensitiveStrategy;

public class SensitiveSerializer extends JsonSerializer<String> implements ContextualSerializer {

  private SensitiveStrategy strategy;
  private String permission;

  @Override
  public void serialize(String value, JsonGenerator gen, SerializerProvider serializers)
      throws IOException {
    if (!StringUtils.hasText(value)) {
      gen.writeString(value);
      return;
    }

    if (StringUtils.hasText(permission) && hasPermission(permission)) {
      gen.writeString(value);
    } else {
      gen.writeString(strategy.getDesensitizer().apply(value));
    }
  }

  private boolean hasPermission(String requiredPermission) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      return false;
    }
    for (GrantedAuthority authority : authentication.getAuthorities()) {
      if (Objects.equals(requiredPermission, authority.getAuthority())) {
        return true;
      }
    }
    return false;
  }

  @Override
  public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property)
      throws JsonMappingException {
    if (property != null) {
      if (Objects.equals(property.getType().getRawClass(), String.class)) {
        Sensitive sensitive = property.getAnnotation(Sensitive.class);
        if (sensitive == null) {
          sensitive = property.getContextAnnotation(Sensitive.class);
        }
        if (sensitive != null) {
          SensitiveSerializer serializer = new SensitiveSerializer();
          serializer.strategy = sensitive.strategy();
          serializer.permission = sensitive.permission();
          return serializer;
        }
      }
      return prov.findValueSerializer(property.getType(), property);
    }
    return prov.findNullValueSerializer(null);
  }
}
