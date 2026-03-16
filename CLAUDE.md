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

完整 token 表见 `DESIGN_SYSTEM.md`。

```kotlin
MaterialTheme.colorScheme.xxx     // Material 3 标准色
MaterialTheme.extendedColors.xxx  // 扩展色（skinMetric/functional/camera）
MaterialTheme.spacing.xxx         // 间距（xs/sm/md/lg/xl/xxl/section）
MaterialTheme.dimens.xxx          // 组件尺寸（buttonHeight/avatarLarge/thumbnailSize 等）
MaterialTheme.gradients.xxx       // 渐变（primary/warm/surface/scoreRing）
MaterialTheme.shapes.xxx          // 圆角
```

### UI 编码规范

- **禁止硬编码**：颜色用 token（Canvas 内部除外），间距用 `spacing.xxx`，组件尺寸用 `dimens.xxx`
- **列表间距**：`Arrangement.spacedBy(spacing.xxx)` 代替手动 Spacer
- **动画时长**：使用 `Motion.SHORT/MEDIUM/LONG`，缓动用 `Motion.EmphasizedDecelerate/Standard`
- **列表入场**：`itemsIndexed` + `Modifier.animateListItem(index)`
- **卡片模式**：Card+Column+padding 用 `SectionCard`，区块标题用 `SectionHeader`
- **趋势显示**：用 `TrendIndicator(value)` 统一 ↑↓→ + functional color
- **Material Icons**：项目仅有 icons-core，ArrowUpward/TrendingUp 等不可用，用 Unicode 箭头替代

### 共享组件 `ui/component/`

SectionCard / SectionHeader / TrendIndicator / MenuItem / ScoreBar / LoadingContent / EmptyContent / ErrorContent / animateListItem

## 产品文档

完整产品规格文档位于 `.dev/product/` 目录：
- `.dev/product/README.md` — 索引文件
- 每个页面一个 `screen-*.md` 文件，含布局结构、组件规格、与当前实现差异
- `navigation.md` — 导航架构（5Tab + 路由图）
- `feature-gating.md` — 付费门控 4 态规格
- `product-overview.md` — 产品定位、用户画像、商业模型

**开发 UI 页面前，先阅读对应的 `screen-*.md` 文件对齐设计稿。**

## 技术栈

KMP + Compose Multiplatform + Supabase + Room KMP

## Skill

使用 `/app` 命令系统。Skill安装在 `~/.claude/skills/app-dev/`。
如未安装，运行 `.dev/setup.sh`（WSL/Linux/Mac）或 `.dev/setup.bat`（Windows）。
