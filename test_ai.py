import urllib.request
import json
url = "https://search.maven.org/solrsearch/select?q=g:org.springframework.ai+a:spring-ai-openai-spring-boot-starter&wt=json"
response = urllib.request.urlopen(url)
data = json.loads(response.read())
print(json.dumps(data, indent=2))
