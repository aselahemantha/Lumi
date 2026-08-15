package com.nebulatech.lumi.data.repository

import com.nebulatech.lumi.core.domain.DataError
import com.nebulatech.lumi.core.domain.Result
import com.nebulatech.lumi.data.local.dao.LhTestDao
import com.nebulatech.lumi.data.mapper.toLhTest
import com.nebulatech.lumi.data.mapper.toLhTestEntity
import com.nebulatech.lumi.data.model.LhIntensityType
import com.nebulatech.lumi.data.model.LhTest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

interface LhTestRepository {
    fun getLhTestForDate(userId: String, date: LocalDate): Flow<LhTest?>
    fun getLhTestsForCycle(cycleId: String): Flow<List<LhTest>>
    fun getLhTestsInRange(userId: String, fromDate: LocalDate, toDate: LocalDate): Flow<List<LhTest>>
    suspend fun saveLhTest(
        userId: String,
        date: LocalDate,
        intensity: LhIntensityType,
        testBrand: String? = null,
        photoLocalPath: String? = null,
        cycleId: String? = null,
        cycleDay: Int? = null
    ): Result<LhTest, DataError.Local>
}

class RoomLhTestRepository(
    private val lhTestDao: LhTestDao
) : LhTestRepository {

    override fun getLhTestForDate(userId: String, date: LocalDate): Flow<LhTest?> {
        return lhTestDao.getTestForDateFlow(userId, date.toString()).map { it?.toLhTest() }
    }

    override fun getLhTestsForCycle(cycleId: String): Flow<List<LhTest>> {
        return lhTestDao.getLhTestsForCycleFlow(cycleId).map { list -> list.map { it.toLhTest() } }
    }

    override fun getLhTestsInRange(userId: String, fromDate: LocalDate, toDate: LocalDate): Flow<List<LhTest>> {
        return lhTestDao.getLhTestsInRangeFlow(userId, fromDate.toString(), toDate.toString()).map { list ->
            list.map { it.toLhTest() }
        }
    }

    override suspend fun saveLhTest(
        userId: String,
        date: LocalDate,
        intensity: LhIntensityType,
        testBrand: String?,
        photoLocalPath: String?,
        cycleId: String?,
        cycleDay: Int?
    ): Result<LhTest, DataError.Local> {
        return try {
            val dateString = date.toString()
            val existing = lhTestDao.getTestForDate(userId, dateString)
            val now = Instant.now().toString()
            val test = LhTest(
                id = existing?.id ?: UUID.randomUUID().toString(),
                userId = userId,
                cycleId = cycleId ?: existing?.cycleId,
                dailyLogId = existing?.dailyLogId,
                testDate = dateString,
                cycleDay = cycleDay ?: existing?.cycleDay,
                intensity = intensity,
                testBrand = testBrand ?: existing?.testBrand,
                photoLocalPath = photoLocalPath ?: existing?.photoLocalPath,
                photoRemoteUrl = existing?.photoRemoteUrl,
                createdAt = existing?.createdAt ?: now,
                updatedAt = now,
                isSynced = false
            )
            lhTestDao.insertOrUpdate(test.toLhTestEntity())
            Result.Success(test)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(DataError.Local.UNKNOWN)
        }
    }
}
