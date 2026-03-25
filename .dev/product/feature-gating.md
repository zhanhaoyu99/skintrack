# 付费门控规格

> 最后更新：2026-03-22
> 设计稿：`.figma-mockups/13-locked-states.html`（亮色 4 态）+ `17-dark-locked.html`（暗色 4 态）

## 门控原则

1. **免费用户能体验核心价值**：可拍照 3 次，可看基础评分（5 维度数值）
2. **门控锁定高级功能**：AI 深度分析、雷达图、归因报告、分享卡
3. **门控展示而非隐藏**：用模糊内容 + Pro 升级卡暗示价值，而非完全隐藏功能
4. **统一升级入口**：所有门控卡都跳转到 PaywallScreen
5. **试用引导**：每个门控卡都显示"14 天免费试用"提示

## 门控触发点（4 处）

| # | 页面 | 触发条件 | 门控样式 | 版本 |
|---|------|---------|---------|------|
| 1 | CameraScreen | 免费用户已拍 ≥3 次 | RecordLimitShowcase（进步展示） | [V1] |
| 2 | RecordDetailScreen | 非 PRO 用户 | 隐藏 AI 分析 + 模糊雷达图 | [V1] |
| 3 | AttributionReportScreen | 非 PRO 用户 | 隐藏 AI 洞察 + 模糊产品排名 | [V1] |
| 4 | ShareCardScreen | 非 PRO 用户 | 模糊分享卡预览 | [V1] |

---

## 门控态 1：RecordDetail — 免费用户限制

### 可见内容
- 照片预览区（正常显示）
- "免费版" 标签（`c2`，白色，`error-50` 背景，`radius-full`，照片左上角）
- 评分总览卡（迷你版）：ScoreRing + 评分 + 标题
- 基础指标网格（5 列）：痘痘/毛孔/均匀/泛红/水润 数值 + mini bar

### Pro 升级卡
- 背景：`linear-gradient(135deg, #FFF8F0, secondary-50, #FFF5ED)`
- 圆角：`shapes.large` (16dp)
- 边框：`card-border-width` (0.5dp)，`rgba(244,162,97,0.1)`
- 装饰圆：右上角 60dp 圆，`rgba(244,162,97,0.06)`
- 内容：
  - 皇冠图标（`dimens.fabSize` 52dp 圆，`gold-gradient` 背景，`shadow-gold-sm`）
  - 标题："解锁深度分析"（`h3`，`content-primary`）
  - 副标题："升级 Pro 查看雷达图、AI 洞察和个性化建议"（`b3`，`content-secondary`）
  - 功能标签（FlowRow，gap `spacing.sm`）：
    - "雷达图" / "AI 分析" / "建议"
    - 样式：`c2`，`secondary-600` 文字，`rgba(244,162,97,0.08)` 背景，`radius-full`
  - 升级按钮："★ 升级 Pro 解锁"（`button-height-sm` 40dp，`gold-gradient` 背景，`gold-text` 文字，`shadow-gold-sm`，`radius-full`）
  - 试用提示："14天免费试用"（`c2`，`content-success`，`surface-success` 背景，`radius-full`）

### 模糊内容
- 雷达图区域：`filter: blur(6px)`，`opacity: 0.5`（亮色）/ `0.4`（暗色）
- 模糊覆盖 pill：居中，`surface-primary` 背景（暗色 `surface-secondary`），锁图标 + "Pro 专属"（`c2`）

---

## 门控态 2：Camera — 拍照限额（RecordLimitShowcase）

### 进步展示页面（非传统锁定卡）
- 副标题："已完成 3 次记录~"（`b2`，`content-secondary`）
- 成功指标："+10"（`num-xl`，`content-success`）+ "皮肤在变好"（`b2`，`content-success`，`surface-success` 背景，`radius-full`）

### 下一步提示卡
- 虚线边框：`2dp dashed border-default`，`radius-large`
- 问号图标：48dp 圆，`surface-tertiary` 背景，"?" 文字（`h2`，`content-disabled`）
- 标题："第 4 次记录？"（`h4`，`content-primary`）
- 副标题："升级 Pro 解锁无限记录"（`b3`，`content-secondary`）

### Pro 权益网格（3 列，gap `spacing.md`）[V1]
- 无限记录 / 每天追踪：primary-50 图标背景
- AI 分析 / 深度洞察：lavender-50 图标背景
- 归因分析 / 效果评估：rose-50 图标背景
- 每项：图标圆(36dp) + 标题(`b3` 600w) + 副标题(`c2` content-tertiary)

### 操作
- 主按钮："升级 Pro 解锁"（primary button，全宽）
- 试用 pill："14天免费试用"

---

## 门控态 3：Attribution — AI 归因锁定

### 可见内容
- 统计卡片行（3 张）：分数变化 +12（surface-success）/ 使用产品 6（surface-lavender）/ 追踪天数 30（surface-brand-subtle）

### Pro 升级卡
- 与 RecordDetail 门控卡结构相同
- 标题："解锁 AI 归因洞察"
- 副标题："了解哪个产品对你的皮肤帮助最大"
- 功能标签："AI 洞察" / "排名" / "建议"

### 模糊内容
- 产品排名列表：`filter: blur(6px)`，`opacity: 0.5`
- 骨架样式：圆形头像占位 + 横线占位 + 分隔线
- 居中模糊 pill："Pro 专属"

---

## 门控态 4：Share — 分享卡锁定

### 模糊内容
- 完整分享卡预览：`filter: blur(6px)`，`opacity: 0.5`
- 卡片结构可见但模糊：Header + Photos + Result + Footer

### Pro 升级卡
- 标题："解锁精美分享卡"
- 副标题："生成精美对比卡片，分享你的蜕变故事"
- 功能标签："多模板" / "社交分享" / "保存图片"

---

## 共享组件：LockedFeatureCard

```
LockedFeatureCard(
    title: String,           // 如 "解锁深度分析"
    subtitle: String,        // 如 "升级 Pro 查看..."
    tags: List<String>,      // 如 ["雷达图", "AI 分析", "建议"]
    onUpgrade: () -> Unit    // → PaywallScreen
)
```

### 结构
```
Card (warm gradient bg, radius-large, border secondary opacity)
├── Decorative circle (absolute top-right, 60dp, secondary 6% opacity)
├── Crown icon (52dp circle, gold-gradient, shadow-gold-sm)
│   └── 👑 SVG (iconSizeLg, gold-text)
├── Title (h3, content-primary)
├── Subtitle (b3, content-secondary)
├── FlowRow tags (c2, secondary-600 text, secondary 8% bg, radius-full)
├── Upgrade button (gold-gradient bg, gold-text, shadow-gold-sm, button-height-sm, radius-full)
│   └── "★ 升级 Pro 解锁"
└── Trial pill (surface-success bg, content-success text, c2, radius-full)
    └── "✅ 14天免费试用" / "新用户14天免费试用"
```

## 暗色模式差异（17-dark-locked.html）

| 元素 | 亮色 | 暗色 |
|------|------|------|
| Pro 卡片背景 | `#FFF8F0 → secondary-50 → #FFF5ED` | `rgba(244,162,97,0.06) → rgba(244,162,97,0.03)` |
| 装饰圆 | `rgba(244,162,97,0.06)` | `rgba(244,162,97,0.04)` |
| 功能标签背景 | `rgba(244,162,97,0.08)` | `rgba(244,162,97,0.08)`（不变） |
| 模糊内容 opacity | 0.5 | 0.4 |
| 模糊 pill 背景 | `surface-primary` | `surface-secondary` |
| 皇冠/按钮 | gold-gradient 不变 | gold-gradient 不变 |
| 照片背景（Detail） | `gradients.skin` 亮色 | `#5A4A40 → #403530 → #3A2E28` |
| 基础指标卡背景 | `surface-secondary` | `surface-secondary` (dark) |

## 与当前实现的差异

- LockedFeatureCard 已实现 title/subtitle/tags 参数 ✅
- 金色渐变按钮 + 皇冠图标已实现 ✅
- 模糊内容 + 覆盖 pill 已实现 ✅
- RecordLimitShowcase 进步展示页已实现 ✅
- 暗色模式适配已完成 ✅
- 无已知偏差
