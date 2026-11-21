package com.paisapilot11819.data.local.dao

import androidx.room.*
import com.paisapilot11819.data.local.entity.AddMoneyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AddMoneyDao {

    @Query("SELECT * FROM add_money WHERE monthPlanId = :monthPlanId ORDER BY dateReceived DESC")
    fun getByMonthPlan(monthPlanId: Long): Flow<List<AddMoneyEntity>>

    @Query("SELECT SUM(amountPaise) FROM add_money WHERE monthPlanId = :monthPlanId")
    fun getTotalForMonth(monthPlanId: Long): Flow<Long?>

    @Query("SELECT SUM(amountPaise) FROM add_money WHERE monthPlanId = :monthPlanId")
    suspend fun getTotalForMonthOnce(monthPlanId: Long): Long?

    @Query("SELECT * FROM add_money WHERE id = :id LIMIT 1")
    fun getById(id: Long): Flow<AddMoneyEntity?>

    @Query("SELECT * FROM add_money WHERE id = :id LIMIT 1")
    suspend fun getByIdOnce(id: Long): AddMoneyEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(addMoney: AddMoneyEntity): Long

    @Update
    suspend fun update(addMoney: AddMoneyEntity)

    @Delete
    suspend fun delete(addMoney: AddMoneyEntity)
}
