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

| Token | 描述 |
|-------|------|
| primary | Mint400 → Mint300 |
| scoreRing | Mint400 → Apricot300 → Mint400 |
| warm | Apricot50 → Mint50 (Light) / Apricot500 → Mint600 (Dark) |
| surface | Background → White (Light) / Background → Surface (Dark) |

## Typography

使用系统字体，标题类 SemiBold/Bold，正文 Normal。详见 `Type.kt`。

## 开发规范

1. **禁止硬编码颜色**：UI 代码中不允许出现 `Color(0x...)` / `Color.White` / `Color.Black`，必须使用 token
2. **禁止硬编码间距**：使用 `MaterialTheme.spacing.xxx` 代替裸 `.dp` 值（组件固有尺寸除外）
3. **Spacer → Arrangement.spacedBy**：使用 `Arrangement.spacedBy(spacing.xxx)` 代替手动 Spacer
4. **相机色不走主题**：`extendedColors.camera` 在 Light/Dark 下值相同

## 文件结构

```
ui/theme/
  Color.kt          — 颜色常量
  ExtendedColors.kt — 扩展色 data class + CompositionLocal
  Spacing.kt        — 间距 token + CompositionLocal
  Shape.kt          — Material 3 Shapes
  Gradient.kt       — 渐变 Brush + CompositionLocal
  Type.kt           — Typography
  Theme.kt          — 主题组装入口 + 扩展属性
```
