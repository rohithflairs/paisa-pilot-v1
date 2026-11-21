package com.paisapilot11819.data.repository

import com.paisapilot11819.data.local.PaisaPilotDatabase
import com.paisapilot11819.data.local.entity.MonthPlanEntity
import kotlinx.coroutines.flow.Flow

class MonthRepository(private val db: PaisaPilotDatabase) {
    private val dao = db.monthPlanDao()

    fun getAllMonths(): Flow<List<MonthPlanEntity>> = dao.getAll()

    fun getByYearMonth(yearMonth: String): Flow<MonthPlanEntity?> = dao.getByYearMonth(yearMonth)

    suspend fun getByYearMonthOnce(yearMonth: String): MonthPlanEntity? = dao.getByYearMonthOnce(yearMonth)

    fun getById(id: Long): Flow<MonthPlanEntity?> = dao.getById(id)

    suspend fun getByIdOnce(id: Long): MonthPlanEntity? = dao.getByIdOnce(id)

    suspend fun createMonth(
        yearMonth: String,
        openingBalancePaise: Long,
        dailyLimitPaise: Long? = null,
        weeklyLimitPaise: Long? = null
    ): Long {
        val plan = MonthPlanEntity(
            yearMonth = yearMonth,
            openingBalancePaise = openingBalancePaise,
            dailyLimitPaise = dailyLimitPaise,
            weeklyLimitPaise = weeklyLimitPaise,
            isSetUp = true
        )
        return dao.insert(plan)
    }

    suspend fun updateMonth(plan: MonthPlanEntity) = dao.update(plan)

    suspend fun deleteMonth(plan: MonthPlanEntity) = dao.delete(plan)
}
