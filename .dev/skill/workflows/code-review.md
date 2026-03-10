<!-- Last updated: 2026-03-10 -->
# 工作流：代码审查

## 触发命令
`/app review [target]` — 如 `/app review skin-record`, `/app review auth`, `/app review all`

## 执行流程

### 第1步：确定审查范围
- 指定模块/feature → 审查该模块所有文件
- `all` → 全项目审查（耗时较长）
- 指定文件路径 → 审查特定文件

### 第2步：加载审查标准
加载知识：
- `knowledge/code-style.md` — 代码规范
- `knowledge/kmp-architecture.md` — 架构规范
- `knowledge/spring-boot-patterns.md` — 后端规范
- `knowledge/api-design.md` — API规范
- `knowledge/security-checklist.md` — 安全检查

### 第3步：多维度审查

**维度1：代码质量**
- [ ] 命名规范（PascalCase/camelCase/snake_case）
- [ ] 函数职责单一
- [ ] 代码重复（DRY）
- [ ] 错误处理完善
- [ ] 空安全处理

**维度2：架构合规**
- [ ] 分层正确（Controller不含业务逻辑）
- [ ] 依赖方向正确（上层依赖下层）
- [ ] expect/actual使用正确
- [ ] DI配置完整（Koin/Spring）

**维度3：CMP UI质量**
- [ ] UiState + Action 模式
- [ ] Screen/Content 分离
- [ ] Modifier 外部传入
- [ ] 三态处理（Loading/Error/Empty）
- [ ] 无副作用在Composable中

**维度4：后端质量**
- [ ] 事务注解正确（Service层）
- [ ] 权限检查（用户数据隔离）
- [ ] DTO转换（不暴露Entity）
- [ ] 参数验证
- [ ] 错误码使用统一

**维度5：安全**
- [ ] 无硬编码密钥
- [ ] SQL注入防护
- [ ] XSS防护
- [ ] 权限校验
- [ ] 敏感数据不日志

**维度6：性能**
- [ ] N+1查询问题
- [ ] 无不必要的数据库查询
- [ ] LazyColumn使用key
- [ ] 图片加载优化

### 第4步：生成审查报告

按严重度分类：
- 🔴 **必须修复**：安全问题、功能bug、架构违反
- 🟡 **建议修复**：代码质量、性能优化
- 🟢 **可选优化**：代码风格、注释

### 第5步：更新记忆
- 如发现系统性问题 → `app-issues.md` 技术债务
- 如发现好的模式 → `app-learnings.md`

---

## 输出格式

```
🔍 代码审查: [审查范围]

━━━ 审查摘要 ━━━
文件数: [X] | 问题数: 🔴[X] 🟡[X] 🟢[X]

━━━ 🔴 必须修复 ━━━
1. [文件:行号] [问题描述]
   建议: [修复方案]
2. ...

━━━ 🟡 建议修复 ━━━
1. [文件:行号] [问题描述]
   建议: [修复方案]
2. ...

━━━ 🟢 可选优化 ━━━
1. ...

━━━ 亮点 ━━━
[做得好的地方]
```
