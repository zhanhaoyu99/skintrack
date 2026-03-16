<!-- Last updated: 2026-03-13 -->
# SkinTrack 全面审查报告

## 审查摘要

| 维度 | 评分 | 关键发现 |
|------|------|---------|
| UI 质感 | 89/100 | 设计系统完整，动画专业，需补表单验证和触觉反馈 |
| 功能完整性 | 52% | 34 项缺失功能，核心认证/支付/同步未闭环 |
| 端云安全 | 30+ 问题 | 1 极高 + 13 高 + 15 中风险，上线前必须修复 |

---

## 第一部分：UI 质感审查

### 整体评价
代码质量卓越，设计系统 token 使用率 98%，动画丰富专业。主要问题集中在**用户反馈缺失**和**表单交互不完善**。

### P0 严重问题（必须修复）

| # | 页面 | 问题 | 影响 |
|---|------|------|------|
| U1 | AuthScreen | 无表单验证，可提交空字符串 | 服务端报错体验差 |
| U2 | AuthScreen | 无密码可见性切换 | 基础易用性缺失 |
| U3 | CameraScreen | PhotoConfirmContent 按"使用照片"后无加载反馈 | 用户不知道系统在处理 |
| U4 | RecordDetailScreen | 分享按钮未校验 isPremium | 门控泄露 |

### P1 中等问题（应优化）

| # | 页面 | 问题 |
|---|------|------|
| U5 | 全站 | 无触觉反馈（CaptureButton/FAB/MenuItem） |
| U6 | MenuItem | 无 ripple 点击反馈 |
| U7 | DashboardScreen | QuickActionItem 无点击视觉反馈 |
| U8 | TrendChart | Y 轴标签硬编码 0/50/100，不适应数据范围 |
| U9 | TrendChart | 无点击交互（不能点选数据点查看详情） |
| U10 | TimelineScreen | 记录图片加载失败无占位符 |
| U11 | animateListItem | delay=index*50 在大列表(>30)时动画过长 |
| U12 | RadarChart | 标签在边界角度（0/90/180/270°）时可能超出画布 |
| U13 | PaywallScreen | 条款链接未实现（onClick 空） |
| U14 | ProfileScreen | "关于 SkinTrack" onClick 空 lambda |

### P2 优化建议

| # | 建议 |
|---|------|
| U15 | TimelineScreen 添加 sticky TrendChart header |
| U16 | ProfileScreen 支持真实用户头像 |
| U17 | LockedFeatureCard 用 Icon(Lock) 替换 emoji |
| U18 | 验证暗色模式全系统对比度（WCAG AA ≥ 4.5:1） |
| U19 | ScoreRing strokeWidth 在小尺寸时自适应变细 |
| U20 | LazyColumn 添加 contentType 优化 recomposition |

---

## 第二部分：功能完整性审查

### 整体覆盖度

| 维度 | 实现度 | 缺失项数 |
|------|--------|---------|
| 用户认证 | 60% | 5 |
| 拍照分析 | 70% | 4 |
| 数据同步 | 65% | 4 |
| 护肤品管理 | 40% | 5 |
| 订阅支付 | 30% | 5 |
| 用户体验 | 50% | 4 |
| 数据安全/隐私 | 20% | 4 |
| 分析归因 | 70% | 3 |

### P0 必须有（上线前）

#### 认证与账户
| # | 功能 | 描述 |
|---|------|------|
| F1 | 忘记密码 | 密码重置流程 + 邮件验证 |
| F2 | 邮箱验证 | 注册后邮箱验证，防假邮箱 |
| F3 | JWT 会话刷新 | 短期 access token + refresh token |
| F4 | 账户注销 | 删除账户及所有数据（GDPR） |
| F5 | 密码修改 | AuthRepository 添加 changePassword() |

#### 拍照分析
| # | 功能 | 描述 |
|---|------|------|
| F6 | 分析失败重试 | 指数退避重试（max 3次） |
| F7 | 分析状态展示 | 进度显示 + 取消按钮 |

#### 数据同步
| # | 功能 | 描述 |
|---|------|------|
| F8 | 增量同步 | 仅拉取 updatedAt 之后的数据 |
| F9 | 冲突解决 | Last-Write-Wins 策略 |
| F10 | 离线队列持久化 | Room 表记录待同步队列 |

#### 支付
| # | 功能 | 描述 |
|---|------|------|
| F11 | 真实支付集成 | Google Play Billing / StoreKit 2 |
| F12 | 订阅恢复 | 真实实现 restorePurchase() |

#### 合规
| # | 功能 | 描述 |
|---|------|------|
| F13 | 隐私政策 | AuthScreen 添加同意勾选 + 链接 |
| F14 | 数据导出 | 导出个人数据（JSON/CSV） |

### P1 重要（V1.0 后可补充）

| # | 功能 | 描述 |
|---|------|------|
| F15 | 首次启动引导（Onboarding） | 3-5 步新手教程 |
| F16 | 设置页面 | 语言、通知开关、关于 |
| F17 | LLM 归因分析 | 替换本地统计为 LLM 分析 |
| F18 | 产品搜索/筛选 | SearchBar + Filter |
| F19 | 推送通知真实实现 | FCM + APNs |
| F20 | 到期续费提醒 | 订阅过期前 3 天提醒 |
| F21 | 多维度时间对比 | 周/月/季度粒度 |
| F22 | 分页加载 | Timeline/Product 列表 Paging 3 |
| F23 | Network 超时配置 | Ktor HttpClient 超时 30s |

### P2 可延后

| # | 功能 |
|---|------|
| F24 | 品牌数据库 + 自动完成 |
| F25 | 扫码录入护肤品 |
| F26 | 账单历史 |
| F27 | 版本更新检查 |
| F28 | 分析结果缓存（基于图片哈希） |
| F29 | 登录设备管理 |
| F30 | 单元测试覆盖（ViewModel/Repository） |
| F31 | 崩溃监控（Firebase Crashlytics） |

### 需新建的文件

**Screen + ViewModel:**
- `OnboardingScreen.kt` + `OnboardingViewModel.kt`
- `SettingsScreen.kt` + `SettingsViewModel.kt`

**UseCase:**
- `ResetPassword.kt` / `ChangePassword.kt` / `DeleteAccount.kt` / `ExportUserData.kt`

**后端新 API:**
- `POST /api/auth/password/reset` — 密码重置
- `POST /api/auth/password/change` — 修改密码
- `DELETE /api/auth/user` — 账户删除
- `GET /api/user/export` — 数据导出
- `POST /api/auth/token/refresh` — Token 刷新

---

## 第三部分：端云安全审查

### 风险统计
- **极高 (CRITICAL)**: 1 个
- **高 (HIGH)**: 13 个
- **中 (MEDIUM)**: 15 个
- **低 (LOW)**: 2 个

### CRITICAL（极高）

| # | 问题 | 位置 |
|---|------|------|
| S1 | JWT 密钥硬编码在配置文件中（默认值 `skintrack-dev-secret-change-in-production`） | server/application.conf:13 |

### HIGH（高）

| # | 问题 | 位置 | 描述 |
|---|------|------|------|
| S2 | CORS anyHost() | server/Application.kt:51-55 | 允许任何来源访问 API |
| S3 | JWT 过期 30 天 | server/application.conf:17 | 被盗 token 可长期利用 |
| S4 | 图片上传无类型/大小限制 | server/ImageRoutes.kt | 可上传恶意文件、DoS |
| S5 | 图片上传用原始文件名 | server/ImageService.kt | 路径遍历攻击风险 |
| S6 | 数据库凭证在配置文件中 | server/application.conf:20-25 | 源代码泄露凭证 |
| S7 | 缺少 HTTPS 强制 | server 全局 | JWT 传输可被拦截 |
| S8 | 允许明文 HTTP（Android） | network_security_config.xml | 敏感数据明文传输 |
| S9 | Token 明文存储在 Room | AuthSessionEntity | 设备攻击可提取 |
| S10 | HTTP 日志 LogLevel.BODY | AppModule.kt:79-81 | 密码/token 打印到日志 |
| S11 | 服务端凭证 BuildConfig 硬编码 fallback | KtorServerConfig.kt / SupabaseProvider.kt | APK 逆向可提取 |
| S12 | 用户授权边界不清晰 | server routes | 未强制 JWT userId |
| S13 | TokenHolder 内存暴露 | AppModule.kt:154-157 | 调试器可访问 |
| S14 | 图片无 EXIF 清理 | ImageCompressor.android.kt | 用户 GPS 位置泄露 |

### MEDIUM（中）

| # | 问题 |
|---|------|
| S15 | 密码强度仅检查长度≥6 |
| S16 | 邮箱格式未验证 |
| S17 | 异常信息泄露给客户端 |
| S18 | 缺少 Rate Limiting |
| S19 | 缺少安全响应头（HSTS/X-Frame-Options 等） |
| S20 | 无审计日志 |
| S21 | Room 数据库未加密 |
| S22 | 无证书锁定 |
| S23 | 调试标志可能在生产版本启用 |
| S24 | 无请求大小限制 |
| S25 | GDPR 不合规（无导出/删除） |
| S26 | 图片本地明文存储 |
| S27 | 依赖项可能过时 |

---

## 第四部分：分阶段行动计划

### Phase 1：安全加固 + 核心修复（1 周）
**目标：消除所有 CRITICAL/HIGH 安全问题 + P0 UI 修复**

1. ~~安全~~ 服务端
   - [ ] S1: JWT 密钥改为强制环境变量（删除默认值）
   - [ ] S2: CORS 白名单替换 anyHost()
   - [ ] S3: JWT 过期改 1h + 实现 refresh token
   - [ ] S4+S5: 图片上传添加类型白名单 + 大小限制 + UUID 重命名
   - [ ] S6: 数据库凭证改为环境变量（无默认值）
   - [ ] S7: 添加 HTTPS 配置 + HSTS 头
   - [ ] S17: 异常处理通用化，不泄露内部信息
   - [ ] S18: 添加 Rate Limiting（登录 5次/分钟）

2. ~~安全~~ 客户端
   - [ ] S8: 生产环境删除 cleartext 配置
   - [ ] S9: Token 存储改用 EncryptedSharedPreferences
   - [ ] S10: 日志级别改为 INFO（生产 NONE）
   - [ ] S14: 图片压缩后清除 EXIF 数据

3. UI 修复
   - [ ] U1+U2: AuthScreen 表单验证 + 密码可见性
   - [ ] U3: CameraScreen 确认按钮加载状态
   - [ ] U4: RecordDetailScreen 分享按钮门控

### Phase 2：认证闭环 + 支付集成（1-2 周）
**目标：核心业务流程完整**

1. 认证
   - [ ] F1: 忘记密码流程（服务端 API + 客户端 UI）
   - [ ] F3: JWT refresh token 机制
   - [ ] F5: 密码修改功能
   - [ ] F13: 隐私政策同意界面

2. 支付
   - [ ] F11: Google Play Billing 集成
   - [ ] F12: 订阅恢复真实实现

3. 同步
   - [ ] F8: 增量同步（updatedAt 标记）
   - [ ] F9: 冲突解决策略
   - [ ] F23: HttpClient 超时配置

### Phase 3：体验提升 + 功能补充（1-2 周）
**目标：产品体验达到上线标准**

1. UI 提升
   - [ ] U5: 全站触觉反馈
   - [ ] U6: MenuItem ripple
   - [ ] U7: QuickActionItem 点击反馈
   - [ ] U8+U9: TrendChart 自适应 Y 轴 + 点击交互
   - [ ] U11: animateListItem delay 上限 2000ms

2. 功能
   - [ ] F15: Onboarding 引导页
   - [ ] F16: 设置页面
   - [ ] F4+F14: 账户注销 + 数据导出（GDPR）
   - [ ] F6+F7: 分析重试 + 进度展示
   - [ ] F22: 分页加载

### Phase 4：差异化功能 + 优化（持续）

- [ ] F17: LLM 归因分析替换本地统计
- [ ] F18: 产品搜索/筛选
- [ ] F19: 推送通知
- [ ] F20: 续费提醒
- [ ] F30: 单元测试
- [ ] F31: 崩溃监控

---

## 需新建的后端 API 端点

| 方法 | 路径 | 用途 | Phase |
|------|------|------|-------|
| POST | /api/auth/token/refresh | Token 刷新 | 1 |
| POST | /api/auth/password/reset | 密码重置请求 | 2 |
| POST | /api/auth/password/reset/verify | 重置验证 | 2 |
| POST | /api/auth/password/change | 修改密码 | 2 |
| DELETE | /api/auth/user | 账户删除 | 3 |
| GET | /api/user/export | 数据导出 | 3 |
| GET | /api/app/version | 版本检查 | 4 |

## 需新建的客户端页面

| 页面 | 用途 | Phase |
|------|------|-------|
| OnboardingScreen | 新手引导 | 3 |
| SettingsScreen | 用户设置 | 3 |
| ForgotPasswordScreen | 忘记密码 | 2 |
