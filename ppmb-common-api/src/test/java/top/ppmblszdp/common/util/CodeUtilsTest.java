package top.ppmblszdp.common.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CodeUtilsTest {

  @Test
  void testUuid() {
    String uuid1 = CodeUtils.uuid();
    String uuid2 = CodeUtils.uuid();
    assertNotNull(uuid1);
    assertNotEquals(uuid1, uuid2);
    assertTrue(uuid1.contains("-"));
    assertEquals(36, uuid1.length());
  }

  @Test
  void testSimpleUuid() {
    String simpleUuid = CodeUtils.simpleUuid();
    assertNotNull(simpleUuid);
    assertFalse(simpleUuid.contains("-"));
    assertEquals(32, simpleUuid.length());
  }

  @Test
  void testGenerateNumericCode() {
    String code4 = CodeUtils.generateNumericCode(4);
    assertEquals(4, code4.length());
    assertTrue(code4.matches("\\d{4}"));

    String code6 = CodeUtils.generateNumericCode(6);
    assertEquals(6, code6.length());
    assertTrue(code6.matches("\\d{6}"));

    assertThrows(IllegalArgumentException.class, () -> CodeUtils.generateNumericCode(3));
    assertThrows(IllegalArgumentException.class, () -> CodeUtils.generateNumericCode(7));
  }

  @Test
  void testGenerateAlphabeticCode() {
    String code4 = CodeUtils.generateAlphabeticCode(4);
    assertEquals(4, code4.length());
    assertTrue(code4.matches("[a-zA-Z]{4}"));

    String code6 = CodeUtils.generateAlphabeticCode(6);
    assertEquals(6, code6.length());
    assertTrue(code6.matches("[a-zA-Z]{6}"));

    assertThrows(IllegalArgumentException.class, () -> CodeUtils.generateAlphabeticCode(3));
    assertThrows(IllegalArgumentException.class, () -> CodeUtils.generateAlphabeticCode(7));
  }

  @Test
  void testEncodeBase62() {
    assertEquals("0", CodeUtils.encodeBase62(0));
    assertEquals("1", CodeUtils.encodeBase62(1));
    assertEquals("Z", CodeUtils.encodeBase62(35));
    assertEquals("a", CodeUtils.encodeBase62(36));
    assertEquals("z", CodeUtils.encodeBase62(61));
    assertEquals("10", CodeUtils.encodeBase62(62));

    long testNum = 123456789L;
    String encoded = CodeUtils.encodeBase62(testNum);
    assertNotNull(encoded);
    assertFalse(encoded.isEmpty());
  }

  @Test
  void testGenerateIdCard() {
    String idCard = CodeUtils.generateIdCard();
    assertNotNull(idCard);
    assertEquals(18, idCard.length());

    // Basic pattern match: 17 digits + 1 digit or X
    assertTrue(idCard.matches("\\d{17}[\\dX]"));

    // Verify checksum
    char[] checksumChars = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};
    int[] weights = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};

    int sum = 0;
    for (int i = 0; i < 17; i++) {
      sum += (idCard.charAt(i) - '0') * weights[i];
    }
    char expectedChecksum = checksumChars[sum % 11];

    assertEquals(expectedChecksum, idCard.charAt(17));
  }

  @Test
  void testGetSnowflakeId() {
    long id1 = CodeUtils.getSnowflakeId();
    long id2 = CodeUtils.getSnowflakeId();

    assertTrue(id1 > 0);
    assertTrue(id2 > 0);
    assertNotEquals(id1, id2);

    // Test uniqueness in a small loop
    Set<Long> ids = new HashSet<>();
    for (int i = 0; i < 1000; i++) {
      assertTrue(ids.add(CodeUtils.getSnowflakeId()));
    }
  }
}
