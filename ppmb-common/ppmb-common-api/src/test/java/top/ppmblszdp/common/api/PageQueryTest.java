package top.ppmblszdp.common.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("分页查询参数测试")
class PageQueryTest {

  private Validator validator;

  @BeforeEach
  void setUp() {
    try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
      validator = factory.getValidator();
    }
  }

  @Test
  @DisplayName("应该使用默认值")
  void testDefaultValues() {
    PageQuery query = new PageQuery(null, null);
    assertEquals(1, query.pageNum());
    assertEquals(10, query.pageSize());

    Set<ConstraintViolation<PageQuery>> violations = validator.validate(query);
    assertTrue(violations.isEmpty());
  }

  @Test
  @DisplayName("应该支持有效值")
  void testValidValues() {
    PageQuery query = new PageQuery(2, 20);
    assertEquals(2, query.pageNum());
    assertEquals(20, query.pageSize());

    Set<ConstraintViolation<PageQuery>> violations = validator.validate(query);
    assertTrue(violations.isEmpty());
  }

  @Test
  @DisplayName("无效页码应该触发验证错误")
  void testInvalidPageNum() {
    PageQuery query = new PageQuery(0, 10);

    Set<ConstraintViolation<PageQuery>> violations = validator.validate(query);
    assertFalse(violations.isEmpty());
    assertEquals(1, violations.size());
    assertEquals("页码必须大于等于1", violations.iterator().next().getMessage());
  }

  @Test
  @DisplayName("无效每页大小应该触发验证错误")
  void testInvalidPageSize() {
    PageQuery query = new PageQuery(1, 0);

    Set<ConstraintViolation<PageQuery>> violations = validator.validate(query);
    assertFalse(violations.isEmpty());
    assertEquals(1, violations.size());
    assertEquals("每页大小必须大于等于1", violations.iterator().next().getMessage());
  }
}
