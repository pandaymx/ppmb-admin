package top.ppmblszdp.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import top.ppmblszdp.common.api.CommonResultCode;
import top.ppmblszdp.common.exception.BusinessException;

/** JSON utility class. */
public class JsonUtil {
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  private JsonUtil() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }

  /**
   * Converts an object to JSON string.
   *
   * @param obj the object
   * @return the JSON string
   */
  public static String toJsonString(Object obj) {
    try {
      return OBJECT_MAPPER.writeValueAsString(obj);
    } catch (JsonProcessingException e) {
      throw new BusinessException(CommonResultCode.SYSTEM_ERROR, e);
    }
  }

  /**
   * Parses JSON string to an object of specified type.
   *
   * @param json the JSON string
   * @param clazz the class of the type
   * @param <T> the generic type
   * @return the parsed object
   */
  public static <T> T parseObject(String json, Class<T> clazz) {
    try {
      return OBJECT_MAPPER.readValue(json, clazz);
    } catch (JsonProcessingException e) {
      throw new BusinessException(CommonResultCode.PARAM_ERROR, e);
    }
  }
}
