package com.paisapilot11819.data.local.dao

import androidx.room.*
import com.paisapilot11819.data.local.entity.ExpenseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Query("SELECT * FROM expenses WHERE monthPlanId = :monthPlanId ORDER BY expenseDate DESC, createdAt DESC")
    fun getByMonthPlan(monthPlanId: Long): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE monthPlanId = :monthPlanId AND categoryId = :categoryId")
    fun getByMonthAndCategory(monthPlanId: Long, categoryId: Long): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE id = :id LIMIT 1")
    fun getById(id: Long): Flow<ExpenseEntity?>

    @Query("SELECT * FROM expenses WHERE id = :id LIMIT 1")
    suspend fun getByIdOnce(id: Long): ExpenseEntity?

    @Query("SELECT SUM(amountPaise) FROM expenses WHERE monthPlanId = :monthPlanId")
    fun getTotalForMonth(monthPlanId: Long): Flow<Long?>

    @Query("SELECT SUM(amountPaise) FROM expenses WHERE monthPlanId = :monthPlanId")
    suspend fun getTotalForMonthOnce(monthPlanId: Long): Long?

    @Query("SELECT SUM(amountPaise) FROM expenses WHERE monthPlanId = :monthPlanId AND categoryId = :categoryId")
    fun getTotalForMonthAndCategory(monthPlanId: Long, categoryId: Long): Flow<Long?>

    @Query("SELECT SUM(amountPaise) FROM expenses WHERE monthPlanId = :monthPlanId AND expenseDate = :date")
    fun getTotalForDate(monthPlanId: Long, date: String): Flow<Long?>

    @Query("SELECT SUM(amountPaise) FROM expenses WHERE monthPlanId = :monthPlanId AND expenseDate BETWEEN :startDate AND :endDate")
    fun getTotalForDateRange(monthPlanId: Long, startDate: String, endDate: String): Flow<Long?>

    @Query("SELECT SUM(amountPaise) FROM expenses WHERE monthPlanId = :monthPlanId AND expenseDate = :date AND isRecurring = 0")
    fun getNonRecurringTotalForDate(monthPlanId: Long, date: String): Flow<Long?>

    @Query("SELECT SUM(amountPaise) FROM expenses WHERE monthPlanId = :monthPlanId AND isRecurring = 0 AND expenseDate BETWEEN :startDate AND :endDate")
    fun getNonRecurringTotalForDateRange(monthPlanId: Long, startDate: String, endDate: String): Flow<Long?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expense: ExpenseEntity): Long

    @Update
    suspend fun update(expense: ExpenseEntity)

    @Delete
    suspend fun delete(expense: ExpenseEntity)
}
