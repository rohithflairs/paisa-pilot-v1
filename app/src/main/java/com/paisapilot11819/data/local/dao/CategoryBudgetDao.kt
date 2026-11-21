package com.paisapilot11819.data.local.dao

import androidx.room.*
import com.paisapilot11819.data.local.entity.CategoryBudgetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryBudgetDao {

    @Query("SELECT * FROM category_budgets WHERE monthPlanId = :monthPlanId")
    fun getByMonthPlan(monthPlanId: Long): Flow<List<CategoryBudgetEntity>>

    @Query("SELECT * FROM category_budgets WHERE monthPlanId = :monthPlanId AND categoryId = :categoryId LIMIT 1")
    fun getByMonthAndCategory(monthPlanId: Long, categoryId: Long): Flow<CategoryBudgetEntity?>

    @Query("SELECT * FROM category_budgets WHERE monthPlanId = :monthPlanId AND categoryId = :categoryId LIMIT 1")
    suspend fun getByMonthAndCategoryOnce(monthPlanId: Long, categoryId: Long): CategoryBudgetEntity?

    @Query("SELECT SUM(budgetPaise) FROM category_budgets WHERE monthPlanId = :monthPlanId")
    fun getTotalBudgetForMonth(monthPlanId: Long): Flow<Long?>

    @Query("SELECT SUM(budgetPaise) FROM category_budgets WHERE monthPlanId = :monthPlanId")
    suspend fun getTotalBudgetForMonthOnce(monthPlanId: Long): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(budget: CategoryBudgetEntity): Long

    @Update
    suspend fun update(budget: CategoryBudgetEntity)

    @Delete
    suspend fun delete(budget: CategoryBudgetEntity)

    @Query("DELETE FROM category_budgets WHERE monthPlanId = :monthPlanId AND categoryId = :categoryId")
    suspend fun deleteByMonthAndCategory(monthPlanId: Long, categoryId: Long)
}
