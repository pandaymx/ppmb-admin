.PHONY: infra backend frontend all stop clean help

# 默认显示帮助
help:
	@echo "PPMB Admin 开发工具集:"
	@echo "  make infra     - 启动基础设施 (Docker: Consul, Postgres, Redis, RabbitMQ, Monitoring)"
	@echo "  make backend   - 一键启动后端所有服务 (Gateway + System)"
	@echo "  make frontend  - 启动前端服务 (React + Vite)"
	@echo "  make all       - 启动全部 (基础设施 + 后端 + 前端)"
	@echo "  make stop      - 停止基础设施"

# 启动基础设施
infra:
	docker-compose up -d

# 停止基础设施
stop:
	docker-compose down

# 启动后端服务
# 使用 --parallel 配合 Gradle 运行多个阻塞任务
backend:
	./gradlew :ppmb-system:bootRun :ppmb-gateway:bootRun --parallel

# 启动前端服务
frontend:
	cd ppmb-admin-ui && bun run dev

# 一键启动所有应用
all:
	@$(MAKE) infra
	@echo "等待基础设施就绪 (5s)..."
	@sleep 5
	@$(MAKE) -j 2 backend frontend

# 清理构建缓存
clean:
	./gradlew clean

# Sonar 扫描 (本地)
# 确保已在 ~/.gradle/gradle.properties 中配置了 sonar.token
sonar-local:
	./gradlew sonar -Psonar.host.url=http://localhost:9000
