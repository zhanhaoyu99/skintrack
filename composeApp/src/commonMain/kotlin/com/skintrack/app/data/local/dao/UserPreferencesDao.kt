package com.skintrack.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.skintrack.app.data.local.entity.UserPreferencesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserPreferencesDao {
    @Query("SELECT * FROM user_preferences WHERE id = 1")
    fun observePreferences(): Flow<UserPreferencesEntity?>

    @Query("SELECT * FROM user_preferences WHERE id = 1")
    suspend fun getPreferences(): UserPreferencesEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePreferences(preferences: UserPreferencesEntity)

    @Query("UPDATE user_preferences SET onboardingCompleted = :completed WHERE id = 1")
    suspend fun setOnboardingCompleted(completed: Boolean)

    @Query("UPDATE user_preferences SET reminderEnabled = :enabled WHERE id = 1")
    suspend fun setReminderEnabled(enabled: Boolean)

    @Query("UPDATE user_preferences SET reminderTime = :time WHERE id = 1")
    suspend fun setReminderTime(time: String)

    @Query("UPDATE user_preferences SET skinType = :skinType WHERE id = 1")
    suspend fun setSkinType(skinType: String)

    @Query("UPDATE user_preferences SET skinGoals = :goals WHERE id = 1")
    suspend fun setSkinGoals(goals: String?)

    @Query("UPDATE user_preferences SET weeklyReportEnabled = :enabled WHERE id = 1")
    suspend fun setWeeklyReportEnabled(enabled: Boolean)

    @Query("UPDATE user_preferences SET lastSyncTimestamp = :timestamp WHERE id = 1")
    suspend fun setLastSyncTimestamp(timestamp: String)

    @Query("SELECT lastSyncTimestamp FROM user_preferences WHERE id = 1")
    suspend fun getLastSyncTimestamp(): String?
}
