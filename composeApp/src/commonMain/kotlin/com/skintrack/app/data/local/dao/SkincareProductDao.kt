package com.skintrack.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.skintrack.app.data.local.entity.SkincareProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SkincareProductDao {
    @Query("SELECT * FROM skincare_products ORDER BY name ASC")
    fun getAll(): Flow<List<SkincareProductEntity>>

    @Query("SELECT * FROM skincare_products WHERE id = :id")
    suspend fun getById(id: String): SkincareProductEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: SkincareProductEntity)

    @Delete
    suspend fun delete(product: SkincareProductEntity)

    @Query("SELECT * FROM skincare_products WHERE synced = 0")
    suspend fun getUnsynced(): List<SkincareProductEntity>

    @Query("UPDATE skincare_products SET synced = 1 WHERE id = :id")
    suspend fun markSynced(id: String)

    @Query("SELECT * FROM skincare_products WHERE userId = :userId ORDER BY name ASC LIMIT :limit OFFSET :offset")
    suspend fun getProductsPaged(userId: String, limit: Int, offset: Int): List<SkincareProductEntity>

    @Query("SELECT COUNT(*) FROM skincare_products WHERE userId = :userId")
    suspend fun countByUser(userId: String): Int

    @Query("SELECT * FROM skincare_products WHERE userId = :userId AND (name LIKE '%' || :query || '%' OR brand LIKE '%' || :query || '%') ORDER BY name ASC")
    suspend fun searchProducts(userId: String, query: String): List<SkincareProductEntity>
}
