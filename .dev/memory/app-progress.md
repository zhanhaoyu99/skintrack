<!-- Last updated: 2026-03-12 -->
# SkinTrack 开发进度

## 当前状态（新session必读5行）
- **阶段**: MVP Phase 2 → Pre-M3
- **当前里程碑**: Supabase 后端集成（进行中）
- **当前工作**: Supabase Auth/Sync/Storage 客户端集成完成，DB schema 就绪，Roborazzi 快照测试基础设施就绪
- **阻塞问题**: 无（Supabase 项目未创建，代码已支持 Mock/Real 自动切换）
- **下一步**: 创建 Supabase 项目 → 填入 credentials → 端到端测试 → M3 内测

## 技术栈
- **客户端**: Compose Multiplatform (KMP) — composeApp(commonMain/androidMain/iosMain)
- **后端**: Supabase (BaaS — Auth + PostgreSQL + Storage)
- **AI**: 多模态LLM API (GPT-4o/Gemini/Claude) via Ktor
- **测试**: Roborazzi + Robolectric (快照测试)
- **代码仓库**: /Users/zane/Projects/skintrack

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
- [x] Dimens.kt — 组件尺寸 token (buttonHeight/avatarLarge/captureButton/thumbnail/chart/scoreBar)
- [x] Motion.kt — 动画规格 (SHORT/MEDIUM/LONG + EmphasizedDecelerate/Standard)
- [x] 全量硬编码 dp 替换为 dimens token（Auth/Camera/Profile/Compare/TrendChart/Timeline/ScoreBar）
- [x] 渐变激活（primary→AuthScreen按钮, warm→CompareCard, surface→ProfileScreen用户卡片）

### M0: 项目搭建 [■■■■■■■■■■] 100%
- [x] KMP项目初始化 (Gradle + libs.versions.toml)
- [x] 配置 Room + Ktor + Supabase + Koin
- [x] 基础架构搭建 (domain/data/ui/platform分层)
- [x] 主题 (Theme) + 导航 (Voyager)
- [x] 域模型 + Repository接口 + Room实体/DAO
- [x] Koin DI模块 + SkinTrackApplication
- [x] expect/actual 占位 (CameraController, ImageCompressor)
- [x] Gradle构建验证 ✅ BUILD SUCCESSFUL
- [x] 修复 ImageCompressor expect class 缺少默认构造函数
- [x] Supabase DB schema (supabase/migrations/001_initial_schema.sql)

### M1: 核心功能 [■■■■■■■■■■] 100%
- [x] 相机页 + 人脸引导框 (expect/actual)
- [x] 图片压缩 + 本地存储 (ImageStorage expect/actual → filesDir/skin_photos/)
- [x] 拍照保存流程 (CameraViewModel: compress → saveImage → SkinRecord → Room)
- [x] 时间线页 (TimelineScreen + TimelineViewModel + formatters)
- [x] 护肤品记录页 (ProductScreen + ProductViewModel + AddProductSheet + 每日打卡)
- [x] AI图像分析接口 (Mock实现, 4级皮肤状态档位, 预留httpClient供真实API)
- [x] 分析结果本地存储 (Room — record.copy() + save, REPLACE策略)
- [x] 时间线趋势图 (TrendChart — Canvas折线图, overallScore, 升序, >=2点显示)
- [x] 前后对比图 (CompareCard — 自动首尾对比, overallScore 变化, 文本箭头指示)
- [x] 记录详情页 (RecordDetailScreen — 评分总览+指标条形图+AI摘要+当日用品+记录信息)
- [x] 归因分析报告页 (本地统计归因 MVP, 后续替换 LLM)
- [x] 用户认证 (Mock Auth — Room auth_session, 后续替换 Supabase Auth)
- [x] 个人中心页 (ProfileScreen + ProfileViewModel + ProductManageScreen包装)
- [x] UI 质量打磨 — 公共组件提取 (SectionCard/SectionHeader/TrendIndicator/MenuItem/Animation)
- [x] UI 质量打磨 — displayName 扩展统一 (ProductCategoryExt+SkinTypeExt → domain/model)
- [x] UI 质量打磨 — 列表入场动画 (animateListItem: Timeline/Product/Attribution)
- [x] UI 质量打磨 — ScoreBar 动画填充 + CameraScreen AnimatedContent
- [x] UI 质量打磨 — Tab 图标语义修正 (Home→Add, 趋势→记录)
- [x] UI 质量打磨 — 交互反馈 (Card onClick/MenuItem trailing/EmptyContent icon)

### M2: 变现功能 [■■■■■■■■■■] 100%
- [x] 订阅模型 — SubscriptionPlan/UserSubscription/CheckInStreak 域模型
- [x] DB v3 — UserSubscriptionEntity + CheckInStreakEntity (Room, fallbackToDestructiveMigration)
- [x] SubscriptionRepository — 订阅+打卡 CRUD，isPremium 判断（试用期14天+订阅有效期）
- [x] PaymentManager — expect/actual Mock（delay 1s → Success）
- [x] ShareManager — expect/actual Mock（log 输出）
- [x] NotificationManager — expect/actual Mock（scheduleReminder/cancelReminder）
- [x] 功能门控 — FeatureGate 枚举 + CheckFeatureAccess use case
- [x] 门控组件 — LockedFeatureCard（🔒 + 消息 + 升级按钮）
- [x] CameraScreen 门控 — 免费版 ≤3 条记录，超出显示 FeatureGated
- [x] RecordDetailScreen 门控 — 非会员隐藏 AI 详细分析，显示 LockedFeatureCard
- [x] AttributionReportScreen 门控 — 非会员隐藏排行列表，显示 LockedFeatureCard
- [x] PaywallScreen — 品牌区+权益列表+方案选择(月/年)+购买按钮+恢复购买
- [x] PaywallViewModel — selectedPlan/isPurchasing/error/success，Mock purchase flow
- [x] ProfileScreen 会员中心入口 — MenuSection 新增"会员中心"MenuItem
- [x] 分享对比卡片 — ShareCardContent（品牌头+对比照+分数变化+水印）
- [x] ShareCardScreen + ShareCardViewModel — 加载 before/after + 门控检查
- [x] CompareCard 分享按钮 — 标题行加"分享"TextButton → ShareCardScreen
- [x] RecordDetailScreen 分享按钮 — TopAppBar actions 加"分享"TextButton
- [x] 打卡连续 — UpdateCheckInStreak use case（currentStreak/longestStreak/milestone 7/14/30天）
- [x] CameraViewModel 打卡集成 — confirm() 保存后调 onNewRecord()
- [x] CameraScreen 里程碑显示 — Saved 状态显示 milestoneMessage
- [x] ProfileScreen 打卡统计 — StatsCard 加"连续打卡 N天"
- [x] ProfileScreen 打卡提醒菜单 — MenuSection 加"打卡提醒"MenuItem
- [x] BUILD SUCCESSFUL ✅ 零错误

### M2.5: Supabase 后端集成 [■■■■■■□□□□] 60%
- [x] Supabase DB schema — 6 表 + RLS + Storage bucket + auto-profile trigger
- [x] SupabaseAuthRepository — 邮箱注册/登录/登出 + 中文错误映射
- [x] SupabaseProvider — BuildConfig 读取 credentials + isConfigured 自动切换
- [x] SupabaseSyncService — uploadSkinRecords/loadSkinRecords/uploadProducts/uploadImage
- [x] SkinRecordRepositoryImpl — syncToRemote() + pullFromRemote() 实现
- [x] ProductRepositoryImpl — syncToRemote() 实现
- [x] CameraViewModel — 真实 userId + Storage 图片上传 + 后端同步
- [x] AppModule DI — Supabase client + SyncService + 条件绑定 Auth/Sync
- [x] AiAnalysisService — 4 级皮肤状态档位 Mock（EXCELLENT/GOOD/MODERATE/CONCERN）
- [x] Roborazzi 快照测试基础设施 — SnapshotTestBase + 12 个测试用例
- [ ] 创建 Supabase 项目 + 填入 credentials
- [ ] 端到端联调测试
- [ ] 离线同步策略（WorkManager 定期上传）
- [ ] 用户资料同步（ProfileScreen ↔ Supabase profiles）

### M3: 测试上线 [□□□□□□□□□□] 0%
- [ ] 内测 20-30人
- [ ] Bug修复 + 优化
- [ ] Android发布
- [ ] iOS提交App Store审核

## 页面完成度
| # | 页面 | CMP(common) | expect/actual | Supabase | 状态 |
|---|------|-------------|---------------|----------|------|
| 1 | 拍照页 CameraScreen | ✅ | 相机+存储 | 图片上传✅ | Supabase 同步就绪 |
| 2 | 时间线 TimelineScreen | ✅ | — | 查询数据 | 分享入口完成 |
| 3 | 护肤品记录 ProductScreen | ✅ | — | CRUD✅ | Supabase 同步就绪 |
| 4 | 记录详情 RecordDetailScreen | ✅ | — | — | M2门控+分享完成 |
| 5 | 归因报告 AttributionReportScreen | ✅ | — | 查询+LLM | M2门控完成 |
| 6 | 个人中心 ProfileScreen | ✅ | — | 用户信息 | M2打卡+会员入口完成 |
| 7 | 登录/注册 AuthScreen | ✅ | — | Auth✅ | Supabase Auth 就绪 |
| 8 | 会员订阅 PaywallScreen | ✅ | PaymentManager | — | Mock 完成 |
| 9 | 分享卡片 ShareCardScreen | ✅ | ShareManager | — | Mock 完成 |

## 快照测试
| 测试文件 | 用例数 | 状态 |
|---------|--------|------|
| ComponentSnapshotTest | 6 | ✅ 通过 |
| AuthScreenSnapshotTest | 3 | ✅ 通过 |
| PaywallScreenSnapshotTest | 3 | ✅ 通过 |

## 自绘/共享组件
| 组件 | 位置 | 用途 | 状态 |
|------|------|------|------|
| TrendChart | screen/timeline | 折线趋势图（皮肤指标变化） | ✅ 完成 |
| ScoreBar | component | 水平评分条（带动画填充） | ✅ 完成 |
| CompareCard | screen/timeline | 前后对比卡片 | ✅ 完成（+分享按钮） |
| SectionCard | component | 全宽内容卡片 | ✅ 完成 |
| SectionHeader | component | 区块标题 | ✅ 完成 |
| TrendIndicator | component | 趋势指示器 ↑↓→ | ✅ 完成 |
| MenuItem | component | 菜单列表项 | ✅ 完成 |
| animateListItem | component | 列表入场动画 | ✅ 完成 |
| LockedFeatureCard | component | 付费门控提示卡片 | ✅ 完成 |
| ShareCardContent | screen/share | 分享对比卡片内容 | ✅ 完成 |
| RadarChart | — | 雷达图（多维度皮肤评分） | 待开始 |

## expect/actual 模块
| 模块 | Android | iOS | 状态 |
|------|---------|-----|------|
| CameraController | CameraX | AVFoundation | Android完成 |
| ImageCompressor | Bitmap压缩 | UIImage压缩 | Android完成 |
| ImageStorage | filesDir写入 | — | Android完成 |
| PaymentManager | Mock(delay) | — | Mock完成 |
| ShareManager | Mock(log) | — | Mock完成 |
| NotificationManager | Mock(log) | — | Mock完成 |

## Supabase 后端
| 组件 | 文件 | 状态 |
|------|------|------|
| DB Schema | supabase/migrations/001_initial_schema.sql | ✅ 就绪 |
| SupabaseProvider | data/remote/SupabaseProvider.kt | ✅ BuildConfig + 自动切换 |
| SupabaseAuthRepository | data/repository/SupabaseAuthRepository.kt | ✅ 就绪 |
| SupabaseSyncService | data/remote/SupabaseSyncService.kt | ✅ 就绪 |
| DTO | data/remote/dto/SupabaseDto.kt | ✅ 6 个 DTO |
| Storage | skin-photos bucket + RLS | ✅ schema 就绪 |

## 商业模型
| 功能 | 免费版（试用期后） | 付费版 / 试用期内 |
|------|------------------|-----------------|
| 拍照记录 | 最多 3 条 | 无限 |
| 基础评分 | ✅ | ✅ |
| AI 详细分析 | 🔒 | ✅ |
| 归因分析报告 | 🔒 | ✅ |
| 分享对比卡片 | 🔒 | ✅ |

- 试用期：注册后 14 天全功能免费
- 月度会员：¥19.9/月
- 年度会员：¥168/年（省¥70.8）
