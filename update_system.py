with open("ppmb-system/build.gradle.kts", "r") as f:
    content = f.read()

# insert dependency
if 'implementation(project(":ppmb-common-doc"))' not in content:
    content = content.replace(
        'implementation(project(":ppmb-common-api"))',
        'implementation(project(":ppmb-common-api"))\n    implementation(project(":ppmb-common-doc"))'
    )

with open("ppmb-system/build.gradle.kts", "w") as f:
    f.write(content)
