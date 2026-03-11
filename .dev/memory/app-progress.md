<!-- Last updated: 2026-03-11 -->
# SkinTrack 开发进度

## 当前状态（新session必读5行）
- **阶段**: MVP Phase 1
- **当前里程碑**: M1 - 核心功能（进行中）
- **当前工作**: 记录详情页 RecordDetailScreen 已完成 — 时间线点击→push详情页→评分+指标+AI摘要+当日用品
- **阻塞问题**: 无（Supabase项目待创建，用placeholder）
- **下一步**: 归因分析报告页 → 用户认证 → 个人中心

## 技术栈
- **客户端**: Compose Multiplatform (KMP) — composeApp(commonMain/androidMain/iosMain)
- **后端**: Supabase (BaaS — Auth + PostgreSQL + Storage)
- **AI**: 多模态LLM API (GPT-4o/Gemini/Claude) via Ktor
- **代码仓库**: H:\projects\skintrack

## 里程碑规划
### M0.5: 设计系统 [■■■■■■■■■■] 100%
- [x] Color.kt — 品牌色阶 + 功能色 + 皮肤指标色 + 相机色
- [x] ExtendedColors.kt — SkinMetricColors / FunctionalColors / CameraColors + CompositionLocal
- [x] Spacing.kt — xs/sm/md/lg/xl/xxl/section tokens
- [x] Shape.kt — Material 3 Shapes + FullRoundedShape
- [x] Gradient.kt — primary / scoreRing / warm / surface gradients
- [x] Type.kt — Typography (系统字体 + 自定义 weight/size)
- [x] Theme.kt — 重写：ColorScheme + ExtendedColors + Gradients + Spacing + Typography + Shapes
- [x] 改造 FaceGuideOverlay / CameraScreen / LoadingContent 使用设计 token
- [x] DESIGN_SYSTEM.md 文档

### M0: 项目搭建 [■■■■■■■■■■] 95%
- [x] KMP项目初始化 (Gradle + libs.versions.toml)
- [x] 配置 Room + Ktor + Supabase + Koin
- [x] 基础架构搭建 (domain/data/ui/platform分层)
- [x] 主题 (Theme) + 导航 (Voyager)
- [x] 域模型 + Repository接口 + Room实体/DAO
- [x] Koin DI模块 + SkinTrackApplication
- [x] expect/actual 占位 (CameraController, ImageCompressor)
- [x] Gradle构建验证 ✅ BUILD SUCCESSFUL
- [x] 修复 ImageCompressor expect class 缺少默认构造函数
- [ ] Supabase项目创建 + 数据库表初始化

### M1: 核心功能 [■■■■■■■□□□] 70%
- [x] 相机页 + 人脸引导框 (expect/actual)
- [x] 图片压缩 + 本地存储 (ImageStorage expect/actual → filesDir/skin_photos/)
- [x] 拍照保存流程 (CameraViewModel: compress → saveImage → SkinRecord → Room)
- [x] 时间线页 (TimelineScreen + TimelineViewModel + formatters)
- [x] 护肤品记录页 (ProductScreen + ProductViewModel + AddProductSheet + 每日打卡)
- [x] AI图像分析接口 (Mock实现, 1.5s延迟 + 随机评分, 预留httpClient供真实API)
- [x] 分析结果本地存储 (Room — record.copy() + save, REPLACE策略)
- [x] 时间线趋势图 (TrendChart — Canvas折线图, overallScore, 升序, >=2点显示)
- [x] 前后对比图 (CompareCard — 自动首尾对比, overallScore 变化, 文本箭头指示)
- [x] 记录详情页 (RecordDetailScreen — 评分总览+指标条形图+AI摘要+当日用品+记录信息)
- [ ] 归因分析报告页 (LLM归因)
- [ ] 用户认证 (Supabase Auth)
- [ ] 个人中心页

### M2: 变现功能 [□□□□□□□□□□] 0%
- [ ] 会员订阅 — Android微信支付 (expect/actual)
- [ ] 会员订阅 — iOS StoreKit 2 (expect/actual)
- [ ] 免费/付费功能门控
- [ ] 分享对比卡片生成
- [ ] 打卡提醒 + 激励机制

### M3: 测试上线 [□□□□□□□□□□] 0%
- [ ] 内测 20-30人
- [ ] Bug修复 + 优化
- [ ] Android发布
- [ ] iOS提交App Store审核

## 页面完成度
| # | 页面 | CMP(common) | expect/actual | Supabase | 状态 |
|---|------|-------------|---------------|----------|------|
| 1 | 拍照页 CameraScreen | ✅ | 相机+存储 | 图片上传 | 基础完成 |
| 2 | 时间线 TimelineScreen | ✅ | — | 查询数据 | 基础完成 |
| 3 | 护肤品记录 ProductScreen | ✅ | — | CRUD | 基础完成 |
| 4 | 记录详情 RecordDetailScreen | ✅ | — | — | 基础完成 |
| 5 | 归因报告 ReportScreen | — | — | 查询+LLM | 待开始 |
| 6 | 个人中心 ProfileScreen | — | — | 用户信息 | 待开始 |
| 7 | 登录/注册 AuthScreen | — | — | Supabase Auth | 待开始 |

## 自绘图表组件
| 组件 | 用途 | 状态 |
|------|------|------|
| TrendChart | 折线趋势图（皮肤指标变化） | ✅ 完成 |
| ScoreBar | 水平评分条（指标详情） | ✅ 完成 |
| RadarChart | 雷达图（多维度皮肤评分） | 待开始 |
| CompareCard | 前后对比卡片 | ✅ 完成 |

## expect/actual 模块
| 模块 | Android | iOS | 状态 |
|------|---------|-----|------|
| CameraController | CameraX | AVFoundation | Android完成 |
| ImageCompressor | Bitmap压缩 | UIImage压缩 | Android完成 |
| ImageStorage | filesDir写入 | — | Android完成 |
| PaymentManager | 微信支付SDK | StoreKit 2 | 待开始 |
| NotificationManager | FCM | APNs | 待开始 |
