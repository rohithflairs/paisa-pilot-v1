package com.paisapilot11819.data.local.entity

import androidx.room.*

@Entity(
    tableName = "refunds",
    foreignKeys = [
        ForeignKey(entity = ExpenseEntity::class, parentColumns = ["id"], childColumns = ["originalExpenseId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = MonthPlanEntity::class, parentColumns = ["id"], childColumns = ["monthPlanId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [
        Index(value = ["originalExpenseId"]),
        Index(value = ["monthPlanId"])
    ]
)
data class RefundEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "originalExpenseId") val originalExpenseId: Long,
    @ColumnInfo(name = "monthPlanId") val monthPlanId: Long,
    @ColumnInfo(name = "amountPaise") val amountPaise: Long,
    @ColumnInfo(name = "dateReceived") val dateReceived: String,
    @ColumnInfo(name = "note") val note: String? = null,
    @ColumnInfo(name = "createdAt") val createdAt: Long = System.currentTimeMillis()
)
