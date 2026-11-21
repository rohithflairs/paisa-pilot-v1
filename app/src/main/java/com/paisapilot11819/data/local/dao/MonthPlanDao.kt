package com.paisapilot11819.data.local.dao

import androidx.room.*
import com.paisapilot11819.data.local.entity.MonthPlanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MonthPlanDao {

    @Query("SELECT * FROM month_plans ORDER BY yearMonth DESC")
    fun getAll(): Flow<List<MonthPlanEntity>>

    @Query("SELECT * FROM month_plans WHERE yearMonth = :yearMonth LIMIT 1")
    fun getByYearMonth(yearMonth: String): Flow<MonthPlanEntity?>

    @Query("SELECT * FROM month_plans WHERE yearMonth = :yearMonth LIMIT 1")
    suspend fun getByYearMonthOnce(yearMonth: String): MonthPlanEntity?

    @Query("SELECT * FROM month_plans WHERE id = :id LIMIT 1")
    fun getById(id: Long): Flow<MonthPlanEntity?>

    @Query("SELECT * FROM month_plans WHERE id = :id LIMIT 1")
    suspend fun getByIdOnce(id: Long): MonthPlanEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(plan: MonthPlanEntity): Long

    @Update
    suspend fun update(plan: MonthPlanEntity)

    @Delete
    suspend fun delete(plan: MonthPlanEntity)
}
