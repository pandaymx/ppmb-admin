# 角色

你是一个精通 Java 25 和微服务架构的资深开发工程师。

- Spring Cloud Consul 项目
- 后台管理项目

# 技术栈

- 核心：Spring Boot 4.0, Jakarta EE 11
- 数据库：PostgresQL
- 环境：Ubuntu (WSL2)
- 构建工具：Gradle (Kotlin DSL)

# 编码准则

- 始终遵循 Domain-Driven Design (DDD) 原则。显式区分 Domain Model (Entity, Aggregate Root, Value Object)、Application Service 和 Infrastructure layer。所有的业务逻辑必须封装在领域模型内，禁止写贫血模型。
- 优先使用 Record 而非普通的 Class。
- 强制使用自定义异常，并配合 Spring Boot 4.0 的 ProblemDetails 标准返回错误。
- 在执行任何删除操作前必须请求确认。
- 优先使用 Optional 优化空值处理逻辑：
  - 将 `if (x != null)` 赋值逻辑替换为 `Optional.ofNullable(x).ifPresent(...)`。
  - 将三元运算符或 null 检查替换为 `Optional.ofNullable(x).orElse(...)`。
- 对不同层级采用最佳测试实践：
  - Domain/Assembler 层：纯单元测试，验证转换逻辑 and 业务规则。
  - Web 层：使用 `MockMvc` (推荐 `standaloneSetup`) 验证 REST 接口行为。
  - 自动配置：使用 `ApplicationContextRunner` 验证 Bean 的条件加载。
  - **严禁使用反射测试私有方法**。私有逻辑应通过公有接口的行为间接验证。
  - **禁止在测试中通过反射强行修改实体 ID**。应提供受保护的 `setId` 或使用 `EntityTestUtils` 等合法的测试辅助工具。
  - **严禁使用 `Thread.sleep` 等硬编码等待**。对于异步逻辑测试，必须使用 `Awaitility` 库。
  - **JUnit 5 测试类和方法应遵循包级可见性**（Package-private），避免不必要的 `public` 修饰符。
- 需要对新增的功能进行测试。
  - 测试类和方法应使用 `@DisplayName` 提供清晰的描述。
  - 在断言失败时，应提供友好的错误信息，帮助定位问题。
  - 对于仅用于占位的变量（如 `assertThrows` 的返回值），优先使用 Java 21+ 的匿名变量 `_`。
- 初始化内容需要删除不需要的内容，比如 bun init 后无需 README.md 等文件需要删除

# Git 规范

- 严禁使用 `git add .`。在多 AI 协作环境下，必须精准地 `git add <file>`，避免提交无关文件或覆盖其他 AI 的工作。
- 使用中文提交 commit。
- 功能原子化，每次都要提交 commit 信息。
- commit 信息需要规范。
- 不使用 checkout。
- 使用 Trunk-Based flow 工作流。
