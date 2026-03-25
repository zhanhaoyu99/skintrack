# 认证页 Auth — 邮箱登录/注册 + 忘记密码

> 设计稿: `.figma-mockups/02-auth.html` (3 屏: Login / Register / Forgot Password)
> 暗色稿: `.figma-mockups/15-dark-screens.html`

## 页面入口

- Onboarding 最后一步 → Auth（默认 Login tab）
- 任何需要登录的深链接 → Auth
- Settings 退出登录 → Auth
- Auth Login → Forgot Password（点击"忘记密码？"）

## 页面状态

| 状态 | 说明 |
|------|------|
| Login tab | 默认态，显示邮箱+密码表单 |
| Register tab | 切到注册，显示昵称+邮箱+密码+确认密码 |
| Forgot Password | 独立页面，TopAppBar 返回 Login |
| Loading | 点击提交按钮后，按钮显示 loading spinner，禁用所有输入 |
| Error | 字段级校验错误（红色边框+错误提示文字） |
| FP Success | 忘记密码发送成功，替换表单为成功提示 |

---

## 布局结构

### Login / Register（共享外壳）

```
Column(fillMaxSize, padding = horizontal spacing.lg(16dp))
├── Spacer(topPadding = spacing.lg) // 16dp
│
├── AuthLogo                           // 居中
│   Box(72x72dp, RoundedCorner 22dp)
│     background: gradients.hero
│     shadow: shadow-brand-md
│     content: 白色 SVG 图标 36x36dp (strokeWidth 1.8)
│
├── Spacer(spacing.md)                 // 12dp — logo 到品牌名
│
├── Text("SkinTrack")                  // 居中
│   h1(30sp), weight 800, contentPrimary
│   letterSpacing = -0.8sp
│
├── Spacer(spacing.xs)                 // 4dp — 品牌名到标语
│
├── Text(tagline)                      // 居中
│   b2(14sp), contentSecondary
│   Login: "记录你的美，追踪你的变"
│   Register: "创建账号，开始你的护肤旅程"
│
├── Spacer(spacing.xxl)                // 24dp — 标语到分段控件
│
├── SegmentedControl                   // 全宽
│   Row(padding = spacing.xs(4dp))
│     background: surfaceTertiary
│     borderRadius: shapes.medium(12dp)
│     tabs: ["登录", "注册"]
│     Tab(flex=1):
│       padding: vertical 10dp
│       font: b2(14sp), weight 600
│       borderRadius: shapes.small(8dp)
│       active: surfacePrimary bg, contentPrimary color, shadow-xs
│       inactive: transparent bg, contentSecondary color
│     transition: Motion.SHORT(150ms), easing-standard
│
├── Spacer(spacing.xxl)                // 24dp — 分段控件到表单
│
├── <Login 或 Register 表单区域>        // 见下方
│
├── <Login 专属区域: 社交登录+信任标记>   // 见下方
│   或
│   <Register 专属区域: 信任标记+法律声明>
│
└── Spacer(weight 1f, min = spacing.lg)  // 底部弹性空间
```

### Login 表单区域

```
Column(spacing = 0dp)  // text-field 自带 marginBottom
│
├── TextField("邮箱")                   // label + input
│   placeholder: "你的邮箱地址"
│   leadingIcon: mail (iconSizeSm 20dp)
│   keyboardType: Email
│
├── TextField("密码")
│   placeholder: "输入密码"
│   leadingIcon: lock (iconSizeSm 20dp)
│   trailingIcon: visibility toggle
│   keyboardType: Password
│
├── Row(fillMaxWidth, horizontalArrangement = End)
│   margin: top -spacing.sm(8dp), bottom spacing.lg(16dp)
│   TextButton("忘记密码？")
│     b3(13sp), weight 500, contentBrand
│     onClick → navigateTo(ForgotPassword)
│
├── PrimaryButton("登录")               // 全宽
│   height: dimens.buttonHeight(52dp)
│   borderRadius: shapes.full
│   background: gradients.button
│   shadow: shadow-brand-sm
│   text: btn(16sp), weight 600, contentInverse
│
├── DividerWithText("或")               // [V2]
│   margin: vertical spacing.xl(20dp)
│   line: 0.5dp borderSubtle
│   text: c1(12sp), weight 500, contentDisabled
│
├── SocialButtons                       // [V2] 水平排列
│   Row(spacing = spacing.md(12dp))
│   margin-bottom: spacing.xl(20dp)
│   SocialButton(flex=1):
│     height: dimens.buttonHeight(52dp)
│     borderRadius: shapes.full
│     border: 1.5dp borderDefault
│     background: surfacePrimary
│     icon: iconSizeSm(20dp)
│     text: b2(14sp), weight 600, contentPrimary
│     gap: spacing.sm(8dp)
│     buttons:
│       - Apple: Apple 图标 (currentColor) + "Apple"
│       - WeChat: 微信图标 (#07C160) + "微信"
│     hover: surfaceSecondary bg
│
└── TrustMarks (3 items)
    Row(justifyContent = Center, spacing = spacing.xl(20dp))
    TrustMark:
      Column(alignItems = Center, spacing = spacing.xs(4dp))
      icon: SVG iconSizeXs(16dp), contentDisabled
      text: c2(10sp), weight 500, contentDisabled
    items:
      - 🛡️ shield icon + "隐私保护"
      - 👥 users icon + "10万+用户"
      - ⭐ star icon (filled) + "4.8 评分"
```

### Register 表单区域

```
Column(spacing = 0dp)
│
├── TextField("昵称")
│   placeholder: "给自己取个名字吧"
│   leadingIcon: person (iconSizeSm 20dp)
│   keyboardType: Text
│
├── TextField("邮箱")
│   placeholder: "你的邮箱地址"
│   leadingIcon: mail (iconSizeSm 20dp)
│   keyboardType: Email
│
├── TextField("密码")
│   placeholder: "至少 6 位密码"
│   leadingIcon: lock (iconSizeSm 20dp)
│   trailingIcon: visibility toggle
│   keyboardType: Password
│
├── TextField("确认密码")
│   placeholder: "再次输入密码"
│   leadingIcon: lock (iconSizeSm 20dp)
│   trailingIcon: visibility toggle
│   keyboardType: Password
│
├── PrimaryButton("创建账号")
│   height: dimens.buttonHeight(52dp)
│   margin-bottom: spacing.lg(16dp)
│   (同 Login 按钮样式)
│
├── TrustMarks (2 items)
│   Row(justifyContent = Center, spacing = spacing.xl(20dp))
│   items:
│     - 🔒 lock icon + "数据加密"
│     - ✅ check icon + "随时取消"
│
└── Text("注册即同意《服务条款》和《隐私政策》")
    c2(10sp), contentDisabled, center
    margin-top: spacing.lg(16dp), lineHeight: 1.5
    《服务条款》和《隐私政策》: contentBrand, clickable → 打开 WebView
```

### Forgot Password 页面

```
Scaffold
├── TopAppBar
│   navigationIcon: back arrow (iconSizeMd 24dp) → popBack
│   title: "找回密码" (h4 16sp, weight 600)
│   height: dimens.topBarHeight(56dp)
│
└── Column(fillMaxSize, horizontalAlignment = Center)
    padding: top spacing.section(32dp), horizontal spacing.lg(16dp)
    │
    ├── ForgotPasswordIcon
    │   Box(88x88dp, shape = CircleShape)
    │     background: gradients.primary
    │     shadow: shadow-brand-md
    │     content: lock SVG 40x40dp, white
    │
    ├── Spacer(spacing.xxl)              // 24dp
    │
    ├── Text("重置密码")
    │   h2(24sp), weight 800, contentPrimary
    │   letterSpacing: h2 default (-0.4sp)
    │
    ├── Spacer(spacing.sm)               // 8dp
    │
    ├── Text("输入你注册时使用的邮箱，我们会发送重置链接")
    │   b2(14sp), contentSecondary, center
    │   maxWidth: 280dp, lineHeight: b2 default (1.45)
    │
    ├── Spacer(spacing.xxl)              // 24dp
    │
    ├── Column(fillMaxWidth)
    │   │
    │   ├── TextField("邮箱")
    │   │   placeholder: "你的邮箱地址"
    │   │   leadingIcon: mail
    │   │   keyboardType: Email
    │   │
    │   ├── InfoBox
    │   │   padding: spacing.md(12dp)
    │   │   borderRadius: shapes.medium(12dp)
    │   │   background: surfaceBrandSubtle
    │   │   borderLeft: 3dp interactivePrimary
    │   │   text: "重置链接将在 24 小时内有效，请及时查收邮件并完成密码修改"
    │   │   font: b3(13sp), contentBrand, lineHeight: 1.5
    │   │   margin-bottom: spacing.xl(20dp)
    │   │
    │   ├── PrimaryButton("发送重置链接")
    │   │   margin-bottom: spacing.md(12dp)
    │   │   (同 Login 按钮样式)
    │   │
    │   └── TextButton("返回登录")
    │       btn(16sp), contentTertiary, center, full width
    │       onClick → popBack to Login
    │
    └── Spacer(weight 1f)
```

---

## 通用组件规格

### TextField

| 属性 | 值 |
|------|------|
| 高度 | dimens.inputHeight (52dp) |
| 边框 | 1.5dp borderDefault |
| 圆角 | shapes.medium (12dp) |
| 背景 | surfaceTertiary (默认) |
| 内边距 | horizontal spacing.lg (16dp) |
| 文字 | b2 (14sp), contentPrimary |
| placeholder | b2 (14sp), contentDisabled |
| label | b3 (13sp), weight 600, contentSecondary, margin-bottom spacing.sm (8dp) |
| 字段间距 | margin-bottom spacing.lg (16dp) |
| 聚焦态 | border → interactivePrimary, bg → surfacePrimary, 外发光 `0 0 0 3dp rgba(42,157,124,0.1)` |
| 错误态 | border → contentError, helper text → contentError |
| helper text | c1 (12sp), contentTertiary, margin-top spacing.xs (4dp) |

### PrimaryButton

| 属性 | 值 |
|------|------|
| 高度 | dimens.buttonHeight (52dp) |
| 宽度 | fillMaxWidth |
| 圆角 | shapes.full |
| 背景 | gradients.button |
| 阴影 | shadow-brand-sm |
| 文字 | btn (16sp), weight 600, contentInverse |
| Loading | CircularProgressIndicator 替换文字, 24dp, contentInverse |
| Disabled | opacity 0.5, 不响应点击 |

---

## 交互行为

### Tab 切换 [V1]
- 点击 SegmentedControl tab 切换 Login/Register
- 动画: active 背景滑动, Motion.SHORT (150ms), easing-standard
- 切换保留已输入内容（同一 ViewModel 持有所有字段）

### 登录流程 [V1]
1. 用户填写邮箱+密码，点击"登录"
2. 按钮进入 loading 态（spinner + 禁用所有输入）
3. 调用 `supabase.auth.signInWithEmail(email, password)`
4. 成功 → navigate to Dashboard (popUpTo Auth inclusive)
5. 失败 → 显示 Toast 错误提示 ("邮箱或密码错误")，恢复按钮

### 注册流程 [V1]
1. 用户填写昵称+邮箱+密码+确认密码，点击"创建账号"
2. 前端校验（见下方字段规格），不通过则显示字段级错误
3. 按钮 loading → 调用 `supabase.auth.signUp(email, password)`
4. 成功 → 创建 profile (nickname) → navigate to Dashboard
5. 失败 → Toast 错误提示 ("邮箱已被注册" 等)

### 忘记密码流程 [V1]
1. 点击"忘记密码？" → navigate to ForgotPassword
2. 输入邮箱，点击"发送重置链接"
3. 按钮 loading → 调用 `supabase.auth.resetPasswordForEmail(email)`
4. 成功 → 替换表单为成功提示 ("重置链接已发送到你的邮箱" + 邮箱图标 + "返回登录" 按钮)
5. 失败 → Toast 错误提示
6. "返回登录" → popBack

### 社交登录 [V2]
- V1 阶段: 点击弹出 Toast "即将上线"
- V2 计划: Apple Sign-In + 微信 OAuth

### 字段校验
| 字段 | 规则 | 错误提示 |
|------|------|---------|
| 昵称 | 非空, 1-20 字符 | "请输入昵称" |
| 邮箱 | 非空, email 格式 | "邮箱格式不正确" |
| 密码 | 非空, >= 6 位 | "密码至少 6 位" |
| 确认密码 | 非空, == 密码 | "两次密码不一致" |

- 校验时机: 提交时统一校验 + 字段 blur 时实时校验
- 错误显示: 字段边框变红 (contentError) + 下方 helper text 显示错误文案

---

## 数据依赖

| 数据 | 来源 | 说明 |
|------|------|------|
| 登录状态 | Supabase Auth | Session token |
| 用户 profile | Supabase DB | nickname, 注册时创建 |

**ViewModel 字段:**
```
email: String
password: String
nickname: String         // 仅注册
confirmPassword: String  // 仅注册
isLogin: Boolean         // tab 状态
isLoading: Boolean
errorMessage: String?
fieldErrors: Map<Field, String>
```

---

## 与其他页面的关系

| 关系 | 目标页面 | 触发 |
|------|---------|------|
| → Dashboard | `DashboardScreen` | 登录/注册成功 |
| → ForgotPassword | `ForgotPasswordScreen` | 点击"忘记密码？" |
| ← Onboarding | `OnboardingScreen` | 引导完成 |
| ← Settings | `SettingsScreen` | 退出登录 |
| → WebView | 服务条款/隐私政策 | 点击法律链接 |

---

## 暗色模式

| 元素 | Light | Dark |
|------|-------|------|
| Logo 背景 | gradients.hero | gradients.hero (dark variant) |
| Logo 阴影 | shadow-brand-md | shadow-brand-md (dark, deeper) |
| SegmentedControl 外壳 | surfaceTertiary (#F1F4F3) | surfaceTertiary (#1E2522) |
| SegmentedControl active tab | surfacePrimary (#FFFFFF) | surfaceSecondary (#171D1A) |
| TextField 背景 | surfaceTertiary | surfaceTertiary (dark) |
| TextField 边框 | borderDefault (#E4E8E6) | borderDefault (rgba white 10%) |
| TextField 聚焦 | interactivePrimary + 绿色光晕 | interactivePrimary (400) + 深色光晕 |
| PrimaryButton | gradients.button | gradients.button (dark) |
| 社交按钮边框 | borderDefault | borderDefault (dark) |
| 社交按钮背景 | surfacePrimary | surfacePrimary (#0F1412) |
| InfoBox 背景 | surfaceBrandSubtle (#E6F5F0) | surfaceBrandSubtle (rgba 12%) |
| InfoBox 边框 | interactivePrimary (500) | interactivePrimary (400) |
| 所有文字色 | 跟随 semantic token | 跟随 dark semantic token |

所有颜色通过 semantic token 自动适配，无需额外条件判断。

---

## 与当前实现的差异

> 基于最近 commit 对比，待核实后更新

| # | 差异项 | 设计稿 | 当前实现 | 优先级 |
|---|--------|--------|---------|--------|
| 1 | 标语文案 | Login: "记录你的美，追踪你的变" / Register: "创建账号，开始你的护肤旅程" | 可能使用旧文案 "AI 驱动的肌肤管理助手" | P1 |
| 2 | SegmentedControl 样式 | surfaceTertiary bg, 12dp 外圆角, 8dp 内圆角, shadow-xs on active | 需核实是否对齐 | P1 |
| 3 | TextField label | b3(13sp), weight 600, contentSecondary | 需核实 | P2 |
| 4 | 忘记密码链接文案 | "忘记密码？" (b3, contentBrand, right-aligned) | 需核实 | P2 |
| 5 | 社交登录按钮 | full 圆角(pill), 1.5dp border, 52dp height | 需核实旧实现 md 圆角 | P2 |
| 6 | Trust marks 颜色 | icon + text 统一 contentDisabled | 旧文档写 primary 色 icon | P1 |
| 7 | 注册页底部文案 | "注册即同意《服务条款》和《隐私政策》" c2 contentDisabled, 链接 contentBrand | 需核实 | P2 |
| 8 | InfoBox 文字色 | contentBrand | 旧文档无明确定义 | P2 |
| 9 | FP "返回登录" 按钮 | contentTertiary text button | 旧文档写 primary 文字链接 | P1 |

---

## 版本标记

| 功能 | 版本 |
|------|------|
| 邮箱登录/注册 | [V1] |
| 忘记密码 | [V1] |
| 字段校验 | [V1] |
| SegmentedControl 切换 | [V1] |
| Trust marks 展示 | [V1] |
| 社交登录 (Apple + WeChat) | [V2] |
| 分隔线 "或" + 社交按钮 UI | [V2] |
