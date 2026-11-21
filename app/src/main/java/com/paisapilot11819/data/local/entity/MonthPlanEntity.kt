package com.paisapilot11819.data.local.entity

import androidx.room.*

@Entity(
    tableName = "month_plans",
    indices = [Index(value = ["yearMonth"], unique = true)]
)
data class MonthPlanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "yearMonth") val yearMonth: String, // "2026-11"
    @ColumnInfo(name = "openingBalancePaise") val openingBalancePaise: Long,
    @ColumnInfo(name = "dailyLimitPaise") val dailyLimitPaise: Long? = null,
    @ColumnInfo(name = "weeklyLimitPaise") val weeklyLimitPaise: Long? = null,
    @ColumnInfo(name = "isSetUp") val isSetUp: Boolean = false,
    @ColumnInfo(name = "createdAt") val createdAt: Long = System.currentTimeMillis()
)
