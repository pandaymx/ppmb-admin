import re

with open("gradle/libs.versions.toml", "r") as f:
    content = f.read()

# Add version
content = content.replace('[versions]\n', '[versions]\nspringdoc = "2.8.4"\n')

# Add libraries
lib_addition = """springdoc-openapi-starter-webmvc-ui = { group = "org.springdoc", name = "springdoc-openapi-starter-webmvc-ui", version.ref = "springdoc" }
springdoc-openapi-starter-webmvc-api = { group = "org.springdoc", name = "springdoc-openapi-starter-webmvc-api", version.ref = "springdoc" }
"""
content = content.replace('[libraries]\n', '[libraries]\n' + lib_addition)

with open("gradle/libs.versions.toml", "w") as f:
    f.write(content)
