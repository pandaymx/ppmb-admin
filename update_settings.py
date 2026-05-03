with open("settings.gradle.kts", "r") as f:
    content = f.read()

if "ppmb-common-doc" not in content:
    content += '\ninclude("ppmb-common-doc")\n'

with open("settings.gradle.kts", "w") as f:
    f.write(content)
