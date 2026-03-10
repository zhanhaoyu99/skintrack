<!-- Last updated: 2026-03-10 -->
# Supabase BaaS 模式知识库

## 概述
SkinTrack使用Supabase作为后端服务（BaaS），提供：
- **Auth** — 用户认证（邮箱/手机/OAuth）
- **Database** — PostgreSQL数据库（Postgrest API自动生成REST端点）
- **Storage** — 对象存储（用户照片）
- **Edge Functions** — 服务端逻辑（如需要）
- **Realtime** — 实时数据同步（如需要）

## Supabase Kotlin SDK (supabase-kt)

### 依赖配置
```kotlin
// libs.versions.toml
[versions]
supabase = "3.x.x"
ktor = "3.x.x"

[libraries]
supabase-auth = { module = "io.github.jan-tennert.supabase:auth-kt", version.ref = "supabase" }
supabase-postgrest = { module = "io.github.jan-tennert.supabase:postgrest-kt", version.ref = "supabase" }
supabase-storage = { module = "io.github.jan-tennert.supabase:storage-kt", version.ref = "supabase" }
```

### 客户端初始化
```kotlin
val supabase = createSupabaseClient(
    supabaseUrl = BuildConfig.SUPABASE_URL,
    supabaseKey = BuildConfig.SUPABASE_ANON_KEY
) {
    install(Auth)
    install(Postgrest)
    install(Storage)
}
```

## Auth 认证模式

### 邮箱注册/登录
```kotlin
// 注册
suspend fun signUp(email: String, password: String) {
    supabase.auth.signUpWith(Email) {
        this.email = email
        this.password = password
    }
}

// 登录
suspend fun signIn(email: String, password: String) {
    supabase.auth.signInWith(Email) {
        this.email = email
        this.password = password
    }
}

// 获取当前用户
val currentUser = supabase.auth.currentUserOrNull()
val userId = currentUser?.id

// 登出
suspend fun signOut() { supabase.auth.signOut() }

// 监听认证状态
supabase.auth.sessionStatus.collect { status ->
    when (status) {
        is SessionStatus.Authenticated -> { /* 已登录 */ }
        is SessionStatus.NotAuthenticated -> { /* 未登录 */ }
        is SessionStatus.LoadingFromStorage -> { /* 加载中 */ }
        is SessionStatus.NetworkError -> { /* 网络错误 */ }
    }
}
```

## Postgrest 数据操作

### 查询
```kotlin
// 列表查询（过滤+排序+分页）
suspend fun getRecords(userId: String, limit: Int = 30): List<SkinRecordDto> {
    return supabase.postgrest["skin_records"]
        .select {
            filter { eq("user_id", userId) }
            order("recorded_at", Order.DESCENDING)
            limit(count = limit.toLong())
        }
        .decodeList()
}

// 条件查询
suspend fun getRecordsBetween(userId: String, start: String, end: String): List<SkinRecordDto> {
    return supabase.postgrest["skin_records"]
        .select {
            filter {
                eq("user_id", userId)
                gte("recorded_at", start)
                lte("recorded_at", end)
            }
            order("recorded_at", Order.ASCENDING)
        }
        .decodeList()
}

// 单条查询
suspend fun getRecordById(id: String): SkinRecordDto? {
    return supabase.postgrest["skin_records"]
        .select { filter { eq("id", id) } }
        .decodeSingleOrNull()
}
```

### 插入
```kotlin
suspend fun insertRecord(record: SkinRecordDto): SkinRecordDto {
    return supabase.postgrest["skin_records"]
        .insert(record) { select() }
        .decodeSingle()
}
```

### 更新
```kotlin
suspend fun updateRecord(id: String, updates: Map<String, Any?>) {
    supabase.postgrest["skin_records"]
        .update(updates) { filter { eq("id", id) } }
}
```

### 删除
```kotlin
suspend fun deleteRecord(id: String) {
    supabase.postgrest["skin_records"]
        .delete { filter { eq("id", id) } }
}
```

### DTO定义
```kotlin
@Serializable
data class SkinRecordDto(
    val id: String? = null,
    @SerialName("user_id") val userId: String,
    @SerialName("skin_type") val skinType: String,
    @SerialName("overall_score") val overallScore: Int? = null,
    @SerialName("acne_count") val acneCount: Int? = null,
    @SerialName("pore_score") val poreScore: Int? = null,
    @SerialName("redness_score") val rednessScore: Int? = null,
    @SerialName("even_score") val evenScore: Int? = null,
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("analysis_json") val analysisJson: JsonObject? = null,
    @SerialName("recorded_at") val recordedAt: String,
    @SerialName("created_at") val createdAt: String? = null
)
```

## Storage 文件存储

### 图片上传
```kotlin
suspend fun uploadSkinPhoto(userId: String, imageData: ByteArray): String {
    val fileName = "${userId}/${Clock.System.now().epochSeconds}.jpg"
    val bucket = supabase.storage["skin-photos"]
    bucket.upload(fileName, imageData) {
        upsert = false
        contentType = ContentType.Image.JPEG
    }
    return bucket.publicUrl(fileName)
}
```

### Storage RLS策略
```sql
CREATE POLICY "Users can upload own photos"
    ON storage.objects FOR INSERT TO authenticated
    WITH CHECK (bucket_id = 'skin-photos' AND (storage.foldername(name))[1] = auth.uid()::text);

CREATE POLICY "Users can view own photos"
    ON storage.objects FOR SELECT TO authenticated
    USING (bucket_id = 'skin-photos' AND (storage.foldername(name))[1] = auth.uid()::text);
```

## Repository模式（Supabase + Room 结合）

```kotlin
class SkinRecordRepositoryImpl(
    private val supabase: SupabaseClient,
    private val dao: SkinRecordDao
) : SkinRecordRepository {

    // 读取 — 先返回本地，后台同步远程
    override fun getRecords(userId: String): Flow<List<SkinRecord>> {
        coroutineScope.launch { syncFromRemote(userId) }
        return dao.getRecordsByUser(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    // 写入 — 先存本地，后台上传
    override suspend fun createRecord(record: SkinRecord) {
        dao.insert(record.toEntity(synced = false))
        try {
            supabase.postgrest["skin_records"].insert(record.toDto())
            dao.markSynced(record.id)
        } catch (e: Exception) { /* 离线模式 */ }
    }

    // 后台同步未上传记录
    suspend fun syncPending() {
        val unsynced = dao.getUnsynced()
        unsynced.forEach { entity ->
            try {
                supabase.postgrest["skin_records"].insert(entity.toDto())
                dao.markSynced(entity.id)
            } catch (e: Exception) { /* 重试 */ }
        }
    }
}
```

## 安全注意事项
1. **ANON KEY 可以暴露** — 安全靠RLS，不是Key
2. **RLS 必须开启** — 否则数据不隔离
3. **Service Role Key 绝对不能放客户端** — 它绕过RLS
4. **图片路径包含userId** — 确保用户只能访问自己的图片

## 免费层限制
| 资源 | 额度 |
|------|------|
| MAU | 50,000 |
| 数据库 | 500 MB |
| Storage | 1 GB |
| Edge Functions | 500K 调用/月 |
| 带宽 | 5 GB |

MVP阶段完全够用。

## 常见陷阱
1. **DTO字段名必须与列名匹配** — 用 @SerialName
2. **userId是UUID字符串** — 不是Long
3. **Postgrest filter链式调用** — 多个filter是AND
4. **Storage上传需设contentType**
5. **时区** — 数据库用TIMESTAMPTZ(UTC)，客户端转本地
6. **Token自动管理** — SDK处理刷新，无需手动
