# Attribution — 护肤产品归因分析报告，展示分数变化与产品影响力排名

> 设计稿: `.figma-mockups/09-attribution.html` (1 屏)
> 暗色: `.figma-mockups/15-dark-screens.html` (Dark — Attribution)

## 页面入口

- **Tab 4** (HomeScreen 底部导航第 4 个 Tab，直接作为 Tab 内容展示，`showBackButton = false`)
- **其他页面推入** (Voyager push，`showBackButton = true`，TopAppBar 显示返回箭头)

## 页面状态

| 状态 | 条件 | 显示内容 |
|------|------|---------|
| Loading | 数据加载中 | `LoadingContent()` 居中 |
| InsufficientData | 已评分记录 < 3 条 | "数据不足" 标题 + "至少需要 3 条皮肤记录才能生成归因报告" 提示 |
| Content | 已评分记录 >= 3 条 | 完整报告 (下方布局结构) |

## 布局结构

```
┌─────────────────────────────────┐
│ ← 归因分析报告                    │ ← TopAppBar (仅 push 时显示返回)
├─────────────────────────────────┤
│ ┌───────────────────────────┐   │
│ │  (70)  →  (82)            │   │ ← ① Score Comparison (warm gradient bg)
│ │ 30天前    现在              │   │
│ └───────────────────────────┘   │
│                                 │
│ ┌──────┐ ┌──────┐ ┌──────┐     │ ← ② Summary Stats (3 cards)
│ │ +12  │ │  6   │ │  30  │     │
│ │分数变化│ │使用产品│ │追踪天数│     │
│ └──────┘ └──────┘ └──────┘     │
│                                 │
│ ┌───────────────────────────┐   │ ← ③ AI Insight Card (gradient bg)
│ │ ✅ AI 归因洞察              │   │
│ │ 分析显示，烟酰胺精华对你的   │   │
│ │ 毛孔收缩和肤色均匀度提升     │   │
│ │ 贡献最大...                 │   │
│ └───────────────────────────┘   │
│                                 │
│ ┌─ 产品影响力排名 ────────────┐  │ ← ④ Product Rankings (SectionCard)
│ │ ❶ 🟣烟酰胺精华 10%    +8   │  │
│ │   The Ordinary · 28 天     │  │
│ ├───────────────────────────┤  │
│ │ ❷ ☀防晒乳 SPF50       +5   │  │
│ │   Anessa · 30 天           │  │
│ ├───────────────────────────┤  │
│ │ ❸ 🌿水乳保湿面霜       +3   │  │
│ │   珂润 · 30 天             │  │
│ ├───────────────────────────┤  │
│ │ ④ 氨基酸洁面乳          +1   │  │
│ │   FANCL · 25 天            │  │
│ └───────────────────────────┘  │
│                                 │
│ ┌─ 改善建议 ─────────────────┐  │ ← ⑤ Suggestions (SectionCard)
│ │ ✓ 继续使用烟酰胺精华...     │  │
│ │ ☀ 坚持每天防晒...           │  │
│ │ ✦ 可以尝试添加含透明质酸... │  │
│ └───────────────────────────┘  │
└─────────────────────────────────┘
```

### ① Score Comparison (.score-cmp)

| 属性 | 值 |
|------|---|
| 容器背景 | `gradients.warm` |
| 圆角 | `radius-large` (16dp) |
| 内边距 | xl (20dp) 上下 + lg (16dp) 左右 |
| 外边距 | 水平 lg (16dp)，底部 md (12dp) |
| 边框 | card-border-width, border-subtle |

- **左侧分数圆**: 70dp, `surface-primary` 背景, shadow-sm
  - 数值: `num-xl` (38sp, weight 800), `content-tertiary` 色
  - 标签: "30天前", `c1` (12sp, weight 500), `content-tertiary`
- **中间箭头**: 32dp 圆, `surface-success` 背景
  - 右箭头 SVG, 18x18, `content-success` 色, stroke-width 2.5
- **右侧分数圆**: 70dp, `surface-primary` 背景, shadow-sm
  - 数值: `num-xl` (38sp, weight 800), `content-brand` 色
  - 标签: "现在", `c1` (12sp, weight 500), `content-tertiary`

### ② Summary Stats (3 cards row)

| 属性 | 值 |
|------|---|
| 容器 | Row, gap sm (8dp) |
| 外边距 | 水平 lg (16dp)，底部 md (12dp) |
| 每个卡片 | flex: 1, padding md (12dp), radius-medium (12dp), text-align center |
| 边框 | card-border-width, border-subtle |
| 阴影 | shadow-xs |

| 卡片 | 背景色 | 数值色 | 数值 | 标签 |
|------|--------|--------|------|------|
| 分数变化 | `surface-success` | `content-success` | "+12" (num-md 18sp) | "分数变化" (c2 10sp, content-tertiary) |
| 使用产品 | `surface-lavender` | `lavender-500` | "6" (num-md 18sp) | "使用产品" (c2, content-tertiary) |
| 追踪天数 | `surface-brand-subtle` | `content-brand` | "30" (num-md 18sp) | "追踪天数" (c2, content-tertiary) |

### ③ AI Insight Card (.ai-ins)

| 属性 | 值 |
|------|---|
| 外边距 | 水平 lg (16dp)，底部 md (12dp) |
| 内边距 | lg (16dp) |
| 背景 | `linear-gradient(135deg, primary-50, lavender-50)` |
| 圆角 | radius-large (16dp) |
| 边框 | card-border-width, `rgba(42,157,124,0.08)` |
| 装饰圆 | 60dp, 右上角 -20dp 偏移, `rgba(42,157,124,0.06)` |

- **Badge**: 内联 flex
  - 勾选图标: 10x10 SVG 圆 + 白色勾, `primary-400` 填充
  - 文字: "AI 归因洞察", `c2` (10sp, weight 600), `content-brand`
  - 底部间距: md (12dp)
- **正文**: `b2` (14sp), `content-primary`, line-height 1.6
  - "分析显示，**烟酰胺精华**对你的毛孔收缩和肤色均匀度提升贡献最大。建议持续使用并配合防晒，效果会更显著。"
  - "烟酰胺精华" 为加粗 + `content-brand` 色

### ④ Product Rankings (SectionCard)

- **Header**: "产品影响力排名", h4 (SectionHeader)

每条排名项:

| 属性 | 值 |
|------|---|
| 容器 | Row, gap md (12dp), padding 垂直 md (12dp) |
| 分隔线 | border-top card-border-width border-subtle (非首项) |

**排名徽章** (28dp 圆, font c1, weight 800):

| 排名 | 背景 | 文字色 | 阴影 |
|------|------|--------|------|
| 1 | `gold-gradient` | `gold-text` | `shadow-gold-sm` |
| 2 | `lavender-100` | `lavender-600` | 无 |
| 3 | `rose-100` | `rose-600` | 无 |
| 4+ | `surface-tertiary` | `content-tertiary` | 无 |

**产品图标**: 36dp, rounded 10dp (`radius-sm`), 品类渐变背景 + 18x18 品类 SVG 图标

| 品类 | 图标背景渐变 | 图标色 |
|------|-------------|--------|
| 精华 | lavender-50 → lavender-100 | lavender-400 |
| 防晒 | secondary-50 → secondary-100 | secondary-400 |
| 面霜 | primary-50 → primary-100 | primary-400 |
| 洁面 | surface-tertiary | content-tertiary |

**信息区** (flex: 1):
- 产品名: `b2` (14sp, weight 600), `content-primary`
- 副信息: "品牌 · 使用 N 天", `c1` (12sp), `content-tertiary`, margin-top 1px

**分数 Badge**: padding xs(4dp)/10dp, radius-full, `c1` (12sp, weight 700)
- 正分: `content-success` + `surface-success` 背景
- 负分: `content-tertiary` + `surface-tertiary` 背景

设计稿中 4 条示例数据:

| 排名 | 产品名 | 品牌 | 使用天数 | 分数 |
|------|--------|------|---------|------|
| 1 | 烟酰胺精华 10% | The Ordinary | 28 天 | +8 |
| 2 | 防晒乳 SPF50 | Anessa | 30 天 | +5 |
| 3 | 水乳保湿面霜 | 珂润 | 30 天 | +3 |
| 4 | 氨基酸洁面乳 | FANCL | 25 天 | +1 |

### ⑤ Suggestions Section (SectionCard)

- **Header**: "改善建议", h4 (SectionHeader)
- **底部间距**: section (32dp) margin-bottom

每条建议:

| 属性 | 值 |
|------|---|
| 容器 | Row, align-items flex-start, gap md (12dp), padding 垂直 10dp |
| 分隔线 | border-top card-border-width border-subtle (非首项) |

**图标圆**: 32dp, radius-full, margin-top 2dp

| 序号 | 圆背景 | SVG 图标色 | 图标 |
|------|--------|-----------|------|
| 1 | `primary-50` | `primary-500` | 勾选 (polyline) |
| 2 | `secondary-50` | `secondary-500` | 太阳 (circle + rays) |
| 3 | `lavender-50` | `lavender-500` | 星芒 (path rays) |

**文字**: `b2` (14sp), `content-secondary`, line-height b2-lh

设计稿中 3 条建议:
1. "继续使用烟酰胺精华，效果正在显现"
2. "坚持每天防晒，泛红会进一步改善"
3. "可以尝试添加含透明质酸的产品提升水润度"

## 交互行为

| 操作 | 行为 |
|------|------|
| 返回按钮 | `navigator.pop()` (仅 push 入栈时显示) |
| 页面加载 | ViewModel 自动加载归因数据，显示 Loading → Content/InsufficientData |
| 列表入场动画 | 产品排名项使用 `animateListItem(index)` |
| 非会员升级 | 点击 LockedFeatureCard "升级 Pro 解锁" → `PaywallScreen` |

## 数据依赖

| 数据 | 来源 | 说明 |
|------|------|------|
| 皮肤记录 | `SkinRecordRepository.getRecordsByUser()` | 需 >= 3 条有 overallScore 的记录 |
| 产品使用记录 | `ProductRepository.getUsageBetween()` | 日期范围内的产品使用日志 |
| 产品信息 | `ProductRepository.getAllProducts()` | 产品名、品类等基础信息 |
| 会员状态 | `CheckFeatureAccess.isPremium()` | 控制 AI 洞察 / 排行 / 建议是否可见 |

### 归因算法 [V1]

MVP 使用本地统计归因（非 AI）：
1. 将记录按日期映射为 `日期 → 分数`
2. 按产品分组使用日期
3. 对每个产品：计算使用日的平均分 vs 未使用日的平均分
4. `impact = avgScoreWhenUsed - avgScoreWhenNotUsed`
5. 按 impact 降序排列

### AI 洞察文本 [V1]

MVP 使用 `buildAiInsight()` 本地模板生成，格式：
- "过去 N 天你的皮肤整体评分从 X 提升/下降到了 Y，变化了 Z%。{产品A}和{产品B}的组合对你很有效。建议保持当前的护肤方案，持续记录观察变化。"

### 建议文本 [V1]

MVP 使用 `buildSuggestions()` 模板生成，最多 3 条：
1. 基于最佳产品: "继续使用「{产品名}」，它对你的肤质有明显的正面影响"
2. 基于最差产品: "考虑减少或停用「{产品名}」，数据显示它可能对你的皮肤状态有负面影响"
3. 基于整体趋势: 上升/下降/稳定各有不同话术

### 数据模型

```kotlin
// 本地归因结果
data class ProductAttribution(
    val product: SkincareProduct,
    val daysUsed: Int,
    val avgScoreWhenUsed: Float,
    val avgScoreWhenNotUsed: Float,
    val impact: Float, // avgUsed - avgNotUsed
)

// AI 归因报告 (V2 接入 LLM 后使用)
data class AttributionReport(
    val summary: String,
    val overallTrend: String, // "improving" / "stable" / "declining"
    val productRankings: List<AiProductAttribution>,
    val recommendations: List<String>,
    val generatedAt: Long,
)

// UI 状态
sealed interface AttributionUiState {
    data object Loading
    data object InsufficientData
    data class Content(
        totalRecords, dateRange, overallTrend, trendDelta,
        attributions, isPremium, productsUsed, analysisDays,
        suggestions, firstScore, lastScore, aiInsight
    )
}
```

## 与其他页面的关系

| 关联页面 | 关系 |
|---------|------|
| HomeScreen | Tab 4 直接展示 `AttributionReportContent(showBackButton = false)` |
| PaywallScreen | 非会员 LockedFeatureCard 点击升级跳转 |
| ProductScreen | 产品数据来源；产品管理影响归因计算结果 |
| TimelineScreen | 皮肤记录数据来源；记录数 < 3 时显示 InsufficientData |
| CameraScreen | 新增记录后会影响归因结果 |

## 门控规格

| 用户类型 | 可见区块 | 锁定区块 |
|---------|---------|---------|
| 免费用户 | Score Comparison + Summary Stats | AI 洞察 + 排行 + 建议 → 替换为 LockedFeatureCard |
| Pro 会员 | 全部区块 | 无 |

**LockedFeatureCard 参数**:
- `title`: "解锁归因分析报告"
- `subtitle`: "AI 智能分析 · 产品效果排行 · 改善建议"
- `tags`: ["AI洞察", "排行榜", "改善建议"]
- `onUpgrade`: → `PaywallScreen`

## 暗色模式

| 区块 | 暗色适配 |
|------|---------|
| Score Comparison | `gradients.warm` 暗色变体 (CSS var 自动切换) |
| Summary Stats | 半透明暗色表面 (rgba 版本)；数值色保持不变 |
| AI Insight Card | 背景渐变: `linear-gradient(135deg, rgba(42,157,124,0.08), rgba(155,138,219,0.08))` |
| 排名徽章 | 保持各自颜色 (gold/lavender/rose/tertiary) |
| 产品图标 | 保持品类彩色背景 |
| 文字 | 自动跟随 `content-primary` / `content-secondary` / `content-tertiary` 暗色值 |

当前代码中 `AiInsightCard` 已实现暗色分支:
- 暗色: `Color(0xFF153E32).copy(0.4f)` → `Color(0xFF1E1830).copy(0.2f)` → `Color(0xFF2E2418).copy(0.15f)`
- 亮色: `Color(0xFFF0FAF6)` → `Color(0xFFF5F0FF)` → `Color(0xFFFFF8F2)`

## 与当前实现的差异

| # | 设计稿 | 当前实现 | 优先级 |
|---|--------|---------|--------|
| 1 | Score Comparison: warm gradient 背景 + 70dp 圆 + 居中箭头圆 | 实现为 Row + 64dp 圆 + border 描边 + 文字箭头 "\u2192" + delta 数字；无 warm gradient 背景 | P1 |
| 2 | Summary Stats: 3 独立卡片 (surface-success/surface-lavender/surface-brand-subtle 纯色背景)，shadow-xs + border | 实现为 SectionCard 内嵌 "分析概览" + 天数 pill + mini 趋势图 + 3 渐变色小卡片 (gradient bg)；多出趋势图和标题行 | P1 |
| 3 | AI Badge: 勾选圆形图标 + "AI 归因洞察" (c2, content-brand) | 实现为 "\u2728 AI 洞察" pill (primaryContainer bg)；文字和样式不一致 | P2 |
| 4 | AI Card 背景: primary-50 → lavender-50，border rgba(42,157,124,0.08) | 实现为 3 色渐变 (mint → lavender → warm) + 顶部 3 色 accent 线条；设计更简洁 | P2 |
| 5 | 排名: SectionCard 内 4 项 + divider 分隔，28dp 徽章 (gold-gradient/lavender-100/rose-100/surface-tertiary) | 实现为独立 Card 列表，30dp 徽章，金银铜渐变方案不同 (gold/gray/orange)；#1 有卡片高亮背景 | P1 |
| 6 | 排名分数: "+N" pill badge (c1, surface-success bg, radius-full) | 实现为右对齐大号 "+N.N 影响分" 文字 (18sp)；设计更紧凑 | P1 |
| 7 | 产品图标: 36dp, rounded 10dp, 品类渐变 + SVG 图标 | 实现为 40dp, rounded 12dp, 使用 emoji 而非 SVG | P3 |
| 8 | 建议图标: 32dp 圆, primary-50/secondary-50/lavender-50 + SVG | 实现为 32dp 渐变圆 + unicode 字符 (checkmark/clock/sun)；背景和图标不完全匹配 | P3 |
| 9 | 建议文字色: `content-secondary` | 实现为 `onSurface` | P3 |
| 10 | LazyColumn padding: 设计稿各区块水平 lg (16dp) | 实现为 contentPadding horizontal md (12dp) | P2 |
