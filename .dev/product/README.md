# SkinTrack 产品文档索引

> 本文档面向 Claude 实例，提供完整的产品规格参考。
> 设计稿来源：Figma + HTML 模拟稿 (`.figma-mockups/`)
> 最后更新：2026-03-22

## 色值引用规则

所有色值均引用 `DESIGN_SYSTEM.md` 中的 token 定义。文档中出现的具体 Hex 值仅为辅助参考。
开发时以 `MaterialTheme.extendedColors.*` / `MaterialTheme.colorScheme.*` 为准。

## 文档结构

### 全局文档
| 文档 | 内容 | 适用场景 |
|------|------|---------|
| [product-overview.md](product-overview.md) | 产品定位、用户画像、评分系统、商业模型、V1/V2 边界 | 理解产品全局 |
| [navigation.md](navigation.md) | 5 Tab + FAB 导航架构、页面路由图、登录状态路由 | 开发导航相关功能 |
| [feature-gating.md](feature-gating.md) | 付费门控 4 态规格、LockedFeatureCard 组件、触发条件 | 开发门控逻辑 |
| [dark-mode.md](dark-mode.md) | 暗色模式全部语义色映射、渐变/阴影差异、页面级适配要点 | 适配暗色主题 |
| [user-flows.md](user-flows.md) | 6 条完整功能链路 + 加载态/Toast 规格 | 理解跨页面衔接 |

### 页面文档（12 个）
| 文档 | 设计稿 | 内容 |
|------|--------|------|
| [screen-onboarding.md](screen-onboarding.md) | `01-onboarding.html` | 引导页 4 屏 + 肤质选择 |
| [screen-auth.md](screen-auth.md) | `02-auth.html` | 登录 / 注册 / 忘记密码 |
| [screen-dashboard.md](screen-dashboard.md) | `03-dashboard.html` | 首页（内容态 + 空状态） |
| [screen-timeline.md](screen-timeline.md) | `04-timeline.html` | 肌肤记录页（内容 + 空态） |
| [screen-camera.md](screen-camera.md) | `05-camera.html` | 拍照页 3 态（取景器/结果/权限） |
| [screen-record-detail.md](screen-record-detail.md) | `06-record-detail.html` | 记录详情（评分/雷达/指标/AI/产品） |
| [screen-profile.md](screen-profile.md) | `07-profile.html` | 个人中心（渐变头/统计/目标/菜单） |
| [screen-product.md](screen-product.md) | `08-product.html` | 护肤品管理（AM/PM 分组/打卡/添加） |
| [screen-attribution.md](screen-attribution.md) | `09-attribution.html` | 归因分析报告（对比/AI 洞察/排名/建议） |
| [screen-paywall.md](screen-paywall.md) | `10-paywall.html` | 会员订阅页（皇冠/权益/方案/社交证明） |
| [screen-settings.md](screen-settings.md) | `11-settings.html` + `20-edit-profile.html` | 设置 + 编辑资料 + 修改密码 + 注销弹窗 |
| [screen-share.md](screen-share.md) | `12-share.html` | 分享对比卡片（预览/模板/目标/操作） |

### 补充设计稿（无独立产品文档，规格已融入相关文档）
| 设计稿 | 对应文档 |
|--------|---------|
| `13-locked-states.html` | → `feature-gating.md` |
| `14-dark-core.html` | → `dark-mode.md` + 各 screen 暗色章节 |
| `15-dark-screens.html` | → `dark-mode.md` + 各 screen 暗色章节 |
| `16-dark-utility.html` | → `dark-mode.md` + 各 screen 暗色章节 |
| `17-dark-locked.html` | → `feature-gating.md` 暗色章节 |
| `18-loading-states.html` | → `user-flows.md` 加载态章节 |
| `19-toast-states.html` | → `user-flows.md` Toast 章节 |
| `20-edit-profile.html` | → `screen-settings.md` 编辑资料/修改密码章节 |

## 必读顺序（新 session）

1. `product-overview.md` — 理解产品全局（3 分钟）
2. `navigation.md` — 理解导航架构和路由图（2 分钟）
3. `user-flows.md` — 理解功能链路和状态反馈（3 分钟）
4. 对应 `screen-*.md` — 开发具体页面前阅读

## V1 vs V2 功能边界

| V1（首发版） | V2（后续迭代） |
|------------|-------------|
| 邮箱注册/登录 | 微信/Apple/手机号登录 |
| 本地贴士轮换 | 天气 API 动态推荐 |
| 系统 ShareSheet 分享 | 微信/微博 SDK 直接分享 |
| 默认分享模板（1 种） | 3 种分享模板可选 |
| 首字母头像 | 头像上传 |
| 本地通知打卡提醒 | FCM 服务端推送 |
| Mock 支付流程 | 微信支付 + StoreKit 2 |
| 手动输入/搜索产品 | 扫码添加产品 |

## 快速参考

- **设计系统**: `DESIGN_SYSTEM.md`（项目根目录）
- **CSS Token 权威来源**: `.figma-mockups/base.css`
- **HTML 设计稿**: `.figma-mockups/`（本地预览 `python3 -m http.server 8767`）
- **开发进度**: `.dev/memory/app-progress.md`
- **架构决策**: `.dev/memory/app-decisions.md`
- **产品策略**: `.dev/memory/app-strategy.md`
