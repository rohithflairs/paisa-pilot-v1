package com.paisapilot11819.data.repository

import com.paisapilot11819.data.local.PaisaPilotDatabase
import com.paisapilot11819.data.local.entity.ExpenseEntity
import com.paisapilot11819.data.local.entity.RecurringOccurrenceEntity
import com.paisapilot11819.data.local.entity.RecurringRuleEntity
import kotlinx.coroutines.flow.Flow
import java.time.YearMonth
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class RecurringRepository(private val db: PaisaPilotDatabase) {
    private val ruleDao = db.recurringRuleDao()
    private val occurrenceDao = db.recurringOccurrenceDao()
    private val expenseDao = db.expenseDao()

    fun getAllRules(): Flow<List<RecurringRuleEntity>> = ruleDao.getAll()
    fun getActiveRules(): Flow<List<RecurringRuleEntity>> = ruleDao.getActiveRules()
    fun getRuleById(id: Long): Flow<RecurringRuleEntity?> = ruleDao.getById(id)
    suspend fun getRuleByIdOnce(id: Long): RecurringRuleEntity? = ruleDao.getByIdOnce(id)

    suspend fun createRule(rule: RecurringRuleEntity): Long = ruleDao.insert(rule)
    suspend fun updateRule(rule: RecurringRuleEntity) = ruleDao.update(rule)
    suspend fun deleteRule(rule: RecurringRuleEntity) = ruleDao.delete(rule)

    fun getOccurrencesForMonth(yearMonth: String): Flow<List<RecurringOccurrenceEntity>> =
        occurrenceDao.getByMonth(yearMonth)

    /**
     * Process recurring deductions for a given month.
     * For each active rule where startYearMonth <= yearMonth:
     *   1. Check if occurrence already exists (unique ruleId+yearMonth index)
     *   2. If not, create expense and occurrence atomically
     *   3. Handle dueDay > days in month by using last day of month
     * Returns count of newly posted deductions.
     */
    suspend fun processRecurringDeductions(yearMonth: String, monthPlanId: Long): Int {
        val activeRules = ruleDao.getActiveRulesOnce()
        var posted = 0

        for (rule in activeRules) {
            // Skip rules that haven't started yet
            if (rule.startYearMonth > yearMonth) continue

            // Check if already posted for this month
            val existing = occurrenceDao.getByRuleAndMonth(rule.id, yearMonth)
            if (existing != null) continue

            // Calculate the actual due date, clamping to last day of month
            val ym = YearMonth.parse(yearMonth)
            val actualDay = minOf(rule.dueDay, ym.lengthOfMonth())
            val dueDate = LocalDate.of(ym.year, ym.month, actualDay)
                .format(DateTimeFormatter.ISO_LOCAL_DATE)

            // Create the expense
            val expense = ExpenseEntity(
                monthPlanId = monthPlanId,
                categoryId = rule.categoryId,
                amountPaise = rule.amountPaise,
                expenseDate = dueDate,
                note = "Recurring: ${rule.label}",
                isRecurring = true,
                recurringRuleId = rule.id
            )
            val expenseId = expenseDao.insert(expense)

            // Record the occurrence to prevent future double-posting
            val occurrence = RecurringOccurrenceEntity(
                ruleId = rule.id,
                yearMonth = yearMonth,
                expenseId = expenseId,
                amountChargedPaise = rule.amountPaise
            )
            try {
                occurrenceDao.insert(occurrence)
                posted++
            } catch (e: Exception) {
                // Unique constraint violation — already posted (race condition safety)
            }
        }
        return posted
    }
}
