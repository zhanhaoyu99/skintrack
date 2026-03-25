# Share — 分享对比卡片，将前后对比成果生成精美卡片图片分享到社交平台

> 设计稿: `.figma-mockups/12-share.html` (1 屏)
> 暗色参考: `.figma-mockups/16-dark-utility.html`

## 页面入口

- TimelineScreen 对比卡片 "分享" 按钮
- RecordDetailScreen TopBar 分享图标
- DashboardScreen "分享对比" 快捷入口

## 页面状态

| 状态 | 条件 | 表现 |
|------|------|------|
| 正常 | 有 2 条含照片的记录 | 完整卡片预览 + 模板选择器 + 分享按钮 |
| 加载中 | 照片/数据加载中 | LoadingContent 占位 |
| 保存中 | 点击"保存图片"后 | 按钮 loading 态，Toast "已保存到相册" |
| 分享中 | 点击"分享"后 | 系统 ShareSheet 弹出 |
| 门控 | 免费用户 | 卡片预览可见，底部覆盖 LockedFeatureCard |

## 布局结构

```
┌─────────────────────────┐
│ <- 分享对比卡片           │  TopAppBar (back + title)
├─────────────────────────┤
│         (居中)           │
│  ┌──────────────────┐   │  Card Preview (314dp, radius-extraLarge, shadow-lg)
│  │ [icon] SkinTrack  │   │    Header (gradients.hero bg, padding lg)
│  │  Lisa 的蜕变       │   │    user subtitle (c2, rgba white 0.7)
│  ├──────────────────┤   │
│  │ [70分  ] [82分   ]│   │    Photos Section (row, 2x flex-1, 200dp height)
│  │  2月14日 VS 3月14日│   │    VS badge (34dp circle, centered)
│  ├──────────────────┤   │
│  │     +10 分        │   │    Result Section (surface-primary bg)
│  │  ↑ 皮肤在变好哦    │   │    trend pill (surface-success bg)
│  │    30 天对比       │   │    period label
│  ├──────────────────┤   │
│  │ SkinTrack·记录你的美 [QR]│  Footer (surface-secondary bg)
│  └──────────────────┘   │
│                         │
│ [模板1*] [模板2] [模板3] │  Template Selector (row of 3, gap md)
│                         │
│ [微信][微博][小红书][更多]│  Share Targets (row of 4, gap xl)
│                         │
│ [ 保存图片 ]  [ 分享 ]   │  Action Buttons (row, gap md)
└─────────────────────────┘
```

## 组件详细规格

### 1. TopAppBar

- 导航图标: 返回箭头
- 标题: "分享对比卡片"

### 2. Card Preview

- **宽度**: 314dp，水平居中
- **圆角**: radius-extraLarge (24dp)
- **阴影**: shadow-lg
- **边框**: card-border-width, border-subtle

#### 2a. Header

- **背景**: gradients.hero
- **Padding**: md (12dp) 垂直, lg (16dp) 水平
- **布局**: Row, gap sm (8dp), verticalAlignment center
- **App Icon**: 24dp 方形, radius-extraSmall (4dp), rgba(255,255,255,0.2) 背景, 内含 14dp 白色 SVG 图标
- **Brand Text**: "SkinTrack", c1 (12sp), weight 700, content-inverse (白色), flex 1
- **User Subtitle**: "{userName} 的蜕变", c2 (10sp), rgba(255,255,255,0.7)

#### 2b. Photos Section

- **布局**: Row, 无间距, 高度 200dp
- **每张照片** (flex 1):
  - 照片填充 (ContentScale.Crop)
  - 占位背景: gradients.skin（左侧偏亮色调 #D4B8A8→#B8A090，右侧偏深色调 #E8CFC0→#D8BCA8）
  - 底部渐变遮罩: transparent → rgba(0,0,0,0.6)
  - **Score overlay**: num-md (18sp), weight 800, content-inverse, 左下
  - **Date**: c2 (10sp), rgba(255,255,255,0.7), score 下方 marginTop 1dp
- **VS Badge**:
  - 34dp 圆形, surface-primary 背景, shadow-md
  - 绝对定位: 垂直水平居中 (照片区域中心)
  - "VS" 文字: c1 (12sp), weight 800, content-brand (primary-500)
  - z-index 高于照片

#### 2c. Result Section

- **背景**: surface-primary
- **Padding**: lg (16dp)
- **对齐**: center
- **Delta**: 44sp, weight 800, content-success, letter-spacing -1.5dp
  - 格式: "+{delta} 分"（正值加号）/ "-{delta} 分"（负值）
  - 负值时颜色切换为 content-error
- **Trend Pill**:
  - marginTop sm (8dp)
  - inline-flex, padding xs (4dp) 垂直 / md (12dp) 水平
  - radius-full
  - 背景: surface-success, 文字: content-success, c1 (12sp), weight 600
  - 内容: "↑ 皮肤在变好哦"（正值）/ "↓ 需要关注一下"（负值）/ "→ 状态保持稳定"（零值）
  - 负值: surface-error bg + content-error text; 零值: surface-secondary bg + content-tertiary text
- **Period**: c1 (12sp), content-tertiary, marginTop sm (8dp)
  - 格式: "{N} 天对比"

#### 2d. Footer

- **背景**: surface-secondary
- **Padding**: sm (8dp) 垂直 / lg (16dp) 水平 (实际 10dp 垂直按 HTML)
- **边框顶部**: card-border-width, border-subtle
- **布局**: Row, spaceBetween, verticalAlignment center
- **Watermark**: "SkinTrack · 记录你的美", c2 (10sp), content-disabled
- **QR Placeholder**: 32dp 方形, radius-extraSmall (4dp), surface-tertiary 背景, 内含 18dp QR 图标 (content-tertiary)
  - V1: 静态占位图标; 上线前替换为真实 QR 指向 App 下载页

### 3. Template Selector

- **布局**: Row, gap md (12dp), 水平居中
- **Margin**: bottom xl (20dp)
- **每个模板**: 56dp 方形, radius-medium (12dp)
- **选中态**: interactive-primary 边框 (2dp), surface-brand-subtle 背景, primary-500 图标色
- **未选中态**: border-default 边框 (2dp), 透明背景, content-disabled 图标色
- **图标内容**:
  - 模板 1 (默认/active): 垂直分割线 — 对比布局
  - 模板 2: 中央圆形 — 单照片+分数 (V1 占位)
  - 模板 3: 趋势折线 — 趋势卡片 (V1 占位)
- **V1 行为**: 仅模板 1 可用并默认选中; 模板 2/3 显示 content-disabled 图标，点击无响应（不切换选中态）

### 4. Share Targets

- **布局**: Row, gap xl (20dp), 水平居中
- **Margin**: bottom xl (20dp)
- **每个目标**: Column, center, gap 6dp
- **Icon Circle**: 48dp, radius-full
- **Label**: c2 (10sp), content-secondary, weight 500

| 目标 | 图标背景 | 图标 | 标签 |
|------|----------|------|------|
| 微信 | #07C160 | 微信 SVG (白色) | "微信" |
| 微博 | #E6162D | 微博 SVG (白色) | "微博" |
| 小红书 | #FE2C55 | 小红书 SVG (白色) | "小红书" |
| 更多 | surface-tertiary | 三点 SVG (content-secondary) | "更多" |

- **V1 行为**: 所有 4 个目标点击统一调用系统 ShareSheet（传入卡片截图图片），不直接对接第三方 SDK
- **V2 计划**: 接入微信/微博/小红书 SDK 实现直接分享

### 5. Action Buttons

- **布局**: Row, gap md (12dp), 水平填满, padding lg (16dp) 水平
- **"保存图片"**: OutlinedButton, flex 1
  - 点击: 将卡片区域截图保存到系统相册 (需存储权限, expect/actual)
  - 成功 Toast: "已保存到相册"
  - 失败 Toast: "保存失败，请检查存储权限"
- **"分享"**: PrimaryButton (filled), flex 1
  - 点击: 将卡片截图传入系统 ShareSheet (Android: Intent.ACTION_SEND; iOS: UIActivityViewController)

## 交互行为

1. **进入页面**: 接收 beforeRecordId + afterRecordId 参数，加载两条记录的照片和分数，渲染卡片预览
2. **模板切换** (V1 仅模板 1 可用): 切换时卡片预览内容重新渲染，带 crossfade 动画 (Motion.SHORT)
3. **保存图片**: 使用 GraphicsLayer 或 ComposeView 截图 → 请求存储权限 → 保存到 MediaStore (Android) / Photos (iOS)
4. **分享**: 截图同上 → 缓存到临时文件 → 调用平台 ShareManager.share(imageFile)
5. **返回**: 左上角返回箭头 → navigateUp

## 数据依赖

| 数据 | 来源 | 说明 |
|------|------|------|
| beforeRecord | Room (SkinRecord) | 前照片 + 分数 + 日期 |
| afterRecord | Room (SkinRecord) | 后照片 + 分数 + 日期 |
| userName | Supabase Auth profile | 显示在 header subtitle |
| scoreDelta | 计算值 | afterScore - beforeScore |
| daysDelta | 计算值 | afterDate - beforeDate (天数) |
| isPro | SubscriptionRepository | 控制门控覆盖 |

### 导航参数

```
ShareCardScreen(
    beforeRecordId: String,
    afterRecordId: String
)
```

## 与其他页面的关系

| 页面 | 关系 |
|------|------|
| TimelineScreen | 对比卡片"分享"按钮 → 传入 before/after recordId |
| RecordDetailScreen | TopBar 分享按钮 → 传入当前 record + 最近一条 record |
| DashboardScreen | "分享对比"快捷入口 → 传入最新两条 record |
| PaywallScreen | 免费用户点击门控按钮 → navigateTo(Paywall) |

## 门控规格

- **免费用户**: 卡片预览完整可见（激发分享欲），但底部覆盖 LockedFeatureCard
  - 标题: "Pro 会员专享功能"
  - 描述: "解锁分享精美对比卡片"
  - 按钮: "升级 Pro 解锁分享" → PaywallScreen
  - 试用提示: "14 天免费试用，随时取消"
- **Pro 用户**: 完整功能，无锁定覆盖

## 暗色模式

- **页面背景**: dark surface (跟随系统 dark theme)
- **TopAppBar**: dark 主题色
- **Card Preview 本身保持亮色主题**（白色 surface sections），确保分享出去的卡片始终可读
  - Header: gradients.hero (不变)
  - Photos: 照片渐变占位使用偏暗肤色色调
  - Result Section: 保持 surface-primary (白色)
  - Footer: 保持 surface-secondary (浅灰)
- **模板选择器**: 跟随 dark theme（dark surface bg, dark border）
- **分享目标**: 图标背景色不变（品牌色），标签色跟随 dark content-secondary
- **按钮**: 跟随 dark theme

> 关键原则: 卡片是要分享出去的图片，必须保持亮色以确保在任何平台上可读。仅卡片外围区域跟随暗色主题。

## 与当前实现的差异

| 项目 | 设计稿规格 | 实现状态 |
|------|-----------|---------|
| Card 宽度 314dp | 314dp, radius-extraLarge, shadow-lg | 已对齐 |
| Header gradients.hero | hero 渐变 + logo + brand + user | 已对齐 |
| Photos 200dp + VS 34dp badge | 双照片 + 底部遮罩 + VS 圆形 | 已对齐 |
| Result delta 44sp | 大号 delta + trend pill + period | 已对齐 |
| Footer watermark + QR 32dp | 水印文案 + QR 占位 | 已对齐 |
| Template Selector 56dp | 3 模板, active/inactive 态 | 已对齐 |
| Share Targets (微信/微博/小红书/更多) | 4 圆形 icon + label | 已对齐 (V1 均走系统 ShareSheet) |
| Action Buttons (outlined + primary) | 保存图片 + 分享 | 已对齐 |
| 门控覆盖 | LockedFeatureCard | 已实现 |
| Dark mode 卡片保持亮色 | 外围 dark, 卡片 light | 待验证 |
| 模板 2/3 可用 | V2 计划 | V1 占位 |
| 社交平台直接分享 | V2 SDK 接入 | V1 系统 ShareSheet |
