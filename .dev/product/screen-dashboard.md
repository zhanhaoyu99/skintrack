# Dashboard — 首页信息流，承载每日核心体验

> 设计稿: `.figma-mockups/03-dashboard.html` (2 态: 内容态 + 空状态)
> 暗色参考: `.figma-mockups/14-dark-core.html`

## 页面入口

- 底部导航 Tab 1 "首页" (House filled icon)
- 启动 App 默认落地页
- Voyager Tab 内嵌，非独立 push 页面

## 页面状态

| 状态 | 条件 | 说明 |
|------|------|------|
| Loading | 数据加载中 | 骨架屏 (DashboardLoadingSkeleton) |
| Empty | 用户无任何 SkinRecord | 空状态引导拍照 |
| Content | 至少有 1 条 SkinRecord | 完整信息流 |

---

## 布局结构 (Content State)

```
┌─────────────────────────────────┐
│  上午好 ☀️                       │
│  Lisa              🔔  (L)      │ ← Header
├─────────────────────────────────┤
│ ┌─────────────────────────────┐ │
│ │ ○ ○ ○ (装饰圆)              │ │
│ │ (82)   皮肤状态不错哦        │ │ ← Hero Card
│ │ 总评分  各项指标都在稳步...   │ │
│ │ ↑较上周+3.2   2026.03.14记录 │ │
│ └─────────────────────────────┘ │
│ [85痘痘][78毛孔][82均匀][75泛红][80水润] │ ← Mini Metrics
│                                 │
│ 🌸 今天还没拍照哦~  [去拍照]     │ ← Photo Reminder
│                                 │
│ [肌肤趋势][护肤品]               │
│ [归因分析][分享对比]              │ ← Quick Actions 2x2
│                                 │
│ ✨ 今日紫外线偏强                │ ← Skin Tip
│    记得涂防晒哦~                 │
│                                 │
│ 每日打卡           🔥连续 7 天   │ ← Check-in Card
│ ✓ ✓ ✓ ✓ ✓ ✓ [7]             │
│ ⭐ 太棒了! 连续打卡 7 天...      │ ← Milestone badge
│                                 │
│ 肌肤趋势          查看全部 >    │ ← Trend Chart
│ [7天] [30天] [90天]             │
│ [=====折线图=====]              │
│ 3/8 ... 今天                    │
├─────────────────────────────────┤
│ 首页  记录  (+)  分析  我的      │ ← Bottom Nav (84dp)
└─────────────────────────────────┘
```

LazyColumn 容器: `contentPadding` horizontal md(12dp), vertical sm(8dp); `verticalArrangement.spacedBy(12dp)`. 每个 item 使用 `animateListItem(index)` 入场动画。

---

### 1. Header [V1]

**容器**: Row, fillMaxWidth, padding horizontal lg(16dp), top 4dp, bottom 14dp

| 位置 | 元素 | 规格 |
|------|------|------|
| 左列 | 问候语 | b3(13sp→实际14sp), FontWeight.Medium, content-secondary, letterSpacing 0.1sp |
| 左列 | 用户昵称 | h2(24sp→实际26sp), FontWeight.ExtraBold(800), content-primary, letterSpacing -0.6sp |
| 右行 | 通知铃铛 | 44x44dp, CircleShape, surface-primary bg, border 0.5dp outlineVariant, shadow-xs; 内部 Notifications icon 20dp, content-secondary tint |
| 右行 | 红点 | 8dp circle, error-500 fill, border 2dp surface (白环), TopEnd 偏移 x=-9dp y=9dp; 仅有未读通知时显示 |
| 右行 | 头像 | 44x44dp, CircleShape, rose-200→300 渐变 bg; 中心首字母大写, titleMedium Bold, white |

**问候语逻辑**:
- 0:00-5:59 → "凌晨好"
- 6:00-11:59 → "上午好 ☀️"
- 12:00-17:59 → "下午好"
- 18:00-23:59 → "晚上好 🌙"

**交互**: 点击头像 → ProfileScreen (当前未实现导航)

---

### 2. Hero Card [V1]

**容器**: Card, fillMaxWidth, shape RoundedCornerShape(28dp), shadow-brand-lg

**背景**: `gradients.hero` (线性渐变 145deg: #1A7A63 → #2D9F7F → #3DBFA0 → #4ECDC4)

**装饰圆**: 3 个绝对定位圆形, `Color.White.copy(alpha = 0.06f~0.08f)`:
- 160dp, offset(100dp, -50dp), alpha 0.08
- 140dp, offset(-30dp, 80dp), alpha 0.05
- 80dp, offset(60dp, 50dp), alpha 0.03

**内容 padding**: horizontal 20dp, vertical 18dp

| 元素 | 规格 |
|------|------|
| ScoreRing | 84x84dp, bg circle rgba(white, 0.12), progress stroke rgba(white, 0.95), strokeWidth 6.5dp |
| 分数 | num-lg(26sp), white, Bold, 居中 |
| "总评分" | c2(10sp), rgba(white, 0.7) |
| 状态标题 | h3(19sp), white, Bold, letterSpacing -0.3sp; 文案根据分数区间动态生成 |
| 状态副文 | b3(13sp), rgba(white, 0.75) |
| 趋势 pill | Row, bg rgba(white, 0.12), radius-full, padding horizontal 10dp vertical 4dp; "↑" + "较上周 +3.2" c2(12sp), rgba(white, 0.85) |
| 日期标签 | c2(12sp), rgba(white, 0.5), 右对齐, "YYYY.MM.DD 记录" |

**分数区间 → 文案映射** (getScoreStatusTitle / getScoreStatusSubtitle):
- ≥80: "皮肤状态不错哦" / "各项指标都在稳步改善中，继续保持~"
- 60-79: "皮肤状态还行" / "还有提升空间，加油~"
- <60: "需要多关注一下哦" / "调整护肤方案，会慢慢好起来的~"

---

### 3. Mini Metrics Row [V1]

**容器**: Row, fillMaxWidth, horizontalArrangement spacedBy sm(8dp), 5 个等宽卡片

每个卡片:
- **背景**: surface-secondary, RoundedCornerShape(14dp → medium)
- **padding**: sm(8dp) horizontal, xs(4dp) vertical
- **顶部色条**: height 2.5dp, 各指标色, radius-full, 贴卡片顶部
- **分值**: num-sm(14sp), FontWeight.Bold, 各指标色
- **标签**: c2(10sp), content-tertiary
- **迷你条**: height 3dp, radius-full, bg surface-tertiary, fill 对应指标色, 宽度 = 分值%

| 指标 | Token | Hex | 标签 |
|------|-------|-----|------|
| 痘痘 | metric-acne | #F27A8E | 痘痘 |
| 毛孔 | metric-pore | #4ECDC4 | 毛孔 |
| 均匀 | metric-evenness | #F5C542 | 均匀 |
| 泛红 | metric-redness | #E87878 | 泛红 |
| 水润 | metric-hydration | #45B7D1 | 水润 |

---

### 4. Photo Reminder Card [V1]

**显示条件**: `!state.hasTakenPhotoToday` (今日未拍照)

**背景**: `gradients.roseWarm` (135deg: rose-50 → #FFF5F6)

**容器**: radius large(16dp), padding lg(16dp)

| 元素 | 规格 |
|------|------|
| 图标容器 | 44dp, rose-100→50 渐变 bg, radius medium(12dp), 内部 camera icon rose-400 |
| 主文案 | h4(16sp), content-primary, "今天还没拍照哦~" |
| 副文案 | b3(13sp), content-secondary, "每天一拍，见证蜕变的美好" |
| 按钮 | "去拍照", btn-sm, rose-400 bg, white text, radius-full, shadow-rose-sm |

**交互**: 点击 "去拍照" → CameraScreen (Voyager push)

---

### 5. Quick Actions Grid [V1]

**布局**: 2x2 网格 (2 列), gap md(12dp)

每个单元格:
- **背景**: surface-secondary, radius medium(12dp), padding lg(16dp), shadow-xs
- **图标圆**: 36dp, gradient bg 135deg (color-50 → color-100), 内部 colored icon
- **标题**: h4(16sp), content-primary
- **副标题**: c1(12sp), content-secondary

| # | 标题 | 副标题 | 图标色系 | 图标 | 导航目标 |
|---|------|--------|---------|------|---------|
| 1 | 肌肤趋势 | 7 天变化 | primary-50/100, primary-500 | chart icon | TimelineScreen (Tab 切换) |
| 2 | 护肤品 | {N} 个在用 | secondary-50/100, secondary-500 | bottle icon | ProductManageScreen (push) |
| 3 | 归因分析 | AI 洞察 | lavender-50/100, lavender-400 | clock icon | AttributionReportScreen (push) |
| 4 | 分享对比 | 生成卡片 | rose-50/100, rose-400 | upload icon | ShareCardScreen (push) |

**动态数据**: "护肤品" 副标题中的数字来自 `state.productCount`

---

### 6. Skin Tip Card [V1 本地 mock / V2 天气 API]

**背景**: `gradients.lavenderSoft` (lavender 柔和渐变), border 0.5dp lavender subtle

**容器**: radius large(16dp), padding lg(16dp)

| 元素 | 规格 |
|------|------|
| 图标容器 | 44dp, lavender-100→50 渐变 bg, radius medium(12dp), lavender-400 sun icon |
| 主文案 | h4(16sp), content-primary, "✨ 今日紫外线偏强" |
| 副文案 | b3(13sp), content-secondary, "记得涂防晒哦~ 出门前 15 分钟使用效果最佳" |

**[V1]**: 本地预设贴士池 (10-15 条护肤常识)，按日期 hash 轮换，每日更换 1 条
**[V2]**: 对接天气 API (紫外线/湿度/温度)，动态推荐护肤建议

---

### 7. Check-in Card [V1]

**容器**: SectionCard

**Header 行**: "每日打卡" (SectionHeader 标题) + streak pill (Row: rose-400 fire icon + "连续 {N} 天" b3 Bold)

**周日历**: Row, 7 个圆形 day item, gap sm(8dp)

| 日类型 | 样式 |
|--------|------|
| 已完成 (非今日) | primary-400 stroke circle, white bg, checkmark icon, shadow-brand-sm |
| 今日 | border 2.5dp interactive-primary, surface-brand-subtle bg, day number text |
| 未来 | 无特殊样式 (灰色描边) |

**周标签**: 一/二/三/四/五/六/日 (weekdayLabel, c2)

**里程碑徽章** (streak 达到里程碑时显示):
- 背景: `gradients.triWarm` (warm 三色渐变)
- radius medium(12dp)
- secondary-400 star icon
- 文案 b3(13sp):
  - 7 天: "太棒了! 连续打卡 7 天，你的坚持正在改变肌肤~"
  - 14 天: "太厉害了! 连续打卡 14 天，达成两周成就"
  - 30 天: "坚持一个月! 连续打卡 30 天，达成月度成就"

---

### 8. Trend Chart Card [V1]

**显示条件**: `state.chartPoints.size >= 2` (至少 2 个有效数据点)

**容器**: SectionCard

**Header**: SectionHeader "肌肤趋势" + "查看全部" action → TimelineScreen

**周期 Chips**: Row, gap sm(8dp)

| Chip | 值 | 选中样式 | 未选中样式 |
|------|---|---------|-----------|
| 7 天 | 7 | interactive-primary bg, white text | surface-tertiary bg, content-secondary text |
| 30 天 | 30 | (同上) | (同上) |
| 90 天 | 90 | (同上) | (同上) |

**图表**: Canvas 绘制 SVG 风格面积折线图 (复用 TrendChart 组件)
- **尺寸**: 约 340x100dp (fillMaxWidth)
- **面积填充**: primary-400 渐变 (上方不透明 → 下方透明)
- **折线**: primary-400 stroke
- **数据点**: 圆形标记
- **当前值 tooltip**: primary-500 bg, white text, radius-full, 显示 "{score} 分"
- **Y 轴**: c2(10sp), content-disabled, 刻度 60/75/90
- **X 轴**: 日期标签, c2(10sp), content-tertiary; 最右侧 "今天" 用 content-brand + Bold

---

## 布局结构 (Empty State)

```
┌─────────────────────────────────┐
│  上午好                          │
│  Zane                    (Z)    │ ← Header (无通知红点)
├─────────────────────────────────┤
│                                 │
│    ○        ○       ○           │ ← 3 个装饰 blob
│                                 │
│         [插画区域]               │ ← 160dp 中心插画
│     ○ radial 渐变圆             │
│       + 浮动 accent 小圆        │
│       + 📷 相机 emoji           │
│                                 │
│    开启你的变美旅程              │ ← h2(24sp), ExtraBold(800)
│                                 │
│  只需一张素颜自拍，              │
│  AI 帮你解读肌肤密码             │ ← b1(15sp→16sp), content-secondary
│  见证皮肤一天天变好~             │    textAlign Center, lineHeight 24sp
│                                 │
│  [📷拍照] [🔬AI分析] [📈追踪]   │ ← 3 步引导
│                                 │
│  [     拍第一张自拍     ]        │ ← primary button, 70% width
│                                 │
│  已有 12,580 位用户在使用        │ ← social proof
│                                 │
├─────────────────────────────────┤
│ 首页  记录  (+)  分析  我的      │
└─────────────────────────────────┘
```

### 装饰背景

3 个 radialGradient 圆形 blob, drawBehind 绘制:
- primary-50 色系: radius 110dp, center offset(-40dp, 12%h), alpha 0.5
- rose-50 色系: radius 90dp, center offset(width+30dp, 70%h), alpha 0.4
- lavender-50 色系: radius 50dp, center offset(80%w, 30%h), alpha 0.3

### Hero 插画区域 (160dp)

- 主圆: 140dp, radialGradient (primary-100 → primary-50 → surfaceVariant), CircleShape
- 浮动圆 1: 40dp, offset(60dp, -60dp), Rose-100→50 渐变, alpha 0.6
- 浮动圆 2: 24dp, offset(-60dp, 50dp), Lavender-100→50 渐变, alpha 0.5
- 中心: 📷 emoji (48sp)

### 文字与 CTA

| 元素 | 规格 |
|------|------|
| 标题 | "开启你的变美旅程", h2(24sp), ExtraBold, letterSpacing -0.5sp, animateFadeIn(100) |
| 描述 | "只需一张素颜自拍，AI 帮你解读肌肤密码\n见证皮肤一天天变好~", b1(15sp), content-secondary, center, lineHeight 24sp, animateFadeIn(150) |
| 3 步引导 | Row, gap lg(16dp), animateFadeIn(200); 每步: 36dp circle + border 1.5dp + emoji 16sp + label 11sp SemiBold |
| CTA 按钮 | "拍第一张自拍", primary bg, fillMaxWidth(0.7f), buttonHeight(52dp), extraLarge shape, animateFadeIn(300) |
| 社会证明 | "已有 **12,580** 位用户在使用", b3(13sp), "12,580" 用 content-brand + Bold; animateFadeIn(400) |

**3 步引导配色**:

| 步骤 | Emoji | 标签 | 边框色 | 背景色 |
|------|-------|------|--------|--------|
| 1 | 📷 | 拍照 | primary-200 (Mint200) | primary-50 (Mint50) |
| 2 | 🔬 | AI 分析 | lavender-200 | lavender-50 |
| 3 | 📈 | 追踪 | rose-200 | rose-50 |

**交互**: CTA "拍第一张自拍" → CameraScreen (Voyager push)

---

## 交互行为

| 交互 | 行为 | 说明 |
|------|------|------|
| 点击 头像 | push ProfileScreen | [V1] 暂未实现导航 |
| 点击 通知铃铛 | (预留) | [V2] 通知列表页 |
| 点击 Hero Card | (无) | 纯展示 |
| 点击 "去拍照" | push CameraScreen | 拍照提醒卡按钮 |
| 点击 快捷操作-肌肤趋势 | Tab 切换到记录页 | 由 HomeScreen 处理 Tab 切换 |
| 点击 快捷操作-护肤品 | push ProductManageScreen | |
| 点击 快捷操作-归因分析 | push AttributionReportScreen | |
| 点击 快捷操作-分享对比 | push ShareCardScreen | |
| 点击 "查看全部" (趋势图) | Tab 切换到记录页 | |
| 切换周期 Chip | 更新趋势图数据 | ViewModel.onTrendPeriodChange(7/30/90) |
| 下拉刷新 | (未实现) | [V2] 考虑加入 |
| 列表入场 | animateListItem(index) | 每个 LazyColumn item 渐入 |
| 空状态入场 | animateFadeIn(delay) | 阶梯式淡入, 100ms 间隔 |

---

## 数据依赖

### ViewModel: DashboardViewModel

注入依赖: `AuthRepository`, `SkinRecordRepository`, `ProductRepository`, `UpdateCheckInStreak`

### UiState 数据结构

```kotlin
sealed interface DashboardUiState {
    data object Loading : DashboardUiState

    data class Empty(
        val username: String,
    ) : DashboardUiState

    data class Content(
        val username: String,
        val latestRecord: SkinRecord,        // 最新一条记录
        val scoreChange: Float,              // 与上一条记录的分差
        val hasTakenPhotoToday: Boolean,     // 今日是否已拍照
        val chartPoints: List<ChartRecord>,  // 当前周期趋势数据
        val allChartPoints: List<ChartRecord>,
        val currentStreak: Int,              // 连续打卡天数
        val totalRecords: Int,               // 总记录数
        val weekCheckIns: List<DayCheckIn>,  // 本周 7 天打卡状态
        val productCount: Int,               // 在用护肤品数量
    ) : DashboardUiState
}

data class DayCheckIn(
    val weekdayLabel: String,   // "一"~"日"
    val dayOfMonth: Int,        // 日期数字
    val isCompleted: Boolean,   // 当天是否有记录
    val isToday: Boolean,       // 是否今日
)
```

### 数据来源

| 字段 | 来源 | 说明 |
|------|------|------|
| username | AuthRepository.currentUser() | displayName 或 email 前缀 |
| latestRecord | SkinRecordRepository (Flow, sorted desc) | 实时响应式 |
| scoreChange | latestRecord.overallScore - records[1].overallScore | 需要至少 2 条记录 |
| hasTakenPhotoToday | latestRecord.recordedAt 日期 == today | |
| chartPoints | records filtered by period (7/30/90 天) | overallScore != null |
| currentStreak | UpdateCheckInStreak.observeStreak() | Room Flow |
| weekCheckIns | 本周一到日, 逐日检查 recordDates | |
| productCount | ProductRepository.countByUser() | 一次性查询 |

---

## 与其他页面的关系

| 目标页面 | 触发点 | 导航方式 |
|---------|--------|---------|
| CameraScreen | "去拍照" 按钮 / 空状态 CTA | Voyager push (全屏) |
| ProfileScreen | 点击头像 | Voyager push |
| TimelineScreen | 快捷操作-肌肤趋势 / 趋势图 "查看全部" | HomeScreen Tab 切换 |
| ProductManageScreen | 快捷操作-护肤品 | Voyager push |
| AttributionReportScreen | 快捷操作-归因分析 | Voyager push |
| ShareCardScreen | 快捷操作-分享对比 | Voyager push |

---

## 暗色模式

所有颜色 token 自动跟随 Material 3 暗色主题映射, 以下为需特殊处理的元素:

| 元素 | Light | Dark |
|------|-------|------|
| Hero 卡片 | gradients.hero (light) | gradients.hero (dark variant) |
| 装饰圆 | White alpha 0.06~0.08 | White alpha 0.06 (不变) |
| 通知铃铛/头像 | 原样 | 原样 |
| Mini Metric 卡片 | surface-secondary (neutral-50) | surface-secondary (dark) |
| 拍照提醒 bg | rose-50 → #FFF5F6 | Rose300.alpha(0.12) → Rose300.alpha(0.06) |
| 拍照按钮 | Rose300 | Rose500 |
| 护肤贴士 bg | Lavender50 | Lavender300.alpha(0.12) |
| 头像渐变 | Rose200→300 | Rose500→600 |
| 空状态装饰圆 | Mint100 alpha 0.5 | (跟随) |
| 空状态 rose bg | Rose50 | Rose300.alpha(0.12) |

实现方式: 顶层 `@Composable` helper 函数 (roseBackground, roseBgEnd, roseButton, lavenderBg, avatarGradient 等) 通过 `isSystemInDarkTheme()` 分支选择色值。

---

## 与当前实现的差异

| # | 设计稿规格 | 当前实现 | 状态 | 优先级 |
|---|-----------|---------|------|--------|
| 1 | Header 问候 b3(13sp) | 实际用 14sp | 差异微小, 可接受 | Low |
| 2 | Header 昵称 h2(24sp) weight 800 | 实际 26sp ExtraBold | 差异微小, 可接受 | Low |
| 3 | Hero ScoreRing 84dp, bg rgba(white,0.12) | 84dp 已对齐, bg 圈待确认 | 需验证 | Medium |
| 4 | Mini Metric border-top 2.5dp | 待确认是否已实现顶部色条 | 需验证 | Medium |
| 5 | 拍照提醒文案 "每天一拍，见证蜕变的美好" | 实现中可能为旧文案 "坚持记录，看见皮肤的变化" | 需更新 | Low |
| 6 | 快捷操作 "分享对比" 副标题 "生成卡片" | 旧版可能为 "记录蜕变" | 需更新 | Low |
| 7 | 空状态标题 "开启你的变美旅程" | 已对齐 | OK | — |
| 8 | 空状态描述文案 | "只需一张素颜自拍，AI 帮你解读肌肤密码" 已对齐 | OK | — |
| 9 | 3 步引导用 emoji 而非 SVG | 已用 emoji | OK | — |
| 10 | 趋势图 "肌肤趋势" Tab 切换导航 | 目前回调为空 `{ }` | 需接线 | Medium |
| 11 | 头像点击 → Profile | 未实现导航 | 需实现 | Medium |
| 12 | 护肤贴士天气数据 [V2] | [V1] 本地 mock | 按计划 | — |
| 13 | Loading 骨架屏 | DashboardLoadingSkeleton 已实现 | OK | — |
