package com.paisapilot11819.data.local.entity

import androidx.room.*

@Entity(
    tableName = "expenses",
    foreignKeys = [
        ForeignKey(entity = MonthPlanEntity::class, parentColumns = ["id"], childColumns = ["monthPlanId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [
        Index(value = ["monthPlanId"]),
        Index(value = ["categoryId"]),
        Index(value = ["expenseDate"]),
        Index(value = ["recurringRuleId"])
    ]
)
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "monthPlanId") val monthPlanId: Long,
    @ColumnInfo(name = "categoryId") val categoryId: Long? = null,
    @ColumnInfo(name = "amountPaise") val amountPaise: Long,
    @ColumnInfo(name = "expenseDate") val expenseDate: String, // "2026-11-21"
    @ColumnInfo(name = "note") val note: String? = null,
    @ColumnInfo(name = "isRecurring") val isRecurring: Boolean = false,
    @ColumnInfo(name = "recurringRuleId") val recurringRuleId: Long? = null,
    @ColumnInfo(name = "createdAt") val createdAt: Long = System.currentTimeMillis()
)
