# 认证页 AuthScreen

> 设计稿: `.figma-mockups/02-auth.html` (2 屏: 登录 + 注册)

## 页面结构

### 公共顶部
```
┌─────────────────────────┐
│    [品牌 Logo 圆角图标]    │ ← 72dp 圆角方形 (22dp radius)，hero 渐变背景，白色叶子 SVG
│       SkinTrack          │ ← 30sp Bold (letter-spacing -0.8)
│  AI 驱动的肌肤管理助手     │ ← 14sp onSurfaceVariant
│                         │
│  [登录] [注册]            │ ← Segmented Control 切换
└─────────────────────────┘
```
- **Logo 光晕**: 外圈 6dp 半透明 primary 渐变光晕 + shadow `0 8px 28px rgba(45,159,127,0.3)`
- **装饰背景**: 3 个渐变圆形 blob (mint/apricot/lavender)，absolute 定位，柔和氛围
```

### 登录态 (Login)

```
┌─────────────────────────┐
│  邮箱                    │ ← 标签
│  [user@example.com    ]  │ ← TextField
│  密码                    │
│  [password123     👁]   │ ← TextField + 密码可见切换
│            忘记密码？     │ ← 右对齐链接 → ForgotPasswordScreen
│                         │
│  [    登 录    ]         │ ← 全宽 primary 按钮
│                         │
│         或               │ ← 分割线
│  [Apple]  [WeChat]       │ ← 2 个社交登录按钮（矩形描边+文字）
│                         │
│ 🔒隐私安全  👥10万+用户  ⭐4.8分好评 │ ← 信任标记
└─────────────────────────┘
```

### 注册态 (Register)

```
┌─────────────────────────┐
│  昵称                    │
│  [请输入昵称            ]  │
│  邮箱                    │
│  [请输入邮箱地址        ]  │
│  密码                    │
│  [请输入密码（至少6位）👁]  │ ← 密码强度提示
│  确认密码                 │
│  [再次输入密码          ]  │
│                         │
│  [   创建账号   ]        │ ← 全宽 primary 按钮
│                         │
│  注册即表示同意 服务条款 和 隐私政策 │ ← 底部法律链接
│                         │
│  🔒数据加密  ✅随时注销   │ ← 注册态信任标记（与登录态不同）
└─────────────────────────┘
```

## 输入框通用样式
- **高度**: 50dp
- **边框**: 1.5px outlineVariant, md 圆角
- **背景**: surfaceContainer (默认) → surface (聚焦)
- **聚焦态**: primary 边框 + `0 0 0 3px rgba(45,159,127,0.1)` 光晕
- **Prefix 图标**: 18dp, onSurfaceVariant, 左侧 14dp padding
- **Suffix 图标** (密码可见): 18dp, onSurfaceVariant, 右侧 12dp padding
- **Placeholder**: 14sp, `#B0B8C1`
- **字段间距**: 14dp (marginBottom)

## 字段规格

| 字段 | 类型 | 校验 | 错误提示 |
|------|------|------|---------|
| 昵称 | 文本 | 非空 | "请输入昵称" |
| 邮箱 | Email | 格式校验 | "邮箱格式不正确" |
| 密码 | 密码 | ≥6 位 | "密码至少 6 位" |
| 确认密码 | 密码 | 与密码一致 | "两次密码不一致" |

## 社交登录（仅登录态显示）
- **V1**: 仅展示 UI 占位，点击弹出 "即将上线" Toast
- **V2 计划**: 接入微信登录 + Apple 登录
- **按钮样式**: 矩形, 50dp 高, flex 等宽, 1.5px outlineVariant 描边, md 圆角, surface 背景
- **按钮内容**: 左侧 20dp 品牌 SVG 图标 + 右侧品牌文字 (14sp Bold)
- **排列**: 水平排列, 12dp 间距
- **按钮列表**: Apple (黑色图标) + WeChat (#07C160 绿色图标)
- **分隔线**: "或" 文字 (13sp, onSurfaceVariant) + 两侧 0.5px 水平线 (outlineVariant), 上下 20dp margin

## 忘记密码 (ForgotPasswordScreen)

> 设计稿: `02-auth.html` 第 3 屏

```
┌─────────────────────────┐
│ ← 忘记密码               │ ← TopAppBar
├─────────────────────────┤
│                         │
│     [🔒 锁图标]          │ ← 88dp 圆形, mint 径向渐变背景, 锁 SVG (40dp)
│                         │
│      重置密码             │ ← 24sp Bold, 居中
│  输入你注册时使用的邮箱地址│ ← 14sp onSurfaceVariant, 居中
│  我们将发送一封包含密码    │
│  重置链接的邮件给你。      │
│                         │
│  注册邮箱                 │ ← 标签 (13sp Bold)
│  [📧 请输入注册邮箱地址 ]  │ ← TextField (50dp, md 圆角, prefix icon)
│                         │
│  ⓘ 重置链接将在24小时内有效│ ← 信息提示框 (mint-50 背景, md 圆角)
│    请及时查收邮件（包括    │
│    垃圾邮件文件夹）。      │
│                         │
│  [  发送重置链接  ]       │ ← primary 全宽按钮
│     返回登录              │ ← primary 文字链接 (14sp Bold)
└─────────────────────────┘
```

- **成功态**: 替换表单区域，显示 "重置链接已发送到你的邮箱" + ✅ 图标 + "返回登录" 按钮
- **错误态**: 邮箱不存在时显示错误提示
- **V1**: Mock 实现 (delay 1s → 成功)

## 信任标记
**登录态** (3 项):
- 🛡️ 隐私安全 (primary 盾牌图标)
- ✅ 10万+用户 (primary 勾选图标)
- ⭐ 4.8分好评 (primary 实心星图标)

**注册态** (2 项):
- 🔒 数据加密 (primary 盾牌图标)
- ✅ 随时注销 (primary 勾选图标)

**样式**: 11sp onSurfaceVariant, 水平居中排列, 18dp 间距, 14dp SVG 图标 (primary 色), 底部对齐 (margin-top: auto)

## Tab 切换
- Segmented Control 样式：选中态 surface 背景 + 粗体，未选中态透明
- 外部容器圆角 pill 形状，surfaceVariant 背景

## 实现状态
✅ **已对齐** (M2.9 Phase 2) — Segmented Control + 昵称 + 确认密码 + 社交登录占位 + 信任标记 + ForgotPassword 完整 UI
