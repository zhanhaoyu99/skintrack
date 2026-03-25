# Timeline — 肌肤记录时间线，展示前后对比、趋势图和历史记录列表

> 设计稿: `.figma-mockups/04-timeline.html` (content), `.figma-mockups/14-dark-core.html` (dark)
> Tab 2 位置，TopAppBar 标题 "肌肤记录"

## 页面入口

- 底部导航栏 Tab 2 "记录" (Grid 图标)
- Dashboard 快捷入口 "查看全部趋势"

## 页面状态

| 状态 | 条件 | 显示内容 |
|------|------|---------|
| Loading | 数据加载中 | 骨架屏 (shimmer placeholder) |
| Empty | records.count == 0 | 空状态引导 (hero + 3-step + CTA) |
| Content | records.count >= 1 | 筛选条 + 对比卡 + 趋势图 + 记录列表 |

## 布局结构 (content)

```
┌─────────────────────────────┐
│  肌肤记录              [⋮]  │ ← TopAppBar: h4 title + 3-dot menu
├─────────────────────────────┤
│ [全部] [本周] [本月] [3个月] │ ← Filter Row
├─────────────────────────────┤ ← LazyColumn 开始
│ ┌─ 前后对比 ──── 分享 ───┐ │
│ │ [130×160] VS [130×160]  │ │ ← Compare Card
│ │  2周前        今天      │ │
│ │     +12 分              │ │
│ │ 皮肤在持续变好哦...      │ │
│ └─────────────────────────┘ │
│ ┌─ 趋势分析 ─────────────┐ │
│ │ [综合][痘痘][毛孔]...   │ │ ← Trend Card
│ │ [====面积图====]        │ │
│ └─────────────────────────┘ │
│ 近期记录                     │ ← SectionHeader
│ ┌─────────────────────────┐ │
│ │ [56] 今天 09:15  ↑+3 82│ │ ← Record Item × N
│ │ [56] 昨天 08:42  ↑+2 79│ │
│ │ [56] 3月12日     →0  77│ │
│ │ ...                     │ │
│ └─────────────────────────┘ │
├─────────────────────────────┤
│ 首页 [记录] (+) 分析  我的  │
└─────────────────────────────┘
```

### 1. TopAppBar

- 标题: "肌肤记录" (h4)
- 右侧: 3-dot overflow menu action icon

### 2. Filter Row [V1]

- 容器: 水平滚动, padding horizontal = lg(16dp), gap = sm(8dp)
- Chip 选项: "全部" / "本周" / "本月" / "3 个月"
- Active chip: interactive-primary bg, white text, radius-full
- Inactive chip: surface-tertiary bg, content-secondary text, radius-full

### 3. Compare Card (前后对比) [V1]

- 容器: SectionCard, gradients.triWarm 背景
- Header row:
  - 左: "前后对比" (b2, weight 600)
  - 右: "分享" 文字按钮 (b3, content-brand) → ShareCardScreen
- 照片区域: Row, gap sm(8dp)
  - 每张: 130 x 160dp, radius-medium, gradients.skin bg
  - 底部渐变遮罩 + 日期标签: "2 周前" / "今天" (c2, white)
  - shadow-sm
- VS 徽章: 32dp 圆形, interactive-primary bg, white text ("VS"), shadow-md, 水平居中于两照片之间
- 结果分数: "+12 分" (num-lg, content-success), 居中
- 鼓励文案: "皮肤在持续变好哦，继续加油~" (b3, content-secondary), 居中

### 4. Trend Card (趋势分析) [V1]

- 容器: SectionCard
- Header: "趋势分析" (SectionHeader)
- 指标 Chips: Row, gap sm(8dp)
  - 选项: "综合" / "痘痘" / "毛孔" / "均匀" / "水润"
  - Active: interactive-primary bg, white text
  - Inactive: surface-tertiary bg, content-secondary text
- 面积图区域: 320 x 80dp
  - 渐变填充 (primary-400 → transparent)
  - 曲线线条 (primary-400, 2dp stroke)
  - 最新端点: 白色填充圆 + primary-400 描边 + 光晕效果
- 显示条件: chartPoints.size >= 2

### 5. Records Section (近期记录) [V1]

- Section header: "近期记录" (SectionHeader)
- 列表: LazyColumn, verticalArrangement spacedBy sm(8dp)
- 分页加载: 滚动触底加载更多

### 6. Record Item [V1]

每个记录项为 Card, 水平布局, padding md(12dp), gap md(12dp):

| 区域 | 规格 |
|------|------|
| 缩略图 | 56 x 56dp, radius-medium(12dp), gradients.skin bg + 人脸 SVG; 有照片时显示 AsyncImage crop |
| 信息列 | weight 1f, gap xs(4dp) |
| — 第一行 | 日期 (b3, content-primary, weight 600) + 趋势 badge |
| — 第二行 | AI 摘要 (b3, content-secondary, maxLines 1, ellipsis) |
| 分数环 | ScoreRing 44dp, surface-tertiary bg stroke, primary-400 progress stroke, center num-sm primary-400 |

趋势 badge 规格:
| 变化 | 文字 | 背景色 | 文字色 | 字号 |
|------|------|--------|--------|------|
| 上升 | ↑+N | surface-success | content-success | c2 |
| 持平 | →0 | surface-tertiary | content-tertiary | c2 |
| 下降 | ↓-N | surface-error | content-error | c2 |

Badge radius: extraSmall(4dp), padding horizontal 6dp, vertical 1dp.

分数差计算: 当前记录 overallScore - 前一条记录 overallScore.

## 布局结构 (empty)

```
┌─────────────────────────────┐
│  肌肤记录              [⋮]  │
├─────────────────────────────┤
│                             │
│      ╭───────────────╮      │
│      │  ◎  ◎  ◎      │      │ ← Hero circle 140dp
│      │    radial      │      │   primary-100 → primary-50
│      │    gradient    │      │   camera grid icon (primary-300)
│      ╰───────────────╯      │
│   ● (rose-50, 28dp)         │ ← 装饰小圆
│            ● (lavender-50, 18dp) │
│                             │
│     还没有记录哦              │ ← h2, weight 800
│ 拍第一张自拍，开始追踪        │ ← b2, content-secondary
│ 你的皮肤变化吧~              │
│                             │
│  ①拍照   ②分析   ③追踪      │ ← 3-step row
│                             │
│  [      开始拍照      ]      │ ← Primary button, full width
│                             │
├─────────────────────────────┤
│ 首页 [记录] (+) 分析  我的  │
└─────────────────────────────┘
```

### Hero 插画

- 圆形: 140dp, radial gradient primary-100 → primary-50
- 中心图标: camera grid SVG, primary-300
- 装饰圆: rose-50(28dp) 左下偏移, lavender-50(18dp) 右上偏移

### 标题 + 描述

- 标题: "还没有记录哦" (h2, weight 800, content-primary)
- 描述: "拍第一张自拍，开始追踪你的皮肤变化吧~" (b2, content-secondary, textAlign center)

### 3-Step 引导

- 水平等分排列, 每步居中
- 每步: 44dp 圆形 + 数字 + 文字标签
  - Step 1 "拍照": primary 色边框, 数字 primary
  - Step 2 "分析": lavender 色边框, 数字 lavender
  - Step 3 "追踪": rose 色边框, 数字 rose

### CTA 按钮

- "开始拍照", primary bg, full width, radius-full
- onClick → CameraScreen (通过 FAB 导航)

## 交互行为

| 操作 | 行为 |
|------|------|
| 切换 Filter Chip | 重新加载数据, 筛选对应时间范围 (全部/本周/本月/3个月) |
| 切换 Metric Chip | 趋势图切换到对应指标 (综合/痘痘/毛孔/均匀/水润) |
| 点击 "分享" | navigator.push(ShareCardScreen(beforeId, afterId)) |
| 点击 Record Item | navigator.push(RecordDetailScreen(recordId)) |
| 滚动触底 | 自动加载更多记录 (分页) |
| 空状态 "开始拍照" | 跳转 CameraScreen |
| 列表项入场 | animateListItem(index) 渐入动画 |
| TopAppBar 3-dot menu | 溢出菜单 (预留, V1 可暂无选项) |

## 数据依赖

| 数据 | 来源 | 说明 |
|------|------|------|
| records: List\<SkinRecord\> | Room DB, skin_records 表 | 按 recordedAt DESC 排序, 分页加载 |
| compareData | 最早 + 最新记录 | 前后对比卡片数据; records.size >= 2 时显示 |
| chartPoints: List\<ChartRecord\> | records 聚合 | 日期 + 各维度评分; size >= 2 时显示趋势图 |
| selectedFilter | ViewModel state | 时间筛选: ALL / WEEK / MONTH / THREE_MONTHS |
| selectedMetric | ViewModel state | 趋势指标: OVERALL / ACNE / PORE / EVENNESS / HYDRATION / REDNESS |
| overallScore | SkinRecord.overallScore | 五维加权平均, 0-100 |
| scoreDiff | 相邻记录差值 | current.overallScore - previous.overallScore |

### ViewModel: TimelineViewModel

- `uiState: StateFlow<TimelineUiState>` — Loading / Empty / Content
- `selectedFilter: StateFlow<TimelineFilter>` — 时间范围筛选
- `selectedMetric: StateFlow<ChartMetric>` — 趋势指标切换
- `setFilter(filter)` — 切换时间范围, 重新查询
- `setMetric(metric)` — 切换趋势指标

## 与其他页面的关系

| 目标页面 | 触发方式 | 传参 |
|---------|---------|------|
| RecordDetailScreen | 点击记录项 | recordId: String |
| ShareCardScreen | 点击 "分享" 按钮 | beforeId: String, afterId: String |
| CameraScreen | 空状态 CTA "开始拍照" | 无 (通过 FAB 导航) |

入站来源:
- Tab 2 直接进入
- DashboardScreen "查看全部趋势" 快捷入口

## 暗色模式

基于 `.figma-mockups/14-dark-core.html`:

| 元素 | Light | Dark |
|------|-------|------|
| Filter Chips 数量 | 4 个 (全部/本周/本月/3个月) | 3 个 (全部/本周/本月) |
| Active chip bg | interactive-primary | interactive-primary (不变) |
| Inactive chip bg | surface-tertiary | dark surface-tertiary |
| Card bg | surface | dark surface |
| Record item surfaces | surface card | dark surface card |
| Score ring progress | primary-400 | primary-400 (不变) |
| content-primary text | dark text | light text (自动映射) |
| content-secondary text | gray-600 | gray-400 (自动映射) |
| Compare card bg | gradients.triWarm | dark triWarm variant |

所有文本色跟随 Material 3 dark token 自动映射, 无需手动切换。

## 与当前实现的差异

| # | 设计稿 | 当前实现 | 优先级 |
|---|--------|---------|--------|
| 1 | TopAppBar 右侧 3-dot overflow menu | 无 action 按钮 | P2 |
| 2 | Filter Row: 4 chips 使用 interactive-primary/surface-tertiary token | 使用 Material3 FilterChip 默认样式 | P1 |
| 3 | Compare Card: gradients.triWarm bg, 130x160dp 照片, 底部渐变遮罩日期标签 | 已实现但部分硬编码 | P1 |
| 4 | Compare Card: VS badge 32dp + shadow-md | 已实现 VS badge (28dp) | P2 |
| 5 | Compare Card: 结果显示 num-lg + content-success | 已实现但需确认 token 对齐 | P2 |
| 6 | Trend Card header: "趋势分析" | 当前为 "趋势变化" | P1 |
| 7 | Trend Card metric chips: 使用 interactive-primary/surface-tertiary token | 使用 Material3 FilterChip 默认样式 | P1 |
| 8 | Trend Card: 面积图 320x80, gradient fill + endpoint circle | 已实现基础 Canvas 图表 | P2 |
| 9 | Records section header: "近期记录" | 当前为 "所有记录" + "N 条" trailing | P1 |
| 10 | Record Item 摘要: AI 生成的摘要文本 | 当前显示 skinType.displayName | P1 |
| 11 | Record Item badge: surface-success/error/tertiary token | 硬编码 Color(0xFFECFDF5) 等 | P1 |
| 12 | Record Item padding: 使用 spacing.md token | 硬编码 12.dp | P2 |
| 13 | Empty state hero: 140dp 圆, radial gradient, camera grid icon | 160dp 圆, linear gradient, emoji 相机 | P1 |
| 14 | Empty state title: "还没有记录哦" | "你的第一次记录将从这里开始" | P1 |
| 15 | Empty state desc: 设计稿单行文案 | 当前为双行文案 | P2 |
| 16 | Empty state 3-step: 44dp circles with colored borders (primary/lavender/rose) | 32dp circles, filled bg | P1 |
| 17 | Empty state step labels: "拍照/分析/追踪" | "自拍一张素颜照/AI自动分析评分/查看趋势变化" | P1 |
| 18 | Empty state CTA: "开始拍照" | "开始第一次记录" | P1 |
| 19 | 设计稿无 AttributionEntryCard | 当前实现含归因入口卡片 (records >= 3 时显示) | 保留 — 设计稿未画但产品需要 |
| 20 | Dark mode: Filter chips 仅 3 个 | 当前 light/dark 均 4 个 | P2 |
