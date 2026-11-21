package com.paisapilot11819.data.local.entity

import androidx.room.*

@Entity(
    tableName = "category_budgets",
    foreignKeys = [
        ForeignKey(entity = MonthPlanEntity::class, parentColumns = ["id"], childColumns = ["monthPlanId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = CategoryEntity::class, parentColumns = ["id"], childColumns = ["categoryId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [
        Index(value = ["monthPlanId", "categoryId"], unique = true),
        Index(value = ["monthPlanId"]),
        Index(value = ["categoryId"])
    ]
)
data class CategoryBudgetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "monthPlanId") val monthPlanId: Long,
    @ColumnInfo(name = "categoryId") val categoryId: Long,
    @ColumnInfo(name = "budgetPaise") val budgetPaise: Long
)
