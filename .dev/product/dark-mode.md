# 暗色模式适配

> 设计稿: `.figma-mockups/14-17` (Dark Mode 文件)

## 适配原则

- 使用 Material 3 Dark ColorScheme 自动适配
- 扩展色 (skinMetric/functional/camera) 已定义 Dark 变体
- 渐变色有 Dark 变体 (见 Gradient.kt)
- 相机色不随主题变化 (固定暗色)

## 暗色全局色值

> 完整定义见 `DESIGN_SYSTEM.md` + `ui/theme/Color.kt`

| Token | Light | Dark |
|-------|-------|------|
| background | `#FAFAFA` | `#111315` |
| surface | `#FFFFFF` | `#1A1C1E` |
| surfaceVariant | `#F1F3F5` | `#2A2D31` |
| onSurface | `#111827` | `#E3E2E6` |
| onSurfaceVariant | `#6B7280` | `#9CA3AF` |
| primary | `#2D9F7F` | `#58CAA5` |
| outline | — | `#33373D` |
| outlineVariant | — | `#4B5058` |

### 暗色指标色 (`extendedColors.skinMetric`)

| 指标 | Light | Dark |
|------|-------|------|
| acne | `#FF6B6B` | `#FF8A8A` |
| pore | `#4ECDC4` | `#6EE7DE` |
| evenness | `#FFE66D` | `#FFF09D` |
| hydration | `#45B7D1` | `#67D4E8` |
| redness | `#F38181` | `#F5A5A5` |

## 关键暗色适配点

### 1. Hero 卡片 (Dashboard)
- Light: `linear-gradient(145deg, #1A7A63, #2D9F7F, #3DBFA0, #4ECDC4)`
- Dark: `linear-gradient(145deg, #1D6B5B, #248068, #2D9F7F)` — 色阶收窄，避免过亮
- 装饰圆 opacity 不变

### 2. 前后对比卡片 (Timeline)
- Light: warm 渐变 (#FFF8F2 → #F0FAF6 → #F5F0FF)
- Dark: 暗色 warm 渐变 (Apricot-500 → Mint-600 方向)

### 3. 用户信息卡片 (Profile)
- Light: surface gradient
- Dark: 深色 surface gradient

### 4. 拍照提醒卡 (Dashboard)
- Light: Rose 淡渐变背景
- Dark: Rose 暗色变体背景

### 5. AI 分析卡片 (RecordDetail)
- Light: #F0FAF6 → #F5F0FF 渐变
- Dark: 深色渐变变体

### 6. 指标色
- 各指标色有独立 Dark 变体（略暗但饱和度保持）
- 见 DESIGN_SYSTEM.md extendedColors 定义

### 7. 门控卡片
- 保持与 Light 模式相同的结构
- 背景色适配暗色 surface

## 需要关注的暗色细节

| 元素 | Light | Dark | 注意 |
|------|-------|------|------|
| 卡片阴影 | shadow-sm/md | 降低阴影或改用微妙边框 | 暗色下阴影效果弱 |
| 装饰圆 opacity | 0.04-0.08 | 可适当提高 | 暗色下需更明显 |
| 分割线 | outlineVariant | 自动适配 | — |
| 文字对比度 | WCAG AA (4.5:1 正文, 3:1 大字) | 需验证所有文字可读性 | 尤其 onSurfaceVariant #9CA3AF on #1A1C1E = 6.3:1 ✅ |
| 照片区域 | 肤色渐变 | 肤色渐变不变 | 照片本身不受主题影响 |

## 当前暗色支持状态

- ✅ Theme.kt 已定义 DarkColorScheme
- ✅ ExtendedColors 有 Dark 变体
- ✅ Gradients 有 Dark 变体
- ✅ 快照测试覆盖 Light/Dark
- ⚠️ 部分新增组件需验证暗色效果
