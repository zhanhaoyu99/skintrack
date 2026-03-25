# 导航架构

> 最后更新：2026-03-22
> 设计稿：`.figma-mockups/03-dashboard.html`（底部导航栏）

## 底部导航栏（5 Tab + FAB）

### 布局规格
- 高度：`dimens.bottomNavHeight` (84dp)
- 背景：`surface-primary`
- 顶部边框：`card-border-width` (0.5dp) `border-subtle`
- 阴影：无（仅边线分隔）

### Tab 定义

| 位置 | 标签 | 图标（active） | 图标（inactive） | 路由 |
|------|------|---------------|----------------|------|
| 1 | 首页 | Home filled | Home outlined | `DashboardScreen` |
| 2 | 记录 | List filled | List outlined | `TimelineScreen` |
| 3 | 拍照 | **FAB**（见下方） | — | `CameraScreen` |
| 4 | 分析 | Star filled | Star outlined | `AttributionReportScreen` |
| 5 | 我的 | Person filled | Person outlined | `ProfileScreen` |

### Tab 样式
- Active 图标：`interactive-primary` 填充，无指示器 pill
- Inactive 图标：`content-tertiary` 描边
- Active 标签：`c2` (10sp/600w)，`interactive-primary`
- Inactive 标签：`c2`，`content-tertiary`
- 图标尺寸：`dimens.iconSizeMd` (24dp)

### FAB 拍照按钮（位置 3）
- 尺寸：`dimens.fabSize` (52dp)
- 形状：`shapes.full`（圆形）
- 背景：`gradients.primaryBold`
- 阴影：`shadow-brand-md`
- 图标：`+` 加号（24dp，`content-inverse` 白色）
- 标签："拍照"（`c2`，`interactive-primary`）
- 位置：居中，向上偏移突出于导航栏

### 导航行为
- Tab 切换：无动画过渡，直接替换内容
- FAB 点击：全屏打开 `CameraScreen`（Voyager push，非 Tab 内嵌）
- 返回行为：CameraScreen 有独立返回箭头，pop 回上一个 Tab

## 页面路由图

```
App 入口
├── OnboardingScreen（首次启动）
│   └── → AuthScreen
├── AuthScreen（未登录）
│   ├── Login Tab
│   ├── Register Tab
│   └── ForgotPasswordScreen
│       └── → AuthScreen（返回登录）
└── HomeScreen（已登录，5 Tab 容器）
    ├── Tab 1: DashboardScreen
    │   ├── → CameraScreen（去拍照 / 空态 CTA）
    │   ├── → TimelineScreen（查看全部趋势）
    │   ├── → ProductScreen（快捷操作-护肤品）
    │   ├── → AttributionReportScreen（快捷操作-归因分析）
    │   └── → ShareCardScreen（快捷操作-分享对比）
    ├── Tab 2: TimelineScreen
    │   ├── → RecordDetailScreen（点击记录卡片）
    │   ├── → ShareCardScreen（对比卡分享按钮）
    │   └── → CameraScreen（空态 CTA）
    ├── Tab 3: CameraScreen（FAB 触发，全屏）
    │   ├── → RecordDetailScreen（查看详情按钮）
    │   └── → PaywallScreen（拍照限额门控）
    ├── Tab 4: AttributionReportScreen
    │   └── → PaywallScreen（归因门控）
    └── Tab 5: ProfileScreen
        ├── → SettingsScreen（齿轮图标）
        │   ├── → EditProfileScreen
        │   ├── → ChangePasswordScreen
        │   ├── → PaywallScreen（会员中心）
        │   └── DeleteAccountDialog（弹窗）
        ├── → ProductScreen（护肤品管理菜单）
        ├── → AttributionReportScreen（归因分析菜单）
        └── → PaywallScreen（会员中心菜单）

独立页面（从多处入口可达）：
├── PaywallScreen（模态全屏，Close 按钮关闭）
├── ShareCardScreen（push 进入，back 返回）
├── RecordDetailScreen（push 进入，back 返回）
│   ├── → ShareCardScreen（分享按钮）
│   └── → PaywallScreen（AI 详情门控）
└── ProductScreen（push 进入，back 返回）
    └── AddProductSheet（BottomSheet 弹出）
```

## 登录状态路由

```
App 启动
├── 已完成引导 + 已登录 → HomeScreen (Tab 1 Dashboard)
├── 已完成引导 + 未登录 → AuthScreen
├── 未完成引导 → OnboardingScreen → AuthScreen → HomeScreen
└── 退出登录 → AuthScreen（清除导航栈）
```

## 暗色模式

底部导航栏在暗色模式下：
- 背景：`surface-primary` (dark: #0F1412)
- 边框：`border-subtle` (dark: rgba white 0.06)
- Active 图标：`interactive-primary` (dark: primary-400 #4BBE97)
- Inactive 图标：`content-tertiary` (dark: #7A857F)
- FAB 渐变：`gradients.primaryBold` 使用暗色变体

## 与当前实现的差异

- 当前实现已完成 5 Tab + FAB 架构，与设计稿一致 ✅
- Tab 图标使用 AutoMirrored 变体 ✅
- FAB 居中突出样式已实现 ✅
- 无已知偏差
