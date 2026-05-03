with open("ppmb-gateway/build.gradle.kts", "r") as f:
    content = f.read()

# insert dependency
if "springdoc-openapi-starter-webmvc-ui" not in content:
    content = content.replace(
        'implementation(libs.spring.cloud.starter.gateway.server.webmvc)',
        'implementation(libs.spring.cloud.starter.gateway.server.webmvc)\n    implementation(libs.springdoc.openapi.starter.webmvc.ui)'
    )

with open("ppmb-gateway/build.gradle.kts", "w") as f:
    f.write(content)
