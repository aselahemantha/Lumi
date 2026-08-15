package com.nebulatech.lumi.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nebulatech.lumi.data.local.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(profile: UserProfileEntity)

    @Query("SELECT * FROM user_profiles WHERE userId = :userId LIMIT 1")
    fun getProfileFlow(userId: String): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profiles WHERE userId = :userId LIMIT 1")
    suspend fun getProfile(userId: String): UserProfileEntity?

    @Query("UPDATE user_profiles SET cycleLength = :cycleLength, periodDuration = :periodDuration, primaryGoal = :primaryGoal, updatedAt = :updatedAt WHERE userId = :userId")
    suspend fun updateCycleSettings(userId: String, cycleLength: Int, periodDuration: Int, primaryGoal: String, updatedAt: String): Int

    @Query("UPDATE user_profiles SET notificationsEnabled = :enabled, updatedAt = :updatedAt WHERE userId = :userId")
    suspend fun updateNotifications(userId: String, enabled: Boolean, updatedAt: String): Int

    @Query("DELETE FROM user_profiles WHERE userId = :userId")
    suspend fun deleteProfile(userId: String): Int
}
