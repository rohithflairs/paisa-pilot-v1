package com.paisapilot11819.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.paisapilot11819.data.local.dao.*
import com.paisapilot11819.data.local.entity.*

@Database(
    entities = [
        MonthPlanEntity::class,
        CategoryEntity::class,
        CategoryBudgetEntity::class,
        ExpenseEntity::class,
        AddMoneyEntity::class,
        RefundEntity::class,
        RecurringRuleEntity::class,
        RecurringOccurrenceEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class PaisaPilotDatabase : RoomDatabase() {

    abstract fun monthPlanDao(): MonthPlanDao
    abstract fun categoryDao(): CategoryDao
    abstract fun categoryBudgetDao(): CategoryBudgetDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun addMoneyDao(): AddMoneyDao
    abstract fun refundDao(): RefundDao
    abstract fun recurringRuleDao(): RecurringRuleDao
    abstract fun recurringOccurrenceDao(): RecurringOccurrenceDao

    companion object {
        @Volatile
        private var INSTANCE: PaisaPilotDatabase? = null

        fun getDatabase(context: Context): PaisaPilotDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PaisaPilotDatabase::class.java,
                    "paisa_pilot_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
