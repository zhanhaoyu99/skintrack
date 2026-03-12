package com.skintrack.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.skintrack.app.data.local.entity.UserSubscriptionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserSubscriptionDao {
    @Query("SELECT * FROM user_subscription WHERE userId = :userId")
    fun observe(userId: String): Flow<UserSubscriptionEntity?>

    @Query("SELECT * FROM user_subscription WHERE userId = :userId")
    suspend fun get(userId: String): UserSubscriptionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(entity: UserSubscriptionEntity)

    @Query("DELETE FROM user_subscription WHERE userId = :userId")
    suspend fun clear(userId: String)
}
