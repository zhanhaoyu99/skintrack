# SkinTrack Design System v6 — "Morning Garden"

> Calm Tech + Warm Data. A design language that makes users feel cared for, not inspected.

## 访问 API

```kotlin
MaterialTheme.colorScheme.xxx     // Material 3 标准色
MaterialTheme.extendedColors.xxx  // 扩展色（skinMetric/functional/camera）
MaterialTheme.spacing.xxx         // 间距（xs/sm/md/lg/xl/xxl/section）
MaterialTheme.dimens.xxx          // 组件尺寸（buttonHeight/avatarLarge 等）
MaterialTheme.gradients.xxx       // 渐变（primary/warm/surface/scoreRing）
MaterialTheme.shapes.xxx          // 圆角
```

---

## Color Tokens

### Primitive Palette

#### Primary — Sage Mint
| Step | Hex | Usage |
|------|-----|-------|
| 50 | `#E6F5F0` | Subtle brand background |
| 100 | `#C0E8DA` | Light brand fill |
| 200 | `#96D9C2` | Disabled brand |
| 300 | `#6BCAAA` | Brand accent (dark mode primary) |
| 400 | `#4BBE97` | Dark mode interactive |
| **500** | **`#2A9D7C`** | **Brand primary** |
| 600 | `#258F71` | Hover/pressed |
| 700 | `#1D7D63` | Active state |
| 800 | `#166B55` | Deep brand |
| 900 | `#0B4D3C` | Darkest brand |

#### Secondary — Warm Apricot
| Step | Hex |
|------|-----|
| 50 | `#FFF5ED` |
| 100 | `#FFE6D0` |
| 200 | `#FFD0A8` |
| 300 | `#FFB87D` |
| 400 | `#F4A261` |
| **500** | **`#E8925B`** |
| 600 | `#D47E46` |
| 700 | `#B86A38` |
| 800 | `#96542E` |
| 900 | `#6B3B1F` |

#### Accent 1 — Petal Rose
| Step | Hex |
|------|-----|
| 50 | `#FFF1F3` |
| 100 | `#FFE0E5` |
| 200 | `#FFCAD3` |
| 300 | `#FFA8B6` |
| **400** | **`#F27A8E`** |
| 500 | `#E85D75` |
| 600 | `#D44460` |
| 700 | `#B2334D` |
| 800 | `#942942` |
| 900 | `#7D263D` |

#### Accent 2 — Soft Lavender
| Step | Hex |
|------|-----|
| 50 | `#F4F2FE` |
| 100 | `#EAE5FD` |
| 200 | `#D8D0FB` |
| 300 | `#BFB2F7` |
| 400 | `#A694F0` |
| **500** | **`#9B8ADB`** |
| 600 | `#7E6EC4` |
| 700 | `#6558A6` |
| 800 | `#4F4587` |
| 900 | `#3B336A` |

#### Neutral — Warm Gray (green undertone)
| Step | Hex |
|------|-----|
| 0 | `#FFFFFF` |
| 50 | `#F8FAF9` |
| 100 | `#F1F4F3` |
| 200 | `#E4E8E6` |
| 300 | `#CED4D1` |
| 400 | `#ABB3AE` |
| 500 | `#8A948E` |
| 600 | `#6B756F` |
| 700 | `#515A55` |
| 800 | `#343B37` |
| 900 | `#1A1F1C` |
| 950 | `#0F1412` |

#### Functional Colors
| Color | 50 (bg) | 500 (main) | 600 (text) |
|-------|---------|-----------|-----------|
| Success | `#ECFDF5` | `#10B981` | `#059669` |
| Warning | `#FFFBEB` | `#F59E0B` | `#D97706` |
| Error | `#FEF2F2` | `#EF4444` | `#DC2626` |
| Info | `#EFF6FF` | `#3B82F6` | `#2563EB` |

#### Skin Metric Colors
| Metric | Light | Dark |
|--------|-------|------|
| Acne | `#F27A8E` | `#FF8FA0` |
| Pore | `#4ECDC4` | `#5EDDD4` |
| Evenness | `#F5C542` | `#FFD55E` |
| Hydration | `#45B7D1` | `#5CC8E0` |
| Redness | `#E87878` | `#FF9090` |

#### Gold / VIP
| Token | Hex | Usage |
|-------|-----|-------|
| `--gold-light` | `#FFD700` | Gold gradient start |
| `--gold-dark` | `#FFA500` | Gold gradient end |
| `--gold-text` | `#7B4C00` | Text on gold background |
| `--gold-gradient` | `linear-gradient(135deg, #FFD700, #FFA500)` | VIP badges, crowns |

#### Product Category Colors
| Token | Hex | Category |
|-------|-----|----------|
| `--cat-clean` | `#3B82F6` | Cleanser |
| `--cat-serum` | `#9B8ADB` | Serum |
| `--cat-cream` | `#2A9D7C` | Moisturizer |
| `--cat-sun` | `#E8925B` | Sunscreen |
| `--cat-toner` | `#00838F` | Toner |
| `--cat-toner-bg` | `#E0F7FA` | Toner background |
| `--cat-mask` | `#9B8ADB` | Mask |

### Semantic Tokens — Light

| Token | Maps to | Usage |
|-------|---------|-------|
| `--content-primary` | neutral-900 | Main text |
| `--content-secondary` | neutral-600 | Subtitles, descriptions |
| `--content-tertiary` | neutral-500 | Timestamps, hints |
| `--content-disabled` | neutral-400 | Disabled text |
| `--content-inverse` | neutral-0 | Text on dark backgrounds |
| `--content-brand` | primary-500 | Brand-colored text |
| `--surface-primary` | neutral-0 | Main background |
| `--surface-secondary` | neutral-50 | Cards, sections |
| `--surface-tertiary` | neutral-100 | Inputs, chips (inactive) |
| `--surface-elevated` | neutral-0 | Sheets, dialogs |
| `--surface-brand-subtle` | primary-50 | Brand hint background |
| `--border-default` | neutral-200 | Default borders |
| `--border-subtle` | neutral-100 | Card borders |
| `--border-strong` | neutral-400 | Focus/emphasis |
| `--border-brand` | primary-400 | Brand borders |
| `--interactive-primary` | primary-500 | Buttons, links |
| `--interactive-on-primary` | neutral-0 | Text on primary button |

### Semantic Tokens — Dark

| Token | Value | Notes |
|-------|-------|-------|
| `--content-primary` | `#EAEFEC` | Reduced brightness |
| `--content-secondary` | `#A0AAA5` | |
| `--surface-primary` | `#0F1412` | Warm charcoal base |
| `--surface-secondary` | `#171D1A` | Cards |
| `--surface-tertiary` | `#1E2522` | Elevated |
| `--surface-brand-subtle` | `rgba(42,157,124,0.12)` | Translucent for depth |
| `--border-default` | `rgba(255,255,255,0.10)` | |
| `--border-subtle` | `rgba(255,255,255,0.06)` | |
| `--interactive-primary` | primary-400 (`#4BBE97`) | Bumped brightness |

---

## Typography

| Token | Size | Weight | Line Height | Letter Spacing | Usage |
|-------|------|--------|-------------|----------------|-------|
| `h1` | 30px | 700 | 1.20 | -0.5px | Page titles |
| `h2` | 24px | 700 | 1.25 | -0.4px | Section titles |
| `h3` | 19px | 600 | 1.30 | -0.2px | Card titles |
| `h4` | 16px | 600 | 1.35 | -0.1px | Item titles |
| `b1` | 16px | 400 | 1.50 | 0px | Large body |
| `b2` | 14px | 400 | 1.45 | 0.05px | Standard body |
| `b3` | 13px | 400 | 1.40 | 0.05px | Small body |
| `c1` | 12px | 500 | 1.35 | 0.2px | Captions, labels |
| `c2` | 10px | 600 | 1.30 | 0.3px | Micro labels |
| `btn` | 16px | 600 | 1.0 | -0.1px | Button text |
| `btn-sm` | 14px | 600 | 1.0 | 0px | Small buttons |
| `num-xl` | 38px | 800 | 1.10 | -1px | Hero scores |
| `num-lg` | 26px | 700 | 1.15 | -0.5px | Large numbers |
| `num-md` | 18px | 600 | 1.20 | -0.2px | Medium numbers |
| `num-sm` | 14px | 700 | 1.20 | 0px | Small numbers |

**Font family:** `'SF Pro Display', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif`

---

## Spacing

| Token | Value | Usage |
|-------|-------|-------|
| `space-2` | 2px | Micro adjustment |
| `space-4` | 4px | Tight spacing |
| `space-6` | 6px | Icon-text gap |
| `space-8` | 8px | Inner element |
| `space-10` | 10px | Compact padding |
| `space-12` | 12px | List item gap |
| `space-16` | 16px | Standard / page gutter |
| `space-20` | 20px | Card internal |
| `space-24` | 24px | Section gap |
| `space-32` | 32px | Large gap |
| `space-40` | 40px | Extra large |
| `space-48` | 48px | Page section |
| `space-64` | 64px | Page segment |

**Aliases:** `page-gutter` = 16px, `section-gap` = 24px, `card-padding` = 16px

---

## Border Radius

| Token | Value | Usage |
|-------|-------|-------|
| `radius-xs` | 4px | Small elements |
| `radius-sm` | 8px | Buttons, chips |
| `radius-md` | 12px | Cards, inputs |
| `radius-lg` | 16px | Large cards |
| `radius-xl` | 24px | Hero cards, sheets |
| `radius-xxl` | 32px | Phone frame |
| `radius-full` | 9999px | Pills, avatars |

---

## Elevation (Shadows)

| Token | Light | Usage |
|-------|-------|-------|
| `shadow-xs` | `0 1px 2px rgba(15,20,18,0.04)` | Subtle lift |
| `shadow-sm` | `0 1px 3px + 0 2px 8px` | Cards |
| `shadow-md` | `0 4px 6px + 0 10px 24px` | Elevated cards |
| `shadow-lg` | `0 8px 16px + 0 20px 40px` | Dialogs |
| `shadow-xl` | `0 20px 60px` | Phone frame |
| `shadow-brand-sm` | Brand-tinted green | Primary buttons |
| `shadow-brand-md` | Brand-tinted green | FAB |
| `shadow-rose-sm` | Rose-tinted | Rose CTAs |
| `shadow-gold-sm` | Gold-tinted | Pro/VIP badges |

Dark mode: All shadows use `rgba(0,0,0,...)` with increased opacity.

---

## Gradients

| Token | Definition | Usage |
|-------|-----------|-------|
| `grad-primary` | 135° primary-500 → 400 → 300 | Brand fills |
| `grad-primary-bold` | 145° primary-700 → 500 → 400 | FAB, strong accent |
| `grad-hero` | 145° primary-800 → 600 → 500 → 300 | Hero cards |
| `grad-button` | 180° primary-400 → 500 | Primary buttons |
| `grad-warm` | 135° #FFF8F2 → primary-50 | Warm backgrounds |
| `grad-surface` | 180° neutral-v-50 → neutral-0 | Page backgrounds |
| `grad-score` | 135° primary → teal → info | Score rings |
| `grad-glass` | 135° white 92% → 72% | Glass morphism |
| `grad-rose-warm` | 135° rose-50 → warm pink | Rose cards |
| `grad-lavender-soft` | 135° lavender-50 → 100 | Lavender cards |
| `grad-tri-warm` | 135° mint → rose → lavender | Compare card bg |
| `grad-skin` | 135° skin tones | Photo placeholder |
| `grad-profile` | 160° primary-600 → 500 → 300 | Profile header |
| `grad-paywall` | 160° primary-700 → 500 → secondary-400 | Paywall hero |

---

## Motion

| Token | Value | Usage |
|-------|-------|-------|
| `duration-fast` | 150ms | Micro interactions |
| `duration-normal` | 300ms | Standard transitions |
| `duration-slow` | 500ms | Emphasis, reveal |
| `duration-slower` | 700ms | Page transitions |
| `easing-standard` | `cubic-bezier(0.2, 0, 0, 1)` | General motion |
| `easing-decelerate` | `cubic-bezier(0, 0, 0, 1)` | Enter animations |
| `easing-accelerate` | `cubic-bezier(0.3, 0, 0.8, 0.15)` | Exit animations |
| `easing-bounce` | `cubic-bezier(0.34, 1.56, 0.64, 1)` | Playful feedback |

---

## Component Tokens

| Token | Value | Usage |
|-------|-------|-------|
| `button-height` | 52px | Standard buttons |
| `button-height-sm` | 40px | Small buttons |
| `input-height` | 52px | Text fields |
| `icon-size-xs` | 16px | Tiny icons |
| `icon-size-sm` | 20px | Menu icons |
| `icon-size-md` | 24px | Standard icons |
| `icon-size-lg` | 32px | Feature icons |
| `icon-size-xl` | 40px | Hero icons |
| `avatar-sm` | 36px | Compact avatar |
| `avatar-md` | 44px | Standard avatar |
| `avatar-lg` | 56px | Profile avatar |
| `avatar-xl` | 72px | Large profile |
| `bottom-nav-height` | 84px | Navigation bar |
| `status-bar-height` | 54px | iOS status bar |
| `top-bar-height` | 56px | App bar |
| `fab-size` | 52px | Floating action button |
| `card-border-width` | 0.5px | Card border |
| `thumbnail-sm` | 48px | Small thumbnails |
| `thumbnail-md` | 64px | Medium thumbnails |
| `thumbnail-lg` | 80px | Large thumbnails |

---

## Shared Components

Available in `ui/component/`:

| Component | Usage |
|-----------|-------|
| `SectionCard` | Card + Column + padding container |
| `SectionHeader` | Title + action row |
| `TrendIndicator` | ↑↓→ with functional color |
| `MenuItem` | Icon + text + trailing chevron |
| `ScoreBar` | Horizontal progress bar |
| `LoadingContent` | Centered loading spinner |
| `EmptyContent` | Icon + title + description |
| `ErrorContent` | Error state with retry |
| `animateListItem` | Staggered list entrance |

---

## Dark Mode

Dark mode is activated via `[data-theme="dark"]` on the phone frame / root container.

**Key principles:**
- Base: `#0F1412` (warm charcoal with green undertone, not pure black)
- Surface elevation: progressively lighter (`#0F1412` → `#171D1A` → `#1E2522`)
- Brand colors: bumped to lighter variants (500 → 400 for interactive, 500 → 300 for text)
- Metric colors: increased saturation for vibrancy
- Functional surfaces: use `rgba()` translucent fills instead of opaque
- Shadows: deeper with `rgba(0,0,0,...)` instead of warm-tinted

---

## File Structure

```
.figma-mockups/
  ├── base.css              ← Complete design token system
  ├── index.html            ← Visual design system showcase
  ├── 01-onboarding.html    ← 4 screens
  ├── 02-auth.html          ← Login + Register + Forgot Password
  ├── 03-dashboard.html     ← Content + Empty state
  ├── 04-timeline.html      ← Content + Empty state
  ├── 05-camera.html        ← Viewfinder + Result + Permission
  ├── 06-record-detail.html ← Full analysis view
  ├── 07-profile.html       ← Profile page
  ├── 08-product.html       ← Product list + Add sheet
  ├── 09-attribution.html   ← Attribution report
  ├── 10-paywall.html       ← Subscription page
  ├── 11-settings.html      ← Settings + Delete dialog
  ├── 12-share.html         ← Share card page
  ├── 13-locked-states.html ← 4 locked/gated states
  ├── 14-dark-core.html     ← Dark: Dashboard + Timeline + Profile
  ├── 15-dark-screens.html  ← Dark: Auth + Detail + Product + Attribution
  ├── 16-dark-utility.html  ← Dark: Onboarding + Paywall + Settings + Share
  ├── 17-dark-locked.html   ← Dark: 4 locked states
  ├── 18-loading-states.html← 3 skeleton screens
  ├── 19-toast-states.html  ← 3 toast variants
  └── 20-edit-profile.html  ← Edit profile + Change password
```
