package top.ppmblszdp.common.web.annotation;

import java.util.function.UnaryOperator;

public enum SensitiveStrategy {
  PHONE(s -> s.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2")),
  ID_CARD(s -> s.replaceAll("(\\d{4})\\d{10}(\\w{4})", "$1****$2")),
  PASSWORD(s -> "******"),
  HIDE(s -> "***"),
  CUSTOM(s -> s);

  private final UnaryOperator<String> desensitizer;

  SensitiveStrategy(UnaryOperator<String> desensitizer) {
    this.desensitizer = desensitizer;
  }

  public UnaryOperator<String> getDesensitizer() {
    return desensitizer;
  }
}
