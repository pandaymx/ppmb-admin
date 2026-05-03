import yaml

with open("ppmb-system/src/main/resources/application.yml", "r") as f:
    config = yaml.safe_load(f)

if "springdoc" not in config:
    with open("ppmb-system/src/main/resources/application.yml", "a") as f:
        f.write("\nspringdoc:\n  api-docs:\n    enabled: true\n    path: /v3/api-docs\n")
