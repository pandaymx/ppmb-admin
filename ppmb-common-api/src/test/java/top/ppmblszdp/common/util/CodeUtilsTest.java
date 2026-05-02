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

    String code5 = CodeUtils.generateNumericCode(5);
    assertEquals(5, code5.length());
    assertTrue(code5.matches("\\d{5}"));

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

    // Loop to ensure both upper and lower cases are covered in generateAlphabeticCode
    boolean seenUpper = false;
    boolean seenLower = false;
    for (int i = 0; i < 100; i++) {
      String s = CodeUtils.generateAlphabeticCode(4);
      for (char c : s.toCharArray()) {
        if (Character.isUpperCase(c)) {
          seenUpper = true;
        }
        if (Character.isLowerCase(c)) {
          seenLower = true;
        }
      }
      if (seenUpper && seenLower) {
        break;
      }
    }
    assertTrue(seenUpper, "Should have generated at least one uppercase letter");
    assertTrue(seenLower, "Should have generated at least one lowercase letter");

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

    // Test getSnowflakeId initialization when already initialized
    long id = CodeUtils.getSnowflakeId();
    assertTrue(id > 0);
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

  @Test
  @DisplayName("createSnowflakeIdWorker 无法获取主机信息时的回退处理")
  void testCreateSnowflakeIdWorkerUnknownHostException() throws Exception {
    // Reset lazy initialized worker using reflection
    java.lang.reflect.Field workerRefField =
        CodeUtils.class.getDeclaredField("SNOWFLAKE_ID_WORKER");
    workerRefField.setAccessible(true);
    java.util.concurrent.atomic.AtomicReference<?> workerRef =
        (java.util.concurrent.atomic.AtomicReference<?>) workerRefField.get(null);
    workerRef.set(null);

    try (var mockedInetAddress = org.mockito.Mockito.mockStatic(java.net.InetAddress.class)) {
      mockedInetAddress
          .when(java.net.InetAddress::getLocalHost)
          .thenThrow(new java.net.UnknownHostException());

      long id = CodeUtils.getSnowflakeId();
      assertTrue(id > 0);
    }
  }

  @Test
  @DisplayName("Snowflake 序列溢出处理")
  void testSnowflakeSequenceOverflow() throws Exception {
    // 强制获取单例 worker
    CodeUtils.getSnowflakeId();
    java.lang.reflect.Field field = CodeUtils.class.getDeclaredField("SNOWFLAKE_ID_WORKER");
    field.setAccessible(true);
    java.util.concurrent.atomic.AtomicReference<?> ref =
        (java.util.concurrent.atomic.AtomicReference<?>) field.get(null);
    Object worker = ref.get();

    java.lang.reflect.Field seqField = worker.getClass().getDeclaredField("sequence");
    seqField.setAccessible(true);
    // 设置为最大序列号
    seqField.set(worker, 4095L);

    // 下一次调用应触发 tilNextMillis
    long id = CodeUtils.getSnowflakeId();
    assertTrue(id > 0);
  }

  @Test
  @DisplayName("getSnowflakeId 并发初始化逻辑覆盖")
  void testGetSnowflakeIdConcurrentInitialization() throws Exception {
    // 1. 重置 SNOWFLAKE_ID_WORKER 为 null
    java.lang.reflect.Field field = CodeUtils.class.getDeclaredField("SNOWFLAKE_ID_WORKER");
    field.setAccessible(true);
    java.util.concurrent.atomic.AtomicReference<Object> ref =
        (java.util.concurrent.atomic.AtomicReference<Object>) field.get(null);
    ref.set(null);

    // 2. 模拟一个线程已经抢先初始化了 worker
    java.lang.reflect.Method createMethod =
        CodeUtils.class.getDeclaredMethod("createSnowflakeIdWorker");
    createMethod.setAccessible(true);
    Object existingWorker = createMethod.invoke(null);

    // 3. 使用 Mockito 模拟 InetAddress.getLocalHost() 的行为来干扰初始化过程
    try (var mockedInetAddress = org.mockito.Mockito.mockStatic(java.net.InetAddress.class)) {
      java.net.InetAddress mockAddress = org.mockito.Mockito.mock(java.net.InetAddress.class);
      org.mockito.Mockito.when(mockAddress.getHostAddress()).thenReturn("127.0.0.1");
      org.mockito.Mockito.when(mockAddress.getHostName()).thenReturn("localhost");

      mockedInetAddress
          .when(java.net.InetAddress::getLocalHost)
          .thenAnswer(
              invocation -> {
                // 在 createSnowflakeIdWorker 运行过程中，我们偷偷把 ref 设为已存在的值
                // 这样 compareAndSet(null, newWorker) 就会失败
                ref.compareAndSet(null, existingWorker);
                return mockAddress;
              });

      // 4. 调用 getSnowflakeId()
      // 此时 worker == null 为 true，进入 if 块。
      // 调用 createSnowflakeIdWorker()，触发 mock 的 answer，ref 被设为 existingWorker。
      // compareAndSet(null, newWorker) 失败，进入 else 块执行 ref.get()。
      long id = CodeUtils.getSnowflakeId();
      assertTrue(id > 0);

      // 验证最终使用的确实是抢先设置的那个 worker
      assertEquals(existingWorker, ref.get());
    }
  }
}
