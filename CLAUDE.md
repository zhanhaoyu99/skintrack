# SkinTrack 项目

## 开发状态

项目进度文件位于 `.dev/memory/` 目录：
- `.dev/memory/app-progress.md` — 进度主文件（每次session必读）
- `.dev/memory/app-issues.md` — 问题追踪
- `.dev/memory/app-decisions.md` — 架构决策
- `.dev/memory/app-strategy.md` — 项目策略
- `.dev/memory/app-learnings.md` — 经验积累

**每次完成实质工作后，必须更新 `.dev/memory/app-progress.md`。**

## 设计系统

详见 `DESIGN_SYSTEM.md`。UI 代码必须使用设计 token，禁止硬编码颜色/间距。

```kotlin
MaterialTheme.extendedColors.xxx  // 扩展色
MaterialTheme.spacing.xxx         // 间距
MaterialTheme.gradients.xxx       // 渐变
MaterialTheme.colorScheme.xxx     // Material 3 标准色
MaterialTheme.shapes.xxx          // 圆角
```

## 技术栈

KMP + Compose Multiplatform + Supabase + Room KMP

## Skill

使用 `/app` 命令系统。Skill安装在 `~/.claude/skills/app-dev/`。
如未安装，运行 `.dev/setup.sh`（WSL/Linux/Mac）或 `.dev/setup.bat`（Windows）。
