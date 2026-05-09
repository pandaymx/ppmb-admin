import re

with open('gradle/libs.versions.toml', 'r') as f:
    content = f.read()

# Add version for spring-ai
if 'springAi' not in content:
    content = re.sub(r'(\[versions\]\n.*?)(\n\n\[libraries\])', r'\1\nspringAi = "1.0.0-M6"\2', content, flags=re.DOTALL)

# Add libraries
if 'spring-ai-bom' not in content:
    libraries_addition = """
spring-ai-bom = { group = "org.springframework.ai", name = "spring-ai-bom", version.ref = "springAi" }
spring-ai-openai-spring-boot-starter = { group = "org.springframework.ai", name = "spring-ai-openai-spring-boot-starter" }
spring-ai-ollama-spring-boot-starter = { group = "org.springframework.ai", name = "spring-ai-ollama-spring-boot-starter" }
"""
    content = re.sub(r'(\[libraries\]\n.*?)(?=\n\n\[plugins\])', r'\1' + libraries_addition, content, flags=re.DOTALL)

with open('gradle/libs.versions.toml', 'w') as f:
    f.write(content)
