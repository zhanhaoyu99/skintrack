# Profile — 个人中心，展示用户信息、统计数据、肌肤目标与功能入口

> 设计稿: `.figma-mockups/07-profile.html` (1 屏)
> 暗色模式: `.figma-mockups/14-dark-core.html`
> Tab 5 位置，标题 "我的" + 齿轮设置图标

## 页面入口

- **Bottom Nav** 第 5 个 Tab "我的"（主入口）
- Dashboard Header 头像点击 → ProfileScreen
- 任何需要登录的场景可跳转至此

## 页面状态

| 状态 | 条件 | 表现 |
|------|------|------|
| Loading | 数据加载中 | 居中 `LoadingContent()` |
| Content | 数据就绪 | 完整页面渲染（无论是否有记录） |

> 注：Profile 不区分"空状态"——无记录时统计值为 0 / "--"，页面结构不变。

## 布局结构

```
┌──────────────────────────────┐
│  我的                    ⚙️   │ ← 标题栏 (白色文字 on gradient)
│                              │
│  (L)  Lisa              >    │ ← 用户卡片 (gradient header ~200dp)
│       lisa@example.com       │
│  [PRO 会员] [混合肌]         │ ← 标签行
├──────────────────────────────┤
│ ┌────┬────┬────┬────┐        │ ← 统计玻璃卡 (上浮 -16dp)
│ │ 42 │  7 │  8 │(82)│        │
│ │总记录│连续 │护肤品│最新 │        │
│ └────┴────┴────┴────┘        │
│                              │
│ 肌肤目标           编辑      │ ← SectionCard
│ [祛痘] [收毛孔] [提亮] [+添加]│
│                              │
│ 🧴 护肤品管理    8 个产品  > │ ← 功能菜单 SectionCard
│ 📊 归因分析报告  AI 洞察   > │
│ 👑 会员中心      PRO …2027 > │
│                              │
│ ⏰ 打卡提醒      每天 08:00> │ ← 设置菜单 SectionCard
│ ☁️ 数据同步      [已同步]  > │
│ ℹ️ 关于 SkinTrack v1.0.0  > │
│                              │
│ 退出登录                     │ ← 独立 SectionCard
│                              │
│      🌿 SKINTRACK            │ ← App Footer
│        v1.0.0                │
├──────────────────────────────┤
│ 首页  记录  (+)  分析  [我的] │ ← Bottom Nav
└──────────────────────────────┘
```

## 布局结构 — 详细规格

### 1. Profile Header

渐变背景区域，约 200dp 高，底部留 52dp 供统计卡浮层重叠。

| 属性 | 值 |
|------|-----|
| 背景 | `gradients.profile`（hero 渐变） |
| 装饰圆 | `::before` 80dp rose-300 opacity 0.15；`::after` 60dp lavender-300 opacity 0.1（drawBehind 实现） |
| 底部额外 padding | 52dp（为 StatsCard overlap 留白） |

#### 1a. 标题栏

- 左: "我的" (h3 / titleLarge, white, Bold)
- 右: 齿轮 `Icons.Default.Settings` (white) → SettingsScreen
- padding: top xl, bottom md

#### 1b. 用户卡片（整行可点击 → EditProfileScreen）

- **头像**: avatarXl (72dp) + 外圈 halo (1.5dp white 0.2 border + 4dp padding + 3dp white 0.35 border)
  - 内部: CircleShape, rose-200→300 线性渐变背景
  - 内容: 首字母大写 (titleLarge, white, Bold)
  - [V2] 支持图片上传替代首字母
  - 阴影: shadow-rose-sm
- **姓名**: h3 (22sp), white, ExtraBold
- **邮箱**: b3 (13sp), white 0.75 opacity; 未登录时显示 "登录后同步数据"
- **标签行** (FlowRow, gap sm):
  - **VIP 标签**: `gradients.vipBadge` (gold) 背景, radius-full, "⭐ VIP" (c2 11sp, #7B4C00 棕色文字), shadow-gold-sm
  - **肤质标签**: white 0.18 bg, radius-full, 肤质中文 (c1 12sp, white, SemiBold)
    - 映射: OILY→油性肌, DRY→干性肌, COMBINATION→混合肌, SENSITIVE→敏感肌, NORMAL→中性肌
    - 设计稿: rgba white 0.15 bg + backdrop-blur 4px
- **右箭头**: KeyboardArrowRight (white, 0.6 opacity, 20dp)

### 2. Stats Glass Card

| 属性 | 值 |
|------|-----|
| 定位 | margin-top -36dp (offset y), 水平 padding lg |
| 背景 | surface-primary (white 0.92 alpha) |
| 圆角 | extraLarge (24dp) |
| 阴影 | shadow-md (elevation 8dp) |
| 内部 padding | horizontal 12dp, vertical 18dp |
| 顶部装饰线 | 2dp gradient line (primary→rose→lavender), 水平 inset 16dp |
| 入场动画 | `animateListItem(0)` |

4 个统计项等宽排列，`VerticalDivider` 分隔（vertical padding sm）:

| # | 标签 | 数据源 | 数值样式 | 数值色 | 图标 |
|---|------|--------|---------|--------|------|
| 1 | 总记录 | records.size | num-md (24sp ExtraBold) | content-primary (onSurface) | 📋 mint-50 bg |
| 2 | 连续打卡 | streak.currentStreak | num-md | apricot-400 (Rose-300 in mockup) | ⚡ apricot-50 bg |
| 3 | 护肤品 | products.size | num-md | content-primary | 🧴 lavender-50 bg |
| 4 | 最新评分 | latestScore | ScoreRing (34dp, stroke 3dp) | content-brand | — (ScoreRing 自带) |

- 标签: labelSmall 11sp, onSurfaceVariant, SemiBold
- 图标: 36dp (menuIconSize) 圆角 10dp 方块，emoji 14sp 居中
- 设计稿第 1 项有底部 gradient underline (gradients.primary, 2dp)——当前实现为顶部整条渐变线

### 3. Skin Goals Card

| 属性 | 值 |
|------|-----|
| 容器 | `SectionCard`, contentPadding horizontal md / vertical 14dp |
| 入场动画 | `animateListItem(1)` |

#### 标题行
- 左: "我的护肤目标" (titleSmall 15sp, Bold)
- 右: "编辑" (bodySmall 13sp, content-brand/primary, SemiBold) → [V1] 暂无交互; [V2] BottomSheet 多选

#### Goal Pills (FlowRow, gap sm)

| 目标ID | 显示 | 背景色 | 文字色 | 边框(设计稿) |
|--------|------|--------|--------|-------------|
| acne | 祛痘 | rose-50 | rose-400/500 | rose-100 |
| pore | 收毛孔 | mint-50 (primary-50) | primary | primary-100 |
| brighten | 提亮 | lavender-50 (secondary-50) | lavender-400 (secondary-600) | secondary-100 |
| hydrate | 补水 | #E8F4FD | #3B82F6 | — |
| anti_aging | 抗老 | #FCE7F3 | #BE185D | — |
| redness | 退红 | #FFF1F2 | #E11D48 | — |

- Pill 样式: radius-full, padding horizontal 14dp / vertical 6dp, shadow 1dp, c1 (labelMedium 13sp, SemiBold)
- "**+ 添加**" pill: surfaceVariant bg, onSurfaceVariant text, 无 shadow
- 无目标时显示默认三个: 祛痘 / 收毛孔 / 提亮
- 数据源: `UserPreferences.skinGoals` (逗号分隔字符串)

### 4. Feature Menu Section (SectionCard)

| 属性 | 值 |
|------|-----|
| 容器 | `SectionCard`, contentPadding horizontal md / vertical 2dp |
| 入场动画 | `animateListItem(2)` |
| 分隔线 | `HorizontalDivider`, horizontal padding md |

每项使用 `MenuItem` 组件:

| 菜单项 | Emoji | 图标底色 | 标题 | 副标题 | 跳转 |
|--------|-------|---------|------|--------|------|
| 护肤品管理 | 🧴 | apricot-50 | 护肤品管理 | 管理你的护肤品清单 | ProductManageScreen |
| 归因分析报告 | 📊 | #EDE9FE (lavender-50/100) | 归因分析报告 | 查看护肤品效果分析 | AttributionReportScreen |
| 会员中心 | 👑 | #FEF3C7 (secondary-50/100) | 会员中心 | Pro 会员 · 到期日 | PaywallScreen |

图标: 38dp 方块, radius 11dp, emoji 16sp 居中

### 5. Settings Menu Section (SectionCard)

同上容器规格。

| 菜单项 | 图标 | 图标底色 | 图标 tint | 标题 | 副标题 | 特殊 trailing |
|--------|------|---------|----------|------|--------|--------------|
| 打卡提醒 | Icons.Notifications | #FEF3C7 | #D97706 | 打卡提醒 | 每天 20:00 提醒 | — |
| 数据同步 | Icons.Refresh | #DBEAFE | #3B82F6 | 数据同步 | 已同步 | "已同步" pill (mint-50 bg, primary text, labelSmall 11sp) |
| 关于 SkinTrack | Icons.Info | surfaceVariant | onSurfaceVariant | 关于 SkinTrack | 版本 1.0.0 | — |

数据同步状态标签:
- 已同步: primary 色 "已同步" pill (mint-50 bg)
- 同步中: loading indicator
- 同步失败: error 色 "同步失败"

### 6. Logout Card (独立 SectionCard)

- `MenuItem`: title "退出登录", textColor = error, showArrow = false
- 点击: viewModel.logout() → navigator.replaceAll(AuthScreen())

### 7. App Footer

| 属性 | 值 |
|------|-----|
| padding | vertical lg |
| 对齐 | 水平居中, spacedBy sm |

- Logo: 36dp 方块, radius 10dp, `gradients.primary` bg, "🌿" 18sp
- 品牌名: "SKINTRACK" (labelMedium, Bold, onSurfaceVariant, letterSpacing 0.5sp)
- 版本: "v1.0.0 (Build 42)" (labelSmall, onSurfaceVariant 0.6 alpha)
- 设计稿: "SkinTrack" (c1, content-disabled, letter-spacing 2dp) + "v1.0.0" (c2, content-disabled)

## 交互行为

| 交互 | 触发 | 行为 |
|------|------|------|
| 设置 | 点击齿轮 icon | navigator.push(SettingsScreen()) |
| 编辑资料 | 点击用户卡片区域 / 右箭头 | navigator.push(EditProfileScreen()) |
| 护肤品管理 | 菜单项点击 | navigator.push(ProductManageScreen()) |
| 归因分析 | 菜单项点击 | navigator.push(AttributionReportScreen()) |
| 会员中心 | 菜单项点击 | navigator.push(PaywallScreen()) |
| 打卡提醒 | 菜单项点击 | [V1] 无操作; [V2] 弹出时间选择器 |
| 数据同步 | 菜单项点击 | [V1] 无操作; [V2] 手动触发同步 |
| 关于 | 菜单项点击 | [V1] 无操作; [V2] 版本信息/隐私/条款页 |
| 编辑目标 | 点击 "编辑" | [V1] 无操作; [V2] BottomSheet 多选目标标签 |
| 添加目标 | 点击 "+ 添加" | [V1] 无操作; [V2] 同上 BottomSheet |
| 退出登录 | 菜单项点击 | viewModel.logout() → replaceAll(AuthScreen()) |
| 列表入场 | 页面首次渲染 | `animateListItem(index)` 渐入动画 |

## 数据依赖

```kotlin
// ProfileViewModel 注入
class ProfileViewModel(
    authRepository: AuthRepository,          // 用户信息 + 登出
    skinRecordRepository: SkinRecordRepository,  // 总记录数 + 最新评分
    productRepository: ProductRepository,    // 护肤品数量
    updateCheckInStreak: UpdateCheckInStreak, // 连续打卡天数
    userPreferencesDao: UserPreferencesDao,   // 肤质 + 肌肤目标
)

// UiState
sealed interface ProfileUiState {
    data object Loading
    data class Content(
        totalRecords: Int,
        totalProducts: Int,
        latestScore: Int?,
        averageScore: Int?,
        currentStreak: Int,
        longestStreak: Int,
        skinType: String?,         // "OILY"/"DRY"/"COMBINATION"/...
        skinGoals: List<String>,   // ["acne","pore","brighten"]
    )
}
```

- `authUser`: 通过 `authRepository.observeAuthUser()` 独立 StateFlow，提供 displayName / email
- 四路 `combine` flow → Content state
- skinGoals 存储格式: 逗号分隔字符串 "acne,pore,brighten"

## 与其他页面的关系

| 目标页面 | 入口 | 备注 |
|---------|------|------|
| SettingsScreen | 齿轮图标 | 设置主页面 |
| EditProfileScreen | 用户卡片点击 | 编辑昵称/邮箱/肤质 |
| ProductManageScreen | 菜单: 护肤品管理 | 产品 CRUD |
| AttributionReportScreen | 菜单: 归因分析报告 | AI 分析报告 |
| PaywallScreen | 菜单: 会员中心 | 付费墙 |
| AuthScreen | 退出登录 | replaceAll 清空导航栈 |

**被引用方**:
- DashboardScreen 头像点击 → 本页面（通过 Tab 切换）

## 暗色模式

参考 `.figma-mockups/14-dark-core.html`:

| 区域 | Light | Dark |
|------|-------|------|
| Profile Header | gradients.profile (明亮版) | gradients.profile dark variant（更低饱和度/更深底色） |
| 装饰圆 | white 0.06/0.04 | 维持低 opacity，暗色调 |
| 用户名/标签 | white | white（在 gradient 上不变） |
| Stats Card | white 0.92 bg | dark surface token |
| 统计数值色 | onSurface / rose-400 等 | 对应 dark token |
| Goal Pills | rose-50/mint-50 等 | dark translucent variants (rgba) |
| 菜单图标底色 | apricot-50 等不透明色 | rgba 版本（更低亮度） |
| SectionCard | surface | dark surface |
| Footer 文字 | onSurfaceVariant / disabled | 维持 disabled token |

> 暗色模式由 Theme 系统自动切换，各 token 在 `DarkExtendedColors` 中已定义。

## 与当前实现的差异

| # | 设计稿规格 | 当前实现 | 优先级 | 版本 |
|---|-----------|---------|--------|------|
| 1 | VIP 标签: "PRO 会员" (gold-gradient, shadow-gold-sm) | "⭐ VIP" (gradients.vipBadge) | Low | V1 ok |
| 2 | 肤质标签: rgba white 0.15 bg + backdrop-blur 4px | white 0.18 bg, 无 backdrop-blur | Low | V1 ok (Compose 无原生 backdrop-blur) |
| 3 | 头像: avatarXl 72dp + shadow-rose-sm | 72dp + halo border, shadow 通过 border 模拟 | Low | V1 ok |
| 4 | Stats Card: margin-top -16dp (设计稿) | offset y = -36dp (实现) | Low | V1 ok (视觉已对齐) |
| 5 | 第一统计项底部 gradient underline | 顶部整条渐变线 | Low | V1 ok |
| 6 | "编辑" 目标 → BottomSheet | 无交互 | Medium | V2 |
| 7 | "+ 添加" 目标 → BottomSheet | 无交互 | Medium | V2 |
| 8 | 打卡提醒时间设置 | 无交互 | Medium | V2 |
| 9 | 数据同步手动触发 | 无交互 | Medium | V2 |
| 10 | 关于页面 (版本/隐私/条款) | 无交互 | Low | V2 |
| 11 | 头像图片上传 | 首字母 avatar | Medium | V2 |
| 12 | 设计稿打卡提醒时间 "每天 08:00" | 实现中写 "每天 20:00 提醒" | Low | 需确认产品需求 |
| 13 | 设计稿 goal pills 有对应色 border (rose-100 等) | 实现用 shadow 1dp 替代 | Low | V1 ok |
| 14 | 设计稿 footer: "SkinTrack" + "v1.0.0" 简洁 | 实现: logo方块 + "SKINTRACK" + "v1.0.0 (Build 42)" | Low | V1 ok (增强版) |

## 实现状态

✅ **已对齐** (M2.9 Phase 5) — gradient header + 4 统计项 + 肌肤目标 FlowRow + 图标圆角底色 + 全部副标题 + 退出登录 + App Footer + 5 Tab 导航
