import re

with open('ppmb-system/src/test/java/top/ppmblszdp/system/infrastructure/init/MenuDataInitializerTest.java', 'r') as f:
    content = f.read()

# Replace times(4) with times(5) for menuRepository.save to account for the newly added AI Assistant menu
content = content.replace("verify(menuRepository, times(4)).save(any(SysMenu.class));", "verify(menuRepository, times(5)).save(any(SysMenu.class));")

with open('ppmb-system/src/test/java/top/ppmblszdp/system/infrastructure/init/MenuDataInitializerTest.java', 'w') as f:
    f.write(content)
