# 会员订阅页 PaywallScreen — 全屏付费墙，展示 Pro 会员权益并引导订阅

> 设计稿: `.figma-mockups/10-paywall.html` (1 屏)
> 暗色模式: `.figma-mockups/16-dark-utility.html`

## 页面入口

- ProfileScreen → "会员中心" 菜单项
- SettingsScreen → "会员管理" 菜单项
- 任意 LockedFeatureCard "升级 Pro" 按钮 (RecordDetail / Attribution / Share / CameraLimit)
- 14 天试用期到期弹窗 → "立即订阅"

## 页面状态

| 状态 | 条件 | 行为 |
|------|------|------|
| 默认 (未订阅) | `!isPremium` | 完整付费墙，年度方案默认选中 |
| 试用期内 | `user.trialEndDate > now && !isPremium` | 显示试用 badge |
| 试用期过期 | `user.trialEndDate <= now && !isPremium` | 隐藏试用 badge，其余不变 |
| 加载中 | 点击"立即订阅"后 | CTA 按钮显示 spinner，禁用交互 |
| 支付成功 | Mock 支付完成 | dismiss 页面 + Toast "订阅成功" |
| 支付失败 | Mock 支付异常 | 显示 Snackbar 错误信息 + 重试 |

## 布局结构

```
┌─────────────────────────────┐
│ [Hero 渐变区域 ~260dp]       │ ← gradients.paywall 背景
│  ✕ (关闭)                    │ ← 左上角半透明关闭按钮
│  ◎ 装饰圆 (120dp + 80dp)    │ ← 半透明白色装饰
│                              │
│      👑 (金色皇冠)            │ ← 80dp 圆, gold-gradient + glow 动画
│                              │
│    解锁全部功能               │ ← h2, weight 800, white
│  AI 深度分析 · 无限记录 · 归因报告│ ← b2, white 70%
│                              │
│  🕐 新用户 14 天免费试用      │ ← 试用 badge (条件显示)
├─────────────────────────────┤
│ [Social Proof]               │
│  (L)(M)(S)(J)(+1K)          │ ← 4 头像 + 计数，重叠排列
│  12,580 位用户已订阅          │ ← c1, 数字加粗 brand 色
├─────────────────────────────┤
│ [Benefits 权益列表]           │
│  ✓ 无限次 AI 肌肤分析         │
│  ✓ 完整归因分析报告           │
│  ✓ 雷达图 + 多维深度分析      │ ← 5 项，checkmark 圆 + b2 文字
│  ✓ 精美对比卡分享             │
│  ✓ 云端备份 · 永不丢失        │
├─────────────────────────────┤
│ [Plan Selection 方案选择]     │
│ ┌──────────┐ ┌──────────────┐│
│ │ ¥19.9/月  │ │ 省¥70.8      ││ ← 浮动节省标签 (rose-400)
│ │ 月度订阅   │ │ ¥168/年      ││
│ │           │ │ 年度订阅·推荐 ││ ← 默认选中，primary 边框
│ └──────────┘ └──────────────┘│
├─────────────────────────────┤
│ [     立即订阅     ]         │ ← 全宽 primary 按钮
│                              │
│ 🔒安全支付  🔄随时取消  ⚡即时生效│ ← 信任信号 row
│                              │
│        恢复购买               │ ← 文字按钮
│ 订阅即同意《服务条款》和《隐私政策》│ ← 法律文案
│     自动续费，可随时取消       │
└─────────────────────────────┘
```

## 组件详细规格

### 1. Hero 渐变区域

- **背景**: `gradients.paywall` (全宽，约 260dp 高)
- **溢出**: `overflow: hidden` (裁切装饰圆)
- **装饰圆**: 两个绝对定位半透明白圆
  - `::before`: 120dp, rgba(255,255,255,0.06), 右上偏移
  - `::after`: 80dp, rgba(255,255,255,0.04), 左下偏移
- **Padding**: section(32dp) top, lg(16dp) horizontal, xxl(24dp) bottom
- **文字居中对齐**

### 2. 关闭按钮

- **位置**: 绝对定位，左上角 (statusBarHeight + 8dp top, spacing.sm left)
- **尺寸**: 40dp x 40dp
- **背景**: rgba(255,255,255,0.15), radius-full
- **图标**: X 图标, 20dp, white
- **点击**: `navigator.pop()` 返回上一页

### 3. 皇冠图标

- **容器**: 80dp 圆形, `gold-gradient` 背景
- **图标**: 皇冠 SVG, iconSizeLg(32dp), white fill
- **阴影**: `shadow-gold-sm` — `0 8px 32px rgba(255,215,0,0.3)`
- **动画**: `crown-glow` keyframe, 2s ease-in-out infinite
  - 0%/100%: `box-shadow 0 8px 32px rgba(255,215,0,0.3)`
  - 50%: `box-shadow 0 8px 40px rgba(255,215,0,0.5)`
- **间距**: marginBottom spacing.lg(16dp)

### 4. 标题 + 副标题

- **标题**: "解锁全部功能"
  - font-h2(24sp), weight 800, content-inverse (white)
  - letterSpacing: font-h2-ls
  - marginBottom: spacing.sm(8dp)
- **副标题**: "AI 深度分析 · 无限记录 · 归因报告"
  - font-b2(14sp), rgba(255,255,255,0.7), line-height: font-b2-lh
- 两者均 `z-index: 1` (位于装饰圆之上)

### 5. 试用 Badge

- **条件**: 仅当 `user.trialEndDate > now && !isPremium` 时显示
- **布局**: inline-flex row, center aligned, gap spacing.xs(4dp)
- **图标**: 时钟 SVG (10dp, content-success stroke)
- **文案**: "新用户 14 天免费试用"
- **样式**: font-c1(12sp), weight 600, content-success 色
- **背景**: surface-success, radius-full
- **Padding**: xs(4dp) vertical, lg(16dp) horizontal
- **间距**: marginTop spacing.lg(16dp)

### 6. Social Proof (社交证明)

- **头像组**: 水平排列，居中
  - 4 个字母头像 + 1 个计数圆，共 5 个
  - 每个: 28dp 圆形, 2dp white border, radius-full
  - 重叠: marginLeft -8dp (首个 0)
  - 字体: 10sp, weight 700, content-inverse

| 头像 | 字母 | 渐变背景 |
|------|------|---------|
| 1 | L | linear-gradient(135deg, rose-300, rose-400) |
| 2 | M | linear-gradient(135deg, primary-300, primary-400) |
| 3 | S | linear-gradient(135deg, lavender-300, lavender-400) |
| 4 | J | linear-gradient(135deg, secondary-300, secondary-400) |
| 5 | +1K | surface-tertiary bg, content-tertiary, 8sp |

- **文案**: "12,580 位用户已订阅"
  - font-c1(12sp), content-tertiary, center
  - 数字 "12,580" 使用 content-brand + bold
- **间距**: 头像 marginTop spacing.lg(16dp), marginBottom spacing.xs(4dp)
  - 文案 marginBottom spacing.xl(20dp)

### 7. Benefits 权益列表

- **容器**: padding lg(16dp) horizontal, marginBottom spacing.xs(4dp)
- **每项**: row layout, gap spacing.md(12dp), padding 10dp vertical
- **勾选圆**: 24dp 圆形, surface-brand-subtle bg
  - 内部: checkmark SVG, 14dp, content-brand, strokeWidth 3
- **文案**: font-b2(14sp), content-primary, weight 500

| # | 权益 |
|---|------|
| 1 | 无限次 AI 肌肤分析 |
| 2 | 完整归因分析报告 |
| 3 | 雷达图 + 多维深度分析 |
| 4 | 精美对比卡分享 |
| 5 | 云端备份 · 永不丢失 |

### 8. Plan Selection (方案选择)

- **容器**: row layout, gap spacing.sm+2(10dp), padding lg(16dp) horizontal
- **间距**: marginTop spacing.xl(20dp), marginBottom spacing.lg(16dp)
- **每个方案**: flex:1, padding lg(16dp), radius-large(16dp), position relative

#### 月度方案 (未选中态)

- **边框**: 1.5dp, border-default
- **背景**: transparent (surface)
- **价格**: "¥19.9" font-num-lg(26sp) + "/月" font-c1(12sp) content-tertiary
- **名称**: "月度订阅" font-b3(13sp), content-secondary, marginTop spacing.xs(4dp)

#### 年度方案 (默认选中态)

- **边框**: 1.5dp → 2dp, interactive-primary (选中时)
- **背景**: surface-brand-subtle
- **阴影**: shadow-brand-sm
- **顶部装饰线**: 绝对定位, top:0, left/right: spacing.lg(16dp), height 2dp, radius-full, `gradients.primary` 背景
- **价格**: "¥168" font-num-lg(26sp) + "/年" font-c1(12sp) content-tertiary
- **名称**: "年度订阅 · 推荐" font-b3(13sp), content-secondary
  - "推荐" 使用 content-brand 色
- **节省标签** (浮动):
  - 绝对定位: top -8dp, right spacing.sm(8dp)
  - 文案: "省 ¥70.8"
  - 样式: 9sp, weight 700, content-inverse (white)
  - 背景: rose-400, radius-full
  - Padding: 2dp vertical, sm(8dp) horizontal

#### 选中态切换

- 点击方案卡片切换选中状态
- 选中态: primary border + surface-brand-subtle bg + shadow-brand-sm + 顶部渐变线
- 未选中态: border-default border + transparent bg
- 动画: `duration-fast` + `easing-standard`

### 9. CTA 按钮

- **样式**: 全宽 primary 按钮 (btn-primary)
- **文案**: "立即订阅"
- **高度**: dimens.buttonHeight
- **间距**: marginBottom spacing.md(12dp)
- **加载态**: 按钮内容替换为 CircularProgressIndicator (white, 20dp), 按钮 disabled
- **成功**: dismiss 页面 + Toast "订阅成功"

### 10. Trust Signals (信任信号)

- **布局**: row, justify center, gap spacing.xxl(24dp)
- **间距**: margin spacing.lg(16dp) vertical
- **每项**: column, center aligned, gap spacing.xs(4dp)
  - 图标: iconSizeSm(16dp), content-tertiary
  - 文案: font-c2(10sp), content-tertiary, weight 500

| 图标 | 文案 |
|------|------|
| Shield (盾牌) | 安全支付 |
| Checkmark (勾选) | 随时取消 |
| Clock (时钟) | 即时生效 |

### 11. 恢复购买

- **类型**: 文字按钮 (btn-text)
- **文案**: "恢复购买"
- **样式**: font-b3(13sp), content-tertiary
- **点击**: 调用 `PaymentManager.restorePurchases()`
- **V1**: Mock 实现，Toast "没有可恢复的购买记录"

### 12. 法律文案

- **文案**: "订阅即同意《服务条款》和《隐私政策》\n自动续费，可随时取消"
- **样式**: font-c2(10sp), content-disabled, center, lineHeight 1.4
- **间距**: marginTop spacing.sm(8dp)
- **链接**: 《服务条款》和《隐私政策》可点击 → 打开对应 WebView
- **V1**: 链接占位，无实际 URL

## 交互行为

### 方案选择

```
点击月度卡片 → selectedPlan = MONTHLY → 年度卡片变为未选中
点击年度卡片 → selectedPlan = YEARLY → 月度卡片变为未选中
默认: selectedPlan = YEARLY
```

### 支付流程 (V1 Mock)

```
点击"立即订阅"
  → 按钮进入 loading 态 (spinner + disabled)
  → delay 1.5s (模拟支付)
  → Mock 更新 SubscriptionRepository: isPremium = true, plan = selectedPlan
  → dismiss PaywallScreen
  → Toast "订阅成功，欢迎成为 Pro 会员！"
```

V2 真实支付流程:
```
点击"立即订阅"
  → PaymentManager.purchase(selectedPlan)
  → 成功: 更新 SubscriptionRepository → dismiss → Toast "订阅成功"
  → 用户取消: 恢复按钮状态，不显示错误
  → 网络错误: Snackbar "网络连接失败，请检查后重试" + 重试
  → 支付失败: Snackbar "支付失败: {原因}" + 重试
```

### 恢复购买

```
点击"恢复购买"
  → PaymentManager.restorePurchases()
  → V1 Mock: Toast "没有可恢复的购买记录"
  → V2 成功: 更新订阅状态 → Toast "购买已恢复"
  → V2 无记录: Toast "没有可恢复的购买记录"
```

### 关闭页面

- 点击左上角 X 按钮 → `navigator.pop()`
- 系统返回手势 → `navigator.pop()`

## 数据依赖

| 数据 | 来源 | 说明 |
|------|------|------|
| isPremium | SubscriptionRepository | 是否 Pro 会员 |
| trialEndDate | SubscriptionRepository | 试用到期日，决定 badge 显示 |
| selectedPlan | 本地 State | MONTHLY / YEARLY，默认 YEARLY |
| isLoading | 本地 State | 支付进行中 |
| subscriberCount | 硬编码 | "12,580" (V1 静态文案) |

### ViewModel

```
PaywallViewModel:
  - selectedPlan: StateFlow<Plan> (MONTHLY | YEARLY)
  - isLoading: StateFlow<Boolean>
  - isPremium: StateFlow<Boolean> (from SubscriptionRepository)
  - isTrialActive: StateFlow<Boolean> (trialEndDate > now)
  - subscribe() → Mock 支付
  - restorePurchases() → Mock 恢复
```

## 与其他页面的关系

| 页面 | 关系 |
|------|------|
| ProfileScreen | 入口: "会员中心" 菜单 → PaywallScreen |
| SettingsScreen | 入口: "会员管理" 菜单 → PaywallScreen |
| RecordDetailScreen | 入口: LockedFeatureCard "升级 Pro" → PaywallScreen |
| AttributionScreen | 入口: LockedFeatureCard "升级 Pro" → PaywallScreen |
| ShareCardScreen | 入口: LockedFeatureCard "升级 Pro" → PaywallScreen |
| CameraScreen | 入口: 拍照限额门控 → PaywallScreen |
| feature-gating.md | 门控 4 态规格，LockedFeatureCard 组件定义 |

订阅成功后:
- 所有 LockedFeatureCard 解锁 (isPremium 状态变化驱动)
- ProfileScreen 会员状态更新
- CameraScreen 拍照限额解除

## 暗色模式

| 元素 | Light | Dark |
|------|-------|------|
| Hero 背景 | gradients.paywall | gradients.paywall 暗色变体 |
| 装饰圆 | rgba(255,255,255,0.06/0.04) | 保持 (暗背景上同样效果) |
| 关闭按钮 bg | rgba(255,255,255,0.15) | 保持 |
| 标题/副标题 | white / white 70% | 保持 (渐变背景上) |
| 试用 badge | surface-success / content-success | dark surface-success / dark content-success |
| 头像 border | surface-primary (white) | dark surface-primary |
| 权益 checkmark bg | surface-brand-subtle | dark surface-brand-subtle |
| 权益文字 | content-primary | dark content-primary |
| 方案卡片 bg | transparent / surface-brand-subtle | dark surface / dark surface-brand-subtle |
| 方案卡片 border | border-default / interactive-primary | dark border-default / dark interactive-primary |
| 价格文字 | content-primary | dark content-primary |
| 信任信号 | content-tertiary | dark content-tertiary |
| 法律文案 | content-disabled | dark content-disabled |
| 皇冠 glow | 保持金色光晕 | 保持金色光晕 |

关键: Hero 渐变区域的文字始终为白色系，不受暗色模式影响。方案卡片、权益列表等内容区域随主题色切换。

## 与当前实现的差异

> 基于 M2.9 Phase 6 对齐后的状态

| # | 差异项 | 设计稿 (HTML) | 当前实现 | 优先级 |
|---|--------|--------------|---------|--------|
| 1 | Hero 背景 | `gradients.paywall` 专用渐变 | 可能用 `gradients.warm` | 确认 token |
| 2 | 皇冠动画 | `crown-glow` 2s infinite 光晕动画 | 需确认是否实现 | P1 |
| 3 | 社交证明头像 | 4 个渐变字母头像 + "+1K" 圆 | 已实现，需确认渐变色 | P2 |
| 4 | 方案卡片布局 | 并排 row，flex:1 等宽 | 已实现 | 已对齐 |
| 5 | 节省标签 | rose-400 bg, 9sp, 绝对定位右上 | 已实现 | 已对齐 |
| 6 | 顶部渐变装饰线 | 选中方案顶部 2dp gradients.primary | 需确认 | P2 |
| 7 | 支付流程 | V1 Mock (delay + 状态更新) | Mock 实现 | V1 |
| 8 | 恢复购买 | 文字按钮 + Mock 响应 | 需确认是否实现 | P2 |
| 9 | 法律文案链接 | 《服务条款》《隐私政策》可点击 | 占位文案 | V2 |
| 10 | 真实支付 | 微信支付 + StoreKit 2 | — | V2 |

## 实现状态

✅ **已对齐** (M2.9 Phase 6) — 80dp 皇冠图标 + 试用提示 pill + 省¥70.8 标签 + 原价删除线 + 社交证明 + 信任信号 + 方案选择
