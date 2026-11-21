package com.paisapilot11819.data.local.dao

import androidx.room.*
import com.paisapilot11819.data.local.entity.RecurringOccurrenceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecurringOccurrenceDao {

    @Query("SELECT * FROM recurring_occurrences WHERE ruleId = :ruleId AND yearMonth = :yearMonth LIMIT 1")
    suspend fun getByRuleAndMonth(ruleId: Long, yearMonth: String): RecurringOccurrenceEntity?

    @Query("SELECT * FROM recurring_occurrences WHERE yearMonth = :yearMonth")
    fun getByMonth(yearMonth: String): Flow<List<RecurringOccurrenceEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(occurrence: RecurringOccurrenceEntity): Long

    @Delete
    suspend fun delete(occurrence: RecurringOccurrenceEntity)
}
