.PHONY: infra backend frontend app all stop clean setup setup-root setup-frontend help

# 默认显示帮助
help:
	@echo "PPMB Admin 开发工具集:"
	@echo "  make infra     - 启动基础设施 (Docker: Consul, Postgres, Redis, RabbitMQ, Monitoring)"
	@echo "  make backend   - 一键启动后端所有服务 (Gateway + System + Auth)"
	@echo "  make frontend  - 启动前端服务 (React + Vite)"
	@echo "  make app       - 启动应用 (后端 + 前端，不启动基础设施；适合 worktree)"
	@echo "  make all       - 启动全部 (基础设施 + 后端 + 前端)"
	@echo "  make setup     - 安装依赖 (根目录工具链 + 前端依赖)"
	@echo "  make stop      - 停止基础设施"

# 安装依赖（面向新同学/首次运行）
setup: setup-root setup-frontend

# 根目录仅包含 commitlint/husky/prettier 等工具链依赖（不影响运行服务）
setup-root:
	@if [ -f package.json ]; then \
		if [ ! -d node_modules ]; then \
			echo "安装根目录工具链依赖 (bun install)..."; \
			bun install; \
		fi; \
	fi

setup-frontend:
	@if [ -d ppmb-admin-ui ]; then \
		if [ ! -d ppmb-admin-ui/node_modules ]; then \
			echo "安装前端依赖 (ppmb-admin-ui, bun install)..."; \
			cd ppmb-admin-ui && bun install; \
		fi; \
	fi

# 启动基础设施
infra:
	docker-compose up -d

# 停止基础设施
stop:
	docker-compose down

# 启动后端服务
# 使用 --parallel 配合 Gradle 运行多个阻塞任务
backend:
	./gradlew :ppmb-system:bootRun :ppmb-auth:bootRun :ppmb-gateway:bootRun --parallel

# 启动前端服务
frontend:
	@$(MAKE) setup-frontend
	cd ppmb-admin-ui && bun run dev

# 启动应用（适合 worktree：只起前后端，不包含基础设施）
app:
	@$(MAKE) -j 2 backend frontend

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
