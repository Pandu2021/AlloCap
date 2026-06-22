package com.pandu.allocap.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "allocation_settings")
data class AllocationSettings(
    @PrimaryKey val id: Int = 1, // Singleton settings
    val needsPercent: Float = 0.5f,
    val wantsPercent: Float = 0.3f,
    val savingsPercent: Float = 0.2f
)
