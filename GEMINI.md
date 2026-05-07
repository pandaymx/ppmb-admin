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
- **禁止使用已弃用且标记为删除的代码** (java:S5738)。在 Hibernate 6+ 中，应使用 `@IdGeneratorType` 或自定义组合注解替代 `@GenericGenerator`。
- **AssertJ 断言应当简化为专用断言**（例如使用 `isEqualTo` 而非 `equals().isTrue()`），以提高错误信息的可读性 (java:S5838)。
- **禁止方法具有相同的实现** (java:S4144)。重复的测试或逻辑应合并或区分测试场景。
- **相似的测试应当合并为参数化测试** (java:S5976)。使用 `@ParameterizedTest` 减少代码冗余并提高覆盖率。
- 所有的 `main` 方法中必须设置 `log4j2.contextSelector` 以开启 Log4j2 全异步模式。
- **禁止硬编码凭据** (java:S6437)。敏感信息（如密码、密钥等）必须通过配置文件（如 `application.yml`）或环境变量注入，严禁在代码中直接写入。

# Git 规范

- 严禁使用 `git add .`。在多 AI 协作环境下，必须精准地 `git add <file>`，避免提交无关文件或覆盖其他 AI 的工作。
- 使用中文提交 commit。
- 功能原子化，每次都要提交 commit 信息。
- commit 信息需要规范。
- 当要修改代码问题（如 Sonar 扫描出的 issue）时，必须将具体的问题描述（如 issue ID 或错误描述）写在 `update_topic` 的提示词或 `summary` 中，以便追溯和防止重复出现。
- 不使用 checkout。
- 使用 Trunk-Based flow 工作流。
