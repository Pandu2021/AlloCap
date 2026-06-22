package com.pandu.allocap.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val amount: Double,
    val category: String, // "Needs", "Wants", "Savings"
    val description: String,
    val isIncome: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
