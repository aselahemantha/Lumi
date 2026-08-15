package com.nebulatech.lumi.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nebulatech.lumi.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(user: UserEntity)

    @Query("SELECT * FROM users LIMIT 1")
    fun getFirstUserFlow(): Flow<UserEntity?>

    @Query("SELECT * FROM users LIMIT 1")
    suspend fun getFirstUser(): UserEntity?

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    fun getUserByIdFlow(userId: String): Flow<UserEntity?>

    @Query("UPDATE users SET email = :email, supabaseUid = :supabaseUid, isSynced = 1, updatedAt = :updatedAt WHERE id = :userId")
    suspend fun updateEmailAndAuth(userId: String, email: String, supabaseUid: String, updatedAt: String): Int

    @Query("UPDATE users SET isPremium = :isPremium, updatedAt = :updatedAt WHERE id = :userId")
    suspend fun updatePremiumStatus(userId: String, isPremium: Boolean, updatedAt: String): Int

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUser(userId: String): Int
}
