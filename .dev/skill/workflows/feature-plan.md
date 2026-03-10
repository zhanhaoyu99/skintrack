<!-- Last updated: 2026-03-10 -->
# 工作流：Feature端到端规划

## 触发命令
`/app plan [feature]` — 如 `/app plan auth`, `/app plan skin-record`, `/app plan skincare-products`

## 执行流程

### 第1步：上下文加载
1. 加载记忆文件：
   - `app-progress.md` — 当前进度，已有页面/API
   - `app-decisions.md` — 已有架构决策
   - `app-strategy.md` — MVP范围确认
   - `app-issues.md` — 相关已知问题
2. 加载相关知识：
   - `knowledge/kmp-architecture.md` — 模块划分
   - `knowledge/api-design.md` — API规范
   - 如涉及领域知识 → `knowledge/skin-domain.md`

### 第2步：Feature拆解
根据 `templates/feature-spec.md` 模板，拆解为：

```
Feature: [名称]
描述: [一句话说明]
所属里程碑: M[X]

━━━ 数据层 ━━━
- 数据库表（新增/修改）
- Entity定义
- Repository接口

━━━ 后端API ━━━
- Endpoint列表（Method + URL + 说明）
- Service逻辑
- DTO设计

━━━ 共享层 (KMP shared) ━━━
- API Service（Ktor Client）
- Repository
- UseCase
- 数据模型

━━━ UI层 (CMP) ━━━
- 页面列表（每页职责）
- 组件列表（可复用组件）
- ViewModel
- 导航关系

━━━ 平台特定 ━━━
- expect/actual 需求（如有）
- 权限需求

━━━ 测试 ━━━
- 后端测试点
- 共享层测试点
- UI测试点（如需要）
```

### 第3步：依赖分析
1. 这个Feature依赖哪些已完成的模块？
2. 需要新建哪些公共组件？
3. 是否需要新的架构决策？

### 第4步：实施顺序
建议开发顺序（通常是）：
1. 数据库表 → `db-migrate`
2. 后端API → `api-create` + `service-create`
3. 共享层 → 手动或 `api-create` 联动
4. UI页面 → `page-create` (一页一页做)
5. 测试 → `test-write`

### 第5步：工作量估算
列出每个子任务，标注：
- 复杂度：🟢简单 / 🟡中等 / 🔴复杂
- 依赖：前置任务

### 第6步：用户确认 + 更新记忆
1. 展示完整Feature规划，等用户确认
2. 确认后更新 `app-progress.md`：
   - 在对应里程碑下添加任务清单
   - 更新当前状态

---

## 输出格式

```
📋 Feature规划: [feature名称]

━━━ 概览 ━━━
[一句话描述 + 所属里程碑]

━━━ 数据库 ━━━
[表名 + 字段列表]

━━━ API清单 ━━━
| Method | Endpoint | 说明 | 复杂度 |
|--------|----------|------|--------|

━━━ 页面清单 ━━━
| # | 页面 | 说明 | 复杂度 |
|---|------|------|--------|

━━━ 共享层 ━━━
[Repository + UseCase列表]

━━━ 实施顺序 ━━━
1. [step1] → `/app db ...`
2. [step2] → `/app api ...`
3. [step3] → `/app page ...`
...

━━━ 依赖 ━━━
[前置依赖列表]

确认开始实施？(Y/N)
```

---

## 注意事项
- Feature规划不写代码，只做设计
- 确保API设计符合 `knowledge/api-design.md` 规范
- 考虑与已有Feature的交互（如认证中间件）
- 识别可复用的组件，避免重复开发
