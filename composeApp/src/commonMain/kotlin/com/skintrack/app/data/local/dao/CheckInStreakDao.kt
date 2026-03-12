package com.skintrack.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.skintrack.app.data.local.entity.CheckInStreakEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CheckInStreakDao {
    @Query("SELECT * FROM check_in_streak WHERE userId = :userId")
    fun observe(userId: String): Flow<CheckInStreakEntity?>

    @Query("SELECT * FROM check_in_streak WHERE userId = :userId")
    suspend fun get(userId: String): CheckInStreakEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(entity: CheckInStreakEntity)
}
