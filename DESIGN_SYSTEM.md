# SkinTrack 设计系统

## 访问 API

```kotlin
// Material 3 标准
MaterialTheme.colorScheme.primary
MaterialTheme.typography.bodyLarge
MaterialTheme.shapes.medium

// 扩展属性
MaterialTheme.extendedColors.skinMetric.acne
MaterialTheme.extendedColors.functional.success
MaterialTheme.extendedColors.camera.overlay
MaterialTheme.spacing.md
MaterialTheme.gradients.primary
MaterialTheme.dimens.buttonHeight
```

## 颜色体系

### 品牌色 — Mint（Primary）

| Token | Hex | 用途 |
|-------|-----|------|
| Mint50 | `#E8F7F3` | 最浅背景 |
| Mint100 | `#B8E8D9` | primaryContainer (Light) |
| Mint200 | `#89D9BF` | — |
| Mint300 | `#58CAA5` | primary (Dark) |
| **Mint400** | **`#2D9F7F`** | **primary (Light)** |
| Mint500 | `#248068` | — |
| Mint600 | `#1D6B5B` | primaryContainer (Dark) |
| Mint700 | `#155146` | onPrimary (Dark) |

### 品牌色 — Apricot（Secondary）

| Token | Hex | 用途 |
|-------|-----|------|
| Apricot50 | `#FFF4EA` | 最浅背景 |
| Apricot100 | `#FFE4C8` | secondaryContainer (Light) |
| Apricot200 | `#FFC996` | secondary (Dark) |
| **Apricot300** | **`#F4A261`** | **secondary (Light)** |
| Apricot400 | `#E68A3E` | — |
| Apricot500 | `#D47826` | onSecondary (Dark) |

### 皮肤指标色 `extendedColors.skinMetric`

| Token | Light | Dark | 域模型 |
|-------|-------|------|--------|
| acne | `#FF6B6B` | `#E05555` | acneCount |
| pore | `#4ECDC4` | `#3DB3AB` | poreScore |
| evenness | `#FFE66D` | `#E0CC5A` | evenScore |
| hydration | `#45B7D1` | `#3A9DB5` | 预留 |
| redness | `#F38181` | `#D86E6E` | rednessScore |

### 功能色 `extendedColors.functional`

| Token | Light | Dark |
|-------|-------|------|
| success | `#34C759` | `#30D158` |
| warning | `#FF9F0A` | `#FFB340` |
| error | `#FF3B30` | `#FF453A` |
| info | `#007AFF` | `#0A84FF` |

### 相机色 `extendedColors.camera`（不随主题变化）

| Token | 值 |
|-------|----|
| overlay | Black 50% alpha |
| guideBorder | White 80% alpha |
| buttonBorder / buttonFill | White |
| buttonDisabled | `#AAAAAA` |
| background | Black |

## 间距系统 `spacing`

| Token | 值 | 用途 |
|-------|----|------|
| xs | 4.dp | 紧凑间距 |
| sm | 8.dp | 小间距 |
| md | 16.dp | 标准间距 |
| lg | 24.dp | 大间距 |
| xl | 32.dp | 超大间距 |
| xxl | 48.dp | 区域间距 |
| section | 64.dp | 区块间距 |

## 组件尺寸 `dimens`

| Token | 值 | 用途 |
|-------|----|------|
| buttonHeight | 48.dp | 按钮高度 |
| buttonSpinnerSize | 20.dp | 按钮加载指示器 |
| buttonStrokeWidth | 2.dp | 按钮边框宽度 |
| avatarLarge | 56.dp | 大头像 |
| avatarIcon | 32.dp | 头像内图标 |
| captureButtonSize | 72.dp | 拍照按钮 |
| captureButtonBorder | 4.dp | 拍照按钮边框 |
| captureButtonInnerPadding | 6.dp | 拍照按钮内间距 |
| thumbnailSize | 72.dp | 缩略图尺寸 |
| photoCompareHeight | 160.dp | 对比照片高度 |
| chartHeight | 200.dp | 图表高度 |
| chartDotRadius | 4.dp | 图表数据点半径 |
| chartLineWidth | 2.dp | 图表线条宽度 |
| scoreBarHeight | 8.dp | 评分条高度 |
| iconSmall | 20.dp | 小图标 |
| iconMedium | 24.dp | 中图标 |
| iconLarge | 32.dp | 大图标 |

## 圆角系统 `shapes`

| Token | 值 |
|-------|----|
| extraSmall | 4.dp |
| small | 8.dp |
| medium | 12.dp |
| large | 16.dp |
| extraLarge | 20.dp |
| FullRoundedShape | 50% |

## 渐变 `gradients`

| Token | 描述 | 使用位置 |
|-------|------|---------|
| primary | Mint400 → Mint300 | AuthScreen 提交按钮 |
| scoreRing | Mint400 → Apricot300 → Mint400 | 预留（RadarChart） |
| warm | Apricot50 → Mint50 (Light) / Apricot500 → Mint600 (Dark) | CompareCard 背景 |
| surface | Background → White (Light) / Background → Surface (Dark) | ProfileScreen 用户卡片 |

## 动画规格 `Motion`

| Token | 值 | 用途 |
|-------|----|------|
| SHORT | 150ms | 淡出、收起 |
| MEDIUM | 300ms | 标准过渡、列表入场 |
| LONG | 500ms | 强调动画 |
| EmphasizedDecelerate | cubic(0.05, 0.7, 0.1, 1.0) | 入场动画缓动 |
| Standard | cubic(0.2, 0.0, 0.0, 1.0) | 标准缓动 |

## Typography

使用系统字体，标题类 SemiBold/Bold，正文 Normal。详见 `Type.kt`。

## 共享组件 `ui/component/`

| 组件 | 用途 | 使用位置 |
|------|------|---------|
| SectionCard | 全宽内容卡片（Card+Column+padding） | RecordDetail, Attribution |
| SectionHeader | 区块标题（titleMedium） | RecordDetail, Attribution |
| TrendIndicator | 趋势指示器（↑↓→ + functional color） | CompareCard, Attribution |
| MenuItem | 菜单列表项（clickable+padding+bodyLarge） | ProfileScreen |
| ScoreBar | 水平评分条（animateFloatAsState） | RecordDetail |
| LoadingContent | 加载占位（Spinner+文字） | 6 个 Screen |
| EmptyContent | 空状态占位（可选 icon） | Timeline, Product |
| ErrorContent | 错误占位（重试按钮+可选 icon） | Camera |
| animateListItem | 列表入场动画 Modifier | Timeline, Product, Attribution |

## 开发规范

1. **禁止硬编码颜色**：UI 代码中不允许出现 `Color(0x...)` / `Color.White` / `Color.Black`，必须使用 token（Canvas 绘图内部除外）
2. **禁止硬编码间距**：使用 `MaterialTheme.spacing.xxx` 代替裸 `.dp` 值（组件固有尺寸除外）
3. **禁止硬编码组件尺寸**：使用 `MaterialTheme.dimens.xxx` 代替裸 `.dp` 值
4. **Spacer → Arrangement.spacedBy**：使用 `Arrangement.spacedBy(spacing.xxx)` 代替手动 Spacer
5. **相机色不走主题**：`extendedColors.camera` 在 Light/Dark 下值相同
6. **动画使用 Motion token**：时长使用 `Motion.SHORT/MEDIUM/LONG`，缓动使用 `Motion.EmphasizedDecelerate/Standard`
7. **列表入场动画**：LazyColumn 的 `itemsIndexed` 配合 `Modifier.animateListItem(index)` 使用
8. **优先复用共享组件**：Card+Column 模式使用 `SectionCard`，趋势显示使用 `TrendIndicator`

## 文件结构

```
ui/theme/
  Color.kt          — 颜色常量
  ExtendedColors.kt — 扩展色 data class + CompositionLocal
  Spacing.kt        — 间距 token + CompositionLocal
  Dimens.kt         — 组件尺寸 token + CompositionLocal
  Motion.kt         — 动画规格（时长 + 缓动曲线）
  Shape.kt          — Material 3 Shapes
  Gradient.kt       — 渐变 Brush + CompositionLocal
  Type.kt           — Typography
  Theme.kt          — 主题组装入口 + 扩展属性

ui/component/
  Animation.kt      — animateListItem Modifier 扩展
  LoadingContent.kt  — LoadingContent / EmptyContent / ErrorContent
  MenuItem.kt        — 菜单列表项
  ScoreBar.kt        — 水平评分条（带动画）
  SectionCard.kt     — 全宽内容卡片
  SectionHeader.kt   — 区块标题
  TrendIndicator.kt  — 趋势指示器
```
