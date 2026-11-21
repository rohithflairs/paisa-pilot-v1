package com.paisapilot11819.data.local.dao

import androidx.room.*
import com.paisapilot11819.data.local.entity.RefundEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RefundDao {

    @Query("SELECT * FROM refunds WHERE monthPlanId = :monthPlanId ORDER BY dateReceived DESC")
    fun getByMonthPlan(monthPlanId: Long): Flow<List<RefundEntity>>

    @Query("SELECT SUM(amountPaise) FROM refunds WHERE monthPlanId = :monthPlanId")
    fun getTotalForMonth(monthPlanId: Long): Flow<Long?>

    @Query(
        "SELECT SUM(r.amountPaise) FROM refunds r " +
        "INNER JOIN expenses e ON r.originalExpenseId = e.id " +
        "WHERE r.monthPlanId = :monthPlanId AND e.categoryId = :categoryId"
    )
    fun getTotalForMonthAndCategory(monthPlanId: Long, categoryId: Long): Flow<Long?>

    @Query("SELECT * FROM refunds WHERE originalExpenseId = :expenseId")
    fun getByExpenseId(expenseId: Long): Flow<List<RefundEntity>>

    @Query("SELECT * FROM refunds WHERE id = :id LIMIT 1")
    fun getById(id: Long): Flow<RefundEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(refund: RefundEntity): Long

    @Update
    suspend fun update(refund: RefundEntity)

    @Delete
    suspend fun delete(refund: RefundEntity)
}
