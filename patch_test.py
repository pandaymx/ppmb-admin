import re

with open('ppmb-system/src/test/java/top/ppmblszdp/system/infrastructure/mq/AuditLogListenerTest.java', 'r') as f:
    content = f.read()

# Replace all occurrences of 12 arguments with 13 arguments
# "127.0.0.1", 100L, LocalDateTime.now()
# -> "127.0.0.1", 1L, 100L, LocalDateTime.now()

content = re.sub(
    r'"127\.0\.0\.1",\s*100L,\s*LocalDateTime\.now\(\)',
    r'"127.0.0.1", 1L, 100L, LocalDateTime.now()',
    content
)

with open('ppmb-system/src/test/java/top/ppmblszdp/system/infrastructure/mq/AuditLogListenerTest.java', 'w') as f:
    f.write(content)
