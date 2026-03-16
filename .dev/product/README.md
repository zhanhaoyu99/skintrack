# SkinTrack 产品文档索引

> 本文档面向 Claude 实例，提供完整的产品规格参考。
> 设计稿来源：Figma (Fs4LqbOvElAVtlC1EoJD5b) + HTML 模拟稿 (.figma-mockups/)
> 最后更新：2026-03-14

## 色值引用规则

所有色值均引用 `DESIGN_SYSTEM.md` 中的 token 定义。文档中出现的具体 Hex 值仅为辅助参考。
开发时以 `MaterialTheme.extendedColors.*` / `MaterialTheme.colorScheme.*` 为准。

### 已知 Token 不一致 (设计稿 vs 代码)

| Token | HTML 设计稿 | Dimens.kt | 说明 |
|-------|-----------|-----------|------|
| buttonHeight | 52px | 48.dp | 设计稿更新了按钮高度，代码待同步 |
| 输入框高度 | 50px | 未定义 token | 需新增 `inputHeight = 50.dp` |
| 圆角 r-md | 14px | shapes.medium = 12.dp | 设计稿圆角略大，需决策是否跟进 |

> M2.9 实施时需统一这些 token 差异。以设计稿为准。

## 文档结构

| 文档 | 内容 | 适用场景 |
|------|------|---------|
| [product-overview.md](product-overview.md) | 产品定位、用户画像、核心价值、商业模型 | 理解产品全局 |
| [navigation.md](navigation.md) | 导航架构、页面路由、Tab 结构 | 开发导航相关功能 |
| [screen-onboarding.md](screen-onboarding.md) | 引导页 4 屏规格 | 开发引导流程 |
| [screen-auth.md](screen-auth.md) | 登录/注册/忘记密码规格 | 开发认证流程 |
| [screen-dashboard.md](screen-dashboard.md) | 首页（内容态+空状态）完整规格 | 开发首页 |
| [screen-timeline.md](screen-timeline.md) | 肌肤记录页规格 | 开发时间线 |
| [screen-camera.md](screen-camera.md) | 拍照页 3 态规格 | 开发拍照功能 |
| [screen-record-detail.md](screen-record-detail.md) | 记录详情页规格 | 开发详情页 |
| [screen-profile.md](screen-profile.md) | 个人中心规格 | 开发个人中心 |
| [screen-product.md](screen-product.md) | 护肤品管理页规格 | 开发产品管理 |
| [screen-attribution.md](screen-attribution.md) | 归因分析报告页规格 | 开发归因报告 |
| [screen-paywall.md](screen-paywall.md) | 会员订阅页规格 | 开发付费墙 |
| [screen-settings.md](screen-settings.md) | 设置页规格 | 开发设置 |
| [screen-share.md](screen-share.md) | 分享对比卡片页规格 | 开发分享功能 |
| [feature-gating.md](feature-gating.md) | 付费门控 4 态规格 | 开发门控逻辑 |
| [dark-mode.md](dark-mode.md) | 暗色模式适配要点 | 适配暗色主题 |

## 必读顺序 (新 session)

1. `product-overview.md` — 理解产品全局 (5 分钟)
2. `navigation.md` — 理解导航架构和与当前代码差异 (3 分钟)
3. 对应 `screen-*.md` — 开发具体页面前阅读

## V1 vs V2 功能边界

| V1 (首发版) | V2 (后续迭代) |
|------------|-------------|
| 邮箱注册/登录 | 微信/Apple/手机号登录 |
| 本地贴士轮换 | 天气 API 动态推荐 |
| 系统 ShareSheet 分享 | 微信/微博 SDK 直接分享 |
| 默认分享模板 | 3 种分享模板可选 |
| 首字母头像 | 头像上传 |
| 本地通知打卡提醒 | FCM 服务端推送 |
| Mock 支付 | 微信支付 + StoreKit 2 |

## 快速参考

- **设计系统**: 见 `DESIGN_SYSTEM.md`（项目根目录）
- **开发进度**: 见 `.dev/memory/app-progress.md`
- **架构决策**: 见 `.dev/memory/app-decisions.md`
- **HTML 设计稿**: 见 `.figma-mockups/`（本地预览 `python3 -m http.server 8767`）
