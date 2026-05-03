# PPMB 后台管理系统

![Java](https://img.shields.io/badge/Java-25-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0-brightgreen?logo=springboot)
![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2025-blue?logo=spring)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-blue?logo=postgresql)
![Redis](https://img.shields.io/badge/Redis-7.x-red?logo=redis)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-3.x-orange?logo=rabbitmq)
![Docker](https://img.shields.io/badge/Docker-Latest-blue?logo=docker)
![Gradle](https://img.shields.io/badge/Gradle-9.5-02303A?logo=gradle)
[![SonarCloud](https://sonarcloud.io/api/project_badges/measure?project=pandaymx_ppmb-admin&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=pandaymx_ppmb-admin)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=pandaymx_ppmb-admin&metric=coverage)](https://sonarcloud.io/summary/new_code?id=pandaymx_ppmb-admin)

PPMB (Professional Project Management Backbone) 是一个基于微服务架构的高性能后台管理项目。采用最新的 Java 25 和 Spring Boot 4.0 技术栈，严格遵循领域驱动设计 (DDD) 原则构建。

## 🚀 技术栈

- **核心框架**: Spring Boot 4.0, Jakarta EE 11
- **开发语言**: Java 25
- **服务治理**: Spring Cloud Consul
- **数据存储**: PostgreSQL
- **网关服务**: Spring Cloud Gateway
- **构建工具**: Gradle (Kotlin DSL)
- **环境要求**: Ubuntu (WSL2) / Linux

## 🏗️ 架构设计

项目严格遵循 **领域驱动设计 (DDD)** 原则，显式区分各层职责：

- **Domain Layer**: 包含 Domain Model (Entity, Aggregate Root, Value Object) 和 Domain Service。
- **Application Layer**: 处理业务编排，通过 Application Service 协调领域对象。
- **Infrastructure Layer**: 负责持久化、消息队列、外部系统集成等。
- **User Interface Layer**: 处理 REST API 请求和响应。

## 📦 模块说明

- `ppmb-gateway`: 基于 Spring Cloud Gateway 的统一 API 网关。
- `ppmb-common-api`: 核心公共模块，包含基础实体、异常处理、工具类及通用 API 定义。
- `ppmb-common-web`: Web 层公共组件，包含过滤器、拦截器及 Web 安全配置。
- `ppmb-user-service`: 用户核心业务模块，负责用户、权限及认证管理。

## 🛠️ 快速开始

### 环境准备

确保本地已安装：

- Java 25
- Docker & Docker Compose
- Bun (用于前端/工具链管理)

### 启动基础服务

使用 Docker Compose 一键启动 PostgreSQL 和 Consul：

```bash
docker-compose up -d
```

### 初始化项目工具链

项目使用 Husky 和 Commitlint 来规范提交信息。在克隆项目后，请运行以下命令初始化：

```bash
# 安装依赖并自动激活 Husky
bun install
```

> [!NOTE]
> `bun install` 会触发 `package.json` 中的 `prepare` 脚本，自动执行 `husky` 初始化。

### 编译与测试

```bash
# 运行单元测试
./gradlew test

# 编译项目
./gradlew build
```

## 🤝 开发规范

### Git 提交规范

项目强制执行 **Conventional Commits** 规范。所有的提交都会经过 Husky 钩子验证。提交格式为中文。

**提交格式:** `<type>(<scope>): <subject>`

**常见类型:**

- `feat`: 新功能
- `fix`: 修补 bug
- `docs`: 文档修改
- `style`: 代码格式修改 (不影响代码运行的变动)
- `refactor`: 重构 (既不是新增功能，也不是修改 bug)
- `test`: 测试用例修改
- `chore`: 构建过程或辅助工具的变动

### 编码准则

1. **优先使用 Record**: 对于 DTO、Value Object 优先使用 Java 21+ 的 `record`。
2. **ProblemDetails 标准**: 强制使用自定义异常并配合 Spring Boot 4.0 的 `ProblemDetails` 标准返回错误。
3. **不可变性**: 尽量保持领域对象不可变。
4. **测试先行**: 新增功能必须同步提供单元测试，且通过覆盖率检查。
