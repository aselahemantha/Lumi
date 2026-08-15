package com.nebulatech.lumi.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nebulatech.lumi.data.local.entity.CustomSymptomEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomSymptomDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(symptom: CustomSymptomEntity)

    @Query("SELECT * FROM custom_symptoms WHERE userId = :userId AND isActive = 1 ORDER BY sortOrder ASC")
    fun getActiveCustomSymptomsFlow(userId: String): Flow<List<CustomSymptomEntity>>

    @Query("SELECT * FROM custom_symptoms WHERE userId = :userId AND isActive = 1 ORDER BY sortOrder ASC")
    suspend fun getActiveCustomSymptoms(userId: String): List<CustomSymptomEntity>

    @Query("UPDATE custom_symptoms SET isActive = 0 WHERE id = :id")
    suspend fun softDelete(id: String): Int

    @Query("DELETE FROM custom_symptoms WHERE id = :id")
    suspend fun delete(id: String): Int
}
