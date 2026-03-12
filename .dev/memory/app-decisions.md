<!-- Last updated: 2026-03-12 -->
# 皮肤管理App (SkinTrack) 架构决策记录 (ADR)

<!-- 格式：
## ADR-XXX: 标题
- **日期**: YYYY-MM-DD
- **状态**: 已采纳 / 已废弃 / 待讨论
- **上下文**: 面临什么问题，为什么需要决策
- **决策**: 选择了什么方案
- **理由**: 为什么选这个方案（对比其他选项）
- **后果**: 这个决策带来的正面和负面影响
- **约束**: 这个决策对后续开发的约束
-->

## ADR-001: 客户端技术栈选择 Compose Multiplatform (KMP)
- **日期**: 2026-03-10
- **状态**: 已采纳
- **上下文**: 需要同时支持 Android + iOS，开发者熟悉 Kotlin + Compose
- **决策**: 使用 Compose Multiplatform (KMP) 共享UI和业务逻辑
- **理由**:
  - 零学习成本 — 已会 Kotlin + Compose
  - Compose Multiplatform iOS稳定版已于2025年5月发布，生产可用
  - 编译为原生代码，性能最佳（非桥接/非自绘引擎）
  - Google + JetBrains 双重投入，趋势明确
  - 美团、B站、腾讯QQ、Google Docs 等大厂背书
  - 预计代码复用率 85-90%
- **后果**:
  - (+) 一套代码双端运行，UI完全一致
  - (-) iOS编译比Android慢，需Mac构建
  - (-) 少数库不支持KMP需用替代方案
- **约束**:
  - 相机/支付/通知/图片压缩 需 expect/actual
  - 需关注CMP版本更新

## ADR-002: 后端选择 Supabase (BaaS) 而非 Spring Boot
- **日期**: 2026-03-10
- **状态**: ~~已采纳~~ → 已废弃，被 ADR-009 取代
- **上下文**: MVP阶段需要快速搭建后端，一个人开发无需复杂架构
- **决策**: 使用 Supabase 作为后端服务（认证 + PostgreSQL数据库 + 对象存储）
- **废弃原因**: Supabase 仅有海外节点，App 面向国内用户，延迟和合规性不满足需求

## ADR-003: 项目结构 — 单composeApp模块
- **日期**: 2026-03-10
- **状态**: 已采纳
- **上下文**: KMP项目可以拆shared+composeApp，也可以合并
- **决策**: 统一在 `composeApp` 模块内，commonMain 包含 UI + domain + data + platform
- **理由**:
  - 一个人开发，模块划分过细增加维护成本
  - 所有业务逻辑、UI、网络、数据库都在commonMain
  - 只有4个模块需要 expect/actual（相机/通知/支付/图片压缩）
- **后果**:
  - (+) 结构简单，开发快速
  - (-) 如果项目膨胀，后期可能需要拆分
- **约束**:
  - domain/data/ui/platform 通过包名分层
  - 平台代码在 androidMain/iosMain 的 platform 包

## ADR-004: 本地数据库选择 Room KMP 而非 SQLDelight
- **日期**: 2026-03-10
- **状态**: 已采纳
- **上下文**: KMP本地数据库有Room KMP版和SQLDelight两个选择
- **决策**: 使用 Room KMP版
- **理由**:
  - Jetpack Room 已KMP化，API与Android版完全一致
  - 开发者已熟悉Room用法
  - Google官方维护，长期支持有保障
  - 比SQLDelight的SQL-first方式更适合已有Room经验的开发者
- **后果**:
  - (+) 零学习成本
  - (+) 和AndroidX其他库集成更好
- **约束**:
  - Entity/DAO/Database 在 commonMain 定义
  - 需 KSP 编译器插件

## ADR-005: 状态管理选择 AndroidX ViewModel KMP版
- **日期**: 2026-03-10
- **状态**: 已采纳
- **上下文**: CMP中需要状态管理方案，可以自定义ViewModel或用官方方案
- **决策**: 使用 AndroidX ViewModel KMP版 (androidx.lifecycle:lifecycle-viewmodel)
- **理由**:
  - 官方KMP化，生命周期感知
  - 开发者已熟悉ViewModel用法
  - 与Compose集成好（collectAsStateWithLifecycle）
- **后果**:
  - (+) 标准化，不需要维护自定义ViewModel基类
- **约束**:
  - 依赖 androidx.lifecycle KMP版本

## ADR-006: AI分析方案 — 多模态LLM直调
- **日期**: 2026-03-10
- **状态**: 已采纳
- **上下文**: 皮肤分析需要AI图像分析能力
- **决策**: Ktor Client 直接调用多模态LLM API (GPT-4o / Gemini / Claude)
- **理由**:
  - 多模态LLM可直接分析皮肤照片
  - 无需训练自定义模型
  - 输出结构化JSON（痘痘数量、毛孔评分等）
  - 写在commonMain，双端通用
  - 成本低（约0.03-0.08元/次）
- **后果**:
  - (+) 快速实现，无需ML基础设施
  - (-) 依赖第三方API稳定性和定价
  - (-) 准确度受Prompt质量影响
- **约束**:
  - 需要持续优化Prompt
  - 照片标准化是准确度的前提
  - 归因分析：时间序列数据 + 护肤品记录 一起喂给LLM

## ADR-008: 数据优先云端存储
- **日期**: 2026-03-12
- **状态**: 已采纳
- **上下文**: 未来需要多端同步（Android + iOS + 可能的Web），数据架构需前瞻性设计
- **决策**: 数据尽量存储在云端（Supabase），本地 Room 作为缓存/离线层
- **理由**:
  - 多端同步时，云端为 single source of truth
  - 本地 Room 提供离线体验，上线后自动同步
  - Supabase RLS 保证数据隔离安全
- **后果**:
  - (+) 未来加 iOS/Web 时数据天然可用
  - (-) 需要实现可靠的离线同步策略
- **约束**:
  - 所有 CRUD 操作：先写本地 Room → 标记 synced=false → 后台同步到 Supabase
  - 登录后首次拉取：Supabase → 合并到 Room（冲突以云端为准）
  - 图片：本地压缩后上传 Supabase Storage，Room 存 localPath + remoteUrl

## ADR-007: 图表方案 — Compose Canvas 自绘
- **日期**: 2026-03-10
- **状态**: 已采纳
- **上下文**: 需要趋势曲线图、雷达图、前后对比图等可视化
- **决策**: 使用 Compose Canvas API 自绘图表
- **理由**:
  - Compose Canvas 跨端统一
  - 无需引入第三方图表库
  - 完全可控的样式和交互
- **后果**:
  - (+) 零依赖，样式完全可控
  - (-) 开发成本比用现成图表库高
- **约束**:
  - 需要自己实现：折线图(TrendChart)、雷达图(RadarChart)、前后对比卡片(CompareCard)

## ADR-009: 后端迁移 — 自建 Ktor Server + 国内云
- **日期**: 2026-03-12
- **状态**: 已采纳（取代 ADR-002）
- **上下文**: Supabase 仅有海外节点，App 面向国内用户，延迟和合规性不满足需求
- **决策**: 自建 Ktor Server 后端，部署在腾讯云/阿里云国内节点
- **理由**:
  - 技术栈一致：项目已用 Ktor Client，服务端用 Ktor Server 是 Kotlin 全栈
  - 部署自由：国内云任意节点，延迟低、合规
  - 成本可控：轻量云服务器（2C4G ~50元/月）+ 云 PostgreSQL
  - AI 分析可在服务端调用 LLM API，避免客户端暴露 API Key
  - 替换简单：已抽取 RemoteSyncService 接口，只需新增 KtorSyncService 实现
- **架构**:
  - Client: KMP App → Ktor Client → JSON/HTTPS → Ktor Server
  - Server: Ktor Server + Exposed/ktorm (PostgreSQL ORM) + 阿里云 OSS (图片)
  - Auth: JWT + 邮箱/手机号注册（未来可扩展微信登录）
  - 本地 Room 作为离线缓存，SyncManager 协调推拉
- **后果**:
  - (+) 国内用户低延迟，合规无忧
  - (+) 服务端可承载 AI 分析、推送通知等复杂逻辑
  - (+) 未来可加 Web 端、管理后台
  - (-) 需要自建服务器和运维
  - (-) 需要开发 API + 鉴权逻辑
- **约束**:
  - 客户端通过 RemoteSyncService 接口解耦，不直接依赖后端实现
  - DTO 格式保持 snake_case JSON，前后端序列化统一
  - 图片存储：阿里云 OSS / 腾讯云 COS（非自建存储）
- **迁移路径**:
  1. ✅ 抽取 RemoteSyncService 接口（已完成）
  2. 创建 server/ 模块（Ktor Server + Exposed）
  3. 实现 KtorSyncService（客户端）替换 SupabaseSyncService
  4. 实现 KtorAuthRepository 替换 SupabaseAuthRepository
  5. 部署到国内云 → 联调 → 移除 Supabase SDK 依赖
