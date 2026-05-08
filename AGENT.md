# Agent 编码准则扩展

本项目除了遵循 [GEMINI.md](file:///home/ppmb/code/ppmb-admin/GEMINI.md) 中的准则外，还需遵循以下扩展规则：

## 代码质量 (SonarQube)

- **禁止在 `throws` 子句中声明不必要的异常** (java:S1130)。如果方法体不抛出所声明的受检异常，或异常是多余的（例如已声明了父类异常），必须将其移除，以保持代码整洁。
- **禁止在测试方法中声明不必要的 `throws Exception`**。除非方法体确实调用了抛出受检异常且未被捕获的代码，否则应移除该声明。

## 持续改进

- 定期扫描并修复 SonarCloud/SonarQube 报告的 Code Smell，优先处理阻碍（Blocker）和严重（Critical）级别的问题。
