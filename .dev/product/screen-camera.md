# 拍照页 CameraScreen — 拍摄自拍并获取 AI 肌肤分析结果

> 设计稿: `.figma-mockups/05-camera.html` (3 态: 取景器 / 分析结果 / 权限请求)

## 页面入口

- 底部导航栏中央 FAB "拍照" 按钮
- Dashboard 拍照提醒卡 "去拍照" 按钮
- Dashboard 空状态 "拍第一张自拍" 按钮
- 全屏页面（非 Tab 内嵌），带返回导航

## 页面状态

| 状态 | 条件 | 展示内容 |
|------|------|---------|
| 权限请求 | 未授予相机权限 | 权限说明 + 授权按钮 |
| 取景器 | 已授予权限，未拍照 | 相机预览 + 人脸引导框 + 快门 |
| 分析结果 | 拍照完成 + AI 分析返回 | 照片 + 总分 + 五维指标 + 操作按钮 |

门控: 免费用户超过 3 条记录 → 显示 FeatureGated 提示 → PaywallScreen（见 `feature-gating.md`）

---

## 布局结构 (viewfinder)

```
┌──────────────────────────────┐  全屏, bg #0A0C0B
│ ←                    ⚡ ⏱ ☆  │  TopBar (topBarHeight 56dp, absolute)
│                              │   左: 返回箭头 (白色)
│       ☀ 光线良好              │   右: 闪光灯 + 计时器 + 滤镜图标
│                              │  光线提示 badge (顶部居中)
│     ┏╌╌╌╌╌╌╌╌╌╌╌╌╌┓        │
│     ┇  ┌──┐  ┌──┐  ┇        │  人脸引导框 240×320dp
│     ┇  └          ┘  ┇        │   椭圆虚线描边 rgba(white, 0.2)
│     ┇                ┇        │   4 角 L 型标记 (primary-300, 2.5dp, 24dp)
│     ┇   (面部轮廓)   ┇        │   面部轮廓 140×180dp, opacity 0.06
│     ┇                ┇        │
│     ┇  ┐          ┌  ┇        │
│     ┇  └──┘  └──┘  ┇        │
│     ┗╌╌╌╌╌╌╌╌╌╌╌╌╌┛        │
│       将面部置于框内          │  提示文字 (c1 12sp, rgba white 0.5)
│                              │
│    🖼      ◉      🔄        │  底部控件
│  (gallery) (shutter) (flip)  │
└──────────────────────────────┘
```

### 组件详细规格

#### 1. 相机预览
- **背景**: 全屏 `#0A0C0B`，预览区 `radial-gradient(#1A1F1C center → #0A0C0B)`

#### 2. TopBar
- 高度 `topBarHeight` (56dp)，绝对定位于顶部
- 左侧: 返回箭头图标，白色
- 右侧: 闪光灯切换 + 计时器 + 滤镜图标，白色

#### 3. 光线提示 Badge
- 顶部居中，`rgba(white, 0.1)` 背景 + `backdrop-blur(8px)`
- 太阳图标 + "光线良好" (c1 12sp, 白色)

#### 4. 人脸引导框
- 尺寸: 240×320dp，居中
- 椭圆虚线描边: `rgba(255,255,255,0.2)`
- 4 个角标记: L 型，`primary-300` 颜色，2.5dp 描边，24dp 长
- 面部剪影: 140×180dp，`opacity 0.06`
- 引导文字: "将面部置于框内" (c1 12sp, `rgba(white, 0.5)`)，椭圆下方居中

#### 5. 底部控件
- **快门按钮** (居中): 76dp 外圈，描边 3.5dp `rgba(white, 0.9)`，内圆 62dp `rgba(white, 0.95)` 实心
- **相册入口** (左): 44dp 圆形，`rgba(white, 0.08)` 背景，相册图标
- **翻转相机** (右): 44dp 圆形，`rgba(white, 0.08)` 背景，翻转图标

---

## 布局结构 (result)

```
┌──────────────────────────────┐
│  ← (glassmorphic)            │  TopBar: 返回按钮 (毛玻璃)
│                              │  状态栏: 浅色 (白色图标)
│  ┌──────────────────────┐    │
│  │                      │    │  照片区 340dp 高
│  │   (拍摄照片 + 渐变)   │    │   bg: gradients.skin
│  │                      │    │   face overlay SVG opacity 0.08
│  │        ┌────┐        │    │
│  └────────│ 82 │────────┘    │  浮动 ScoreRing (80dp)
│           └────┘             │   底部溢出 -40dp
│                              │
│  皮肤状态不错哦              │  标题 (h3 19sp, weight 700)
│  各项指标都在稳步改善中...    │  副标题 (b3 13sp, content-secondary)
│                              │
│  [85] [78] [82] [75] [80]   │  5 迷你指标卡 (row, gap sm 8dp)
│  痘痘 毛孔 均匀 泛红 水润    │
│                              │
│  🔥 连续打卡 7 天            │  Streak 徽章
│                              │
│  [继续拍照]  [查看详情]       │  双按钮 (row, gap md 12dp)
└──────────────────────────────┘
```

### 组件详细规格

#### 1. 照片预览区
- 高度: 340dp
- 背景: `gradients.skin` 渐变
- 人脸轮廓叠加: SVG, `opacity 0.08`
- 状态栏: 浅色模式（白色系统图标）

#### 2. TopBar
- 返回按钮: 毛玻璃 (glassmorphic) 样式

#### 3. 浮动 ScoreRing
- 尺寸: 80dp 圆形
- 定位: 照片区底部居中，向下偏移 -40dp（半悬浮效果）
- 背景: `surface-primary`，`shadow-lg`，`radius-full`
- 环形进度:
  - 底色描边: `tertiary` stroke
  - 进度描边: `primary-400`
- 分数: "82" (num-lg 26sp, `content-brand`)

#### 4. 结果文案
- 标题: "皮肤状态不错哦" (h3 19sp, weight 700)
  - 根据评分档位动态切换文案
- 副标题: "各项指标都在稳步改善中，继续保持~" (b3 13sp, `content-secondary`)

#### 5. 迷你指标卡 (5 个)
- 容器: Row, `gap sm` (8dp), 等宽
- 每个卡片:
  - 宽度: 56dp
  - 背景: `surface-secondary`
  - 圆角: `radius-small` (8dp)
  - 内边距: `sm` (8dp)
  - 分值: num-sm 14sp, 对应 metric 颜色 (acne/pore/evenness/redness/hydration)
  - 标签: c2 10sp, `content-tertiary`
  - 迷你条形图: 底部，对应 metric 颜色
- 数据: 痘痘 85, 毛孔 78, 均匀 82, 泛红 75, 水润 80

#### 6. Streak 徽章
- 背景: `linear-gradient(secondary-50 → #FFF8F0)`, 描边 `secondary` opacity 0.1
- 内容: 火焰图标 (`secondary-500`) + "连续打卡 7 天" (b3 13sp, weight 700, `secondary-600`)

#### 7. 双操作按钮
- 容器: Row, `gap md` (12dp), 等宽
- "继续拍照": outlined 样式, full width
- "查看详情": primary 样式, full width

---

## 布局结构 (permission)

```
┌──────────────────────────────┐
│                              │  居中布局, padding section (32dp)
│                              │
│          ┌─────┐             │  权限图标 80dp 圆形
│          │ 📷  │             │   bg: rose-50
│          └─────┘             │   icon: rose-400 相机, iconSizeXl (40dp)
│                              │
│       来拍张自拍吧            │  标题 (h2 24sp, weight 800)
│                              │
│   我们需要使用相机来拍摄      │  描述 (b2 14sp, content-secondary)
│   你的皮肤照片，AI 会帮      │
│   你分析肌肤状况              │
│                              │
│   ✓ AI 智能分析五大肌肤维度   │  3 条 feature benefits
│   ✓ 追踪肌肤变化趋势         │
│   ✓ 照片仅保存在本地，       │
│     隐私安全                  │
│                              │
│   [   允许使用相机   ]        │  primary 按钮
│        下次再说               │  text 按钮
│                              │
└──────────────────────────────┘
```

### 组件详细规格

#### 1. 权限图标
- 容器: 80dp 圆形, `rose-50` 背景
- 图标: 相机, `rose-400` 颜色, `iconSizeXl` (40dp)

#### 2. 标题
- "来拍张自拍吧" (h2 24sp, weight 800)

#### 3. 描述
- "我们需要使用相机来拍摄你的皮肤照片，AI 会帮你分析肌肤状况" (b2 14sp, `content-secondary`)

#### 4. Feature Benefits (3 条)
- 每条前有勾选圆标记: 22dp 圆形, `surface-brand-subtle` 背景, 白色 checkmark
- 文本 (b2 14sp, `content-secondary`, line-height 1.5):
  1. "AI 智能分析五大肌肤维度"
  2. "追踪肌肤变化趋势"
  3. "照片仅保存在本地，隐私安全"

#### 5. 操作按钮
- "允许使用相机": primary 全宽按钮 (buttonHeight 52dp)
- "下次再说": text 按钮, `content-primary` 颜色（注意: 设计稿从 tertiary 改为 primary 色）

---

## 交互行为

### 拍照流程
```
权限请求 → 授权 → 取景器 → 点击快门 → 压缩图片 → AI 分析(异步) → 保存 Room → 显示结果
                                                        ↓ 失败
                                                  保存原始记录 (pending 状态)
                                                  后台队列重试
```

### 取景器交互
- **快门按钮**: 点击拍照，拍照时有轻微缩放反馈动画
- **闪光灯**: 切换 off/on/auto 三态
- **翻转**: 切换前后摄像头
- **返回**: 返回上一页面
- **光线检测**: 实时检测环境光线，badge 显示 "光线良好" / "光线不足" 等提示

### 结果页交互
- **继续拍照**: 返回取景器状态，可重新拍摄
- **查看详情**: 导航至 RecordDetailScreen，展示完整分析报告
- **返回**: 返回上一页面（Dashboard）

### 权限请求交互
- **允许使用相机**: 触发系统权限弹窗
  - 用户允许 → 进入取景器
  - 用户拒绝 → 保持当前页面（可再次点击重试，或引导至系统设置）
- **下次再说**: 返回上一页面

### 门控 [V1]
- 免费用户超过 3 条记录 → 拍照前拦截，显示 FeatureGated 提示 → PaywallScreen
- 见 `feature-gating.md`

---

## 数据依赖

| 数据 | 来源 | 说明 |
|------|------|------|
| 相机权限状态 | 系统 API | 决定显示取景器或权限请求 |
| 拍摄照片 | CameraX | 压缩后送 AI 分析 |
| AI 分析结果 | Supabase Edge Function | 返回总分 + 五维指标 + 文案 |
| 连续打卡天数 | Room 本地计算 | Streak 徽章显示 |
| 用户记录数 | Room | 门控判断 (免费 ≤3) |
| 状态文案 | 本地映射表 | 根据总分档位选择标题/副标题 |

---

## 与其他页面的关系

| 目标页面 | 触发条件 |
|----------|---------|
| DashboardScreen | 返回（取景器/结果页） |
| RecordDetailScreen | 结果页 "查看详情" |
| PaywallScreen | 免费用户超额拍照触发门控 |
| 系统设置 | 权限被永久拒绝时引导跳转 |

---

## 暗色模式

| 区域 | 适配说明 |
|------|---------|
| 取景器 | 本身已是深色，无需额外适配 |
| 结果页 — 照片区 | 渐变切换为深色肤色调: `#5A4A40 → #3A2E28` |
| 结果页 — 主体 | dark surface 背景，文字/卡片色跟随暗色 token |
| 权限请求 | dark background，图标/文字色跟随暗色 token |

---

## 与当前实现的差异

> 所有差异标记为 [V1]，即首发版需对齐。

| # | 差异项 | 设计稿 | 当前实现 | 优先级 |
|---|--------|--------|---------|--------|
| 1 | 光线提示 badge | 毛玻璃 badge, `backdrop-blur(8px)`, 顶部居中 | 简单文字提示 | [V1] |
| 2 | 角标记样式 | L 型, primary-300, 2.5dp stroke, 24dp | 实现待核实 | [V1] |
| 3 | 面部剪影 | 140×180dp, opacity 0.06 | 实现待核实 | [V1] |
| 4 | ScoreRing 悬浮定位 | 80dp, 照片底部 -40dp 悬浮 | 实现待核实 | [V1] |
| 5 | 迷你指标卡宽度 | 56dp 固定宽度 | 等分/自适应 | [V1] |
| 6 | Streak 徽章配色 | secondary-50→#FFF8F0 渐变, secondary-600 文字 | Apricot 色系 (已基本对齐) | [V1] |
| 7 | "下次再说" 按钮颜色 | `content-primary` | `content-tertiary` | [V1] |
| 8 | 暗色结果页渐变 | `#5A4A40 → #3A2E28` | 待实现 | [V1] |
