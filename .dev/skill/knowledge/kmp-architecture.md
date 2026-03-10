<!-- Last updated: 2026-03-10 -->
# KMP (Kotlin Multiplatform) 架构知识库

## 项目结构（SkinTrack）

```
skintrack/
├── composeApp/                           # 唯一主模块（UI+业务+数据全部在此）
│   └── src/
│       ├── commonMain/kotlin/com/skintrack/
│       │   ├── App.kt                    # 应用入口
│       │   ├── ui/
│       │   │   ├── screen/               # 页面
│       │   │   │   ├── CameraScreen.kt
│       │   │   │   ├── TimelineScreen.kt
│       │   │   │   ├── SkincareScreen.kt
│       │   │   │   ├── ReportScreen.kt
│       │   │   │   └── ProfileScreen.kt
│       │   │   ├── component/            # 可复用组件
│       │   │   │   ├── RadarChart.kt     # Compose Canvas雷达图
│       │   │   │   ├── CompareCard.kt    # 前后对比卡片
│       │   │   │   └── TrendChart.kt     # 折线趋势图
│       │   │   └── theme/                # 主题
│       │   │       └── Theme.kt
│       │   ├── domain/
│       │   │   ├── model/                # 数据模型（SkinRecord, Product等）
│       │   │   ├── repository/           # Repository接口
│       │   │   └── usecase/              # 业务逻辑UseCase
│       │   ├── data/
│       │   │   ├── local/
│       │   │   │   ├── AppDatabase.kt    # Room数据库
│       │   │   │   └── SkinRecordDao.kt  # DAO
│       │   │   ├── remote/
│       │   │   │   ├── AiAnalysisService.kt   # AI API调用（Ktor）
│       │   │   │   └── SupabaseClient.kt      # Supabase客户端
│       │   │   └── repository/           # Repository实现
│       │   └── platform/
│       │       ├── CameraController.kt   # expect声明
│       │       ├── NotificationManager.kt
│       │       ├── PaymentManager.kt
│       │       └── ImageCompressor.kt
│       ├── androidMain/kotlin/com/skintrack/
│       │   ├── platform/                 # actual实现
│       │   │   ├── CameraController.android.kt    # CameraX
│       │   │   ├── NotificationManager.android.kt # FCM
│       │   │   ├── PaymentManager.android.kt      # 微信支付
│       │   │   └── ImageCompressor.android.kt     # Bitmap压缩
│       │   └── MainActivity.kt
│       └── iosMain/kotlin/com/skintrack/
│           ├── platform/                 # actual实现
│           │   ├── CameraController.ios.kt        # AVFoundation
│           │   ├── NotificationManager.ios.kt     # APNs
│           │   ├── PaymentManager.ios.kt          # StoreKit 2
│           │   └── ImageCompressor.ios.kt         # UIImage压缩
│           └── MainViewController.kt
├── iosApp/                               # iOS Xcode项目壳
├── gradle/libs.versions.toml             # 统一版本管理
├── build.gradle.kts
└── settings.gradle.kts
```

**核心原则：** 所有业务逻辑、UI、网络、数据库都在commonMain。只有相机、通知、支付、图片压缩4个模块需要平台特定代码。预计代码复用率 85-90%。

## 核心依赖库

| 功能 | 库 | 说明 |
|------|-----|------|
| UI框架 | Compose Multiplatform | @Composable统一写UI |
| 导航 | Voyager 或 Decompose | 轻量KMP导航库 |
| 状态管理 | **AndroidX ViewModel KMP版** | androidx.lifecycle:lifecycle-viewmodel |
| 网络 | Ktor Client | 原生Kotlin HTTP客户端 |
| 序列化 | Kotlinx Serialization | JSON解析 |
| 本地数据库 | **Room KMP版** | androidx.room:room-runtime |
| KV存储 | **DataStore KMP版** | androidx.datastore:datastore-preferences |
| 分页 | **Paging KMP版** | 时间线分页加载 |
| 图片加载 | Coil 3 KMP版 | io.coil-kt.coil3:coil-compose |
| DI | Koin | KMP友好 |
| 日期时间 | kotlinx-datetime | KMP原生 |
| 协程 | kotlinx-coroutines | KMP原生 |
| 后端/BaaS | **Supabase (supabase-kt)** | 认证+数据库+存储 |
| 图表 | Compose Canvas自绘 | 雷达图/折线图/对比图 |
| 相机 | expect/actual | Android: CameraX, iOS: AVFoundation |
| 崩溃监控 | Firebase Crashlytics | KMP插件支持 |
| 日志 | Napier / Kermit | KMP日志库 |

### 依赖版本管理
- 使用 Gradle Version Catalog (libs.versions.toml)
- 推荐用 kmp.jetbrains.com 初始化项目确保版本兼容

## expect/actual 使用规范

### 本项目需要 expect/actual 的模块
| 功能 | commonMain | androidMain | iosMain |
|------|-----------|-------------|---------|
| 相机 | expect接口定义 | CameraX实现 (~100-200行) | AVFoundation实现 |
| 图片压缩 | expect接口定义 | Bitmap压缩 | UIImage压缩 |
| 支付 | expect接口定义 | 微信支付SDK | StoreKit 2 |
| 推送通知 | expect接口定义 | FCM | APNs |
| 权限申请 | expect接口定义 | ActivityCompat | Info.plist + AVAuth |

### 使用模式
```kotlin
// commonMain - 声明
expect class CameraController {
    suspend fun takePhoto(): ByteArray?
    fun isAvailable(): Boolean
}

// androidMain - 实现
actual class CameraController(private val context: Context) {
    actual suspend fun takePhoto(): ByteArray? { /* CameraX */ }
    actual fun isAvailable(): Boolean = true
}

// iosMain - 实现
actual class CameraController {
    actual suspend fun takePhoto(): ByteArray? { /* AVFoundation */ }
    actual fun isAvailable(): Boolean = true
}
```

### 最佳实践
- expect/actual 尽量薄，复杂逻辑放 commonMain
- 用接口抽象而非直接 expect class
- Koin注入平台实现

## Koin 依赖注入模式

```kotlin
// commonMain - 定义模块
val commonModule = module {
    single { SupabaseClient.create() }
    single { AiAnalysisService(get()) }
    single<SkinRecordRepository> { SkinRecordRepositoryImpl(get(), get()) }
    factory { GetSkinRecordsUseCase(get()) }
    viewModelOf(::CameraViewModel)
    viewModelOf(::TimelineViewModel)
}

// androidMain - 平台模块
val androidModule = module {
    single { CameraController(get()) }
    single { ImageCompressor(get()) }
    single { PaymentManager(get()) }
}

// iosMain - 平台模块
val iosModule = module {
    single { CameraController() }
    single { ImageCompressor() }
    single { PaymentManager() }
}
```

## Room KMP 使用

```kotlin
// commonMain - 定义Entity和DAO
@Entity(tableName = "skin_records")
data class SkinRecordEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val skinType: String,
    val overallScore: Int?,
    val imageUrl: String?,
    val analysisJson: String?,  // AI分析结果JSON
    val recordedAt: Long,
    val createdAt: Long,
    val synced: Boolean = false
)

@Dao
interface SkinRecordDao {
    @Query("SELECT * FROM skin_records WHERE userId = :userId ORDER BY recordedAt DESC")
    fun getRecordsByUser(userId: String): Flow<List<SkinRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: SkinRecordEntity)

    @Query("SELECT * FROM skin_records WHERE synced = 0")
    suspend fun getUnsynced(): List<SkinRecordEntity>
}

@Database(entities = [SkinRecordEntity::class, ...], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun skinRecordDao(): SkinRecordDao
}
```

## Supabase 集成

```kotlin
// commonMain - Supabase客户端
val supabase = createSupabaseClient(
    supabaseUrl = "https://xxx.supabase.co",
    supabaseKey = "your-anon-key"
) {
    install(Auth)
    install(Postgrest)
    install(Storage)
}

// 认证
suspend fun signIn(email: String, password: String) {
    supabase.auth.signInWith(Email) {
        this.email = email
        this.password = password
    }
}

// 数据库查询
suspend fun getRecords(userId: String): List<SkinRecord> {
    return supabase.postgrest["skin_records"]
        .select { filter { eq("user_id", userId) } }
        .decodeList()
}

// 图片上传
suspend fun uploadImage(bucket: String, path: String, data: ByteArray) {
    supabase.storage[bucket].upload(path, data)
}
```

## 常见陷阱
1. **版本兼容性** — 各库版本必须匹配，用libs.versions.toml统一管理
2. **iOS调试** — Xcode环境配置复杂，安装KMP插件支持直接调试
3. **编译速度** — iOS比Android慢，K2编译器已默认开启提升~40%
4. **OkHttp不支持KMP** — 网络用Ktor替代
5. **Kotlin/Native反射有限** — Room KMP版已处理好
6. **包体积** — Compose CMP含Skia引擎，iOS包比SwiftUI大~15-20MB（可接受）
7. **需要Mac** — iOS编译必须Xcode，无Mac先做Android
