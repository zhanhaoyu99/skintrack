# 用户流程（User Flows）

> 最后更新：2026-03-22
> 本文档描述 SkinTrack 的 6 条完整功能链路，确保页面间衔接无断裂。

## 流程 1：新用户首次使用

```
App 启动（首次安装）
│
├── 1. OnboardingScreen（4 页水平翻页）
│   ├── Page 1: "记录你的皮肤变化"（可跳过）
│   ├── Page 2: "管理你的护肤方案"（可跳过）
│   ├── Page 3: "AI 深度分析洞察"（可跳过）
│   └── Page 4: 肤质选择（油性/干性/混合/敏感/中性）→ 保存到 UserPreferences
│       └── 点击"开始使用"
│
├── 2. AuthScreen（注册 Tab）
│   ├── 填写：昵称 + 邮箱 + 密码 + 确认密码
│   ├── 勾选同意服务条款
│   └── 点击"创建账号" → 保存 auth_session + displayName
│
├── 3. DashboardScreen（空态）
│   ├── 看到 3 步引导：拍照 → AI 分析 → 追踪
│   ├── 社会证明："已有 12,580 位用户在使用"
│   └── 点击"拍第一张自拍" → CameraScreen
│
├── 4. CameraScreen（权限请求态）
│   ├── 看到 3 个利益点 + 隐私保证
│   └── 点击"允许使用相机" → 系统权限弹窗 → 取景器
│
├── 5. CameraScreen（取景器态）
│   ├── 对准人脸框 + 光线检测
│   └── 按下快门 → AI 分析（后台）→ 结果态
│
├── 6. CameraScreen（结果态）
│   ├── 看到 ScoreRing(82) + 5 维指标 + 打卡徽章
│   └── 点击"查看详情"
│
└── 7. RecordDetailScreen
    ├── 看到完整分析：照片 + 评分 + 雷达图 + 指标条 + AI 分析 + 使用产品
    └── 返回 → DashboardScreen（内容态，显示首条记录数据）
```

### 关键转化节点
- Onboarding → Auth：肤质选择增加参与感，降低注册流失
- Dashboard 空态 → Camera：单一 CTA 引导，无干扰
- Camera 权限 → 取景器：隐私保证降低拒绝率

---

## 流程 2：日常记录

```
每日打开 App
│
├── 1. DashboardScreen（内容态）
│   ├── 查看昨日评分 Hero Card
│   ├── 看到"今天还没拍照哦~"提醒卡（rose 渐变）
│   └── 点击"去拍照"或 FAB 拍照按钮
│
├── 2. CameraScreen（取景器态）
│   ├── 对准人脸 → 快门 → AI 分析
│   └── 结果态：查看评分 + 指标 + 打卡天数
│       ├── "继续拍照"（重新进入取景器）
│       └── "查看详情" → RecordDetailScreen
│
├── 3. RecordDetailScreen
│   ├── 查看完整分析
│   ├── 底部"使用产品" FlowRow 显示关联产品
│   └── 返回
│
├── 4. ProductScreen（可选：从 Dashboard 快捷操作进入）
│   ├── AM/PM 分组显示今日护肤品
│   ├── 勾选已使用产品（打卡）
│   ├── 查看打卡进度条（2/5 已打卡）
│   └── 如有未记录：提醒横幅"还有 3 个没记录哦~"
│
└── 5. TimelineScreen（Tab 2）
    ├── 查看趋势图（综合/痘痘/毛孔/均匀/水润 切换）
    ├── 查看前后对比卡（VS 徽章 + 分数变化）
    └── 滚动浏览近期记录（带变化 badge ↑+3/↓-1/→0）
```

### 打卡机制
- 每次拍照保存 = 自动打卡
- 连续打卡天数显示在 Dashboard 周历 + Camera 结果页徽章
- 里程碑：7/14/30 天时显示特殊徽章 + 鼓励文案

---

## 流程 3：护肤品管理

```
├── 入口 1: Dashboard 快捷操作"护肤品"卡片
├── 入口 2: Profile 菜单"护肤品管理"
├── 入口 3: Tab 4 分析页（关联产品）
│
├── 1. ProductScreen
│   ├── 搜索栏：输入产品名或品牌 → 实时过滤
│   ├── 分类筛选：全部/洁面/精华/面霜/防晒/化妆水/面膜
│   ├── 打卡进度卡：2/5 已完成 + 进度条
│   ├── AM 早间护肤分组（☀️）
│   │   └── 产品列表 + 打卡复选框
│   ├── PM 晚间护肤分组（🌙）
│   │   └── 产品列表 + 打卡复选框
│   └── 点击 + 添加按钮 → AddProductSheet
│
├── 2. AddProductSheet（BottomSheet）
│   ├── "搜索产品"→ 搜索产品库 [V1]
│   ├── "扫码添加"→ 条码扫描 [V2]
│   └── "手动输入"→ 手动填写产品信息 [V1]
│       ├── 产品名称
│       ├── 品牌（可选）
│       ├── 品类选择（洁面/精华/面霜/防晒/化妆水/面膜/其他）
│       └── 使用时段（AM/PM/Both）
│
└── 3. 打卡 → 数据关联到当日拍照记录
    └── RecordDetailScreen "使用产品" 区域显示当日已打卡产品
```

### 产品数据流
- 添加产品 → 保存到 Room + 标记 synced=false → 后台同步到服务端
- 每日打卡 → 关联 SkinRecord + 当日 timestamp
- 归因分析时：关联产品使用记录 + 皮肤指标变化 → LLM 分析

---

## 流程 4：付费转化

```
├── 触发入口 1: CameraScreen（免费用户已拍 ≥3 次）
│   └── RecordLimitShowcase → "升级 Pro 解锁" → PaywallScreen
│
├── 触发入口 2: RecordDetailScreen（非 PRO 查看详情）
│   └── 看到基础评分 + 模糊雷达图 + LockedFeatureCard
│   └── "★ 升级 Pro 解锁" → PaywallScreen
│
├── 触发入口 3: AttributionReportScreen（非 PRO）
│   └── 看到统计卡 + 模糊产品排名 + LockedFeatureCard
│   └── "★ 升级 Pro 解锁" → PaywallScreen
│
├── 触发入口 4: ShareCardScreen（非 PRO）
│   └── 看到模糊分享卡 + LockedFeatureCard
│   └── "★ 升级 Pro 解锁" → PaywallScreen
│
├── 触发入口 5: ProfileScreen 菜单"会员中心"
│
├── 触发入口 6: SettingsScreen 菜单"会员中心"
│
└── PaywallScreen（模态全屏）
    ├── 皇冠图标 + 动画光晕
    ├── "解锁全部功能" + 5 项权益列表
    ├── 社会证明："12,580 位用户已订阅"
    ├── 方案选择：月度 ¥19.9 / 年度 ¥168（默认选中，省 ¥70.8）
    ├── "14 天免费试用" badge
    ├── "立即订阅" → 支付流程 [V1 Mock / V2 真实支付]
    ├── "恢复购买" → 检查历史订阅
    ├── 信任信号：安全支付 / 随时取消 / 即时生效
    └── Close 按钮 → 关闭返回

    订阅成功后：
    └── 返回触发页面 → 锁定内容解锁 → 完整功能可用
```

### 14 天试用策略
- 新注册用户自动获得 14 天 PRO 试用
- 试用期内所有功能可用
- 试用到期前推送提醒
- 到期后降级为免费版，已有数据保留但高级功能锁定

---

## 流程 5：社交分享

```
├── 入口 1: TimelineScreen 对比卡"分享"文字按钮
├── 入口 2: RecordDetailScreen TopAppBar 分享图标
├── 入口 3: Dashboard 快捷操作"分享对比"卡片
│
└── ShareCardScreen [V1: PRO 专属]
    ├── 1. 加载 Before/After 对比数据
    │   ├── Before: 最早一条记录
    │   └── After: 最新一条记录
    │
    ├── 2. 卡片预览（314dp 宽）
    │   ├── Header: SkinTrack Logo + "Lisa 的蜕变"
    │   ├── 双照片 + VS 徽章 + 各自评分/日期
    │   ├── 结果："+10 分" + "↑ 皮肤在变好哦" + "30 天对比"
    │   └── Footer: "SkinTrack · 记录你的美" + QR 占位
    │
    ├── 3. 模板选择器（3 种样式）
    │   ├── 模板 1: 竖向对比（V1 默认激活）
    │   ├── 模板 2: 圆形对比（V2 占位）
    │   └── 模板 3: 趋势线（V2 占位）
    │
    ├── 4. 分享目标（4 项）
    │   ├── 微信 [V2: 微信 SDK 直接分享]
    │   ├── 微博 [V2]
    │   ├── 小红书 [V2]
    │   └── 更多 → 系统 ShareSheet [V1]
    │
    └── 5. 操作按钮
        ├── "保存图片" → 保存到相册
        └── "分享" → 系统 ShareSheet [V1] / SDK 直分享 [V2]
```

---

## 流程 6：设置与账户

```
├── 入口: ProfileScreen 右上角齿轮图标
│
└── SettingsScreen
    ├── 账户
    │   ├── "编辑资料" → EditProfileScreen
    │   │   ├── 修改头像（点击 → 拍照/相册选择 [V2]）
    │   │   ├── 修改昵称
    │   │   ├── 邮箱（只读，不可修改）
    │   │   ├── 个性签名
    │   │   ├── 肤质类型选择（油性/干性/混合/敏感/中性）
    │   │   ├── 肌肤目标选择（祛痘/收毛孔/提亮/抗老/补水/祛斑，多选）
    │   │   └── "保存" → 更新 UserPreferences + 服务端同步
    │   │
    │   ├── "修改密码" → ChangePasswordScreen
    │   │   ├── 当前密码
    │   │   ├── 新密码（≥6 位）+ 强度指示器（4 段）
    │   │   ├── 确认新密码
    │   │   └── "确认修改" → API 调用 → 成功提示
    │   │
    │   └── "会员中心" → PaywallScreen
    │
    ├── 通知
    │   ├── "打卡提醒" — Toggle 开关 + "每天 08:00"
    │   ├── "周报推送" — Toggle 开关
    │   └── "AI 分析通知" — Toggle 开关
    │
    ├── 数据与隐私
    │   ├── "数据同步" — 显示上次同步时间 + "已同步" badge
    │   ├── "清除缓存" — 显示缓存大小 "23.5 MB" → 确认弹窗
    │   └── "导出数据" → GDPR 合规，导出 JSON/CSV
    │
    ├── 关于
    │   ├── "版本 1.0.0"
    │   ├── "隐私政策" → WebView/外链
    │   └── "服务条款" → WebView/外链
    │
    └── 底部操作
        ├── "退出登录" → 确认 → 清除 session → AuthScreen
        └── "注销账户" → DeleteAccountDialog
            ├── 警告："此操作不可逆，所有数据将被永久删除"
            ├── 输入密码确认
            ├── "取消" → 关闭
            └── "确认注销" → API 删除 → AuthScreen
```

---

## 状态反馈（贯穿所有流程）

### 加载态（18-loading-states.html）
| 页面 | 骨架屏结构 |
|------|-----------|
| Dashboard | Header shimmer + Hero 卡片 + 指标行 + 提醒卡 + 2x2 网格 + 打卡 + 趋势图 |
| RecordDetail | 照片渐变占位(280dp) + 评分卡 shimmer + 雷达图圆 + 指标条 |
| Timeline | 筛选 chips shimmer + 对比卡 + 4 条记录 shimmer（缩略图+文字+评分环） |

### Toast 通知（19-toast-states.html）
| 类型 | 背景色 | 文字色 | 图标 | 示例 | 操作 |
|------|--------|--------|------|------|------|
| Success | `success-600` | `content-inverse` | ✓ 圆 | "照片已保存，分析完成!" | "查看" |
| Error | `error-500` | `content-inverse` | ✗ 圆 | "分析失败，请重试" | "重试" |
| Warning | `warning-500` | `neutral-900` | △ 三角 | "网络连接不稳定" | "知道了" |

Toast 规格：
- 位置：底部，距 bottomNav `spacing.md`(12dp)
- 圆角：`shapes.medium` (12dp)
- 阴影：`shadow-lg`
- 内边距：`spacing.md`(12dp) 上下，`spacing.lg`(16dp) 左右
- 字号：`b2` (14sp/500w)
- 操作文字：`b3` (13sp/700w)
- 动画：`slide-up`，`duration-normal`(300ms)，`easing-decelerate`
- 图标：`dimens.iconSizeSm` (20dp)
