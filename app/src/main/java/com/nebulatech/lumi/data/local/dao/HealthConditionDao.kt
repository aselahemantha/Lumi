package com.nebulatech.lumi.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nebulatech.lumi.data.local.entity.HealthConditionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthConditionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(conditions: List<HealthConditionEntity>)

    @Query("SELECT * FROM user_health_conditions WHERE userId = :userId")
    fun getConditionsFlow(userId: String): Flow<List<HealthConditionEntity>>

    @Query("SELECT * FROM user_health_conditions WHERE userId = :userId")
    suspend fun getConditions(userId: String): List<HealthConditionEntity>

    @Query("DELETE FROM user_health_conditions WHERE userId = :userId")
    suspend fun deleteConditionsForUser(userId: String): Int
}
