package com.nebulatech.lumi.data.repository

import com.nebulatech.lumi.core.domain.DataError
import com.nebulatech.lumi.core.domain.EmptyResult
import com.nebulatech.lumi.core.domain.Result
import com.nebulatech.lumi.data.local.dao.HealthConditionDao
import com.nebulatech.lumi.data.local.dao.UserDao
import com.nebulatech.lumi.data.local.dao.UserProfileDao
import com.nebulatech.lumi.data.local.database.LumiDatabase
import com.nebulatech.lumi.data.local.entity.HealthConditionEntity
import com.nebulatech.lumi.data.mapper.toUser
import com.nebulatech.lumi.data.mapper.toUserEntity
import com.nebulatech.lumi.data.mapper.toUserProfile
import com.nebulatech.lumi.data.mapper.toUserProfileEntity
import com.nebulatech.lumi.data.model.User
import com.nebulatech.lumi.data.model.UserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.Instant
import java.util.UUID

interface UserRepository {
    fun getCurrentUser(): Flow<User?>
    suspend fun getOrCreateUser(name: String): Result<User, DataError.Local>
    suspend fun getUserProfile(userId: String): Result<UserProfile?, DataError.Local>
    fun getUserProfileFlow(userId: String): Flow<UserProfile?>
    suspend fun saveUserProfile(profile: UserProfile): EmptyResult<DataError.Local>
    suspend fun updateEmailAndAuth(userId: String, email: String, supabaseUid: String): EmptyResult<DataError.Local>
    suspend fun clearAllData(): EmptyResult<DataError.Local>
}

class RoomUserRepository(
    private val userDao: UserDao,
    private val userProfileDao: UserProfileDao,
    private val healthConditionDao: HealthConditionDao,
    private val database: LumiDatabase
) : UserRepository {

    companion object {
        const val DEFAULT_LOCAL_USER_ID = "local-user-default"
    }

    override fun getCurrentUser(): Flow<User?> {
        return userDao.getFirstUserFlow().map { it?.toUser() }
    }

    override suspend fun getOrCreateUser(name: String): Result<User, DataError.Local> = withContext(Dispatchers.IO) {
        try {
            val existing = userDao.getFirstUser()
            if (existing != null) {
                Result.Success(existing.toUser())
            } else {
                val now = Instant.now().toString()
                val newUser = User(
                    id = DEFAULT_LOCAL_USER_ID,
                    name = name,
                    createdAt = now,
                    updatedAt = now,
                    memberSince = now
                )
                userDao.insertOrUpdate(newUser.toUserEntity())
                Result.Success(newUser)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override suspend fun getUserProfile(userId: String): Result<UserProfile?, DataError.Local> = withContext(Dispatchers.IO) {
        try {
            val profileEntity = userProfileDao.getProfile(userId)
            val conditions = healthConditionDao.getConditions(userId)
            Result.Success(profileEntity?.toUserProfile(conditions))
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override fun getUserProfileFlow(userId: String): Flow<UserProfile?> {
        return combine(
            userProfileDao.getProfileFlow(userId),
            healthConditionDao.getConditionsFlow(userId)
        ) { profileEntity, conditionEntities ->
            profileEntity?.toUserProfile(conditionEntities)
        }
    }

    override suspend fun saveUserProfile(profile: UserProfile): EmptyResult<DataError.Local> = withContext(Dispatchers.IO) {
        try {
            userProfileDao.insertOrUpdate(profile.toUserProfileEntity())
            val now = Instant.now().toString()
            val conditionEntities = profile.healthConditions.map { condition ->
                HealthConditionEntity(
                    id = UUID.randomUUID().toString(),
                    userId = profile.userId,
                    condition = condition.name,
                    createdAt = now,
                    isSynced = false
                )
            }
            healthConditionDao.deleteConditionsForUser(profile.userId)
            healthConditionDao.insertAll(conditionEntities)
            Result.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override suspend fun updateEmailAndAuth(
        userId: String,
        email: String,
        supabaseUid: String
    ): EmptyResult<DataError.Local> = withContext(Dispatchers.IO) {
        try {
            val user = userDao.getUserById(userId)
            if (user != null) {
                val updated = user.copy(
                    email = email,
                    supabaseUid = supabaseUid,
                    updatedAt = Instant.now().toString()
                )
                userDao.insertOrUpdate(updated)
                Result.Success(Unit)
            } else {
                Result.Error(DataError.Local.NOT_FOUND)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override suspend fun clearAllData(): EmptyResult<DataError.Local> = withContext(Dispatchers.IO) {
        try {
            database.clearAllTables()
            Result.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(DataError.Local.UNKNOWN)
        }
    }
}
