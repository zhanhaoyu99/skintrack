# 护肤品管理 — 产品列表与打卡管理、添加产品入口

> 设计稿: `.figma-mockups/08-product.html` (2 态: 产品列表 + 添加产品 BottomSheet)

## 页面入口

- ProfileScreen → "护肤品管理" 菜单项
- DashboardScreen → 2x2 快捷操作 → "护肤品" 卡片
- 进入时 `showBackButton = true`（从上述入口跳转），Tab 内嵌时 `false`

## 页面状态

| 状态 | 条件 | 表现 |
|------|------|------|
| Loading | 数据加载中 | `LoadingContent()` 居中 |
| Empty | 产品列表为空 | `EmptyContent` + "添加护肤品" TextButton |
| Content | 有产品数据 | 完整列表（搜索 + 筛选 + 进度 + AM/PM 分组） |
| Add Sheet | 点击 "+" 按钮 | overlay-dark 遮罩 + BottomSheet |

## 布局结构 (list)

```
┌─────────────────────────┐
│ ← 护肤品管理          +  │  TopAppBar
├─────────────────────────┤
│ 🔍 搜索产品名称或品牌    │  SearchBar (radius-full)
│                         │
│ [全部][洁面][精华][面霜]..│  CategoryFilters (横滑)
│                         │
│ 今日打卡进度        2/5  │  CheckinProgressCard
│ ═══════════──────────── │  进度条 + 进度点
│ ● ● ○ ○ ○              │
│                         │
│ ⚠ 今天还有 3 个没记录哦~ │  ReminderBanner (Rose)
│                         │
│ ☀️ AM 早间护肤    2/3已打卡│  RoutineSection AM
│ ┌─────────────────────┐ │
│ │🧴 氨基酸洁面乳    ✓ │ │  ProductCard (checked)
│ │   FANCL · Cleansing  │ │
│ │   [洁面]             │ │
│ ├─────────────────────┤ │
│ │✨ 烟酰胺精华 10%  ✓ │ │
│ │   The Ordinary       │ │
│ │   [精华]             │ │
│ ├─────────────────────┤ │
│ │☀️ 轻薄防晒乳SPF50 ○ │ │  ProductCard (unchecked)
│ │   Anessa             │ │
│ │   [防晒]             │ │
│ └─────────────────────┘ │
│                         │
│ 🌙 PM 晚间护肤   0/2未打卡│  RoutineSection PM
│ ┌─────────────────────┐ │
│ │🫙 水乳保湿面霜    ○ │ │
│ │   珂润 · Curel       │ │
│ │   [面霜]             │ │
│ ├─────────────────────┤ │
│ │✨ 视黄醇精华 0.5% ○ │ │
│ │   CeraVe             │ │
│ │   [精华]             │ │
│ └─────────────────────┘ │
└─────────────────────────┘
```

### 1. TopAppBar

- 返回箭头 `Icons.AutoMirrored.Filled.ArrowBack`（`showBackButton=true` 时显示）
- 标题: "护肤品管理"
- 右侧: `Icons.Default.Add` → `viewModel.showAddSheet()`

### 2. SearchBar

- `OutlinedTextField`, `shape = RoundedCornerShape(percent = 50)` (radius-full)
- 背景: `surface-secondary`
- Leading: Search icon (`content-disabled`)
- Placeholder: "搜索产品名称或品牌" (b2, `content-disabled`)
- Trailing: 有输入时显示 Close icon 清除
- 实时过滤: 匹配产品名或品牌名 (case-insensitive)
- 水平 padding: `spacing.md` (12dp)

### 3. CategoryFilters

- `LazyRow`, `Arrangement.spacedBy(spacing.sm)` (8dp), contentPadding `spacing.md`
- 选项: 全部 / 洁面 / 精华 / 面霜 / 防晒 / 化妆水 / 面膜 / 其他
- 选中态 (on): `interactive-primary` 背景, `content-inverse` 白色文字
- 未选态 (off): `surface-tertiary` 背景, `content-secondary` 文字
- 圆角: radius-full
- 字号: c1 (12sp), fontWeight 600
- chip padding: 6dp vertical, 12dp horizontal

### 4. CheckinProgressCard

- 容器: `surface-primary` 背景, radius-lg, shadow-xs, border-subtle
- 当前实现: Mint-50/Lavender-50 渐变背景 + ProgressRing(56dp) + 文字
- **设计稿规格**:
  - 顶行: "今日打卡进度" (b3/c1, `content-secondary`) + "2/5" (b3, `content-brand`, fontWeight 700)
  - 进度条: 6dp 高, radius-full, `gradients.primary` 填充, `surface-tertiary` 轨道
  - 进度点: 5 个圆点 (8dp), 已完成=`interactive-primary` 实心, 未完成=`border-default` 实心
  - 居中对齐 dots, gap `spacing.sm`

### 5. ReminderBanner

- 条件: `uncheckedCount > 0` 时显示
- 背景: `surface-rose`, radius-md (当前实现: Rose-50 渐变 + Rose border)
- 布局: Row, gap `spacing.sm`
- 图标: alert circle SVG (rose-400 填充, icon-size-xs)
- 文案: "今天还有 **N 个** 护肤品没记录哦~"
  - "N 个" 加粗 (fontWeight 700), `rose-500` 色
  - 其余文字: c1 (12sp), `rose-500`

### 6. AM Routine Section

- Header (`RoutineSectionHeader`):
  - Icon: ☀️ 在 28dp 圆角背景 (`Apricot300.copy(alpha=0.12f)`)
  - 标题: "AM 早间护肤" (h4/15sp, Bold, `content-primary`)
  - 右侧: "2/3 已打卡" (c1/12sp, `content-tertiary`)
- 产品列表: `itemsIndexed` + `animateListItem(index)`

### 7. PM Routine Section

- Header: "🌙 PM 晚间护肤" + "0/2 未打卡" (c1, `content-tertiary`)
- Icon bg: `Lavender300.copy(alpha=0.12f)`
- 顶部额外 padding: 14dp

### 8. ProductCard

- 容器: `Card` + `shape.large`, combinedClickable (click=toggleUsage, longClick=delete)
- Checked 态: Mint-400 border (1.5dp, alpha 0.15f) + 绿色渐变背景 `#F0FAF6→#F5FFFA`
- Unchecked 态: subtle border (0.5dp, black 0.03f) + surface 背景
- 内部 Row padding: 14dp horizontal, `spacing.sm` vertical
- **图标区** (44dp, radius 12dp):
  - 品类对应渐变底色 + emoji 图标 (titleMedium)
- **信息区**:
  - 产品名: bodyLarge, 15sp Bold, letterSpacing -0.1sp
  - 品牌: bodySmall, `onSurfaceVariant`, top 1dp
  - 标签行 (Row, top `spacing.xs`):
    - 品类 pill: labelSmall 10sp SemiBold, 品类前景色 + 品类背景色, radius 6dp, padding 8dp/2dp
    - 时段 pill: "⏰ AM/PM/Both", onSurfaceVariant, surfaceVariant bg
- **打卡指示器** (26dp circle):
  - Checked: primary 背景 + shadow(6dp, Mint-400 0.3f) + white Check icon (14dp)
  - Unchecked: outlineVariant border (2dp) 空心圆

### 品类标签色

| 品类 | 前景色 | 背景色 | CSS class |
|------|--------|--------|-----------|
| 洁面 CLEANSER | `#2563EB` (info-600) | `#DBEAFE` (info-50) | cat-clean |
| 精华 SERUM | `#7B1FA2` (lavender-600) | `#F3E5F5` (lavender-50) | cat-serum |
| 面霜 CREAM | `#2E7D32` (primary-600) | `#E8F5E9` (primary-50) | cat-cream |
| 防晒 SUNSCREEN | `#E65100` (secondary-600) | `#FFF3E0` (secondary-50) | cat-sun |
| 化妆水 TONER | `#00838F` (cat-toner) | `#E0F7FA` (cat-toner-bg) | cat-toner |
| 面膜 MASK | lavender-600 | lavender-50 | cat-mask |
| 眼霜 EYE_CREAM | `#8B5CF6` | `#F3E8FF` | — |
| 其他 OTHER | `#6B7B73` | `#F1F5F2` | — |

### 品类图标 (emoji)

| 品类 | Emoji |
|------|-------|
| CLEANSER | 🧴 |
| TONER | 💧 |
| SERUM | ✨ |
| CREAM | 🫙 |
| SUNSCREEN | ☀️ |
| MASK | 🎭 |
| EYE_CREAM | 👁️ |
| EXFOLIATOR | 🧹 |
| OTHER | 🧴 |

## 布局结构 (add sheet)

```
┌─────────────────────────┐
│      (overlay-dark)      │
│                          │
│                          │
├──────────────────────────┤
│         ━━━━             │  handle (36x4dp, border-default)
│       添加产品            │  sheet-title (h3/19sp, Bold, center)
│                          │
│  🔍  搜索产品         >  │  [V1] option 1
│      从产品库中搜索添加    │
│  ─ ─ ─ ─ ─ ─ ─ ─ ─ ─  │
│  📷  扫码添加         >  │  [V2] option 2
│      扫描条形码快速添加    │
│  ─ ─ ─ ─ ─ ─ ─ ─ ─ ─  │
│  ✏️  手动输入          >  │  [V1] option 3
│      手动填写产品信息      │
│                          │
│        [ 取消 ]          │  btn-text, content-tertiary
└──────────────────────────┘
```

### Sheet 容器

- Overlay: `overlay-dark` 背景
- BottomSheet: `surface-elevated` 背景, radius-xl 顶部, padding 12dp top / 16dp horizontal / 32dp bottom
- Handle: 36x4dp, radius-full, `border-default`, 水平居中, bottom margin 16dp
- Title: "添加产品" (h3/19sp, Bold, `content-primary`, text-align center), bottom margin 16dp

### 添加选项 (add-option)

每个选项: Row, gap 12dp, padding 14dp vertical

- **图标区** (44dp, radius-md):
  1. 搜索产品: primary-50→primary-100 渐变, primary-400 搜索图标
  2. 扫码添加: lavender-50→lavender-100 渐变, lavender-400 相机图标
  3. 手动输入: secondary-50→secondary-100 渐变, secondary-400 编辑图标
- **文字区**:
  - 标题: b1 (16sp), fontWeight 600, `content-primary`
  - 描述: b3 (13sp), `content-secondary`, top 1dp
- **箭头**: chevron-right (18dp, strokeWidth 2.5), `content-disabled`
- 选项间: border-subtle 分隔线

### 取消按钮

- `btn-text` 样式, `content-tertiary` 文字色, top margin 8dp

## 交互行为

| 操作 | 行为 |
|------|------|
| 点击产品卡片 | toggle 今日打卡状态 (checked <-> unchecked) |
| 长按产品卡片 | 弹出 AlertDialog 确认删除 |
| 搜索输入 | 实时过滤产品列表 (name / brand) |
| 切换分类 chip | 按品类筛选，"全部" 显示所有 |
| 点击 "+" | 显示 AddProductSheet |
| Sheet: 搜索产品 | [V1] 跳转产品搜索流程（从产品库搜索） |
| Sheet: 扫码添加 | [V2] 打开相机扫描条形码 |
| Sheet: 手动输入 | [V1] 跳转手动输入表单（产品名+品牌+品类+使用时段） |
| Sheet: 取消 | 关闭 BottomSheet |
| 打卡成功 | Snackbar "产品已添加到护肤方案" (SUCCESS) |
| 删除成功 | Snackbar "产品已删除" (SUCCESS) |

### 手动输入表单 (当前 AddProductSheet 实现)

- 产品名称: OutlinedTextField, 必填
- 品牌: OutlinedTextField, 可选
- 分类: ExposedDropdownMenuBox, 所有 ProductCategory 选项
- 使用时段: FilterChip 组 (AM / PM / Both)
- 保存按钮: 主按钮, name 非空时 enabled

## 数据依赖

| 数据 | 来源 | 说明 |
|------|------|------|
| 产品列表 | `ProductRepository.getAllProducts()` | Flow, 实时更新 |
| 今日使用记录 | `ProductRepository.getUsageByDate(userId, today)` | 判断打卡状态 |
| AM/PM 分组 | `SkincareProduct.usagePeriod` | AM / PM / BOTH，BOTH 出现在两组 |
| 搜索/筛选 | ViewModel 本地 `combine` | `_searchQuery` + `_selectedCategory` |
| 用户 ID | `AuthRepository.currentUser()` | 默认 "local-user" |

### 数据模型

```kotlin
// SkincareProduct
data class SkincareProduct(
    val id: String,
    val userId: String,
    val name: String,
    val brand: String?,
    val category: ProductCategory,
    val usagePeriod: UsagePeriod,
)

// ProductCategory: CLEANSER, TONER, SERUM, EMULSION, CREAM, SUNSCREEN, MASK, EYE_CREAM, EXFOLIATOR, OTHER
// UsagePeriod: AM, PM, BOTH

// DailyProductUsage
data class DailyProductUsage(
    val id: String,
    val userId: String,
    val productId: String,
    val usedDate: LocalDate,
)
```

### UiState

```kotlin
sealed interface ProductUiState {
    data object Loading : ProductUiState
    data object Empty : ProductUiState
    data class Content(
        val products: List<SkincareProduct>,
        val todayUsedProductIds: Set<String>,
    ) {
        val amProducts  // usagePeriod == AM || BOTH
        val pmProducts  // usagePeriod == PM || BOTH
        val checkedCount / totalCount / uncheckedCount
    }
}
```

## 与其他页面的关系

| 页面 | 关系 |
|------|------|
| ProfileScreen | 入口: "护肤品管理" 菜单 → `ProductManageScreen` |
| DashboardScreen | 入口: 快捷操作 "护肤品" → `ProductManageScreen`; 打卡数据显示在首页打卡卡片 |
| AttributionReportScreen | 读取产品使用数据进行归因分析 |
| RecordDetailScreen | 显示拍照当日使用的产品列表 |

## 暗色模式

- 产品卡片: dark surface 背景, checked 态保持 primary 色边框 + 深色渐变
- 打卡指示器: checked 态 primary 背景不变, unchecked 保持 outlineVariant 描边
- 图标背景: rgba 半透明变体 (品类色 + 低 alpha)
- 品类标签: 维持各品类前景/背景色 (暗色适配)
- 进度条: primary fill 不变, track 用 surfaceVariant
- SearchBar: 暗色 surface 背景, placeholder 色适配
- ReminderBanner: rose 系色维持, 暗色下 surface-rose 适配
- BottomSheet: surface-elevated 暗色变体

## 与当前实现的差异

### 已对齐

- TopAppBar (返回+标题+添加)
- SearchBar 圆角搜索 + 实时过滤
- CategoryFilter chips 横滑
- AM/PM 分组 + UsagePeriod 数据模型
- 品类标签色 + 品类 emoji 图标
- 打卡 toggle (checked/unchecked 样式)
- 长按删除 AlertDialog
- Snackbar 反馈
- AddProductSheet 手动输入表单 (name/brand/category/usagePeriod)
- `animateListItem` 列表入场动画

### 待对齐

| 差异项 | 设计稿 | 当前实现 | 优先级 |
|--------|--------|---------|--------|
| CheckinProgressCard 布局 | 文字+进度条+dots 纵向排列 | ProgressRing(56dp)+文字 横向排列，渐变背景 | P2 — 功能等价，风格差异 |
| 进度点大小 | 8dp | 10dp | P3 |
| 进度条高度 | 6dp (设计稿 HTML) | 6dp (一致) | — |
| ReminderBanner 图标 | alert circle SVG (rose-400) | 🔔 emoji in 36dp 圆角框 | P3 |
| AddProductSheet 入口模式 | 3 选项 (搜索/扫码/手动) + 取消 | 直接进入手动输入表单 | P1 — 需改为 3 选项入口 |
| Sheet: 搜索产品 | [V1] 从产品库搜索 | 未实现 | P1 |
| Sheet: 扫码添加 | [V2] 条形码扫描 | 未实现 | V2 |
| SearchBar 样式 | surface-secondary bg, no border by default | OutlinedTextField (有描边) | P3 |
| FilterChip 样式 | 纯背景色切换 (无 M3 FilterChip 默认边框) | M3 FilterChip 默认样式 | P3 |
| 产品项分隔 | border-top 分隔线 | Card 间距分隔 | P3 — 设计差异可接受 |
