import re

with open('ppmb-system/src/test/java/top/ppmblszdp/system/infrastructure/mq/AuditLogListenerTest.java', 'r') as f:
    content = f.read()

# Replace the specific instance that failed
content = content.replace(
"""            "127.0.0.1",
            1L,
            LocalDateTime.now());""",
"""            "127.0.0.1",
            1L,
            100L,
            LocalDateTime.now());"""
)

with open('ppmb-system/src/test/java/top/ppmblszdp/system/infrastructure/mq/AuditLogListenerTest.java', 'w') as f:
    f.write(content)
