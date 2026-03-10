<!-- Last updated: 2026-03-10 -->
# 数据库模式知识库

## 架构概览
- **云端数据库**: Supabase (PostgreSQL) — 用户数据持久化、跨设备同步
- **本地数据库**: Room KMP版 — 离线缓存、快速读取

## 本地数据库 — Room KMP版

### Entity 定义（commonMain）
```kotlin
@Entity(tableName = "skin_records")
data class SkinRecordEntity(
    @PrimaryKey val id: String,          // UUID
    val userId: String,
    val skinType: String,                // 枚举值字符串
    val overallScore: Int? = null,       // 0-100综合评分
    val acneCount: Int? = null,          // 痘痘数量
    val poreScore: Int? = null,          // 毛孔评分
    val rednessScore: Int? = null,       // 红血丝评分
    val evenScore: Int? = null,          // 肤色均匀度
    val blackheadDensity: Int? = null,   // 黑头密度
    val notes: String? = null,
    val imageUrl: String? = null,        // Supabase Storage URL
    val localImagePath: String? = null,  // 本地缓存路径
    val analysisJson: String? = null,    // AI分析完整结果JSON
    val recordedAt: Long,               // 记录时间戳
    val createdAt: Long = System.currentTimeMillis(),
    val synced: Boolean = false          // 是否已同步到Supabase
)

@Entity(tableName = "skincare_products")
data class SkincareProductEntity(
    @PrimaryKey val id: String,
    val name: String,
    val brand: String? = null,
    val category: String,              // CLEANSER/TONER/SERUM等
    val imageUrl: String? = null,
    val barcode: String? = null,
    val synced: Boolean = false
)

@Entity(tableName = "daily_product_usage")
data class DailyProductUsageEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val productId: String,
    val usedDate: Long,                // 使用日期
    val synced: Boolean = false
)
```

### DAO 定义
```kotlin
@Dao
interface SkinRecordDao {
    @Query("SELECT * FROM skin_records WHERE userId = :userId ORDER BY recordedAt DESC")
    fun getRecordsByUser(userId: String): Flow<List<SkinRecordEntity>>

    @Query("SELECT * FROM skin_records WHERE userId = :userId AND recordedAt BETWEEN :start AND :end ORDER BY recordedAt ASC")
    fun getRecordsBetween(userId: String, start: Long, end: Long): Flow<List<SkinRecordEntity>>

    @Query("SELECT * FROM skin_records WHERE id = :id")
    suspend fun getById(id: String): SkinRecordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: SkinRecordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(records: List<SkinRecordEntity>)

    @Delete
    suspend fun delete(record: SkinRecordEntity)

    @Query("SELECT * FROM skin_records WHERE synced = 0")
    suspend fun getUnsynced(): List<SkinRecordEntity>

    @Query("UPDATE skin_records SET synced = 1 WHERE id = :id")
    suspend fun markSynced(id: String)
}

@Dao
interface SkincareProductDao {
    @Query("SELECT * FROM skincare_products ORDER BY name ASC")
    fun getAllProducts(): Flow<List<SkincareProductEntity>>

    @Query("SELECT * FROM skincare_products WHERE barcode = :barcode")
    suspend fun getByBarcode(barcode: String): SkincareProductEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: SkincareProductEntity)
}

@Dao
interface DailyProductUsageDao {
    @Query("SELECT * FROM daily_product_usage WHERE userId = :userId AND usedDate = :date")
    suspend fun getUsageForDate(userId: String, date: Long): List<DailyProductUsageEntity>

    @Query("SELECT * FROM daily_product_usage WHERE userId = :userId ORDER BY usedDate DESC")
    fun getAllUsage(userId: String): Flow<List<DailyProductUsageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(usage: DailyProductUsageEntity)

    // 一键复用昨天的记录
    @Query("SELECT * FROM daily_product_usage WHERE userId = :userId AND usedDate = :yesterday")
    suspend fun getYesterdayUsage(userId: String, yesterday: Long): List<DailyProductUsageEntity>
}
```

### Database 定义
```kotlin
@Database(
    entities = [
        SkinRecordEntity::class,
        SkincareProductEntity::class,
        DailyProductUsageEntity::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun skinRecordDao(): SkinRecordDao
    abstract fun skincareProductDao(): SkincareProductDao
    abstract fun dailyProductUsageDao(): DailyProductUsageDao
}
```

## 云端数据库 — Supabase PostgreSQL

### 表设计
```sql
-- 用户表（Supabase Auth自动管理，auth.users）
-- 额外用户信息用profiles表
CREATE TABLE profiles (
    id UUID REFERENCES auth.users(id) PRIMARY KEY,
    nickname TEXT,
    avatar_url TEXT,
    skin_type TEXT,          -- 用户设置的肤质
    gender TEXT,
    age_range TEXT,          -- 年龄段
    is_member BOOLEAN DEFAULT FALSE,
    member_expires_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- 皮肤记录表
CREATE TABLE skin_records (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id UUID REFERENCES auth.users(id) NOT NULL,
    skin_type TEXT NOT NULL,
    overall_score INTEGER,
    acne_count INTEGER,
    pore_score INTEGER,
    redness_score INTEGER,
    even_score INTEGER,
    blackhead_density INTEGER,
    notes TEXT,
    image_url TEXT,
    analysis_json JSONB,     -- AI分析完整结果
    recorded_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 护肤品表
CREATE TABLE skincare_products (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    name TEXT NOT NULL,
    brand TEXT,
    category TEXT NOT NULL,
    barcode TEXT UNIQUE,
    image_url TEXT,
    ingredients TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 每日护肤品使用记录
CREATE TABLE daily_product_usage (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id UUID REFERENCES auth.users(id) NOT NULL,
    product_id UUID REFERENCES skincare_products(id) NOT NULL,
    used_date DATE NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    UNIQUE(user_id, product_id, used_date)
);

-- 归因分析报告
CREATE TABLE analysis_reports (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id UUID REFERENCES auth.users(id) NOT NULL,
    report_type TEXT NOT NULL,    -- WEEKLY / MONTHLY / ATTRIBUTION
    report_json JSONB NOT NULL,
    period_start DATE NOT NULL,
    period_end DATE NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW()
);
```

### 索引
```sql
CREATE INDEX idx_skin_records_user_date ON skin_records(user_id, recorded_at);
CREATE INDEX idx_daily_usage_user_date ON daily_product_usage(user_id, used_date);
CREATE INDEX idx_reports_user ON analysis_reports(user_id, created_at);
```

### RLS (Row Level Security)
```sql
-- 用户只能访问自己的数据
ALTER TABLE skin_records ENABLE ROW LEVEL SECURITY;
CREATE POLICY "Users can only access own records"
    ON skin_records FOR ALL
    USING (auth.uid() = user_id);

ALTER TABLE daily_product_usage ENABLE ROW LEVEL SECURITY;
CREATE POLICY "Users can only access own usage"
    ON daily_product_usage FOR ALL
    USING (auth.uid() = user_id);

-- 护肤品表所有人可读
ALTER TABLE skincare_products ENABLE ROW LEVEL SECURITY;
CREATE POLICY "Products are readable by all"
    ON skincare_products FOR SELECT TO authenticated
    USING (true);
```

## Supabase Kotlin SDK 使用

```kotlin
// 查询
val records = supabase.postgrest["skin_records"]
    .select {
        filter {
            eq("user_id", userId)
            gte("recorded_at", startDate)
        }
        order("recorded_at", Order.DESCENDING)
        limit(count = 30)
    }
    .decodeList<SkinRecordDto>()

// 插入
supabase.postgrest["skin_records"]
    .insert(record)

// 更新
supabase.postgrest["skin_records"]
    .update({ set("notes", newNotes) }) {
        filter { eq("id", recordId) }
    }
```

## 离线同步策略
1. **写入**: 先写Room（synced=false），后台同步到Supabase
2. **读取**: 优先读Room，定期从Supabase拉取最新数据
3. **冲突**: 以Supabase为准（最后写入胜）
4. **离线**: 完全可用，网络恢复后批量同步

## 常见陷阱
1. **Room KMP需要KSP** — build.gradle.kts配置KSP编译器插件
2. **Supabase RLS必须开启** — 否则数据不隔离
3. **时间统一UTC** — 客户端转换为本地时间显示
4. **JSONB用于灵活结构** — AI分析结果用JSONB存储
5. **UUID作为主键** — 方便离线生成ID，不冲突
