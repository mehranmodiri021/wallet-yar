package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * لاگ تاریخچه استعلام آدرس‌های بلاک‌چین به همراه کش ۲۴ ساعته
 */
@Entity(tableName = "scan_history")
data class ScanHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val address: String,
    val blockchain: String,
    val balance: String,
    val balanceSymbol: String,
    val txCount: Int,
    val cachedAt: Long = System.currentTimeMillis(),
    val details: String = ""
)
