# 导航架构

## 底部导航栏（5 Tab + FAB）

设计稿定义了 5 个底部导航项：

```
┌──────┬──────┬──────┬──────┬──────┐
│ 首页  │ 记录  │  (+)  │ 分析  │ 我的  │
│ Home │ Grid │ FAB  │Chart │Person│
└──────┴──────┴──────┴──────┴──────┘
```

| # | Tab | 图标 | 页面 | 说明 |
|---|-----|------|------|------|
| 1 | 首页 | House filled | DashboardScreen | 主信息流 |
| 2 | 记录 | 2x2 Grid | TimelineScreen | 肌肤记录列表 |
| 3 | 拍照 | + (FAB) | CameraScreen | 居中凸起 FAB，全屏页面 |
| 4 | 分析 | Line Chart | AttributionReportScreen | 归因分析报告 |
| 5 | 我的 | Person | ProfileScreen | 个人中心 |

### 底部导航栏样式 (base.css `.bottom-nav`)
- **高度**: 84dp (含底部安全区)
- **背景**: surface, 顶部 0.5px 分割线 `rgba(0,0,0,0.06)`
- **布局**: `space-around`, 水平排列

### Tab 项样式 (`.ni`)
- **尺寸**: 64dp 宽, 4dp paddingTop
- **图标**: 24dp SVG
- **标签**: 10sp, font-weight 500
- **未选中**: 图标 `#9CA3AF`, 标签 `#9CA3AF`
- **选中**: 图标 `primary`, 标签 `primary` + font-weight 700

### FAB 样式 (`.ni-fab` + `.fab`)
- **凸起**: margin-top -22dp (从导航栏顶部向上凸出)
- **圆形按钮**: 52dp, primary 背景, 圆形
- **图标**: 26dp 白色 `+` (stroke-width 2.5)
- **阴影**: `0 4px 16px rgba(45,159,127,0.4)`
- **标签**: "拍照", 10sp, `#9CA3AF`, marginTop 3dp
- 点击后进入全屏 CameraScreen（Voyager push，非 Tab 内嵌）

## 页面路由图

```
App Entry
  ├─ OnboardingScreen (首次安装)
  │   └─ AuthScreen
  │       └─ HomeScreen (5-Tab)
  │
  └─ HomeScreen (已登录)
      ├─ Tab 1: DashboardScreen
      │   ├─ → CameraScreen (去拍照)
      │   ├─ → TimelineScreen (查看全部趋势)
      │   ├─ → ProductScreen (产品快捷入口)
      │   ├─ → AttributionReportScreen (归因快捷入口)
      │   └─ → ShareCardScreen (分享快捷入口)
      │
      ├─ Tab 2: TimelineScreen
      │   ├─ → RecordDetailScreen (点击记录卡片)
      │   └─ → ShareCardScreen (分享按钮)
      │
      ├─ FAB: CameraScreen (全屏)
      │   ├─ → RecordDetailScreen (查看详情)
      │   └─ ← 返回首页 (继续拍照)
      │
      ├─ Tab 4: AttributionReportScreen
      │   └─ (无子页面)
      │
      └─ Tab 5: ProfileScreen
          ├─ → SettingsScreen (齿轮图标)
          ├─ → ProductScreen (护肤品管理)
          ├─ → AttributionReportScreen (归因分析报告)
          ├─ → PaywallScreen (会员中心)
          └─ → ShareCardScreen (数据同步)

独立全屏页面:
  ├─ CameraScreen
  ├─ RecordDetailScreen
  ├─ PaywallScreen
  ├─ ShareCardScreen
  ├─ SettingsScreen
  ├─ ProductScreen
  ├─ ForgotPasswordScreen
  └─ OnboardingScreen
```

## 与当前实现的差异

### 关键变更：3 Tab → 5 Tab

| 项目 | 当前代码 | 设计稿 |
|------|---------|--------|
| Tab 数量 | 3（首页/记录/我的）| 5（首页/记录/拍照/分析/我的）|
| 归因分析入口 | 仅 Profile 菜单 | 独立 Tab |
| FAB 位置 | 覆盖在 Tab 上 | 作为第 3 个 Tab 位置 |
| 拍照标签 | 无 | FAB 下方有"拍照"文字 |

### 实现要点

**FAB 交互说明**: FAB 不是传统 Tab 项，而是嵌入 NavigationBar 中央的凸起按钮。
它不参与 Tab 选中态切换 — 点击后打开全屏 CameraScreen（Voyager push），
CameraScreen 返回后恢复之前选中的 Tab。

**迁移步骤** (M2.9 Phase 1):
1. HomeScreen Tab 枚举: `Dashboard / Timeline / Attribution / Profile` (4 个 Tab)
2. NavigationBar 中央插入 FAB (非 Tab，固定位置)
3. 归因分析从 Profile 菜单提升为 Tab 4 直达
4. 更新快照测试适配新导航结构

**不影响的部分**: 各 Screen 的内部逻辑不变，仅调整 HomeScreen 的 Tab 组织和导航栏布局。
