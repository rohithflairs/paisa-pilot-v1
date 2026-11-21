package com.paisapilot11819.data.local.entity

import androidx.room.*

@Entity(tableName = "recurring_rules")
data class RecurringRuleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "label") val label: String,
    @ColumnInfo(name = "amountPaise") val amountPaise: Long,
    @ColumnInfo(name = "categoryId") val categoryId: Long? = null,
    @ColumnInfo(name = "startYearMonth") val startYearMonth: String,
    @ColumnInfo(name = "dueDay") val dueDay: Int,
    @ColumnInfo(name = "isActive") val isActive: Boolean = true,
    @ColumnInfo(name = "createdAt") val createdAt: Long = System.currentTimeMillis()
)
