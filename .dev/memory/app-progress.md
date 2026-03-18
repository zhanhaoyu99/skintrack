<!-- Last updated: 2026-03-18 -->
# SkinTrack 开发进度

## 当前状态（新session必读5行）
- **阶段**: M3 测试上线 95% 进行中
- **当前里程碑**: M3 视觉还原审查（Dashboard/Timeline 对比HTML设计稿修复中）
- **当前工作**: RecordDetail+Attribution 设计稿像素对齐 — RecordDetail(评分卡标题改人性化文案19sp/13sp/18dp间距, 浮动卡overlap36dp, 照片按钮42dp/0.2alpha, 分数标签14sp/18dp*7dp, 雷达图"综合"→"整体", 指标行10dp间距+divider+13sp标签+15sp分数+36dp变化宽度, AI卡圆角extraLarge, 高亮区12dp*10dp填充, 产品芯片13sp/14dp*7dp), Attribution(对比区8dp上+20dp下+6dp标签间距, delta文字18sp, 影响分18sp+10sp标签, 产品名15sp, 统计卡14dp垂直, 建议图标渐变bg, 产品图标渐变bg, 排名1渐变bg), ScoreRing(compact模式24sp/ExtraBold/mini模式分离)
- **阻塞问题**: 部署服务器到国内云（需要云服务器），真实支付/FCM为V2范围
- **下一步**: 部署到国内云 → 服务端联调 → 内测20-30人 → Bug修复 → 发布
- **产品文档**: `.dev/product/README.md`（索引）— 含全部 16 页设计规格

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
- [x] Motion.kt — 动画规格 (SHORT/MEDIUM/LONG/EXTRA_LONG + EmphasizedDecelerate/EmphasizedAccelerate/Standard + BouncySpring/GentleSpring)
- [x] Animation.kt — 可复用动画 Modifier (animateListItem/animateCardEntrance/animateFadeIn/animatePulse)
- [x] 动画效果全面提升 — AuthScreen(入场+AnimatedContent)/ProfileScreen(卡片入场+计数动画)/HomeScreen(FAB+Tab滑动)/FaceGuideOverlay(呼吸脉冲)/TrendChart(线条绘制)/PaywallScreen(入场+选中过渡)/CameraScreen(Saved弹性)/DashboardScreen(Empty淡入)/RecordDetailScreen(列表入场)/ShareCardScreen(卡片入场)/AttributionReportScreen(卡片入场)/ProductScreen(颜色过渡)/CompareCard(卡片入场)/EmptyContent+LoadingContent(淡入)
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

### M2.5: 后端集成 [■■■■■■■■■■] 100%
- [x] Supabase DB schema — 6 表 + RLS + Storage bucket + auto-profile trigger
- [x] SupabaseAuthRepository — 邮箱注册/登录/登出 + 中文错误映射
- [x] SupabaseProvider — BuildConfig 读取 credentials + isConfigured 自动切换
- [x] SupabaseSyncService — uploadSkinRecords/loadSkinRecords/uploadProducts/uploadImage
- [x] SkinRecordRepositoryImpl — syncToRemote() + pullFromRemote() 实现
- [x] ProductRepositoryImpl — syncToRemote() + pullFromRemote() 实现
- [x] CameraViewModel — 真实 userId + Storage 图片上传 + 后端同步
- [x] AppModule DI — Supabase client + SyncService + 条件绑定 Auth/Sync
- [x] AiAnalysisService — 4 级皮肤状态档位 Mock（EXCELLENT/GOOD/MODERATE/CONCERN）
- [x] AiAnalysisService — LLM 归因分析 Mock（generateAttributionReport + prompt 模板）
- [x] AttributionReport 数据模型 — AI 摘要 + 趋势 + 产品排行 + 建议
- [x] AttributionReportViewModel — 注入 AiAnalysisService，先显示本地统计再异步 AI 分析，24h 缓存
- [x] AttributionReportScreen — AI 洞察卡片（加载/错误/结果三态），AI 产品评估行
- [x] Roborazzi 快照测试基础设施 — SnapshotTestBase + 24 个测试用例
- [x] SyncManager — push/pull 协调，登录和启动时自动全量同步
- [x] SkincareProductEntity userId 字段修复
- [x] 全量 ViewModel userId 修复（Timeline/Profile/Product 使用真实 userId）
- [x] RadarChart 雷达图组件 — Canvas 六边形，动画，多维度皮肤指标可视化
- [x] ScoreRing 环形评分组件 — sweepGradient 进度环 + 动画
- [x] RecordDetailScreen 商业级重构 — ScoreRing + RadarChart + ScoreBar 三级展示
- [x] TimelineScreen 质感提升 — 迷你 ScoreRing 替代纯文本分数
- [x] AuthScreen 暗色模式修复 — Surface 包裹
- [x] 全量 userId 硬编码消除 — CheckFeatureAccess/UpdateCheckInStreak 内部解析 userId
- [x] ShareCardViewModel/AttributionReportViewModel 注入 AuthRepository
- [x] ProductScreen 质感提升 — SectionHeader + 打卡卡片背景高亮 + 品牌/分类信息
- [x] CameraScreen Saved 状态 — ScoreRing 替代纯文本评分
- [x] ProductScreen + AttributionReportScreen 快照测试
- [x] RemoteSyncService 接口抽取 — 后端无关抽象（ADR-009）
- [x] CameraViewModel 解耦 — 图片上传移入 SkinRecordRepository
- [x] 创建 Ktor Server 后端（替代 Supabase）— Exposed + PostgreSQL + JWT + BCrypt
- [x] 实现 KtorSyncService + KtorAuthRepository — 客户端 Ktor 后端集成
- [x] DI 三模式切换 — KtorServer > Supabase > Mock 自动降级
- [x] 增量同步 — lastSyncTimestamp + since 参数，服务端按 updatedAt 过滤
- [x] 冲突解决 — Last-Write-Wins 策略（比较 updatedAt），Entity 添加 updatedAt 字段
- [x] 离线队列 — SyncQueueEntity + SyncQueueDao 持久化待同步操作
- [x] 同步状态展示 — SyncState(Idle/Syncing/Success/Error) + Dashboard 状态指示器
- [x] 网络状态监听 — NetworkMonitor expect/actual + 网络恢复自动同步
- [x] AI 分析重试机制 — 指数退避 (1s→2s→4s, 最多 3 次)，失败不阻塞保存
- [x] 待分析记录后台重试 — SyncManager.retryPendingAnalysis()，syncAll() 时自动触发
- [x] Timeline/Product 分页加载 — DAO 分页查询 + ViewModel loadMore + LazyColumn 滚动触发
- [x] Room 数据库索引优化 — SkinRecordEntity(userId+recordedAt) + SkincareProductEntity(userId)
- [x] 产品搜索筛选 — SearchBar + CategoryFilter chips + DAO searchProducts + ViewModel 实时过滤
- [x] 产品未记录提醒 — ProductScreen 顶部 warning 卡片（今日还有 N 个产品未记录）
- [x] 推送通知基础 — UsersTable fcm_token + POST /api/user/device-token + NotificationManager.registerDeviceToken
- [x] Dashboard 未记录提醒 — 今日未拍照时显示 secondaryContainer 卡片 + 拍照按钮
- [ ] 部署到国内云 → 端到端联调

### M2.7: UI 商业级重构 [■■■■■■■■■■] 100%
- [x] 阶段1: 导航架构重构 — HomeScreen Tab 改为首页/记录/我的 + FAB 拍照入口
- [x] 阶段1: DashboardScreen 新建 — Hero 卡片(ScoreRing+渐变) + 快捷操作 + 打卡 + 趋势图
- [x] 阶段1: CameraScreen 改为全屏 Voyager Screen + TopAppBar 返回按钮
- [x] 阶段2: TimelineScreen — TopAppBar + 空状态 CTA + Scaffold 统一结构
- [x] 阶段2: EmptyContent 增强 — actionLabel + onAction 按钮
- [x] 阶段3: ProfileScreen — 渐变用户卡 + 统计数据加粗 + VerticalDivider
- [x] 阶段3: MenuItem 升级 — leading icon + subtitle + 默认 → 箭头
- [x] 阶段4: CameraScreen 权限页增强 — 说明文案 + 大号按钮
- [x] 阶段4: CameraScreen Saved 状态 — ScoreRing + 双按钮(继续拍照/查看详情)
- [x] 阶段4: FaceGuideOverlay 增加 "将面部置于框内" 提示文字
- [x] 阶段5: SectionHeader trailing action + Dashboard animateListItem
- [x] 快照测试 42 个全部通过 (light+dark × 所有页面状态)

### M2.9: 设计稿对齐 [■■■■■■■■■■] 100%
> 产品文档: `.dev/product/` | 设计稿: `.figma-mockups/`

#### Phase 1: 导航架构 ✅
- [x] HomeScreen 3 Tab → 5 Tab (首页/记录/拍照FAB/分析/我的)
- [x] AttributionReportScreen 提升为 Tab 4 (AttributionReportContent composable)
- [x] FAB 圆形居中拍照按钮 + "拍照" 标签
- [x] Tab 图标: Home/List/Star/Person + AutoMirrored

#### Phase 2: 引导+认证 ✅
- [x] OnboardingScreen 3页→4页 — 新增肤质选择页 (油性/干性/混合/敏感/中性)
- [x] 肤质数据模型 + UserPreferencesEntity.skinType 持久化
- [x] AuthScreen — Segmented Control pill 样式切换
- [x] AuthScreen 注册 — 昵称字段 + 确认密码 + 服务条款
- [x] AuthScreen 登录 — 微信/Apple/手机 社交登录占位 + 分隔线
- [x] AuthScreen — 信任标记 (隐私保护/100k+用户/4.8评分)
- [x] Brand Logo 64dp 圆角方块 + 品牌色

#### Phase 3: 首页对齐 ✅
- [x] Dashboard Header — 问候+通知铃铛(带红点)+用户头像
- [x] Dashboard — Hero Card (ScoreRing 96dp + 渐变背景 + 趋势 pill)
- [x] Dashboard — 迷你指标行 (5 卡片 + mini-bar 样式)
- [x] Dashboard — 拍照提醒卡 Apricot 色系渐变
- [x] Dashboard — 2x2 快捷操作网格 (卡片样式 + 副标题)
- [x] Dashboard — 护肤贴士卡 (Lavender 色系 mock)
- [x] Dashboard — 打卡里程碑徽章 (渐变条 + 文案)
- [x] Dashboard — 趋势图周期选择 Chips (7天/30天/90天)
- [x] Dashboard 空状态 — 3步引导 + 社会证明 + CTA

#### Phase 4: 记录页对齐 ✅
- [x] Timeline CompareCard — VS 圆形徽章 + 三色渐变背景
- [x] Timeline 记录卡片 — 变化标签 badge (↑+N/→0/↓-N)
- [x] TrendChart — 指标选择 chips (总评分/痘痘/毛孔/均匀度)
- [x] RecordDetail — 全宽照片预览区 (260dp) + 悬浮 TopBar
- [x] RecordDetail — 悬浮评分卡片 (-40dp 浮层)
- [x] RecordDetail — 底部评分标签 (模糊背景 "82分·良好")
- [x] RecordDetail — 指标条形图渐变色 + 变化值列
- [x] RecordDetail — 当日产品 FlowRow Chips

#### Phase 5: 个人中心+产品 ✅
- [x] Profile 统计行 — 3项→4项 (新增最新评分)
- [x] Profile — 肌肤目标标签区 (FlowRow pills + 添加)
- [x] Profile 菜单 — 图标圆角底色 + 全部副标题
- [x] ProductScreen — AM/PM 早晚分组
- [x] ProductScreen — UsagePeriod 数据模型 (Entity/DTO/Mapper 全栈)
- [x] ProductScreen — 品类标签色 (洁面蓝/精华紫/面霜绿/防晒橙)
- [x] ProductScreen — 产品图标彩色圆角底色
- [x] AddProductSheet — 使用时段选择器 (AM/PM/Both)

#### Phase 6: 归因+付费墙+分享 ✅
- [x] Attribution — 概览迷你趋势图 + 3统计卡片 (评分变化/使用产品/分析天数)
- [x] Attribution — 排名徽章色 (金银铜)
- [x] Attribution — 产品排行添加使用天数
- [x] Attribution — 改善建议区块 (动态生成 AI 建议)
- [x] Paywall — 皇冠图标 + 试用提示 pill
- [x] Paywall — 年度方案省钱标签 + 原价删除线
- [x] ShareCard — 品牌头 (Logo + 渐变) + VS 徽章
- [x] ShareCard — 模板选择器 (3 种样式, V1 首选)
- [x] ShareCard — 分享目标 (微信/微博/小红书/更多)
- [x] ShareCard — 保存图片 + 分享按钮

#### Phase 7: 门控升级 ✅
- [x] LockedFeatureCard — 添加标签 pills + 试用提示
- [x] LockedFeatureCard — Apricot 渐变升级按钮
- [x] 拍照限额门控 — 改为进步展示页面 (RecordLimitShowcase)
- [x] RecordDetail 门控 — tags: AI深度分析/雷达图/趋势预测
- [x] Attribution 门控 — tags: AI洞察/排行榜/改善建议

#### Phase 8: 设置页 ✅
- [x] Settings — 周报通知开关 (weeklyReportEnabled)
- [x] Settings — 菜单图标圆角底色 + 副标题
- [x] Settings — 隐私政策/服务条款链接占位
- [x] Settings — 编辑资料页面实现 (EditProfileScreen + EditProfileViewModel)
- [x] Settings — 修改密码页面实现 (ChangePasswordScreen + ChangePasswordViewModel)
- [x] Settings — 导出数据功能实现 (GDPR, exportUserData + 确认弹窗)
- [x] Settings — 退出登录按钮 + 注销账户链接 + DeleteAccountDialog 接入
- [x] Settings — 分组标题 3dp primary 竖条装饰
- [x] Settings — AI 分析通知开关
- [x] Settings — 会员管理菜单项 → PaywallScreen
- [x] Settings — 数据同步菜单项

### M3: 测试上线 [■■■■■■■■■□] 90%
- [x] 接入真实 LLM API（服务端多 Provider: OpenAI/Gemini/Claude + 客户端自动降级）
- [x] 服务端 AI 路由 (POST /api/ai/analyze-skin, /api/ai/attribution-report + JWT + 限流)
- [x] 服务端 Profile 更新 (PUT /api/user/profile + displayName + skinType)
- [x] Docker 部署配置 (Dockerfile + docker-compose + Nginx + .env.example + deploy.sh)
- [x] 服务端健康检查端点 (GET /health)
- [x] App.kt 生产认证流程 (Onboarding → Auth → Home, 退出登录 → Auth)
- [x] 注册时保存 displayName + 服务端同步 (offline-first)
- [x] Release 构建配置 (R8/ProGuard + 签名配置占位)
- [x] 设置页快照测试 (SettingsSnapshotTest light/dark)
- [x] 密码相关 API 全栈对齐验证 (change/reset/verify-reset)
- [x] 设计 token 对齐 (buttonHeight 52dp, inputHeight 50dp, shapes.medium 14dp)
- [x] 骨架屏加载态 (Dashboard/Timeline/RecordDetail shimmer skeleton)
- [x] 暗色模式修复 (Rose/Lavender 色系集中定义, Dashboard/Onboarding/Paywall 暗色适配)
- [x] .env.example 补充 AI_PROVIDER/AI_MODEL 变量
- [x] 真机 E2E 验证 (Dashboard/Timeline/Analysis/Profile/Camera/Paywall 全部正常)
- [x] 快照测试 42 个全部通过 (token 变更后重新录制)
- [x] 设计稿视觉对齐二轮 — Profile渐变Header+浮层统计卡+肌肤目标+方角图标+Footer / Dashboard周历打卡+Hero日期+趋势图Tooltip / Auth装饰性背景+Logo光晕 / Paywall社会证明+皇冠光晕
- [x] 设计稿视觉对齐三轮 — 底部导航栏(去指示器pill+灰色inactive+FAB突出52dp) / Dashboard Header(分行问候+大字名+Bell圆形容器+红点+首字母头像) / Dashboard空状态(装饰性渐变背景+渐变插画+横排彩色步骤+社会证明高亮)
- [x] Release APK 构建验证 (12MB, R8+资源缩减通过, debug签名 fallback)
- [x] App 图标升级 (品牌矢量 adaptive icon: Mint背景+叶子前景)
- [x] docker-compose AI 环境变量补全 (AI_PROVIDER/AI_API_KEY/AI_MODEL/AI_MAX_DAILY_ANALYSES)
- [x] 真机 E2E 二次验证 (Dashboard/Timeline/Analysis/Profile/Camera/Settings 6页面全部正常)
- [x] ProGuard 规则补全 (OkHttp/Napier/CameraX keep rules)
- [x] 服务端安全加固 (AppConfig 敏感字段 toString 脱敏)
- [x] Dockerfile JVM 调优 (G1GC + Xmx512m + StringDeduplication)
- [x] SyncManager lastSyncTimestamp 持久化 (UserPreferences 存储，重启不丢失)
- [x] 全面代码审查+安全加固 — SyncManager Mutex 并发保护 / AuthViewModel 确认密码校验 / 服务端 AI 限流 ConcurrentHashMap+AtomicInteger / 密码重置码日志脱敏+时间安全比较 / ProductRepository Last-Write-Wins
- [x] 部署配置 P1 修复 — Nginx 安全头 location 块覆盖+server_tokens off / deploy.sh 回滚机制+pre-flight+.env加载 / 版本号 gradle.properties 统一管理 / docker-compose 内存限制 768M
- [x] CameraScreen 结果页增强 — "继续拍照"/"查看详情" 双按钮 + 5 维迷你指标行 + 打卡里程碑 pill
- [x] TimelineScreen 时间筛选 — 全部/本周/本月/3个月 FilterChip + ViewModel 按时间过滤
- [x] ProfileScreen 动态肤质标签 — 从 UserPreferences 读取 skinType 显示 (油性/干性/混合/敏感/中性)
- [x] ProductScreen AM/PM 分组 — 早间/晚间护肤分组 + 品类彩色标签 + 打卡进度条 + 未记录提醒
- [x] RecordDetailScreen 照片预览 — 280dp 全宽照片区 + 悬浮评分卡片(-40dp交叠) + 底部评分标签
- [x] CameraViewModel println→Napier 日志规范化
- [x] ProfileScreen 菜单图标修复 — 瓶子/图表/云 emoji 替代通用Star + 图标tint彩色化
- [x] ForgotPasswordScreen "返回登录" 导航修复 — clearFocus → navigator.pop()
- [x] 真机 E2E 三次验证 (Dashboard/Timeline/Analysis/Profile/Camera/Settings 全部正常, 菜单图标对齐设计稿)
- [x] 统一 Toast/Snackbar 组件 — AppSnackbar(图标圆+类型色) + AppSnackbarHost + showTyped 扩展
- [x] CameraScreen Toast 反馈 — 拍照成功/AI分析完成 SnackbarMessage
- [x] ProductScreen Toast 反馈 — 产品添加/删除成功 SnackbarMessage
- [x] ProductScreen 搜索栏 + 分类筛选 — SearchBar 实时过滤 + CategoryFilter chips (全部/洁面/精华/面霜/防晒/化妆水/面膜/其他)
- [x] TrendChart 指标选择 chips — 总评分/痘痘/毛孔/均匀度/水润 多维度切换
- [x] RecordDetail 评分变化 pill — "↑ +N 较上次" 成功/错误色 pill 浮层卡片
- [x] Auth 密码校验 >=8 → >=6 对齐产品规格 (AuthScreen + ForgotPasswordScreen)
- [x] Profile 用户卡片点击 → EditProfileScreen 导航
- [x] 快照测试 42 个全部通过 + Release APK 构建通过
- [x] 视觉还原大修 — 6组并行agent逐页对齐HTML设计稿:
  - Dashboard: Hero Card 28dp圆角+decorative circles / ScoreRing 84dp / 迷你指标行重排(值在前) / 护肤贴士卡水平布局 / 打卡周历+里程碑徽章
  - Auth+Onboarding: Logo 72dp+hero渐变 / 社交登录矩形卡片 / 信任标记内联 / 引导页260dp多层插画+渐变按钮 / 肤质选择44dp圆角方块
  - Timeline+RecordDetail: 趋势卡片内嵌metric chips / 记录卡56dp缩略图+变化badge / 空状态3步引导 / 详情页浮层卡片Row布局+评分pill / 渐变指标条 / AI卡片gradient bg / FlowRow产品chips
  - Profile+Settings: VIP pill形状 / 统计卡extraLarge圆角 / 目标pill颜色准确 / 菜单图标RoundedCorner(10dp) / Settings副标题(缓存大小/同步时间) / EditProfile Rose头像+相机覆盖
  - Product+Camera: 搜索栏pill形 / 进度卡Mint50 / AM/PM图标头 / 打卡指示器圆环 / 权限页3利益点+渐变按钮 / 打卡徽章Apricot渐变
  - Attribution+Paywall+Share+Locked: 金银铜排名徽章 / 80dp金色皇冠圆 / 社会证明头像 / 省钱浮动badge / 56dp Apricot锁图标 / 品牌头Row布局
- [x] 视觉还原二次对齐 — 逐页对比HTML设计稿+快照, 修复21文件~2000行:
  - Dashboard: 趋势图SectionCard包裹+X轴日期 / 迷你指标卡14dp圆角 / 趋势周期自定义pill替代FilterChip / 护肤品动态数量
  - Attribution: Before/After对比圆圈(70→82) / 概览卡片3统计子卡 / AI洞察卡片(渐变背景) / 排行金银铜编号徽章+品类图标 / 改善建议区块
  - Paywall快照: Hero皇冠+变美潜力标题 / 试用badge+社交证明 / 5权益双行+云端同步 / 浮动省钱标签+原价删除线 / 信任信号
  - RecordDetail: 评分卡Row布局+74dp ScoreRing / 百分位文案 / 指标变化值计算 / ScoreRing小尺寸自适应
  - Profile: emoji菜单图标(🧴📊👑) / 总记录数值onSurface色 / 用户卡右箭头
  - Auth: 输入框leadingIcon / 创建账号文案 / 信任标记emoji(🛡️✅)
  - Camera: 权限拒绝Close图标 / "下次再说"primary色
  - Product: 56dp打卡进度圆环 / 产品频率标签 / 提醒横幅加粗变色
  - Settings: emoji图标(📊💬📥🛡️📄) / 数据同步"已同步"badge / 退出登录图标
  - LockedFeatureCard: 👑皇冠+title/subtitle参数 / tags标签pills
  - Timeline: 区块排列顺序调整(对比卡→趋势图) / 标题"肌肤记录"
- [x] 视觉还原三次对齐 — 5组并行agent全面审查HTML设计稿+快照+代码三方比对:
  - ScoreRing: 数字颜色primary→onSurface / Dashboard传scoreColor=White
  - RecordDetail: 雷达图标签"黑头"→"水润"+顺序修正 / AI卡暗色模式渐变 / 快照测试完全重写(渐变条+变化值)
  - Attribution: StatSubCard单色→渐变背景 / AI洞察暗色渐变 / Rank1暗色适配 / 快照同步
  - Paywall: TopAppBar Close图标+去标题 / 购买按钮高度52dp
  - Camera: 快照测试"下次再说"颜色修正(onSurfaceVariant→primary)
  - Dashboard: 快照迷你指标圆角+趋势图trailing+周期选择器pill
  - Timeline: 记录卡内padding 8→12dp / 快照测试完全重写(结构性偏差10处)
  - Profile: Footer版本号+letterSpacing / 菜单SectionCard紧凑padding / 快照emoji图标同步
  - Settings: 菜单紧凑padding / 快照emoji图标同步
  - SectionCard: 圆角12→20dp+新增contentPadding参数 / MenuItem: 箭头20dp+outline色
  - LockedFeatureCard: 皇冠金色渐变背景+卡片暖色渐变+标签阴影样式
  - Product: 快照ProgressRing布局+提醒文本加粗颜色+频率标签
- [ ] 真实支付集成 (微信支付 + StoreKit 2)
- [ ] Firebase FCM 推送
- [ ] 部署 Ktor Server 到国内云
- [ ] 端到端联调
- [ ] 内测 20-30人
- [ ] Bug修复 + 优化
- [ ] Android发布
- [ ] iOS提交App Store审核

## 页面完成度（功能 + 设计对齐）
| # | 页面 | 功能 | 设计对齐 | 产品文档 |
|---|------|------|---------|---------|
| 1 | HomeScreen 导航 | ✅ 5Tab+FAB | ✅ 对齐 | navigation.md |
| 2 | OnboardingScreen | ✅ 4页+肤质选择 | ✅ 对齐 | screen-onboarding.md |
| 3 | AuthScreen | ✅ Segmented+昵称+社交 | ✅ 对齐 | screen-auth.md |
| 4 | DashboardScreen | ✅ 全功能 | ✅ 对齐 | screen-dashboard.md |
| 5 | TimelineScreen | ✅ VS徽章+变化badge | ✅ 对齐 | screen-timeline.md |
| 6 | CameraScreen | ✅ 进步展示门控 | ✅ 对齐 | screen-camera.md |
| 7 | RecordDetailScreen | ✅ 浮层+渐变+Chips | ✅ 对齐 | screen-record-detail.md |
| 8 | ProfileScreen | ✅ 4统计+目标+图标色 | ✅ 对齐 | screen-profile.md |
| 9 | ProductScreen | ✅ AM/PM+品类色 | ✅ 对齐 | screen-product.md |
| 10 | AttributionReportScreen | ✅ 概览+徽章+建议 | ✅ 对齐 | screen-attribution.md |
| 11 | PaywallScreen | ✅ 皇冠+试用+省钱标签 | ✅ 对齐 | screen-paywall.md |
| 12 | ShareCardScreen | ✅ 模板+分享目标 | ✅ 对齐 | screen-share.md |
| 13 | SettingsScreen | ✅ 全功能(退出/注销/导出/同步) | ✅ 对齐 | screen-settings.md |
| 14 | LockedFeatureCard | ✅ tags+渐变按钮+试用 | ✅ 对齐 | feature-gating.md |
| 15 | EditProfileScreen | ✅ 头像+昵称+肤质选择 | ✅ 对齐 | screen-settings.md |
| 16 | ChangePasswordScreen | ✅ 三密码+校验+成功提示 | ✅ 对齐 | screen-settings.md |

## 快照测试
| 测试文件 | 用例数 | 状态 |
|---------|--------|------|
| ComponentSnapshotTest | 6 | ✅ 通过 |
| AuthScreenSnapshotTest | 3 | ✅ 通过（暗色修复） |
| PaywallScreenSnapshotTest | 3 | ✅ 通过 |
| RecordDetailSnapshotTest | 4 | ✅ 通过（RadarChart+ScoreRing） |
| TimelineSnapshotTest | 6 | ✅ 通过（content/empty × light/dark） |
| ProfileSnapshotTest | 2 | ✅ 通过（渐变卡+统计+菜单图标） |
| ProductScreenSnapshotTest | 2 | ✅ 通过（打卡高亮+产品库） |
| AttributionReportSnapshotTest | 2 | ✅ 通过（影响排行+标签色） |
| DashboardSnapshotTest | 4 | ✅ 通过（content/empty × light/dark） |
| HomeScreenSnapshotTest | 2 | ✅ 通过（导航栏+FAB） |
| CameraSnapshotTest | 8 | ✅ 通过（权限请求/拒绝/保存+分析/保存无分析 × light/dark） |

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
| RadarChart | component | 六边形雷达图（多维度皮肤评分） | ✅ 完成 |
| ScoreRing | component | 环形进度评分（sweepGradient） | ✅ 完成 |
| DeleteAccountDialog | component | 账户注销确认对话框（密码验证） | ✅ 完成 |

## expect/actual 模块
| 模块 | Android | iOS | 状态 |
|------|---------|-----|------|
| CameraController | CameraX | AVFoundation | Android完成 |
| ImageCompressor | Bitmap压缩 | UIImage压缩 | Android完成 |
| ImageStorage | filesDir写入 | — | Android完成 |
| PaymentManager | Mock(delay) | — | Mock完成 |
| ShareManager | Mock(log) | — | Mock完成 |
| NotificationManager | Mock(log) | — | Mock完成 |

## 后端
| 组件 | 文件 | 状态 |
|------|------|------|
| **Ktor Server** | server/ 模块 | ✅ 完整 API |
| Server — Auth | server/routes/AuthRoutes.kt | ✅ 注册/登录 + JWT |
| Server — CRUD | server/routes/*.kt | ✅ 6 实体全覆盖 |
| Server — DB | server/database/ | ✅ Exposed + PostgreSQL |
| Server — Image | server/service/ImageService.kt | ✅ 文件系统存储 + 类型/大小验证 |
| Server — Security | Application.kt + StatusPages.kt | ✅ CORS白名单/Rate Limit/安全头/异常脱敏 |
| Server — Refresh Token | AuthRoutes.kt + UserService.kt | ✅ POST /api/auth/token/refresh |
| Server — GDPR | AuthRoutes.kt + UserService.kt | ✅ DELETE /api/auth/user + GET /api/user/export |
| Server — AI Analysis | server/service/AiAnalysisService.kt | ✅ 多 Provider (OpenAI/Gemini/Claude) + 重试 + 限流 |
| Server — AI Routes | server/routes/AiRoutes.kt | ✅ POST /api/ai/analyze-skin + /api/ai/attribution-report |
| Server — Health | Application.kt | ✅ GET /health |
| **部署** | | |
| Dockerfile | server/Dockerfile | ✅ 多阶段构建 + non-root + 健康检查 |
| Docker Compose | docker-compose.yml | ✅ app + db + nginx + certbot |
| Nginx | deploy/nginx/nginx.conf | ✅ 反向代理 + 限流 + 安全头 + SSL |
| Deploy Script | deploy/deploy.sh | ✅ 拉取+构建+重启+健康检查 |
| **客户端 Ktor** | | |
| KtorServerConfig | data/remote/KtorServerConfig.kt | ✅ BuildConfig 配置 |
| KtorSyncService | data/remote/KtorSyncService.kt | ✅ RemoteSyncService 实现 |
| KtorAuthRepository | data/repository/KtorAuthRepository.kt | ✅ JWT Bearer Auth |
| KtorDto | data/remote/dto/KtorDto.kt | ✅ ApiResponse + Auth DTO |
| **Supabase (legacy)** | | |
| SupabaseProvider | data/remote/SupabaseProvider.kt | ✅ 保留兼容 |
| SupabaseAuthRepository | data/repository/SupabaseAuthRepository.kt | ✅ 保留兼容 |
| SupabaseSyncService | data/remote/SupabaseSyncService.kt | ✅ 保留兼容 |
| **通用** | | |
| RemoteSyncService | data/remote/RemoteSyncService.kt | ✅ 后端无关接口 |
| SyncManager | data/remote/SyncManager.kt | ✅ 增量同步+离线队列+网络监听+待分析重试 |
| NetworkMonitor | data/remote/NetworkMonitor.kt | ✅ expect/actual 网络状态监听 |
| SyncQueueEntity | data/local/entity/SyncQueueEntity.kt | ✅ 离线队列持久化 |
| SyncQueueDao | data/local/dao/SyncQueueDao.kt | ✅ 队列 CRUD |
| DI 模式切换 | di/AppModule.kt | ✅ Ktor > Supabase > Mock |

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
