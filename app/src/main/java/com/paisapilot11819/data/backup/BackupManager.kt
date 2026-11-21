package com.paisapilot11819.data.backup

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.paisapilot11819.data.local.PaisaPilotDatabase
import com.paisapilot11819.data.local.entity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

data class BackupData(
    val version: String = "1.0",
    val exportTimestamp: Long = System.currentTimeMillis(),
    val monthPlans: List<MonthPlanEntity>,
    val categories: List<CategoryEntity>,
    val categoryBudgets: List<CategoryBudgetEntity>,
    val expenses: List<ExpenseEntity>,
    val addMoneyEntries: List<AddMoneyEntity>,
    val refunds: List<RefundEntity>,
    val recurringRules: List<RecurringRuleEntity>,
    val recurringOccurrences: List<RecurringOccurrenceEntity>
)

data class ImportResult(
    val success: Boolean,
    val message: String,
    val counts: Map<String, Int> = emptyMap()
)

class BackupManager(private val db: PaisaPilotDatabase) {

    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    suspend fun exportToJson(): String = withContext(Dispatchers.IO) {
        val data = BackupData(
            monthPlans = db.monthPlanDao().getAll().first(),
            categories = db.categoryDao().getAllIncludingArchived().first(),
            categoryBudgets = getAllCategoryBudgets(),
            expenses = getAllExpenses(),
            addMoneyEntries = getAllAddMoney(),
            refunds = getAllRefunds(),
            recurringRules = db.recurringRuleDao().getAll().first(),
            recurringOccurrences = getAllOccurrences()
        )
        gson.toJson(data)
    }

    suspend fun importFromJson(json: String): ImportResult = withContext(Dispatchers.IO) {
        try {
            val data = gson.fromJson(json, BackupData::class.java)
                ?: return@withContext ImportResult(false, "Invalid backup file format")

            // Clear existing data (order matters due to foreign keys)
            db.clearAllTables()

            // Import in order (parents before children)
            data.monthPlans.forEach { db.monthPlanDao().insert(it) }
            data.categories.forEach { db.categoryDao().insert(it) }
            data.categoryBudgets.forEach { db.categoryBudgetDao().insert(it) }
            data.expenses.forEach { db.expenseDao().insert(it) }
            data.addMoneyEntries.forEach { db.addMoneyDao().insert(it) }
            data.refunds.forEach { db.refundDao().insert(it) }
            data.recurringRules.forEach { db.recurringRuleDao().insert(it) }
            data.recurringOccurrences.forEach {
                try { db.recurringOccurrenceDao().insert(it) } catch (_: Exception) {}
            }

            val counts = mapOf(
                "monthPlans" to data.monthPlans.size,
                "categories" to data.categories.size,
                "categoryBudgets" to data.categoryBudgets.size,
                "expenses" to data.expenses.size,
                "addMoneyEntries" to data.addMoneyEntries.size,
                "refunds" to data.refunds.size,
                "recurringRules" to data.recurringRules.size,
                "recurringOccurrences" to data.recurringOccurrences.size
            )

            ImportResult(true, "Successfully imported all data", counts)
        } catch (e: Exception) {
            ImportResult(false, "Import failed: ${e.message}")
        }
    }

    // Helper functions to get all records for tables that only have month-scoped queries
    private suspend fun getAllCategoryBudgets(): List<CategoryBudgetEntity> {
        val months = db.monthPlanDao().getAll().first()
        return months.flatMap { month ->
            db.categoryBudgetDao().getByMonthPlan(month.id).first()
        }
    }

    private suspend fun getAllExpenses(): List<ExpenseEntity> {
        val months = db.monthPlanDao().getAll().first()
        return months.flatMap { month ->
            db.expenseDao().getByMonthPlan(month.id).first()
        }
    }

    private suspend fun getAllAddMoney(): List<AddMoneyEntity> {
        val months = db.monthPlanDao().getAll().first()
        return months.flatMap { month ->
            db.addMoneyDao().getByMonthPlan(month.id).first()
        }
    }

    private suspend fun getAllRefunds(): List<RefundEntity> {
        val months = db.monthPlanDao().getAll().first()
        return months.flatMap { month ->
            db.refundDao().getByMonthPlan(month.id).first()
        }
    }

    private suspend fun getAllOccurrences(): List<RecurringOccurrenceEntity> {
        val months = db.monthPlanDao().getAll().first()
        return months.flatMap { month ->
            db.recurringOccurrenceDao().getByMonth(month.yearMonth).first()
        }
    }
}
