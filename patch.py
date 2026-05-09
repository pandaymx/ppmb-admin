with open('ppmb-gateway/src/main/resources/application.yml', 'r') as f:
    content = f.read()

import re
replacement = """            - id: user-service-route
              uri: lb://ppmb-system
              predicates:
                - Path=/api/user/**
              filters:
                - StripPrefix=2
                - RateLimit=1,20
            - id: ai-service-route
              uri: lb://ppmb-ai
              predicates:
                - Path=/api/ai/**
              filters:
                - StripPrefix=1
                - RateLimit=1,20
"""

content = re.sub(r'            - id: user-service-route.*?RateLimit=1,20\n', replacement, content, flags=re.DOTALL)

with open('ppmb-gateway/src/main/resources/application.yml', 'w') as f:
    f.write(content)
