import re

with open('ppmb-common/ppmb-common-web/src/test/java/top/ppmblszdp/common/web/audit/AuditLogAspectTest.java', 'r') as f:
    content = f.read()

content = content.replace("AuditLogAspect aspect =", "final AuditLogAspect aspect =")

with open('ppmb-common/ppmb-common-web/src/test/java/top/ppmblszdp/common/web/audit/AuditLogAspectTest.java', 'w') as f:
    f.write(content)

with open('ppmb-common/ppmb-common-web/src/test/java/top/ppmblszdp/common/web/audit/AuditEntityListenerTest.java', 'r') as f:
    content = f.read()

content = content.replace("AuditEntityListener listener =", "final AuditEntityListener listener =")

with open('ppmb-common/ppmb-common-web/src/test/java/top/ppmblszdp/common/web/audit/AuditEntityListenerTest.java', 'w') as f:
    f.write(content)
