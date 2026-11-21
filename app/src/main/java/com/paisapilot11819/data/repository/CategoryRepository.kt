package com.paisapilot11819.data.repository

import com.paisapilot11819.data.local.PaisaPilotDatabase
import com.paisapilot11819.data.local.entity.CategoryEntity
import com.paisapilot11819.data.local.entity.CategoryBudgetEntity
import kotlinx.coroutines.flow.Flow

class CategoryRepository(private val db: PaisaPilotDatabase) {
    private val categoryDao = db.categoryDao()
    private val budgetDao = db.categoryBudgetDao()

    fun getAllCategories(): Flow<List<CategoryEntity>> = categoryDao.getAll()

    fun getAllIncludingArchived(): Flow<List<CategoryEntity>> = categoryDao.getAllIncludingArchived()

    fun getCategoryById(id: Long): Flow<CategoryEntity?> = categoryDao.getById(id)

    suspend fun getCategoryByIdOnce(id: Long): CategoryEntity? = categoryDao.getByIdOnce(id)

    suspend fun createCategory(name: String): Long {
        return categoryDao.insert(CategoryEntity(name = name))
    }

    suspend fun updateCategory(category: CategoryEntity) = categoryDao.update(category)

    suspend fun archiveCategory(id: Long) = categoryDao.archive(id)

    // Budget operations
    fun getBudgetsForMonth(monthPlanId: Long): Flow<List<CategoryBudgetEntity>> =
        budgetDao.getByMonthPlan(monthPlanId)

    fun getBudgetForMonthAndCategory(monthPlanId: Long, categoryId: Long): Flow<CategoryBudgetEntity?> =
        budgetDao.getByMonthAndCategory(monthPlanId, categoryId)

    suspend fun getBudgetForMonthAndCategoryOnce(monthPlanId: Long, categoryId: Long): CategoryBudgetEntity? =
        budgetDao.getByMonthAndCategoryOnce(monthPlanId, categoryId)

    fun getTotalBudgetForMonth(monthPlanId: Long): Flow<Long?> =
        budgetDao.getTotalBudgetForMonth(monthPlanId)

    suspend fun getTotalBudgetForMonthOnce(monthPlanId: Long): Long? =
        budgetDao.getTotalBudgetForMonthOnce(monthPlanId)

    suspend fun setBudget(monthPlanId: Long, categoryId: Long, budgetPaise: Long): Long {
        val existing = budgetDao.getByMonthAndCategoryOnce(monthPlanId, categoryId)
        return if (existing != null) {
            budgetDao.update(existing.copy(budgetPaise = budgetPaise))
            existing.id
        } else {
            budgetDao.insert(
                CategoryBudgetEntity(
                    monthPlanId = monthPlanId,
                    categoryId = categoryId,
                    budgetPaise = budgetPaise
                )
            )
        }
    }

    suspend fun deleteBudget(budget: CategoryBudgetEntity) = budgetDao.delete(budget)

    suspend fun deleteBudgetByMonthAndCategory(monthPlanId: Long, categoryId: Long) =
        budgetDao.deleteByMonthAndCategory(monthPlanId, categoryId)
}
