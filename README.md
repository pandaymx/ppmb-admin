# PPMB 后台管理系统

## 🛠️ 快速开始

### 初始化项目工具链

项目使用 Husky 和 Commitlint 来规范提交信息。在克隆项目后，请运行以下命令初始化：

```bash
# 安装依赖并自动激活 Husky
bun install
```

> [!NOTE]
> `bun install` 会触发 `package.json` 中的 `prepare` 脚本，自动执行 `husky` 初始化。

### 2. 提交代码 (触发工具链)

你无需手动调用 Husky 或 Commitlint。只需像往常一样提交代码，钩子会自动运行：

```bash
git add .
# 此时会触发 pre-commit (格式化) 和 commit-msg (规范检查)
git commit -m "feat: 你的提交信息"
```

---

## 🤝 开发规范

### Git 提交规范

项目强制执行 **Conventional Commits** 规范。所有的提交都会经过 Husky 钩子验证。

**提交格式:**
`<type>(<scope>): <subject>`

**常见类型:**

- `feat`: 新功能
- `fix`: 修补 bug
- `docs`: 文档修改
- `style`: 代码格式修改 (不影响代码运行的变动)
- `refactor`: 重构 (既不是新增功能，也不是修改 bug)
- `test`: 测试用例修改

**示例:**

```bash
git commit -m "feat(auth): 实现基于 JWT 的用户认证"
```
