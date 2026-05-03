package top.ppmblszdp.common.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.http.HttpStatus;
import top.ppmblszdp.common.api.CommonResultCode;
import top.ppmblszdp.common.exception.BusinessException;

/** Utility class for generating various codes (Snowflake, UUID, verification codes, etc.). */
public class CodeUtils {
  private CodeUtils() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }

  private static final Random RANDOM = new SecureRandom();

  // ================== UUID ==================

  /**
   * Generate standard UUID.
   *
   * @return standard UUID string
   */
  public static String uuid() {
    return UUID.randomUUID().toString();
  }

  /**
   * Generate simple UUID (without dashes).
   *
   * @return simple UUID string
   */
  public static String simpleUuid() {
    return UUID.randomUUID().toString().replace("-", "");
  }

  // ================== Verification Codes ==================

  /**
   * Generate numeric verification code (4-6 digits).
   *
   * @param length between 4 and 6
   * @return numeric verification code
   */
  public static String generateNumericCode(int length) {
    if (length < 4 || length > 6) {
      throw new IllegalArgumentException("Length must be between 4 and 6");
    }
    StringBuilder code = new StringBuilder();
    for (int i = 0; i < length; i++) {
      code.append(RANDOM.nextInt(10));
    }
    return code.toString();
  }

  /**
   * Generate alphabetic verification code (4-6 letters).
   *
   * @param length between 4 and 6
   * @return alphabetic verification code
   */
  public static String generateAlphabeticCode(int length) {
    if (length < 4 || length > 6) {
      throw new IllegalArgumentException("Length must be between 4 and 6");
    }
    StringBuilder code = new StringBuilder();
    for (int i = 0; i < length; i++) {
      // Randomly choose uppercase or lowercase letter
      boolean isUpper = RANDOM.nextBoolean();
      char base = isUpper ? 'A' : 'a';
      code.append((char) (base + RANDOM.nextInt(26)));
    }
    return code.toString();
  }

  // ================== 10 to 62 Base Conversion ==================

  private static final String BASE62_CHARS =
      "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

  /**
   * Convert base-10 long to base-62 string.
   *
   * @param num base-10 number
   * @return base-62 string
   */
  public static String encodeBase62(long num) {
    if (num == 0) {
      return "0";
    }
    StringBuilder sb = new StringBuilder();
    // Use unsigned division to handle potential negative numbers (treating them as unsigned)
    // or just handle the positive part. For IDs, they are usually positive.
    // To satisfy S2676, we avoid Math.abs.
    long value = num < 0 ? -num : num;
    // Special case for MIN_VALUE: if num is MIN_VALUE, -num is still MIN_VALUE.
    // However, for base62 encoding of IDs, we typically don't expect MIN_VALUE.
    // If we want to be truly robust with unsigned:
    if (num == Long.MIN_VALUE) {
      // Handle MIN_VALUE specifically if needed, or use unsigned approach
      return encodeBase62Unsigned(num);
    }

    while (value > 0) {
      sb.append(BASE62_CHARS.charAt((int) (value % 62)));
      value /= 62;
    }
    return sb.reverse().toString();
  }

  private static String encodeBase62Unsigned(long num) {
    StringBuilder sb = new StringBuilder();
    long value = num;
    while (Long.compareUnsigned(value, 0) > 0) {
      long remainder = Long.remainderUnsigned(value, 62);
      sb.append(BASE62_CHARS.charAt((int) remainder));
      value = Long.divideUnsigned(value, 62);
    }
    return sb.reverse().toString();
  }

  // ================== ID Card Generator ==================

  /**
   * Automatically generate a random valid 18-digit Chinese ID Card number. (Basic implementation:
   * valid format and checksum, random dob and sequence).
   *
   * @return random valid ID card number
   */
  public static String generateIdCard() {
    StringBuilder idCard = new StringBuilder();

    // 1. Province/City/County (6 digits) - Randomly using a standard prefix
    String[] prefixes = {"110105", "310104", "440106", "330106", "320102", "510104"};
    idCard.append(prefixes[RANDOM.nextInt(prefixes.length)]);

    // 2. Date of birth (8 digits) - Random date between 1970 and 2005
    int year = 1970 + RANDOM.nextInt(36);
    int month = 1 + RANDOM.nextInt(12);
    int day = 1 + RANDOM.nextInt(28); // Keep it simple to avoid invalid dates
    String dob = String.format("%04d%02d%02d", year, month, day);
    idCard.append(dob);

    // 3. Sequence number and gender (3 digits)
    int seq = RANDOM.nextInt(1000);
    idCard.append(String.format("%03d", seq));

    // 4. Checksum
    char checksum = calculateIdCardChecksum(idCard.toString());
    idCard.append(checksum);

    return idCard.toString();
  }

  private static char calculateIdCardChecksum(String idCard17) {
    int[] weights = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
    char[] checksumChars = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};

    int sum = 0;
    for (int i = 0; i < 17; i++) {
      sum += (idCard17.charAt(i) - '0') * weights[i];
    }

    return checksumChars[sum % 11];
  }

  // ================== Snowflake Algorithm ==================

  /** Customized Snowflake ID Generator instance. Lazy initialized. */
  private static final AtomicReference<SnowflakeIdWorker> SNOWFLAKE_ID_WORKER =
      new AtomicReference<>();

  /**
   * Get a Snowflake ID.
   *
   * @return Snowflake ID
   */
  public static long getSnowflakeId() {
    SnowflakeIdWorker worker = SNOWFLAKE_ID_WORKER.get();
    if (worker == null) {
      SnowflakeIdWorker newWorker = createSnowflakeIdWorker();
      if (SNOWFLAKE_ID_WORKER.compareAndSet(null, newWorker)) {
        worker = newWorker;
      } else {
        worker = SNOWFLAKE_ID_WORKER.get();
      }
    }
    return worker.nextId();
  }

  private static SnowflakeIdWorker createSnowflakeIdWorker() {
    long workerId = 0L;
    long datacenterId = 0L;

    try {
      // Generate IDs based on local IP and Hostname to avoid collisions in distributed envs like
      // k8s/docker
      InetAddress inetAddress = InetAddress.getLocalHost();
      String hostAddress = inetAddress.getHostAddress();
      String hostName = inetAddress.getHostName();

      // Extract workerId from hostName (if it contains numbers, useful in StatefulSets like pod-0,
      // pod-1)
      // Or use hash of IP
      // Use Math.floorMod to ensure positive result even if hashCode is negative
      workerId = Math.floorMod(hostAddress.hashCode(), 32);
      datacenterId = Math.floorMod(hostName.hashCode(), 32);
    } catch (UnknownHostException _) {
      // Fallback to random if host info cannot be fetched
      workerId = RANDOM.nextInt(32);
      datacenterId = RANDOM.nextInt(32);
    }

    return new SnowflakeIdWorker(workerId, datacenterId);
  }

  /** Internal class for Snowflake Algorithm. */
  private static class SnowflakeIdWorker {
    // Start timestamp (2020-01-01)
    private static final long TWEPOCH = 1577836800000L;

    // Number of bits for workerId and datacenterId
    private static final long WORKER_ID_BITS = 5L;
    private static final long DATACENTER_ID_BITS = 5L;

    // Max values (31)
    private static final long MAX_WORKER_ID = -1L ^ (-1L << WORKER_ID_BITS);
    private static final long MAX_DATACENTER_ID = -1L ^ (-1L << DATACENTER_ID_BITS);

    // Sequence bits
    private static final long SEQUENCE_BITS = 12L;

    // Shifts
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    private static final long DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    private static final long TIMESTAMP_LEFT_SHIFT =
        SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;

    // Sequence mask (4095)
    private static final long SEQUENCE_MASK = -1L ^ (-1L << SEQUENCE_BITS);

    private final long workerId;
    private final long datacenterId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    public SnowflakeIdWorker(long workerId, long datacenterId) {
      if (workerId > MAX_WORKER_ID || workerId < 0) {
        throw new IllegalArgumentException(
            String.format("worker Id can't be greater than %d or less than 0", MAX_WORKER_ID));
      }
      if (datacenterId > MAX_DATACENTER_ID || datacenterId < 0) {
        throw new IllegalArgumentException(
            String.format(
                "datacenter Id can't be greater than %d or less than 0", MAX_DATACENTER_ID));
      }
      this.workerId = workerId;
      this.datacenterId = datacenterId;
    }

    public synchronized long nextId() {
      long timestamp = timeGen();

      if (timestamp < lastTimestamp) {
        throw new BusinessException(
            HttpStatus.INTERNAL_SERVER_ERROR,
            CommonResultCode.SYSTEM_ERROR,
            "Clock moved backwards",
            String.format(
                "Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
      }

      if (lastTimestamp == timestamp) {
        sequence = (sequence + 1) & SEQUENCE_MASK;
        if (sequence == 0) {
          timestamp = tilNextMillis(lastTimestamp);
        }
      } else {
        sequence = 0L;
      }

      lastTimestamp = timestamp;

      return ((timestamp - TWEPOCH) << TIMESTAMP_LEFT_SHIFT)
          | (datacenterId << DATACENTER_ID_SHIFT)
          | (workerId << WORKER_ID_SHIFT)
          | sequence;
    }

    protected long tilNextMillis(long lastTimestamp) {
      long timestamp = timeGen();
      while (timestamp <= lastTimestamp) {
        timestamp = timeGen();
      }
      return timestamp;
    }

    protected long timeGen() {
      return System.currentTimeMillis();
    }
  }
}
