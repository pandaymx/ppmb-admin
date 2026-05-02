package top.ppmblszdp.common.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
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

  @Test
  @DisplayName("调用私有构造方法应抛出 UnsupportedOperationException")
  void shouldThrowExceptionWhenInstantiating() throws Exception {
    java.lang.reflect.Constructor<CodeUtils> constructor = CodeUtils.class.getDeclaredConstructor();
    constructor.setAccessible(true);
    java.lang.reflect.InvocationTargetException exception =
        assertThrows(java.lang.reflect.InvocationTargetException.class, constructor::newInstance);
    assertTrue(exception.getCause() instanceof UnsupportedOperationException);
    assertEquals(
        "This is a utility class and cannot be instantiated", exception.getCause().getMessage());
  }

  @Test
  @DisplayName("encodeBase62 负数和最小值")
  void testEncodeBase62NegativeAndMin() {
    assertEquals(CodeUtils.encodeBase62(123456789L), CodeUtils.encodeBase62(-123456789L));
    String minValEncoded = CodeUtils.encodeBase62(Long.MIN_VALUE);
    assertNotNull(minValEncoded);
    assertFalse(minValEncoded.isEmpty());
  }

  @Test
  @DisplayName("SnowflakeIdWorker 构造参数异常")
  void testSnowflakeIdWorkerExceptions() throws Exception {
    Class<?> workerClass = Class.forName("top.ppmblszdp.common.util.CodeUtils$SnowflakeIdWorker");
    java.lang.reflect.Constructor<?> constructor =
        workerClass.getDeclaredConstructor(long.class, long.class);
    constructor.setAccessible(true);

    var ex1 =
        assertThrows(
            java.lang.reflect.InvocationTargetException.class,
            () -> constructor.newInstance(32L, 0L));
    assertTrue(ex1.getCause() instanceof IllegalArgumentException);

    var ex2 =
        assertThrows(
            java.lang.reflect.InvocationTargetException.class,
            () -> constructor.newInstance(-1L, 0L));
    assertTrue(ex2.getCause() instanceof IllegalArgumentException);

    var ex3 =
        assertThrows(
            java.lang.reflect.InvocationTargetException.class,
            () -> constructor.newInstance(0L, 32L));
    assertTrue(ex3.getCause() instanceof IllegalArgumentException);

    var ex4 =
        assertThrows(
            java.lang.reflect.InvocationTargetException.class,
            () -> constructor.newInstance(0L, -1L));
    assertTrue(ex4.getCause() instanceof IllegalArgumentException);
  }

  @Test
  @DisplayName("SnowflakeIdWorker 时钟回拨异常")
  void testSnowflakeIdWorkerClockMovedBackwards() throws Exception {
    Class<?> workerClass = Class.forName("top.ppmblszdp.common.util.CodeUtils$SnowflakeIdWorker");
    java.lang.reflect.Constructor<?> constructor =
        workerClass.getDeclaredConstructor(long.class, long.class);
    constructor.setAccessible(true);
    Object worker = constructor.newInstance(0L, 0L);

    java.lang.reflect.Field lastTimestampField = workerClass.getDeclaredField("lastTimestamp");
    lastTimestampField.setAccessible(true);
    lastTimestampField.set(worker, System.currentTimeMillis() + 100000L);

    java.lang.reflect.Method nextIdMethod = workerClass.getDeclaredMethod("nextId");
    nextIdMethod.setAccessible(true);

    var ex =
        assertThrows(
            java.lang.reflect.InvocationTargetException.class, () -> nextIdMethod.invoke(worker));
    assertTrue(ex.getCause() instanceof top.ppmblszdp.common.exception.BusinessException);
  }

  @Test
  @DisplayName("SnowflakeIdWorker 序列溢出处理")
  void testSnowflakeIdWorkerSequenceOverflow() throws Exception {
    Class<?> workerClass = Class.forName("top.ppmblszdp.common.util.CodeUtils$SnowflakeIdWorker");
    java.lang.reflect.Constructor<?> constructor =
        workerClass.getDeclaredConstructor(long.class, long.class);
    constructor.setAccessible(true);
    Object worker = constructor.newInstance(0L, 0L);

    java.lang.reflect.Method nextIdMethod = workerClass.getDeclaredMethod("nextId");
    nextIdMethod.setAccessible(true);

    // Mock lastTimestamp and sequence to trigger overflow
    java.lang.reflect.Field lastTimestampField = workerClass.getDeclaredField("lastTimestamp");
    lastTimestampField.setAccessible(true);
    long now = System.currentTimeMillis();
    lastTimestampField.set(worker, now);

    java.lang.reflect.Field sequenceField = workerClass.getDeclaredField("sequence");
    sequenceField.setAccessible(true);
    sequenceField.set(worker, 4095L); // Max sequence for 12 bits

    // The next call should trigger sequence = 0 and tilNextMillis
    long id = (long) nextIdMethod.invoke(worker);
    assertTrue(id > 0);
    assertEquals(0L, sequenceField.get(worker));
    assertTrue((long) lastTimestampField.get(worker) >= now);
  }

  @Test
  @DisplayName("SnowflakeIdWorker 相同毫秒内生成多个 ID")
  void testSnowflakeIdWorkerSameMillisecond() throws Exception {
    Class<?> workerClass = Class.forName("top.ppmblszdp.common.util.CodeUtils$SnowflakeIdWorker");
    java.lang.reflect.Constructor<?> constructor =
        workerClass.getDeclaredConstructor(long.class, long.class);
    constructor.setAccessible(true);
    Object worker = constructor.newInstance(0L, 0L);

    java.lang.reflect.Method nextIdMethod = workerClass.getDeclaredMethod("nextId");
    nextIdMethod.setAccessible(true);

    long id1 = (long) nextIdMethod.invoke(worker);
    long id2 = (long) nextIdMethod.invoke(worker);

    assertNotEquals(id1, id2);
  }
}
