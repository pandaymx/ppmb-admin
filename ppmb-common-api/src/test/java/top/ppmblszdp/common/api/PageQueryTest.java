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
import org.junit.jupiter.api.Test;

class PageQueryTest {

  private Validator validator;

  @BeforeEach
  void setUp() {
    try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
      validator = factory.getValidator();
    }
  }

  @Test
  void testDefaultValues() {
    PageQuery query = new PageQuery();
    assertEquals(1, query.getPageNum());
    assertEquals(10, query.getPageSize());

    Set<ConstraintViolation<PageQuery>> violations = validator.validate(query);
    assertTrue(violations.isEmpty());
  }

  @Test
  void testValidValues() {
    PageQuery query = new PageQuery();
    query.setPageNum(2);
    query.setPageSize(20);

    Set<ConstraintViolation<PageQuery>> violations = validator.validate(query);
    assertTrue(violations.isEmpty());
  }

  @Test
  void testInvalidPageNum() {
    PageQuery query = new PageQuery();
    query.setPageNum(0);

    Set<ConstraintViolation<PageQuery>> violations = validator.validate(query);
    assertFalse(violations.isEmpty());
    assertEquals(1, violations.size());
    assertEquals("页码必须大于等于1", violations.iterator().next().getMessage());
  }

  @Test
  void testInvalidPageSize() {
    PageQuery query = new PageQuery();
    query.setPageSize(0);

    Set<ConstraintViolation<PageQuery>> violations = validator.validate(query);
    assertFalse(violations.isEmpty());
    assertEquals(1, violations.size());
    assertEquals("每页大小必须大于等于1", violations.iterator().next().getMessage());
  }
}
