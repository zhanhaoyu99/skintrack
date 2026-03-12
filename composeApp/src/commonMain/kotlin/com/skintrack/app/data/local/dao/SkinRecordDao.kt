package com.skintrack.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.skintrack.app.data.local.entity.SkinRecordEntity
import kotlinx.coroutines.flow.Flow

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

    @Query("SELECT COUNT(*) FROM skin_records WHERE userId = :userId")
    suspend fun countByUser(userId: String): Int
}
