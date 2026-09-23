package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracked_wallets")
data class TrackedWalletEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val label: String,
    val address: String,
    val blockchain: String,
    val lastKnownBalance: String = "0.0",
    val status: String = "Investigating",
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
