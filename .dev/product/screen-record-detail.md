# Record Detail — 单条肌肤检测记录的完整分析报告

> 设计稿: `.figma-mockups/06-record-detail.html` (1 屏)
> 暗色适配: `.figma-mockups/15-dark-screens.html`
> 版本: V1

## 页面入口

| 来源 | 触发方式 | 传参 |
|------|---------|------|
| TimelineScreen | 点击记录卡片 | `recordId: String` |
| CameraScreen | 分析完成后 "查看详情" 按钮 | `recordId: String` |
| DashboardScreen | 点击最近记录摘要 | `recordId: String` |

路由: `RecordDetailScreen(recordId)` — Voyager Screen，`data class` 以 `recordId` 为唯一标识。

## 页面状态

| 状态 | 条件 | 表现 |
|------|------|------|
| **Loading** | 数据加载中 | 骨架屏 `RecordDetailLoadingSkeleton` |
| **Content** | 记录存在 | 完整详情页 |
| **NotFound** | recordId 无效或已删除 | 居中提示 "未找到该记录" (bodyLarge, onSurfaceVariant) |

## 布局结构

整体容器: `LazyColumn`，全屏，无额外 padding（各区块自行管理水平间距）。

```
┌───────────────────────────────┐
│  Photo Header (280dp)         │ ← 全宽照片 + 渐变覆盖 + 悬浮按钮
│  ┌── 返回(←)          分享 ──┐│ ← glassmorphic 圆形按钮
│  │                           ││
│  │  [照片 / 肤色渐变占位]     ││ ← AsyncImage crop + face SVG 水印
│  │                           ││
│  │  2026.03.14 09:15  82 分  ││ ← 左下日期 tag + 右下评分 tag
│  └───────────────────────────┘│
│  ┌── Score Overview Card ────┐│ ← 浮动卡片 margin-top -40dp
│  │ (82)  皮肤状态不错哦       ││ ← ScoreRing + 标题 + 百分位
│  │       超过 75% 的同龄用户  ││
│  │       ↑ 较上次 +3          ││ ← trend pill
│  └───────────────────────────┘│
│                               │
│  ┌── 多维分析 ───────────────┐│ ← SectionCard
│  │  [六边形雷达图 190dp]      ││ ← 6 轴: 整体/毛孔/泛红/水润/均匀/痘痘
│  └───────────────────────────┘│
│                               │
│  ┌── 指标详情 ───────────────┐│ ← SectionCard
│  │  痘痘  [████████] 85 +3   ││
│  │  毛孔  [██████]   78 +5   ││
│  │  均匀  [███████]  82 +2   ││
│  │  泛红  [█████]    75  0   ││
│  │  水润  [██████]   80 +4   ││
│  └───────────────────────────┘│
│                               │
│  ┌── AI 分析 ────────────────┐│ ← 渐变背景卡片 / 非会员→LockedFeatureCard
│  │  ✓ AI 智能生成             ││
│  │  分析文本...               ││
│  │  ┌ 建议高亮框 ───────────┐││
│  │  │ 建议：含烟酰胺精华...  │││
│  │  └───────────────────────┘││
│  └───────────────────────────┘│
│                               │
│  ┌── 使用产品 ───────────────┐│ ← SectionCard + FlowRow chips
│  │  [●烟酰胺精华] [●水乳面霜]││
│  │  [●防晒霜SPF50] [●洁面]   ││
│  └───────────────────────────┘│
│                               │
│  ┌── 记录信息 ───────────────┐│ ← SectionCard (补充信息)
│  │  拍摄时间 / 肤质类型       ││
│  └───────────────────────────┘│
└───────────────────────────────┘
```

### 1. Photo Header

- **高度**: 280dp，全宽
- **照片**: `AsyncImage` + `ContentScale.Crop`，无照片时显示肤色渐变占位
  - 肤色渐变 (light): `linear-gradient(170deg, #D4B8A8, #C8A898, #B8A088, #A89480)`
  - 肤色渐变 (dark): `linear-gradient(170deg, #5A4A40, #4A3E38, #3E3430, #3A2E28)`
- **渐变覆盖**: `gradients.skin`，铺在照片底部以确保底部标签可读
- **Face SVG 水印**: 半透明人脸椭圆轮廓
  - Light: 深色描边，opacity 0.08
  - Dark: 白色描边，opacity 0.06
- **顶部按钮栏**: absolute 定位于 status bar 下方
  - 返回按钮 (左): 42dp 圆形，`rgba(0,0,0,0.2)` bg + `blur(8px)`，白色 ArrowBack 图标 22dp
  - 分享按钮 (右): 42dp 圆形，同上 glassmorphic 样式，分享图标 16sp
  - Dark mode: bg 改为 `rgba(0,0,0,0.3)`
- **日期标签** (左下): `"2026.03.14 09:15"` — c1(12sp), 白色, bg `rgba(0,0,0,0.3)`, radius-full, padding sm(8dp)/md(12dp)
- **评分标签** (右下): `"82 分"` — c1(12sp) weight 700, 白色, bg `rgba(0,0,0,0.3)`, radius-full

> 当前实现将评分标签放在 bottom-center 并包含文案 "82分 · 良好"。设计稿为左下日期 + 右下评分分离布局。

### 2. Score Overview Card (浮动)

- **定位**: margin-top `-40dp`，z-index 5，水平 padding md(12dp)
- **背景**: surface-primary (即 `colorScheme.surface`)
- **圆角**: large (16dp)
- **阴影**: shadow-md
- **内边距**: xl (20dp)
- **布局**: Row

| 元素 | 规格 |
|------|------|
| ScoreRing | 74dp 直径，5dp stroke，tertiary 底圈，primary-400 进度弧，中心 "82" num-lg(26sp) content-primary |
| 标题 | "皮肤状态不错哦" — h3(19sp), weight 700 |
| 副标题 | "超过 75% 的同龄用户" — b3(13sp), content-secondary |
| Trend pill | "↑ 较上次 +3" — c2(10sp), content-success 文字, surface-success bg, radius-full, padding sm/xs |

**评分文案映射**:

| 分数区间 | 标题文案 |
|----------|---------|
| >= 85 | 你的肌肤状态太棒了 |
| >= 70 | 皮肤状态不错哦 |
| >= 55 | 你的肌肤状态还不错 |
| >= 40 | 你的肌肤需要更多关注 |
| < 40 | 让我们一起改善肌肤吧 |

**Trend pill 颜色规则**:

| 变化 | 箭头 | 文字色 | 背景色 |
|------|------|--------|--------|
| > 0 | ↑ | content-success | surface-success (0.1 alpha) |
| < 0 | ↓ | error | error (0.1 alpha) |
| = 0 | → | onSurfaceVariant | onSurfaceVariant (0.1 alpha) |

### 3. Radar Chart Section

- **容器**: SectionCard，水平 padding md(12dp)
- **标题**: "多维分析" — SectionHeader (h4)
- **图表**: 六边形雷达图，190dp x 190dp
- **6 轴** (顺时针从 12 点): 整体 → 毛孔 → 泛红 → 水润 → 均匀 → 痘痘
- **网格**: 3 层六边形同心线 (outlineVariant, 0.5dp) + 6 条轴线
- **数据面**: primary fill opacity 0.08 + primary stroke 2dp + 顶点圆标记 4dp
- **填充渐变**: 径向 teal(#4ECDC4 0.12α) → mint(#2D9F7F 0.12α) → lavender(#A78BFA 0.12α)
- **轴标签**: c2(10sp), content-secondary, 六边形外侧
- **显示条件**: 至少 3 个维度有数据

> 当前实现标题为 "多维评分"，设计稿为 "多维分析"。

### 4. Metric Bars Section

- **容器**: SectionCard，padding horizontal md(12dp) + vertical sm(8dp)
- **标题**: "指标详情" — SectionHeader (h4)
- **每行布局**: Row，vertical padding 10dp，horizontal spacedBy sm(8dp)
  - 指标名: b3(13sp), content-primary, fontWeight SemiBold, 固定宽 52dp
  - 渐变条: weight 1f, height 6dp, radius-full, surface-tertiary 底色
  - 分值: num-sm(14sp), fontWeight ExtraBold
  - 变化值: c2(10sp), fontWeight Bold, 固定宽 36dp, 右对齐
- **行间分隔**: HorizontalDivider (outlineVariant, 0.5dp)

| 指标 | 色值 | 渐变 (左→右) |
|------|------|-------------|
| 痘痘 | #F27A8E | #FF8A8A → #F27A8E |
| 毛孔 | #4ECDC4 | #7EDCD4 → #4ECDC4 |
| 均匀 | #F5C542 | #FFEE8D → #F5C542 |
| 泛红 | #E87878 | #F5A1A1 → #E87878 |
| 水润 | #45B7D1 | #75C7E1 → #45B7D1 |

**变化值颜色规则**:

| 变化量 | 颜色 |
|--------|------|
| >= +3 | content-success (绿) |
| +1 ~ +2 | onSurfaceVariant (灰) |
| 0 | content-tertiary (淡灰) |
| <= -1 | error (红) |

**条形动画**: `animateFloatAsState`，时长 `Motion.LONG`，缓动 `Motion.EmphasizedDecelerate`。

> 当前实现标题为 "各项指标"，设计稿为 "指标详情"。条形高度当前为 7dp，设计稿为 6dp。

### 5. AI Analysis Card

- **背景**: `linear-gradient(135deg, primary-50(#F0FAF6), lavender-50(#F5F0FF))`
- **边框**: 0.5dp, border-subtle
- **圆角**: extraLarge
- **顶部装饰线**: 3 色渐变 (primary → lavender → rose), 2dp 高, 水平 inset 16dp
- **内边距**: md(12dp)

| 元素 | 规格 |
|------|------|
| Badge | 行: checkmark 图标 + "AI 智能生成" — c2(10sp), content-brand, primaryContainer bg, radius-full, padding sm/xs |
| 分析文本 | b2(14sp), content-primary, line-height 1.6 (≈24sp) |
| 建议高亮框 | surface-brand-subtle bg, radius-small(8dp), padding md(12dp), border-left 3dp interactive-primary |
| 建议文本 | b3(13sp), content-secondary, line-height ≈20sp |

**示例文案**:
- 分析: "你的肌肤整体状态良好，水润度和痘痘指标有明显改善。毛孔方面也有收紧趋势，建议继续保持当前的护肤方案。"
- 建议: "建议：可以适当增加含烟酰胺成分的精华，帮助进一步改善毛孔和均匀度~"

### 6. Products Used Section

- **容器**: SectionCard，padding horizontal md(12dp) + vertical sm(8dp)
- **标题**: "使用产品" — SectionHeader (h4)，trailing "编辑" 链接 (bodySmall, primary, SemiBold)
- **布局**: FlowRow，spacedBy sm(8dp) 水平+垂直
- **每个 Chip**:
  - 背景: surface-secondary (即 surfaceVariant)
  - 圆角: radius-full
  - 内边距: sm(8dp)/md(12dp) (vertical/horizontal → 实际 7dp/14dp)
  - 内容: 彩色圆点 (8dp circle, 品类色) + 产品名 (b3/13sp, content-primary, SemiBold)

**品类色映射** (来自设计稿):

| 品类 | 圆点色 |
|------|--------|
| 精华类 | lavender (#A78BFA) |
| 面霜/水乳 | primary (#2D9F7F) |
| 防晒类 | secondary (#F5C542) |
| 洁面类 | info (#45B7D1) |

**空状态**: "当日无使用记录" (bodyMedium, onSurfaceVariant)

> 当前实现标题为 "当日使用产品"，设计稿为 "使用产品"。当前 chip 缺少彩色圆点。

### 7. Record Info Section (补充)

- **容器**: SectionCard，padding start/end md(12dp) + bottom lg(16dp)
- **标题**: "记录信息" — SectionHeader
- **内容**: 拍摄时间 + 肤质类型 (bodyMedium, onSurfaceVariant)

> 此区块在 HTML 设计稿中未出现，为当前实现独有。保留作为补充信息。

## 交互行为

| 交互 | 行为 |
|------|------|
| 返回按钮 | `navigator.pop()` |
| 分享按钮 | 跳转 `ShareCardScreen()` |
| AI 锁定卡片 "升级" | 跳转 `PaywallScreen()` |
| 产品 "编辑" 链接 | [V2] 跳转产品编辑页 |
| 下拉 | 自然滚动，无下拉刷新 |
| 条形图 | 进入时渐进动画填充 (Motion.LONG + EmphasizedDecelerate) |
| 雷达图 | 进入时展开动画 |

## 数据依赖

```
RecordDetailViewModel.loadRecord(recordId)
  → SkinRecordRepository.getRecordById(recordId)     // Room 本地查询
  → 计算 scoreDiff (与上一条记录对比)
  → 计算 metricDiffs (各指标与上条记录对比)
  → 计算 percentile (百分位，可选)
  → 查询关联产品 usedProducts
  → 查询 AI 分析 summary + recommendations
  → 查询 isPremium (会员状态，决定门控)
```

**UiState 结构**:

| 字段 | 类型 | 说明 |
|------|------|------|
| record | SkinRecord | 完整记录对象 |
| scoreDiff | Int? | 与上次记录的总分差 |
| percentile | Int? | 百分位排名 |
| metricDiffs | MetricDiffs | 各维度差值 (acne/pore/evenness/redness/hydration) |
| summary | String? | AI 分析文本 |
| recommendations | List\<String\> | AI 建议列表 |
| usedProducts | List\<SkincareProduct\> | 当日使用产品 |
| isPremium | Boolean | 是否为付费用户 |

## 门控规格

详见 [feature-gating.md](feature-gating.md) 场景 1。

| 区块 | 免费用户 | 付费用户 |
|------|---------|---------|
| 照片 + 评分概览 | 正常显示 | 正常显示 |
| 雷达图 | [V1] 正常显示 | 正常显示 |
| 指标条形图 | 正常显示 | 正常显示 |
| AI 分析卡片 | 替换为 LockedFeatureCard | 正常显示 |
| 使用产品 | 正常显示 | 正常显示 |

**LockedFeatureCard 参数**:
- title: "解锁完整分析报告"
- subtitle: "AI 智能分析 · 多维雷达图 · 个性化建议"
- message: `FeatureGate.DETAILED_AI_REPORT.lockedMessage`
- onUpgrade → PaywallScreen

## 与其他页面的关系

| 页面 | 关系 |
|------|------|
| TimelineScreen | 主入口，点击记录卡片进入 |
| CameraScreen | 拍照分析完成后进入 |
| DashboardScreen | 最近记录摘要点击进入 |
| ShareCardScreen | 分享按钮跳转目标 |
| PaywallScreen | AI 锁定卡片升级入口 |
| ProductScreen | [V2] "编辑" 链接跳转目标 |

## 暗色模式

| 元素 | Light | Dark |
|------|-------|------|
| Photo 渐变占位 | #D4B8A8→#A89480 | #5A4A40→#3A2E28 |
| Face SVG 水印 | 深色描边, 8% opacity | 白色描边, 6% opacity |
| Glassmorphic 按钮 bg | rgba(0,0,0,0.2) | rgba(0,0,0,0.3) |
| Score Overview Card | surface (白) | surface (dark surface) |
| 指标条形图颜色 | 保持各指标色不变 | 保持各指标色不变 |
| AI Card 渐变 | #F0FAF6 → #F5F0FF | #153E32(0.5α) → #1E1830(0.3α) |
| AI 建议高亮框 | surface-brand-subtle | #58CAA5(0.06α) |
| 整体背景 | background | dark background |

## 与当前实现的差异

| # | 设计稿 | 当前实现 | 优先级 |
|---|--------|---------|--------|
| 1 | 日期 tag 左下 + 评分 tag 右下 (分离) | 评分标签 bottom-center "82分 · 良好" (合并) | P1 |
| 2 | 雷达图标题 "多维分析" | "多维评分" | P2 |
| 3 | 指标条形图标题 "指标详情" | "各项指标" | P2 |
| 4 | 指标条形图高度 6dp | 7dp | P3 |
| 5 | 产品标题 "使用产品" | "当日使用产品" | P2 |
| 6 | 产品 chip 带彩色品类圆点 (8dp) | 无彩色圆点 | P1 |
| 7 | Score Overview 副标题 "超过 75% 的同龄用户" (h3 标题为 "皮肤状态不错哦") | 标题文案映射略有不同 | P2 |
| 8 | AI Card border-left 3dp 高亮建议框 | 无 border-left 装饰 | P2 |
| 9 | 记录信息区块 | 设计稿无此区块 (当前实现额外添加) | P3 — 保留 |
| 10 | Score Overview Card 圆角 large(16dp) + shadow-md | extraLarge + shadow 8dp (偏大) | P2 |
