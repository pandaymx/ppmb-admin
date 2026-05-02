package top.ppmblszdp.common.mq;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("通用消息包装类测试")
class CommonMessageTest {

  @Test
  @DisplayName("测试消息的创建和属性")
  void testMessageCreation() {
    String payload = "Hello MQ";
    CommonMessage<String> message =
        CommonMessage.<String>builder()
            .eventType("USER_CREATED")
            .topic("user.topic")
            .payload(payload)
            .build();

    assertNotNull(message.getMessageId());
    assertNotNull(message.getTimestamp());
    assertEquals("USER_CREATED", message.getEventType());
    assertEquals("user.topic", message.getTopic());
    assertEquals(payload, message.getPayload());
  }

  @Test
  @DisplayName("测试无参构造和 Setter")
  void testNoArgsConstructorAndSetters() {
    CommonMessage<String> message = new CommonMessage<>();
    message.setMessageId("123");
    message.setTimestamp(1000L);
    message.setEventType("TEST");
    message.setTopic("test.topic");
    message.setPayload("data");

    assertEquals("123", message.getMessageId());
    assertEquals(1000L, message.getTimestamp());
    assertEquals("TEST", message.getEventType());
    assertEquals("test.topic", message.getTopic());
    assertEquals("data", message.getPayload());
  }

  @Test
  @DisplayName("测试 Equals 和 HashCode")
  void testEqualsAndHashCode() {
    CommonMessage<String> m1 = new CommonMessage<>("1", 100L, "T", "TOP", "P");
    CommonMessage<String> m2 = new CommonMessage<>("1", 100L, "T", "TOP", "P");
    CommonMessage<String> m3 = new CommonMessage<>("2", 100L, "T", "TOP", "P");

    assertEquals(m1, m2);
    assertEquals(m1.hashCode(), m2.hashCode());
    assertNotEquals(m1, m3);
    assertNotEquals(m1.hashCode(), m3.hashCode());
    assertEquals(m1, m1);
    assertNotEquals(m1, null);
    assertNotEquals(m1, "string");
  }

  @Test
  @DisplayName("测试 ToString")
  void testToString() {
    CommonMessage<String> message = new CommonMessage<>("1", 100L, "T", "TOP", "P");
    String toString = message.toString();
    assertTrue(toString.contains("messageId=1"));
    assertTrue(toString.contains("payload=P"));
  }
}
