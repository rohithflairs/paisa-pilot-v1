package com.paisapilot11819.data.local.entity

import androidx.room.*

@Entity(
    tableName = "recurring_occurrences",
    foreignKeys = [
        ForeignKey(entity = RecurringRuleEntity::class, parentColumns = ["id"], childColumns = ["ruleId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = ExpenseEntity::class, parentColumns = ["id"], childColumns = ["expenseId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [
        Index(value = ["ruleId", "yearMonth"], unique = true),
        Index(value = ["ruleId"]),
        Index(value = ["expenseId"])
    ]
)
data class RecurringOccurrenceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "ruleId") val ruleId: Long,
    @ColumnInfo(name = "yearMonth") val yearMonth: String,
    @ColumnInfo(name = "expenseId") val expenseId: Long,
    @ColumnInfo(name = "amountChargedPaise") val amountChargedPaise: Long
)
