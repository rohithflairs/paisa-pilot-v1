package com.paisapilot11819.data.local.entity

import androidx.room.*

@Entity(
    tableName = "add_money",
    foreignKeys = [
        ForeignKey(entity = MonthPlanEntity::class, parentColumns = ["id"], childColumns = ["monthPlanId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index(value = ["monthPlanId"])]
)
data class AddMoneyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "monthPlanId") val monthPlanId: Long,
    @ColumnInfo(name = "amountPaise") val amountPaise: Long,
    @ColumnInfo(name = "dateReceived") val dateReceived: String,
    @ColumnInfo(name = "source") val source: String? = null,
    @ColumnInfo(name = "note") val note: String? = null,
    @ColumnInfo(name = "createdAt") val createdAt: Long = System.currentTimeMillis()
)
