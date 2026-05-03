# PPMB 后台管理系统

![Java](https://img.shields.io/badge/Java-25-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0-brightgreen?logo=springboot)
![React](https://img.shields.io/badge/React-18-61DAFB?logo=react)
![Vite](https://img.shields.io/badge/Vite-5-646CFF?logo=vite)
![Ant Design](https://img.shields.io/badge/Ant%20Design-5-0170FE?logo=antdesign)
![Tailwind CSS](https://img.shields.io/badge/Tailwind%20CSS-3-38B2AC?logo=tailwindcss)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-blue?logo=postgresql)
![Redis](https://img.shields.io/badge/Redis-7.x-red?logo=redis)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-3.x-orange?logo=rabbitmq)
![Prometheus](https://img.shields.io/badge/Prometheus-Monitoring-E6522C?logo=prometheus)
[![SonarCloud](https://sonarcloud.io/api/project_badges/measure?project=pandaymx_ppmb-admin&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=pandaymx_ppmb-admin)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=pandaymx_ppmb-admin&metric=coverage)](https://sonarcloud.io/summary/new_code?id=pandaymx_ppmb-admin)

PPMB (Professional Project Management Backbone) 是一个基于微服务架构的高性能后台管理项目。采用最新的 Java 25、Spring Boot 4.0 和 React 18 技术栈，严格遵循领域驱动设计 (DDD) 原则构建，旨在提供极致的开发体验与系统稳定性。

## 🚀 技术栈

### 后端 (Backend)

- **核心框架**: Spring Boot 4.0, Jakarta EE 11
- **开发语言**: Java 25 (支持 GraalVM 原生镜像)
- **服务治理**: Spring Cloud Consul, Spring Cloud Gateway
- **持久层**: Spring Data JPA, Liquibase, PostgreSQL 17
- **消息中间件**: RabbitMQ
- **缓存**: Redis 7, Caffeine (本地二级缓存)
- **构建工具**: Gradle (Kotlin DSL)

### 前端 (Frontend)

- **框架**: React 18 + TypeScript + Vite 5
- **UI 组件库**: Ant Design 5 (Token-based design)
- **状态管理**: Zustand
- **数据请求**: TanStack Query (React Query) + Axios
- **样式**: Tailwind CSS

### 监控与治理 (Monitoring)

- **代码质量**: SonarCloud
- **度量监控**: Micrometer + Prometheus + Grafana
- **日志分析**: RabbitMQ 异步日志采集

## 🏗️ 架构设计

项目严格遵循 **领域驱动设计 (DDD)** 原则，显式区分各层职责：

- **Domain Layer**: 包含 Domain Model (Entity, Aggregate Root, Value Object) 和 Domain Service。
- **Application Layer**: 处理业务编排，通过 Application Service 协调领域对象。
- **Infrastructure Layer**: 负责持久化、消息队列、外部系统集成等。
- **User Interface Layer**: 处理 REST API 请求和响应。

## 📦 模块说明

- `ppmb-common-api`: 核心公共模块，包含基础实体、异常处理、工具类及通用 API 定义。
- `ppmb-common-security`: 基于 Spring Security 的统一安全框架封装。
- `ppmb-common-web`: Web 层公共组件，包含 ProblemDetails 异常映射。
- `ppmb-gateway`: 基于 Spring Cloud Gateway 的统一 API 网关。
- `ppmb-system`: 系统管理核心模块（菜单、角色、用户、字典等）。
- `ppmb-admin-ui`: 基于 React 的管理后台前端。

## 🛠️ 快速开始

### 环境准备

确保本地已安装：

- Java 25
- Docker & Docker Compose
- Bun (用于前端/工具链管理)

### 启动项目

项目提供了 `Makefile` 以支持一键启动：

```bash
# 1. 启动基础设施 (Consul, DB, Redis, MQ, Monitoring)
make infra

# 2. 一键启动后端所有服务 (Gateway + System)
make backend

# 或者：一键启动全栈 (基础服务 + 后端 + 前端)
make all
```

> [!TIP]
> 如果你的环境不支持 `make`，可以手动运行 `./gradlew :ppmb-gateway:bootRun :ppmb-system:bootRun --parallel`。

## 📊 基础设施面板

| 服务            | 地址                                             | 默认账号/密码     |
| :-------------- | :----------------------------------------------- | :---------------- |
| **Consul UI**   | [http://localhost:8500](http://localhost:8500)   | -                 |
| **Grafana**     | [http://localhost:3000](http://localhost:3000)   | `admin` / `admin` |
| **Prometheus**  | [http://localhost:9090](http://localhost:9090)   | -                 |
| **RabbitMQ UI** | [http://localhost:15672](http://localhost:15672) | `guest` / `guest` |
| **SonarQube**   | [http://localhost:9000](http://localhost:9000)   | `admin` / `admin` |

## 🤝 开发规范

### Git 提交规范

项目强制执行 **Conventional Commits** 规范，提交格式为中文。

**格式:** `<type>(<scope>): <subject>`

### 编码准则

1. **优先使用 Record**: 对于 DTO、Value Object 优先使用 Java 21+ 的 `record`。
2. **ProblemDetails 标准**: 强制使用自定义异常并配合 Spring Boot 4.0 的 `ProblemDetails` 标准返回错误。
3. **不可变性**: 尽量保持领域对象不可变。
4. **测试先行**: 新增功能必须同步提供单元测试，且通过覆盖率检查。
