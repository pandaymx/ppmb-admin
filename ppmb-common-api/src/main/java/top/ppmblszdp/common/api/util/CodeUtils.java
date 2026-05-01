package top.ppmblszdp.common.api.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

/** Utility class for generating various codes (Snowflake, UUID, verification codes, etc.) */
public class CodeUtils {

  private static final Random RANDOM = new SecureRandom();

  // ================== UUID ==================

  /** Generate standard UUID */
  public static String uuid() {
    return UUID.randomUUID().toString();
  }

  /** Generate simple UUID (without dashes) */
  public static String simpleUuid() {
    return UUID.randomUUID().toString().replace("-", "");
  }

  // ================== Verification Codes ==================

  /**
   * Generate numeric verification code (4-6 digits)
   *
   * @param length between 4 and 6
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
   * Generate alphabetic verification code (4-6 letters)
   *
   * @param length between 4 and 6
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

  /** Convert base-10 long to base-62 string */
  public static String encodeBase62(long num) {
    if (num == 0) {
      return "0";
    }
    StringBuilder sb = new StringBuilder();
    long value =
        Math.abs(num); // Ensure positive handling if needed, though usually IDs are positive
    while (value > 0) {
      sb.insert(0, BASE62_CHARS.charAt((int) (value % 62)));
      value /= 62;
    }
    return sb.toString();
  }

  // ================== ID Card Generator ==================

  /**
   * Automatically generate a random valid 18-digit Chinese ID Card number. (Basic implementation:
   * valid format and checksum, random dob and sequence)
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
  private static volatile SnowflakeIdWorker snowflakeIdWorker;

  /** Get a Snowflake ID */
  public static long getSnowflakeId() {
    if (snowflakeIdWorker == null) {
      synchronized (CodeUtils.class) {
        if (snowflakeIdWorker == null) {
          snowflakeIdWorker = createSnowflakeIdWorker();
        }
      }
    }
    return snowflakeIdWorker.nextId();
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
      workerId = Math.abs(hostAddress.hashCode()) % 32;
      datacenterId = Math.abs(hostName.hashCode()) % 32;
    } catch (UnknownHostException e) {
      // Fallback to random if host info cannot be fetched
      workerId = RANDOM.nextInt(32);
      datacenterId = RANDOM.nextInt(32);
    }

    return new SnowflakeIdWorker(workerId, datacenterId);
  }

  /** Internal class for Snowflake Algorithm */
  private static class SnowflakeIdWorker {
    // Start timestamp (2020-01-01)
    private final long twepoch = 1577836800000L;

    // Number of bits for workerId and datacenterId
    private final long workerIdBits = 5L;
    private final long datacenterIdBits = 5L;

    // Max values (31)
    private final long maxWorkerId = -1L ^ (-1L << workerIdBits);
    private final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);

    // Sequence bits
    private final long sequenceBits = 12L;

    // Shifts
    private final long workerIdShift = sequenceBits;
    private final long datacenterIdShift = sequenceBits + workerIdBits;
    private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;

    // Sequence mask (4095)
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);

    private long workerId;
    private long datacenterId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    public SnowflakeIdWorker(long workerId, long datacenterId) {
      if (workerId > maxWorkerId || workerId < 0) {
        throw new IllegalArgumentException(
            String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
      }
      if (datacenterId > maxDatacenterId || datacenterId < 0) {
        throw new IllegalArgumentException(
            String.format(
                "datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
      }
      this.workerId = workerId;
      this.datacenterId = datacenterId;
    }

    public synchronized long nextId() {
      long timestamp = timeGen();

      if (timestamp < lastTimestamp) {
        throw new RuntimeException(
            String.format(
                "Clock moved backwards. Refusing to generate id for %d milliseconds",
                lastTimestamp - timestamp));
      }

      if (lastTimestamp == timestamp) {
        sequence = (sequence + 1) & sequenceMask;
        if (sequence == 0) {
          timestamp = tilNextMillis(lastTimestamp);
        }
      } else {
        sequence = 0L;
      }

      lastTimestamp = timestamp;

      return ((timestamp - twepoch) << timestampLeftShift)
          | (datacenterId << datacenterIdShift)
          | (workerId << workerIdShift)
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
