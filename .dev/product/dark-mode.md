# 暗色模式适配

> 最后更新：2026-03-22
> 设计稿：`.figma-mockups/14-dark-core.html`（Dashboard/Timeline/Profile）、`15-dark-screens.html`（Auth/Detail/Product/Attribution）、`16-dark-utility.html`（Onboarding/Paywall/Settings/Share）、`17-dark-locked.html`（4 种门控态）
> Token 来源：`.figma-mockups/base.css` `[data-theme="dark"]` 区块

## 适配原则

1. **暖炭底色**：基底 `#0F1412`（暖炭灰带绿色调），远离纯黑 `#000000`
2. **渐进提亮**：Surface 层级通过亮度递增表示层叠深度
3. **品牌色提亮**：交互色从 primary-500 → primary-400，文本品牌色从 500 → 300
4. **指标色增饱和**：五大皮肤指标色在暗色下提高饱和度以保持可辨识
5. **透明填充**：功能色背景使用 `rgba()` 半透明而非实色，保留层次感
6. **阴影加深**：所有阴影使用 `rgba(0,0,0,...)` 并增大透明度

## 语义色映射

### Content（文字 & 图标）
| Token | 亮色 | 暗色 |
|-------|------|------|
| `content-primary` | `neutral-900` (#1A1F1C) | `#EAEFEC` |
| `content-secondary` | `neutral-600` (#6B756F) | `#A0AAA5` |
| `content-tertiary` | `neutral-500` (#8A948E) | `#7A857F` |
| `content-disabled` | `neutral-400` (#ABB3AE) | `#515A55` |
| `content-inverse` | `neutral-0` (#FFFFFF) | `neutral-900` (#1A1F1C) |
| `content-brand` | `primary-500` (#2A9D7C) | `primary-300` (#6BCAAA) |
| `content-success` | `success-600` (#059669) | `success-400` (#34D399) |
| `content-warning` | `warning-600` (#D97706) | `warning-400` (#FBBF24) |
| `content-error` | `error-500` (#EF4444) | `error-400` (#F87171) |
| `content-info` | `info-600` (#2563EB) | `info-400` (#60A5FA) |

### Surface（背景）
| Token | 亮色 | 暗色 |
|-------|------|------|
| `surface-primary` | `neutral-0` (#FFFFFF) | `#0F1412` |
| `surface-secondary` | `neutral-50` (#F8FAF9) | `#171D1A` |
| `surface-tertiary` | `neutral-100` (#F1F4F3) | `#1E2522` |
| `surface-elevated` | `neutral-0` (#FFFFFF) | `#1E2522` |
| `surface-inverse` | `neutral-900` | `neutral-100` |
| `surface-brand` | `primary-500` | `primary-600` |
| `surface-brand-subtle` | `primary-50` (#E6F5F0) | `rgba(42,157,124,0.12)` |
| `surface-success` | `success-50` (#ECFDF5) | `rgba(16,185,129,0.12)` |
| `surface-warning` | `warning-50` (#FFFBEB) | `rgba(245,158,11,0.12)` |
| `surface-error` | `error-50` (#FEF2F2) | `rgba(239,68,68,0.12)` |
| `surface-info` | `info-50` (#EFF6FF) | `rgba(59,130,246,0.12)` |
| `surface-rose` | `rose-50` (#FFF1F3) | `rgba(242,122,142,0.10)` |
| `surface-lavender` | `lavender-50` (#F4F2FE) | `rgba(155,138,219,0.10)` |

### Border
| Token | 亮色 | 暗色 |
|-------|------|------|
| `border-default` | `neutral-200` (#E4E8E6) | `rgba(255,255,255,0.10)` |
| `border-subtle` | `neutral-100` (#F1F4F3) | `rgba(255,255,255,0.06)` |
| `border-strong` | `neutral-400` (#ABB3AE) | `rgba(255,255,255,0.20)` |
| `border-brand` | `primary-400` | `primary-600` |

### Interactive
| Token | 亮色 | 暗色 |
|-------|------|------|
| `interactive-primary` | `primary-500` (#2A9D7C) | `primary-400` (#4BBE97) |
| `interactive-primary-hover` | `primary-600` | `primary-300` |
| `interactive-primary-pressed` | `primary-700` | `primary-200` |
| `interactive-primary-disabled` | `primary-200` | `rgba(42,157,124,0.20)` |
| `interactive-secondary` | `neutral-100` | `neutral-800` |
| `interactive-on-primary` | `neutral-0` | `neutral-950` |

### Overlay
| Token | 亮色 | 暗色 |
|-------|------|------|
| `overlay-light` | `rgba(255,255,255,0.88)` | `rgba(30,37,34,0.90)` |
| `overlay-dark` | `rgba(15,20,18,0.5)` | `rgba(0,0,0,0.6)` |
| `overlay-scrim` | `rgba(15,20,18,0.32)` | `rgba(0,0,0,0.45)` |

### 皮肤指标色
| Metric | 亮色 | 暗色 |
|--------|------|------|
| Acne | `#F27A8E` | `#FF8FA0` |
| Pore | `#4ECDC4` | `#5EDDD4` |
| Evenness | `#F5C542` | `#FFD55E` |
| Hydration | `#45B7D1` | `#5CC8E0` |
| Redness | `#E87878` | `#FF9090` |

## 渐变映射

| Token | 亮色 | 暗色 |
|-------|------|------|
| `grad-primary` | `135deg primary-500→400→300` | `135deg primary-400→500→600` |
| `grad-primary-bold` | `145deg primary-700→500→400` | `145deg primary-300→500→700` |
| `grad-hero` | `145deg primary-800→600→500→300` | `145deg #0A2E24→primary-800→600→500` |
| `grad-button` | `180deg primary-400→500` | `180deg primary-400→600` |
| `grad-warm` | `135deg #FFF8F2→primary-50` | `135deg #1A1714→#0F1A16` |
| `grad-surface` | `180deg neutral-v-50→neutral-0` | `180deg #0F1412→#131917` |
| `grad-glass` | `135deg rgba(white,0.92)→0.72` | `135deg rgba(30,37,34,0.92)→0.72` |
| `grad-rose-warm` | `135deg rose-50→#FFE8EC→#FFF5F3` | `135deg rgba(rose,0.06)→0.03` |
| `grad-lavender-soft` | `135deg lavender-50→100→#F8F5FF` | `135deg rgba(lavender,0.06)→0.03` |
| `grad-tri-warm` | `135deg primary-50→rose-50→lavender-50` | `135deg rgba(primary,0.06)→rgba(rose,0.04)→rgba(lavender,0.04)` |
| `grad-skin` | `135deg #E8CFC0→#D4B8A8→...` | `135deg #5A4A40→#4A3C34→#403530→...` |
| `grad-profile` | `160deg primary-600→500→300` | `160deg primary-800→700→600` |
| `grad-paywall` | `160deg primary-700→500→secondary-400` | `160deg #0A2E24→primary-700→secondary-700` |

## 阴影映射

| Token | 亮色 base | 暗色 base |
|-------|----------|----------|
| `shadow-xs` | `rgba(15,20,18, 0.04)` | `rgba(0,0,0, 0.20)` |
| `shadow-sm` | `rgba(15,20,18, 0.05/0.04)` | `rgba(0,0,0, 0.25/0.15)` |
| `shadow-md` | `rgba(15,20,18, 0.04/0.06)` | `rgba(0,0,0, 0.25/0.20)` |
| `shadow-lg` | `rgba(15,20,18, 0.05/0.08)` | `rgba(0,0,0, 0.30/0.25)` |
| `shadow-xl` | `rgba(15,20,18, 0.12)` | `rgba(0,0,0, 0.40)` |
| `shadow-brand-sm` | `rgba(42,157,124, 0.14/0.10)` | `rgba(42,157,124, 0.20/0.15)` |
| `shadow-brand-md` | `rgba(42,157,124, 0.20/0.12)` | `rgba(42,157,124, 0.28/0.18)` |
| `shadow-brand-lg` | `rgba(42,157,124, 0.25/0.15)` | `rgba(42,157,124, 0.35/0.20)` |
| `shadow-rose-sm` | `rgba(242,122,142, 0.14/0.10)` | `rgba(242,122,142, 0.20/0.15)` |
| `shadow-gold-sm` | `rgba(245,158,11, 0.16/0.12)` | `rgba(245,158,11, 0.22/0.16)` |

## 页面级暗色适配要点

### Dashboard（14-dark-core.html）
- Hero Card：`gradients.hero` 暗色变体，装饰圆 `rgba(white,0.06)`
- 迷你指标卡：`surface-secondary` 暗色，border-top 指标色不变
- 周历打卡：已完成天 primary-400 stroke，今天 interactive-primary border

### Timeline（14-dark-core.html）
- 记录卡片：暗色 surface
- Score ring：primary-400 维持
- 趋势 badge 颜色：success/error surface 使用 rgba 变体

### Profile（14-dark-core.html）
- Header 渐变：`gradients.profile` 暗色变体（primary-800→700→600）
- 统计卡：暗色 surface-primary
- VIP badge：gold-gradient 不变

### Auth（15-dark-screens.html）
- Segmented control：`surface-tertiary` 背景，active tab `surface-secondary`
- Logo 渐变：gradients.hero 暗色变体

### RecordDetail（15-dark-screens.html）
- 照片渐变：暗色皮肤色调（`#5A4A40→#3A2E28`）
- Glassmorphic 按钮：`rgba(0,0,0,0.3)` + `backdrop-blur(8px)`
- AI 卡片渐变：`rgba(primary,0.08) → rgba(lavender,0.08)`

### Product（15-dark-screens.html）
- 打卡复选框：checked 维持 interactive-primary
- 图标背景：使用 rgba 颜色变体（如 `rgba(42,157,124,0.12)`）

### Attribution（15-dark-screens.html）
- 统计卡：surface-success/lavender/brand-subtle 的 rgba 变体
- AI 洞察渐变：`rgba(primary,0.08) → rgba(lavender,0.08)`

### Onboarding（16-dark-utility.html）
- 插画渐变：primary opacity 0.15→0.05

### Paywall（16-dark-utility.html）
- Hero 渐变：`gradients.paywall` 暗色变体
- 方案卡：暗色 surface
- 皇冠 glow 动画维持

### Settings（16-dark-utility.html）
- Section 卡：`surface-secondary` 暗色
- 菜单图标背景：rgba 颜色变体
- 切换开关：active 色维持

### Share（16-dark-utility.html）
- **分享卡本身保持亮色主题**（白色卡片），确保截图/分享时可读性
- 周围环境为暗色

### Locked States（17-dark-locked.html）
- Pro 升级卡背景：rgba 暖色变体（`rgba(244,162,97,0.06→0.03)`）
- 模糊内容 opacity：0.4（比亮色 0.5 更低）
- 模糊覆盖 pill：`surface-secondary` 暗色

## 实现注意事项

1. 所有暗色 token 通过 `[data-theme="dark"]` / Compose `isSystemInDarkTheme()` 自动切换
2. 不需要手动判断暗色模式 — 使用语义 token 即可
3. 唯一例外：分享卡（ShareCardContent）强制使用亮色主题
4. 照片预览区的渐变需要单独处理（skin gradient 有亮/暗两套）
5. Glassmorphic 效果在暗色下使用 `rgba(0,0,0,0.3)` 替代 `rgba(white,0.2)`

## 与当前实现的差异

- 暗色模式 token 已全面实现 ✅
- Rose/Lavender surface rgba 变体已定义 ✅
- 分享卡强制亮色已实现 ✅
- 无已知偏差
