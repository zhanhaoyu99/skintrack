# Settings — 应用设置、编辑资料与修改密码

> 设计稿: `.figma-mockups/11-settings.html` (设置主页 + 注销弹窗)、`.figma-mockups/20-edit-profile.html` (编辑资料 + 修改密码)
> 暗色: `.figma-mockups/16-dark-utility.html` (Dark — Settings)

## 页面入口

- ProfileScreen → TopAppBar 齿轮图标 → SettingsScreen
- SettingsScreen → "编辑资料" MenuItem → EditProfileScreen
- SettingsScreen → "修改密码" MenuItem → ChangePasswordScreen
- SettingsScreen → "会员中心" MenuItem → PaywallScreen
- SettingsScreen → "隐私政策" / "服务条款" MenuItem → WebView
- SettingsScreen → "注销账户" 文字链接 → DeleteAccountDialog

## 页面状态

| 状态 | 说明 |
|------|------|
| Default | 正常展示所有分组、开关读取用户偏好 |
| Syncing | 数据同步 item 显示 loading indicator，badge 变为 "同步中" |
| SyncFailed | badge 变为 "同步失败" (error 色) |
| DeleteDialog | 弹出注销确认弹窗，背景半透明遮罩 |
| Logging Out | 退出登录按钮 loading 态 |

## 布局结构 (settings main)

```
┌──────────────────────────────┐
│ ←  设置                       │ TopAppBar: back + title "设置"
├──────────────────────────────┤
│ ▎账户                         │ SectionLabel: c1/700, content-tertiary
│ ┌──────────────────────────┐ │   left accent bar: 3px, interactive-primary, radius-full
│ │ 👤 编辑资料              > │ │ SectionCard: surface-primary bg, radius-lg, shadow-sm
│ │    昵称、头像、肤质        │ │   border: card-border-width, border-subtle
│ │ 🔑 修改密码              > │ │   padding: 4dp/16dp (vertical/horizontal)
│ │ 👑 会员中心              > │ │   margin: 0/16dp/8dp (top/horizontal/bottom)
│ │    PRO 会员                │ │
│ └──────────────────────────┘ │
│                              │
│ ▎通知                         │
│ ┌──────────────────────────┐ │
│ │ ⏰ 打卡提醒        [ON]   │ │ Toggle items — no chevron
│ │    每天 08:00              │ │
│ │ 📊 周报推送        [ON]   │ │
│ │ 💬 AI 分析通知     [OFF]  │ │
│ └──────────────────────────┘ │
│                              │
│ ▎数据与隐私                    │
│ ┌──────────────────────────┐ │
│ │ ☁️ 数据同步      [已同步]  │ │ Badge trailing (no chevron)
│ │    上次同步：2 小时前       │ │
│ │ 🗑 清除缓存              > │ │
│ │    23.5 MB                 │ │
│ │ 📥 导出数据              > │ │
│ └──────────────────────────┘ │
│                              │
│ ▎关于                         │
│ ┌──────────────────────────┐ │
│ │ ℹ️ 版本            1.0.0  │ │ Version text trailing (no chevron)
│ │ 🛡 隐私政策              > │ │
│ │ 📄 服务条款              > │ │
│ └──────────────────────────┘ │
│                              │
│ [      退出登录      ]       │ Outlined button: error-400 border, error-500 text
│       注销账户                │ Text link: content-error, b3
└──────────────────────────────┘
```

### 分组标题 (SectionLabel) 规格

| 属性 | 值 |
|------|-----|
| 字体 | c1 (12sp), weight 700, letter-spacing 0.5sp |
| 颜色 | content-tertiary |
| 内边距 | 4dp top, 8dp bottom, 12dp left (为竖条留空间) |
| 外边距 | 4dp top, 16dp horizontal |
| 左竖条 | width 3px, radius-full, interactive-primary |
| 竖条定位 | absolute left 0, 从 top padding 到 bottom padding |

### MenuItem 规格

每个 MenuItem 由以下部分组成：

| 部件 | 规格 |
|------|------|
| icon-wrap | 36dp 圆角方形 (radius 10dp), 渐变背景 135deg, SVG icon 20dp |
| primary-text | b2 (14sp), weight 600, content-primary |
| secondary-text | c1 (12sp), content-tertiary |
| trailing | chevron (导航型) / toggle (开关型) / badge (状态型) / version text (纯文本) |

### 菜单项详细

#### 账户组

| 菜单项 | icon-wrap 背景 | icon 颜色 | 副标题 | trailing |
|--------|---------------|-----------|--------|----------|
| 编辑资料 | `linear-gradient(135deg, primary-50, primary-100)` | primary-400 | "昵称、头像、肤质" | chevron |
| 修改密码 | `linear-gradient(135deg, lavender-50, lavender-100)` | lavender-400 | — | chevron |
| 会员中心 | `linear-gradient(135deg, secondary-50, secondary-100)` | secondary-400 | "PRO 会员" | chevron |

#### 通知组

| 菜单项 | icon-wrap 背景 | icon 颜色 | 副标题 | trailing |
|--------|---------------|-----------|--------|----------|
| 打卡提醒 | `linear-gradient(135deg, rose-50, rose-100)` | rose-400 | "每天 08:00" | toggle (active) |
| 周报推送 | `linear-gradient(135deg, primary-50, primary-100)` | primary-400 | — | toggle (active) |
| AI 分析通知 | `linear-gradient(135deg, lavender-50, lavender-100)` | lavender-400 | — | toggle (inactive) |

#### 数据与隐私组

| 菜单项 | icon-wrap 背景 | icon 颜色 | 副标题 | trailing |
|--------|---------------|-----------|--------|----------|
| 数据同步 | `linear-gradient(135deg, primary-50, primary-100)` | primary-400 | "上次同步：2 小时前" | badge "已同步" |
| 清除缓存 | surface-tertiary | content-tertiary | "23.5 MB" | chevron |
| 导出数据 | `linear-gradient(135deg, info-50, #D6EFF8)` | info-500 | — | chevron |

**数据同步 Badge 规格**:
- 字体: c2 (10sp), weight 600
- 颜色: content-brand
- 背景: surface-brand-subtle
- 内边距: 2dp vertical, 8dp horizontal
- 圆角: radius-full

#### 关于组

| 菜单项 | icon-wrap 背景 | icon 颜色 | 副标题 | trailing |
|--------|---------------|-----------|--------|----------|
| 版本 | surface-tertiary | content-tertiary | — | "1.0.0" text (b3, content-tertiary) |
| 隐私政策 | surface-tertiary | content-tertiary | — | chevron |
| 服务条款 | surface-tertiary | content-tertiary | — | chevron |

### 底部操作

| 元素 | 规格 |
|------|------|
| 退出登录 | 全宽 outlined button, buttonHeight (52dp), radius-full, border 1.5px error-400, text error-500 |
| 注销账户 | 文字按钮 (btn-text), content-error, b3 (13sp), 居中, margin-top 12dp |

## 布局结构 (edit profile)

```
┌──────────────────────────────┐
│ ←  编辑资料            保存   │ TopAppBar: back + title + "保存" action
├──────────────────────────────┤   保存: b2 (14sp), weight 600, content-brand
│                              │
│          (L)                 │ Avatar: 88dp circle
│          [📷]                │   gradient: rose-300 → rose-400 (135deg)
│                              │   initial: h1, white, weight 700
│                              │   shadow: shadow-rose-sm
│  昵称                        │   edit-badge: 28dp circle, bottom-right
│  [Lisa                    ]  │     bg: interactive-primary
│                              │     border: 2px solid surface-primary
│  邮箱                        │     icon: camera 14dp, white
│  [lisa@example.com        ]  │
│  邮箱注册后不可修改            │ Email: disabled, content-tertiary text
│                              │   helper: c1, content-tertiary
│  肤质类型                     │
│  [油性] [干性] [混合✓] [敏感] │ FlowRow, gap sm (8dp)
│  [中性]                      │   label: b3, weight 600, content-secondary, mb 8dp
│                              │
│  肌肤目标                     │
│  [祛痘✓] [收毛孔✓] [提亮✓]   │ FlowRow, gap sm (8dp)
│  [抗老] [补水] [祛斑]         │   label: b3, weight 600, content-secondary, mb 8dp
│                              │
│  个性签名                     │
│  [一句话描述你的护肤态度    ]  │ placeholder text
│                              │
└──────────────────────────────┘
```

### 肤质类型 Pill 规格

| 状态 | border | background | text color |
|------|--------|------------|------------|
| Inactive | 1.5px border-default | transparent | content-secondary |
| Active | 1.5px interactive-primary | surface-brand-subtle | content-brand |

共通: radius-full, padding 8dp/16dp (vertical/horizontal), b3 (13sp), weight 600

### 肌肤目标 Pill 规格

| 状态 | border | background | text color |
|------|--------|------------|------------|
| Unselected | 1.5px border-default | transparent | content-secondary |
| 祛痘 (selected) | 1.5px rose-300 | rose-50 | rose-500 |
| 收毛孔 (selected) | 1.5px primary-300 | primary-50 | primary-500 |
| 提亮 (selected) | 1.5px secondary-300 | secondary-50 | secondary-600 |
| 抗老 (selected) | 1.5px lavender-300 | lavender-50 | lavender-500 |
| 补水 (selected) | 1.5px primary-300 | primary-50 | primary-500 |
| 祛斑 (selected) | 1.5px rose-300 | rose-50 | rose-500 |

共通: 同肤质 pill 尺寸/字体

### 表单域规格

| 属性 | 值 |
|------|-----|
| label | b3 (13sp), weight 600, content-secondary |
| input | b2 (14sp), content-primary, 标准 text-field 样式 |
| disabled input | content-tertiary text |
| helper text | c1 (12sp), content-tertiary |
| field 间距 | 标准 text-field margin-bottom |

## 布局结构 (change password)

```
┌──────────────────────────────┐
│ ←  修改密码                   │ TopAppBar: back + title
├──────────────────────────────┤
│                              │ content padding-top: 24dp
│          🔒                  │ Lock icon: 64dp circle
│                              │   bg: gradients.primary
│  当前密码                     │   shadow: shadow-brand-sm
│  [输入当前密码            ]   │   lock SVG: iconSizeLg (24dp), white
│                              │
│  新密码                       │
│  [至少 6 位新密码         ]   │
│  ████░░░░                    │ Password strength bars
│  密码强度：中等               │   4 bars, flex 1 each, gap xs (4dp)
│                              │   height 3dp, radius-full
│  确认新密码                   │   filled: content-success (green)
│  [再次输入新密码          ]   │   medium: warning-400 (orange)
│                              │   empty: surface-tertiary
│  [      确认修改      ]      │   hint: c2 (10sp), content-tertiary
│                              │
└──────────────────────────────┘ Button: primary, full width, margin-top 8dp
```

### 密码强度等级

| 等级 | 填充条数 | 颜色 | 文案 |
|------|---------|------|------|
| 弱 | 1 filled | content-error (red) | "密码强度：弱" |
| 中等 | 2 filled + 1 medium | content-success + warning-400 | "密码强度：中等" |
| 强 | 3 filled | content-success | "密码强度：强" |
| 非常强 | 4 filled | content-success | "密码强度：非常强" |

## 布局结构 (delete dialog)

```
┌─────────────────────────────────┐
│          overlay-dark bg         │ backdrop-filter: blur(4px)
│  ┌───────────────────────────┐  │
│  │         🗑️                │  │ Icon container: 56dp circle
│  │   确定注销账户？           │  │   bg: surface-error
│  │                           │  │   icon: 28dp, error-500
│  │ ┌───────────────────────┐ │  │
│  │ │ 注销后，所有肌肤记录、  │ │  │ Warning box:
│  │ │ 分析数据将被永久删除，  │ │  │   bg: surface-error
│  │ │ 且无法恢复。            │ │  │   border-left: 3px solid error-400
│  │ └───────────────────────┘ │  │   radius: radius-sm
│  │                           │  │   text: c1, content-error, line-height 1.5
│  │  请输入密码确认             │  │   padding: 10dp/12dp
│  │  [输入当前密码          ]  │  │
│  │                           │  │
│  │  [取消]     [确认注销]     │  │ Buttons row: gap 12dp
│  └───────────────────────────┘  │
└─────────────────────────────────┘
```

### 弹窗卡片规格

| 属性 | 值 |
|------|-----|
| 背景 | surface-elevated |
| 圆角 | radius-xl |
| 内边距 | 24dp |
| 阴影 | shadow-xl |
| 文本对齐 | center |
| 装饰 | 右上角 80dp 半透明红圆 `rgba(239,68,68,0.05)` |

### 弹窗元素

| 元素 | 规格 |
|------|------|
| 警告图标容器 | 56dp circle, surface-error bg, margin-bottom 16dp |
| 警告图标 | 28dp trash icon, error-500 |
| 标题 | h3 (19sp), weight 700, content-primary, margin-bottom 8dp |
| Warning box | 见上方规格 |
| 密码输入 | 标准 text-field, label "请输入密码确认", placeholder "输入当前密码" |
| 取消按钮 | btn-cancel: buttonHeight, radius-full, 1.5px border-default, transparent bg, content-primary text |
| 确认注销按钮 | btn-danger: buttonHeight, radius-full, error-500 bg, content-inverse text, shadow `0 4px 14px rgba(239,68,68,0.3)` |

## 交互行为

### Settings 主页

| 交互 | 行为 |
|------|------|
| 编辑资料 tap | navigate → EditProfileScreen |
| 修改密码 tap | navigate → ChangePasswordScreen |
| 会员中心 tap | navigate → PaywallScreen |
| 打卡提醒 toggle | 更新 UserPreferences.dailyReminderEnabled，切换本地通知 |
| 周报推送 toggle | 更新 UserPreferences.weeklyReportEnabled |
| AI 分析通知 toggle | 更新 UserPreferences.aiNotificationEnabled |
| 数据同步 tap | 手动触发 SyncManager，badge 变 "同步中" → "已同步" / "同步失败" |
| 清除缓存 tap | 确认弹窗 → 清除图片缓存 → 更新缓存大小显示 |
| 导出数据 tap | 确认弹窗 → exportUserData() → 分享 JSON/CSV 文件 |
| 版本 tap | 无响应 (纯展示) |
| 隐私政策 tap | 打开 WebView 加载隐私政策 URL |
| 服务条款 tap | 打开 WebView 加载服务条款 URL |
| 退出登录 tap | 确认弹窗 → Supabase signOut → navigate → AuthScreen (清除返回栈) |
| 注销账户 tap | 弹出 DeleteAccountDialog |

### EditProfileScreen

| 交互 | 行为 |
|------|------|
| 返回 (back) | 若有未保存更改 → 弹出 "放弃更改?" 确认弹窗; 否则直接返回 |
| 头像 tap | V1: 无操作 (自动生成首字母+渐变); V2: 打开相册/相机选择器 |
| 肤质类型 pill tap | 单选切换，更新选中态 |
| 肌肤目标 pill tap | 多选切换，每个目标独立 toggle |
| 保存 tap | 校验昵称非空 → updateProfile() → Toast "资料已更新" → 返回 |
| 保存失败 | Toast 显示错误信息 |

### ChangePasswordScreen

| 交互 | 行为 |
|------|------|
| 新密码输入 | 实时计算密码强度，更新 strength bars + hint 文案 |
| 确认修改 tap | 校验: 当前密码非空 + 新密码 >= 6 位 + 两次一致 → updatePassword() |
| 修改成功 | Toast "密码修改成功" → 返回 SettingsScreen |
| 修改失败 | 显示错误 (当前密码错误 / 网络错误) |
| 密码可见性 | 每个密码框右侧 eye icon toggle 明文/密文 |

### DeleteAccountDialog

| 交互 | 行为 |
|------|------|
| 取消 tap | 关闭弹窗 |
| 确认注销 tap | 校验密码 → DELETE 请求 → signOut → navigate → AuthScreen |
| 密码错误 | 输入框显示错误态 + 错误提示 |
| 遮罩 tap | 关闭弹窗 |

## 数据依赖

| 数据 | 来源 | 说明 |
|------|------|------|
| dailyReminderEnabled | UserPreferences (local) | 打卡提醒开关 |
| dailyReminderTime | UserPreferences (local) | 提醒时间，V1 固定 08:00 |
| weeklyReportEnabled | UserPreferences (local) | 周报推送开关 |
| aiNotificationEnabled | UserPreferences (local) | AI 分析通知开关 |
| lastSyncTime | SyncManager | 上次同步时间戳 → "X 小时前" 格式化 |
| cacheSize | CacheManager | 图片缓存大小 → "XX.X MB" |
| appVersion | BuildConfig | 应用版本号 |
| userProfile | Supabase Auth + profiles table | 昵称、邮箱、头像 |
| skinType | UserPreferences / profiles | 肤质类型 (单选) |
| skinGoals | UserPreferences / profiles | 肌肤目标 (多选列表) |
| bio | profiles table | 个性签名 |

## 与其他页面的关系

| 目标页面 | 入口 | 说明 |
|---------|------|------|
| ProfileScreen | back 按钮 | 返回个人中心 |
| EditProfileScreen | 账户 → 编辑资料 | 子页面 push |
| ChangePasswordScreen | 账户 → 修改密码 | 子页面 push |
| PaywallScreen | 账户 → 会员中心 | 导航到付费墙 |
| AuthScreen | 退出登录 / 注销账户 | 清除返回栈，重置到登录页 |
| WebView | 隐私政策 / 服务条款 | 内嵌浏览器打开 URL |

## 暗色模式

参考 `.figma-mockups/16-dark-utility.html` (Dark — Settings):

| 元素 | Light | Dark |
|------|-------|------|
| SectionCard bg | surface-primary | surface-secondary |
| SectionCard border | border-subtle | border-subtle (dark token) |
| Icon-wrap gradient bg | `linear-gradient(color-50, color-100)` | `rgba(color, 0.12)` |
| SectionLabel accent bar | interactive-primary | interactive-primary (不变) |
| Toggle active | 保持 | 保持 |
| 退出登录 button | error-400 border, error-500 text | 同 token (dark 值) |
| Delete dialog card bg | surface-elevated | surface-elevated (dark token) |
| Warning box bg | surface-error | surface-error (dark token) |

暗色 icon-wrap 渐变替换映射:
- primary-50/100 → `rgba(42,157,124, 0.12)`
- lavender-50/100 → `rgba(155,138,219, 0.12)`
- rose-50/100 → `rgba(242,122,142, 0.12)`
- secondary-50/100 → `rgba(secondary-rgb, 0.12)`
- info-50 → `rgba(info-rgb, 0.12)`
- surface-tertiary → 维持 dark surface-tertiary token

## 与当前实现的差异

> 基于 M2.9 Phase 8 实现状态

| # | 差异点 | 设计稿 | 当前实现 | 优先级 |
|---|--------|--------|---------|--------|
| 1 | 编辑资料副标题 | "昵称、头像、肤质" | 已实现 | -- |
| 2 | 肌肤目标彩色 pill | 每个目标用独立色系 (rose/primary/secondary) | 统一 brand 色 | V1 |
| 3 | 个性签名字段 | EditProfile 含 "个性签名" 输入框 | 未实现 | V1 |
| 4 | 密码强度条 | 4 bars + 颜色分级 + hint 文案 | 未实现或简化 | V1 |
| 5 | 修改密码锁图标 | 64dp circle, gradients.primary, shadow-brand-sm | 可能缺失 | V1 |
| 6 | 导出数据 icon-wrap | info-50/#D6EFF8 渐变, info-500 icon | 可能用其他色 | V1 |
| 7 | 清除缓存 icon | trash icon, surface-tertiary bg, content-tertiary | 需确认 | V1 |
| 8 | 版本 trailing | 纯文本 "1.0.0" (b3, content-tertiary) | 可能在副标题位置 | V1 |
| 9 | 注销弹窗图标 | 56dp trash icon (非 warning icon) | 可能用 warning icon | V1 |
| 10 | Dark icon-wrap | rgba colored variants | 可能沿用 light gradient | V1 |
