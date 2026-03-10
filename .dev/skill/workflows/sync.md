# Workflow: Sync（跨机器同步）

## 触发
`/app sync`

## 功能
智能检测并同步 `.dev/skill/` ↔ `~/.claude/skills/app-dev/`

## 执行步骤

### 1. 检测两端文件状态
```
对比 .dev/skill/ 和 ~/.claude/skills/app-dev/ 下的所有文件：
- 用文件修改时间判断哪边更新
- 列出差异文件清单
```

### 2. 显示差异报告
```
📋 Sync 检测结果：

Skill目录更新（需拉入repo）:
  - SKILL.md (skill端更新于 2026-03-10 20:30)
  - knowledge/xxx.md

Repo更新（需推到skill目录）:
  - workflows/yyy.md

无差异:
  - 28 files
```

### 3. 确认并执行同步
- 向用户确认同步方向
- 执行文件复制
- 显示完成摘要

### 4. 提示后续操作
如果repo端有变更：
```
建议提交：
  git add .dev/skill/
  git commit -m "sync: skill changes from evolve"
  git push
```

## 注意事项
- 冲突时（双方都有修改）→ 提示用户手动选择
- sync不处理 `.dev/memory/` — memory文件由正常工作流更新
