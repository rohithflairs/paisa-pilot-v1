package com.paisapilot11819.data.repository

import com.paisapilot11819.data.local.PaisaPilotDatabase
import com.paisapilot11819.data.local.entity.ExpenseEntity
import com.paisapilot11819.data.local.entity.AddMoneyEntity
import com.paisapilot11819.data.local.entity.RefundEntity
import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val db: PaisaPilotDatabase) {
    private val expenseDao = db.expenseDao()
    private val addMoneyDao = db.addMoneyDao()
    private val refundDao = db.refundDao()

    // === Expenses ===
    fun getExpensesForMonth(monthPlanId: Long): Flow<List<ExpenseEntity>> =
        expenseDao.getByMonthPlan(monthPlanId)

    fun getExpensesForMonthAndCategory(monthPlanId: Long, categoryId: Long): Flow<List<ExpenseEntity>> =
        expenseDao.getByMonthAndCategory(monthPlanId, categoryId)

    fun getExpenseById(id: Long): Flow<ExpenseEntity?> = expenseDao.getById(id)

    suspend fun getExpenseByIdOnce(id: Long): ExpenseEntity? = expenseDao.getByIdOnce(id)

    fun getTotalExpensesForMonth(monthPlanId: Long): Flow<Long?> =
        expenseDao.getTotalForMonth(monthPlanId)

    suspend fun getTotalExpensesForMonthOnce(monthPlanId: Long): Long? =
        expenseDao.getTotalForMonthOnce(monthPlanId)

    fun getTotalExpensesForMonthAndCategory(monthPlanId: Long, categoryId: Long): Flow<Long?> =
        expenseDao.getTotalForMonthAndCategory(monthPlanId, categoryId)

    fun getTotalExpensesForDate(monthPlanId: Long, date: String): Flow<Long?> =
        expenseDao.getTotalForDate(monthPlanId, date)

    fun getTotalExpensesForDateRange(monthPlanId: Long, startDate: String, endDate: String): Flow<Long?> =
        expenseDao.getTotalForDateRange(monthPlanId, startDate, endDate)

    // Non-recurring totals for daily/weekly limits (recurring excluded per requirement)
    fun getNonRecurringTotalForDate(monthPlanId: Long, date: String): Flow<Long?> =
        expenseDao.getNonRecurringTotalForDate(monthPlanId, date)

    fun getNonRecurringTotalForDateRange(monthPlanId: Long, startDate: String, endDate: String): Flow<Long?> =
        expenseDao.getNonRecurringTotalForDateRange(monthPlanId, startDate, endDate)

    suspend fun addExpense(expense: ExpenseEntity): Long = expenseDao.insert(expense)

    suspend fun updateExpense(expense: ExpenseEntity) = expenseDao.update(expense)

    suspend fun deleteExpense(expense: ExpenseEntity) = expenseDao.delete(expense)

    // === Add Money ===
    fun getAddMoneyForMonth(monthPlanId: Long): Flow<List<AddMoneyEntity>> =
        addMoneyDao.getByMonthPlan(monthPlanId)

    fun getTotalAddMoneyForMonth(monthPlanId: Long): Flow<Long?> =
        addMoneyDao.getTotalForMonth(monthPlanId)

    suspend fun getTotalAddMoneyForMonthOnce(monthPlanId: Long): Long? =
        addMoneyDao.getTotalForMonthOnce(monthPlanId)

    fun getAddMoneyById(id: Long): Flow<AddMoneyEntity?> = addMoneyDao.getById(id)

    suspend fun getAddMoneyByIdOnce(id: Long): AddMoneyEntity? = addMoneyDao.getByIdOnce(id)

    suspend fun addMoney(addMoney: AddMoneyEntity): Long = addMoneyDao.insert(addMoney)

    suspend fun updateAddMoney(addMoney: AddMoneyEntity) = addMoneyDao.update(addMoney)

    suspend fun deleteAddMoney(addMoney: AddMoneyEntity) = addMoneyDao.delete(addMoney)

    // === Refunds ===
    fun getRefundsForMonth(monthPlanId: Long): Flow<List<RefundEntity>> =
        refundDao.getByMonthPlan(monthPlanId)

    fun getTotalRefundsForMonth(monthPlanId: Long): Flow<Long?> =
        refundDao.getTotalForMonth(monthPlanId)

    fun getTotalRefundsForMonthAndCategory(monthPlanId: Long, categoryId: Long): Flow<Long?> =
        refundDao.getTotalForMonthAndCategory(monthPlanId, categoryId)

    fun getRefundsByExpenseId(expenseId: Long): Flow<List<RefundEntity>> =
        refundDao.getByExpenseId(expenseId)

    fun getRefundById(id: Long): Flow<RefundEntity?> = refundDao.getById(id)

    suspend fun addRefund(refund: RefundEntity): Long = refundDao.insert(refund)

    suspend fun updateRefund(refund: RefundEntity) = refundDao.update(refund)

    suspend fun deleteRefund(refund: RefundEntity) = refundDao.delete(refund)
}
