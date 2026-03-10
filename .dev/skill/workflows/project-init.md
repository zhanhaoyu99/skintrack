<!-- Last updated: 2026-03-10 -->
# 工作流：项目初始化

## 触发命令
`/app init` — 初始化 KMP + Spring Boot 皮肤管理App项目

## 执行流程

### 第1步：确认项目配置
1. 加载记忆文件：
   - `app-strategy.md` — 项目策略
   - `app-decisions.md` — 已有架构决策
2. 与用户确认：
   - 项目名称（英文，如 `skincare-app`）
   - 项目根目录位置
   - 包名（如 `com.skincare`）
   - 最低SDK版本（Android API 26+ / iOS 15+）
   - 后端是否与客户端同repo

**输出：** 项目配置确认表

### 第2步：KMP客户端项目脚手架
1. 使用 Kotlin Multiplatform Wizard 或手动创建：
```
{project-name}/
├── build.gradle.kts                  # 根构建文件
├── settings.gradle.kts               # 模块注册
├── gradle.properties                 # Gradle配置
├── gradle/
│   └── libs.versions.toml            # 版本目录
├── shared/                           # 共享业务逻辑
│   ├── build.gradle.kts
│   └── src/
│       ├── commonMain/kotlin/{package}/
│       │   ├── data/
│       │   │   ├── api/              # Ktor API Service
│       │   │   ├── model/            # 数据模型
│       │   │   └── repository/       # Repository
│       │   ├── domain/
│       │   │   ├── model/            # 领域模型
│       │   │   └── usecase/          # UseCase
│       │   ├── di/
│       │   │   └── SharedModule.kt   # Koin模块
│       │   └── util/                 # 工具
│       ├── androidMain/kotlin/{package}/
│       │   └── platform/             # Android expect/actual
│       └── iosMain/kotlin/{package}/
│           └── platform/             # iOS expect/actual
└── composeApp/                       # Compose UI
    ├── build.gradle.kts
    └── src/
        ├── commonMain/kotlin/{package}/
        │   ├── ui/
        │   │   ├── screen/           # 页面
        │   │   ├── component/        # 通用组件
        │   │   ├── theme/            # 主题
        │   │   └── navigation/       # 导航
        │   ├── viewmodel/            # ViewModel
        │   └── App.kt                # 应用入口
        ├── androidMain/
        │   ├── kotlin/{package}/MainActivity.kt
        │   └── AndroidManifest.xml
        └── iosMain/
            └── kotlin/{package}/MainViewController.kt
```

2. 初始化关键文件：
   - `libs.versions.toml` — 版本目录（KMP、CMP、Ktor、Koin、Coil、Voyager等）
   - `build.gradle.kts` — 根项目配置
   - `shared/build.gradle.kts` — 共享模块（KMP配置）
   - `composeApp/build.gradle.kts` — UI模块（CMP配置）

3. 加载 `knowledge/kmp-architecture.md` 确认模块划分
4. 加载 `knowledge/compose-multiplatform.md` 设置主题和导航

### 第3步：基础组件创建
1. **主题** — `ui/theme/SkinCareTheme.kt`
   - 颜色方案（亮/暗）
   - 字体
   - 形状
2. **导航** — `ui/navigation/AppNavigation.kt`
   - Voyager Navigator设置
   - 底部导航Tab定义
3. **通用组件**：
   - `LoadingContent` — 加载状态
   - `ErrorContent` — 错误状态
   - `EmptyContent` — 空状态

### 第4步：Spring Boot后端脚手架
1. 加载 `knowledge/spring-boot-patterns.md` 确认分层结构
2. 创建后端项目：
```
server/
├── build.gradle.kts
├── src/main/kotlin/{package}/
│   ├── SkinCareApplication.kt
│   ├── config/
│   │   ├── SecurityConfig.kt
│   │   ├── WebConfig.kt
│   │   └── JwtProperties.kt
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── entity/
│   ├── dto/
│   │   ├── request/
│   │   └── response/
│   ├── exception/
│   │   ├── GlobalExceptionHandler.kt
│   │   ├── BusinessException.kt
│   │   └── ErrorCode.kt
│   ├── security/
│   │   ├── JwtTokenProvider.kt
│   │   └── JwtAuthFilter.kt
│   └── util/
├── src/main/resources/
│   ├── application.yml
│   └── application-dev.yml
└── src/test/
```

3. 加载 `knowledge/api-design.md` 创建统一响应类：
   - `ApiResponse<T>`
   - `PagedResult<T>`
   - `ErrorInfo`

### 第5步：数据库初始化
1. 创建 `application.yml` 配置PostgreSQL连接
2. 创建初始迁移脚本（用户表）
3. 加载 `knowledge/database-patterns.md` 确认Entity规范

### 第6步：验证构建
1. 编译 shared 模块
2. 编译 composeApp 模块
3. 编译 server 模块
4. 运行基本测试

### 第7步：更新记忆文件
1. 更新 `app-progress.md`：
   - 当前状态 → M0进行中
   - 记录已完成的初始化步骤
   - 记录项目路径
2. 更新 `app-decisions.md`：
   - 记录初始化过程中的决策（如导航方案选择）
3. 更新 `app-strategy.md`：
   - 确认技术栈
   - 更新项目路径

---

## 输出格式

```
🏗️ 项目初始化

━━━ 项目配置 ━━━
项目名: {name}
包名: {package}
根目录: {path}

━━━ 客户端 (KMP + CMP) ━━━
[已创建的模块和文件列表]

━━━ 后端 (Spring Boot) ━━━
[已创建的模块和文件列表]

━━━ 基础组件 ━━━
[主题 + 导航 + 通用组件]

━━━ 数据库 ━━━
[初始化状态]

━━━ 构建验证 ━━━
[编译结果]

━━━ 记忆更新 ━━━
[已更新的记忆文件]

下一步建议：
→ `/app plan auth` 规划用户认证模块
→ `/app page splash` 创建启动页
```

---

## 注意事项
- 每步确认后再进行下一步
- 如果用户已有部分项目代码，识别现有结构并适配
- 优先确保编译通过，再完善细节
- 不要过度设计初始架构，MVP够用即可
