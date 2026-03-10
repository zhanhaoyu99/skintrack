<!-- Last updated: 2026-03-10 -->
# 工作流：自主学习

## 触发命令
`/app learn [topic]` — 如 `/app learn KMP相机集成`, `/app learn CMP导航最佳实践`, `/app learn Spring Boot JWT`

## 设计理念
> 自主学习会**直接修改 knowledge/ 文件**，这是设计初衷。
> 通过持续学习，让知识库不断更新，跟上技术变化。

---

## 学习域映射

| 用户输入的主题 | 目标知识文件 |
|-------------|-----------|
| KMP/多平台/模块/expect-actual | `knowledge/kmp-architecture.md` |
| CMP/Compose UI/导航/主题 | `knowledge/compose-multiplatform.md` |
| Supabase/后端/BaaS/认证 | `knowledge/supabase-patterns.md` |
| API/REST/DTO/响应 | `knowledge/api-design.md` |
| 数据库/JPA/SQLDelight/迁移 | `knowledge/database-patterns.md` |
| 测试/单元测试/集成测试 | `knowledge/testing-strategy.md` |
| 安全/JWT/认证/权限 | `knowledge/security-checklist.md` |
| 代码规范/命名/风格 | `knowledge/code-style.md` |
| 部署/CI-CD/Docker/签名 | `knowledge/devops-deploy.md` |
| 皮肤/护肤/成分/肤质 | `knowledge/skin-domain.md` |

---

## 执行流程

### 第1步：主题解析
1. 解析用户输入的学习主题
2. 映射到对应的知识文件
3. 读取当前知识文件内容
4. 确定需要学习的具体方向

### 第2步：信息搜索
进行 3-5 次搜索，中英文双语：

```
搜索1：[主题] + "2025 2026" + "Kotlin"
搜索2：[主题] + "best practices" + "multiplatform"
搜索3：[主题] + "latest" + "tutorial"
搜索4：[主题] + "pitfalls" / "common mistakes"
搜索5：[主题] + 具体框架名（Ktor/Koin/Voyager等）
```

### 第3步：信息提取
```
├── 核心事实：[已确认的信息]
├── 实用技巧：[可立即应用的方法]
├── 避坑指南：[常见错误/风险]
├── 代码示例：[可参考的代码片段]
├── 不确定信息：[需要进一步验证的]
└── 与现有知识的冲突：[如果有]
```

### 第4步：知识库更新
**更新原则：**
1. 新增信息添加到对应章节
2. 过时信息标注为"过时"而非直接删除
3. 更新文件顶部的时间戳
4. 保持文件整体结构不变

使用 Edit 工具直接修改知识文件。

### 第5步：记录学习日志
在 `app-learnings.md` 中记录：
```markdown
## [日期] 学习记录：[主题]

**来源：** [搜索关键词/URL]
**核心发现：**
1. [发现1]
2. [发现2]
3. [发现3]

**已更新文件：** [文件列表]
**具体更改：** [简述修改内容]
**影响评估：** [对当前开发的影响]
```

---

## 输出格式

```
📚 学习报告：[主题]

━━━ 搜索结果 ━━━
[关键发现摘要]

━━━ 知识更新 ━━━
[已修改的文件 + 修改内容]

━━━ 行动建议 ━━━
[基于新知识的开发建议]

━━━ 学习日志 ━━━
[已记录到 app-learnings.md]
```

---

## 常见学习场景

| 场景 | 建议搜索 |
|------|---------|
| KMP新版本发布 | `/app learn KMP最新版本变化` |
| CMP组件问题 | `/app learn CMP [组件名] 最佳实践` |
| 性能优化 | `/app learn Kotlin协程性能优化` |
| 新库评估 | `/app learn [库名] vs [替代库]` |
| 平台适配 | `/app learn iOS [功能] KMP实现` |
