package com.skintrack.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.skintrack.app.data.local.entity.DailyProductUsageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyProductUsageDao {
    @Query("SELECT * FROM daily_product_usage WHERE userId = :userId AND usedDate = :date")
    fun getUsageByDate(userId: String, date: Long): Flow<List<DailyProductUsageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(usage: DailyProductUsageEntity)

    @Query("SELECT * FROM daily_product_usage WHERE synced = 0")
    suspend fun getUnsynced(): List<DailyProductUsageEntity>

    @Query("DELETE FROM daily_product_usage WHERE userId = :userId AND productId = :productId AND usedDate = :date")
    suspend fun deleteByUserAndProductAndDate(userId: String, productId: String, date: Long)

    @Query("UPDATE daily_product_usage SET synced = 1 WHERE id = :id")
    suspend fun markSynced(id: String)
}
