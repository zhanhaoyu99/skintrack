package com.skintrack.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.skintrack.app.data.local.dao.AuthSessionDao
import com.skintrack.app.data.local.dao.DailyProductUsageDao
import com.skintrack.app.data.local.dao.SkinRecordDao
import com.skintrack.app.data.local.dao.SkincareProductDao
import com.skintrack.app.data.local.entity.AuthSessionEntity
import com.skintrack.app.data.local.entity.DailyProductUsageEntity
import com.skintrack.app.data.local.entity.SkinRecordEntity
import com.skintrack.app.data.local.entity.SkincareProductEntity

@Database(
    entities = [
        SkinRecordEntity::class,
        SkincareProductEntity::class,
        DailyProductUsageEntity::class,
        AuthSessionEntity::class,
    ],
    version = 2,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun skinRecordDao(): SkinRecordDao
    abstract fun skincareProductDao(): SkincareProductDao
    abstract fun dailyProductUsageDao(): DailyProductUsageDao
    abstract fun authSessionDao(): AuthSessionDao
}
