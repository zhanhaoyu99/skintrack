package com.skintrack.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.skintrack.app.data.local.entity.AuthSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AuthSessionDao {
    @Query("SELECT * FROM auth_session WHERE id = 1")
    fun observeSession(): Flow<AuthSessionEntity?>

    @Query("SELECT * FROM auth_session WHERE id = 1")
    suspend fun getSession(): AuthSessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSession(session: AuthSessionEntity)

    @Query("UPDATE auth_session SET displayName = :displayName WHERE id = 1")
    suspend fun updateDisplayName(displayName: String)

    @Query("DELETE FROM auth_session")
    suspend fun clearSession()
}
