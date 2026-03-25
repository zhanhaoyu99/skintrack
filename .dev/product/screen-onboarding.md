# 引导页 OnboardingScreen — 首次安装 4 屏引导 + 肤质选择 [V1]

> 设计稿: `.figma-mockups/01-onboarding.html` (4 屏)
> 暗色模式: `.figma-mockups/16-dark-utility.html` (Dark — Onboarding)

---

## 页面入口

- **触发条件**: 首次安装 App（`UserPreferencesEntity.onboardingCompleted == false`）
- **出口**: 完成引导（跳过或选择肤质后）→ `AuthScreen`
- **不再显示**: 完成后写入 `onboardingCompleted = true`，后续启动不再展示

---

## 页面状态

| 状态 | 说明 |
|------|------|
| Page 1-3 介绍页 | 水平 Pager，可左右滑动或点击"下一步"前进 |
| Page 4 肤质选择 | 5 选项单选列表，必须选择才能点击"开始使用" |

---

## 布局结构

### 整体框架

```
Column(fillMaxSize, background: colorScheme.background)
├── HorizontalPager(weight: 1f, pageCount: 4)
│   ├── Page 1-3: OnboardingPageContent  ← 介绍页
│   └── Page 4: SkinTypeSelectionPage    ← 肤质选择
└── BottomArea(padding-h: spacing.section(32dp), padding-bottom: space-48)
    ├── PageIndicator(4 dots)
    ├── PrimaryButton(full-width)
    └── SkipText (仅 Page 1-3)
```

### Page 1-3: 介绍页通用布局

```
Column(fillMaxSize, center-aligned, padding-h: spacing.section(32dp))
├── IllustrationArea (240x240dp, radius-full)
│   ├── RadialGradient background (见各页配色)
│   ├── FloatingAccent 1 (小圆, absolute positioned, opacity 0.5-0.6)
│   ├── FloatingAccent 2 (小圆, absolute positioned, opacity 0.4-0.5)
│   └── SVG icon (100x100dp, strokeWidth: 1.2)
├── Spacer(spacing.section / 32dp) ← illus marginBottom
├── Title (28sp, weight 800, content-primary, letterSpacing: -0.5, lineHeight: 1.25)
│   └── 允许 <br> 换行，居中对齐
├── Spacer(space-10)
├── Description (b2/14sp, content-secondary, lineHeight: b2-lh, maxWidth: 280dp, center)
└── Spacer(spacing.section / 32dp) ← desc marginBottom
```

### Page 4: 肤质选择页布局

```
Column(fillMaxSize)
├── HeaderArea(padding: 20dp-h 24dp-v, paddingBottom: 8dp)
│   ├── Title "选择你的肤质" (26sp, weight 800, content-primary, letterSpacing: -0.5)
│   ├── Spacer(spacing.xs / 4dp)
│   └── Subtitle "帮助 AI 更准确地分析你的皮肤" (b2, content-secondary)
├── Spacer(spacing.md / 12dp)
└── SkinTypeList(column, gap: space-10, padding-h: page-gutter/16dp)
    └── SkinTypeCard x5 (见下方详细规格)
```

### 底部区域 (所有页面共享)

```
Column(padding-h: spacing.section/32dp, paddingBottom: space-48, center-aligned)
├── PageIndicator
│   └── Row(gap: spacing.sm/8dp, marginBottom: spacing.xxl/24dp)
│       ├── ActiveDot: 28dp x 8dp, radius-full, interactive-primary
│       └── InactiveDot: 8dp x 8dp, radius-full, border-default
├── PrimaryButton
│   └── Button(fullWidth, height: dimens.buttonHeight/52dp, radius-full)
│       ├── Page 1-3: label "下一步", gradient: gradients.primary
│       └── Page 4: label "开始使用", gradient: gradients.primary
│           └── disabled (alpha 0.38) when no skin type selected
└── SkipText (仅 Page 1-3)
    └── "跳过" (b3/13sp, weight 500, content-tertiary, marginTop: spacing.md/12dp)
```

---

## 各页详细规格

### Page 1 — 记录你的皮肤变化

| 属性 | 值 |
|------|-----|
| 插画底色 | `radial-gradient(circle at 40% 40%, primary-100, primary-50 70%, transparent)` |
| SVG 图标 | 人脸轮廓 + 相机 + 闪光星 (color: primary-400) |
| 浮动圆 1 | 36x36dp, rose-50, top-right, opacity 0.6 |
| 浮动圆 2 | 24x24dp, lavender-50, bottom-left, opacity 0.5 |
| 标题 | "记录你的\n皮肤变化" |
| 描述 | "每天拍一张自拍，AI 帮你追踪皮肤状态，见证变美的每一步" |

### Page 2 — 管理你的护肤方案

| 属性 | 值 |
|------|-----|
| 插画底色 | `radial-gradient(circle at 40% 40%, secondary-100, secondary-50 70%, transparent)` |
| SVG 图标 | 护肤瓶罐 (color: secondary-400) |
| 浮动圆 1 | 32x32dp, primary-50, top-left, opacity 0.5 |
| 浮动圆 2 | 20x20dp, rose-50, bottom-right, opacity 0.4 |
| 标题 | "管理你的\n护肤方案" |
| 描述 | "记录每天使用的护肤品，AI 分析哪个产品对你最有效" |

### Page 3 — AI 深度分析洞察

| 属性 | 值 |
|------|-----|
| 插画底色 | `radial-gradient(circle at 40% 40%, lavender-100, lavender-50 70%, transparent)` |
| SVG 图标 | 表盘/仪表 + 指针 (color: lavender-400) |
| 浮动圆 1 | 28x28dp, primary-50, bottom-left, opacity 0.5 |
| 浮动圆 2 | 22x22dp, secondary-50, top-right, opacity 0.4 |
| 标题 | "AI 深度\n分析洞察" |
| 描述 | "智能归因分析，了解哪个产品对你的皮肤帮助最大" |

### Page 4 — 肤质选择

#### 肤质卡片规格

| 属性 | Token |
|------|-------|
| 卡片高度 | 自适应内容 |
| 卡片 padding | spacing.md(12dp) vertical, spacing.lg(16dp) horizontal |
| 卡片圆角 | shapes.large / radius-lg (16dp) |
| 卡片间距 | space-10 (10dp) |
| 内部 gap | spacing.md (12dp) |

#### 5 种肤质选项

| 肤质 | id | 图标底色渐变 | SVG 图标色 | 描述文案 |
|------|-----|------------|-----------|---------|
| 油性肌肤 | `oily` | `135deg, secondary-50 → secondary-100` | secondary-400 | T区容易出油，毛孔较大 |
| 干性肌肤 | `dry` | `135deg, info-50 → #D6EFF8` | info-500 | 容易干燥紧绷，需要保湿 |
| 混合肌肤 | `combination` | `135deg, primary-50 → primary-100` | primary-500 | T区偏油，两颊偏干 |
| 敏感肌肤 | `sensitive` | `135deg, rose-50 → rose-100` | rose-400 | 容易泛红过敏，需要温和护理 |
| 中性肌肤 | `normal` | `135deg, lavender-50 → lavender-100` | lavender-400 | 肤质均衡，不油不干 |

#### 卡片内部结构

```
Row(verticalAlignment: CenterVertically, gap: spacing.md/12dp)
├── IconContainer (40x40dp, radius-md/12dp, gradient background)
│   └── SVG icon (icon-size-sm/20dp)
├── TextColumn(weight: 1f)
│   ├── Name (b1/16sp, weight 600, content-primary)
│   └── Description (c1/12sp, content-secondary, marginTop: 1dp)
└── CheckCircle (22x22dp, radius-full)
    ├── Unselected: border 2dp border-default, empty
    └── Selected: fill interactive-primary, checkmark SVG (12dp, white, strokeWidth: 3)
```

#### 卡片选中态 vs 未选中态

| 状态 | 边框 | 背景 | 阴影 |
|------|------|------|------|
| 未选中 | 1.5dp, border-default | surface-primary (透明/白) | 无 |
| 选中 | 1.5dp, interactive-primary | surface-brand-subtle (primary-50) | shadow-brand-sm |

选中态过渡动画: `duration-fast (150ms)` + `easing-standard`

---

## 交互行为

### 页面切换 [V1]
1. **左右滑动**: HorizontalPager 原生手势，任意页面可左右滑动
2. **"下一步"按钮**: 点击 → `animateScrollToPage(currentPage + 1)`
3. **页面指示器**: 跟随当前页动画更新，active dot 宽度 8dp → 28dp 过渡 (`Motion.MEDIUM / 300ms`)

### 跳过 [V1]
4. **"跳过"文字**: 仅 Page 1-3 显示，点击 → `completeOnboarding(skinType: null)` → 跳转 `AuthScreen`
5. **Page 4 无跳过**: 肤质选择是必填项

### 肤质选择 [V1]
6. **单选逻辑**: 点击任一卡片 → 设为选中态，之前选中项取消
7. **按钮状态**: 未选择时 "开始使用" 按钮半透明 (alpha 0.38)，不可点击；选择后变为满透明度
8. **确认流程**: 点击 "开始使用" → `completeOnboarding(skinType)` → 跳转 `AuthScreen`

### 动画 [V1]
9. **列表入场**: 每个元素使用 `animateListItem(index)` 实现交错入场
10. **按钮区切换**: Page 3→4 时底部按钮区使用 `AnimatedContent` 过渡 (fadeIn + slideInVertically)
11. **指示器颜色**: `animateColorAsState` 过渡 (`Motion.MEDIUM`)

---

## 数据依赖

| 数据 | 来源 | 说明 |
|------|------|------|
| `onboardingCompleted` | `UserPreferencesEntity` (Room) | 判断是否显示引导页 |
| `skinType` | `UserPreferencesEntity.skinType` (Room) | 持久化用户选择的肤质 |

### ViewModel: `OnboardingViewModel`

```
- 依赖: UserPreferencesDao
- State: isOnboardingCompleted: StateFlow<Boolean?>
- Action: completeOnboarding(skinType: String?)
  → 写入 onboardingCompleted = true
  → 写入 skinType (如果非 null)
```

### 肤质枚举值

| 显示名 | 存储值 |
|--------|--------|
| 油性肌肤 | `oily` |
| 干性肌肤 | `dry` |
| 混合肌肤 | `combination` |
| 敏感肌肤 | `sensitive` |
| 中性肌肤 | `normal` |

---

## 与其他页面的关系

| 关系 | 页面 | 说明 |
|------|------|------|
| 后续 | `AuthScreen` | 引导完成后跳转到登录/注册页 |
| 读取 | `ProfileScreen` | 个人中心展示用户肤质 |
| 读取 | `DashboardScreen` | 首页 AI 分析参考用户肤质 |
| 设置 | `SettingsScreen` / `EditProfileScreen` | 可在设置中修改肤质 [V2] |

---

## 暗色模式

> 参考: `16-dark-utility.html` Dark — Onboarding

| 元素 | Light | Dark |
|------|-------|------|
| 页面背景 | surface-primary (白) | surface-primary (#0F1412) |
| 插画底色渐变 | `primary-100 → primary-50` 等不透明色 | `rgba(42,157,124,0.15) → rgba(42,157,124,0.05)` 半透明 |
| 浮动装饰圆 | 不显示 (dark mockup 中未绘制) | 可省略或降低 alpha |
| SVG 图标色 | primary-400 / secondary-400 / lavender-400 | primary-300 / secondary-300 / lavender-300 (提亮一档) |
| 标题文字 | content-primary (neutral-900) | content-primary (#EAEFEC) |
| 描述文字 | content-secondary (neutral-600) | content-secondary (#A0AAA5) |
| 跳过文字 | content-tertiary (neutral-500) | content-tertiary (dark token) |
| 指示器 inactive | border-default (neutral-200) | border-default (rgba(255,255,255,0.10)) |
| 指示器 active | interactive-primary (primary-500) | interactive-primary (primary-400) |
| 主按钮 | gradients.primary (500→400→300) | gradients.primary (dark variant) |
| 状态栏图标 | 深色 | 白色 |
| 肤质卡片边框 | border-default (neutral-200) | border-default (rgba(255,255,255,0.10)) |
| 肤质卡片选中背景 | surface-brand-subtle (primary-50) | surface-brand-subtle (rgba(42,157,124,0.12)) |

---

## 与当前实现的差异

> 基于当前代码 `OnboardingScreen.kt` + `OnboardingViewModel.kt` 对比 HTML 设计稿

### 已对齐

- [x] 4 页 HorizontalPager (3 介绍页 + 1 肤质选择)
- [x] 页面指示器 4 dots, active=28dp bar, inactive=8dp circle
- [x] "下一步" / "开始使用" 按钮切换 + AnimatedContent 过渡
- [x] "跳过" 仅 Page 1-3 显示
- [x] 5 种肤质选项单选
- [x] 选中态 primary 边框 + primary 填充 check circle
- [x] skinType 持久化到 UserPreferencesEntity
- [x] animateListItem 交错入场动画

### 需修正

| # | 差异点 | 设计稿 | 当前实现 | 优先级 |
|---|--------|--------|---------|--------|
| 1 | **P1-3 标题/描述文案** | P1 "记录你的皮肤变化" / P2 "管理你的护肤方案" / P3 "AI 深度分析洞察" + 对应新描述 | 沿用旧文案 (P1 "肌肤变化", P3 "AI 帮你找到最佳护肤方案") | P2 |
| 2 | **P1-3 插画渐变色** | P1 primary-100→50, P2 secondary-100→50, P3 lavender-100→50 (radialGradient 正圆) | 使用多色列表 radialGradient, P2 用 Rose 而非 Secondary | P2 |
| 3 | **P4 标题文案** | "选择你的肤质" | "你的肤质是？" | P3 |
| 4 | **P4 副标题文案** | "帮助 AI 更准确地分析你的皮肤" | "选择肤质帮助 AI 更准确地分析" | P3 |
| 5 | **肤质卡片圆角** | radius-lg (16dp) | shapes.extraLarge (24dp) — 偏大 | P2 |
| 6 | **肤质图标容器** | 40x40dp, radius-md/12dp, 渐变底色 (50→100) | 44dp, 13dp radius, 单色 copy(alpha=0.2) | P2 |
| 7 | **肤质描述文案** | 油性 "T区容易出油，毛孔较大" / 敏感 "容易泛红过敏，需要温和护理" / 中性 "肤质均衡，不油不干" | 略有不同文案 | P3 |
| 8 | **干性肤质图标色** | info-50 → info-500 (蓝色系) | SkinHydrationLight (#45B7D1) | P3 |
| 9 | **油性肤质图标色** | secondary-50→100 渐变 + secondary-400 SVG | Apricot300 单色 copy(alpha=0.2) | P2 |
| 10 | **底部 padding** | padding-h: space-32, padding-bottom: space-48 | padding-h: 36dp, padding-bottom: 50dp — 偏大 | P2 |
| 11 | **内容区 padding** | P1-3 padding-h: space-32; P4 header padding: 20-24dp | P1-3: 36dp, P4: 28dp | P2 |
| 12 | **指示器 inactive 颜色** | border-default (neutral-200) | outlineVariant (可能不同 token) | P3 |
| 13 | **暗色模式插画** | 半透明 rgba 渐变 + primary-300 SVG | 使用 copy(alpha=0.15/0.1) — 基本正确但颜色档位需核实 | P3 |
