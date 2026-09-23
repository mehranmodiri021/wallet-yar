package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScan(scan: ScanHistoryEntity): Long

    @Query("SELECT * FROM scan_history WHERE address = :address AND blockchain = :blockchain ORDER BY cachedAt DESC LIMIT 1")
    suspend fun getCachedScan(address: String, blockchain: String): ScanHistoryEntity?

    @Query("SELECT * FROM scan_history ORDER BY cachedAt DESC LIMIT 50")
    fun getAllScans(): Flow<List<ScanHistoryEntity>>

    @Query("SELECT COUNT(*) FROM scan_history")
    suspend fun getScanCount(): Int

    @Query("DELETE FROM scan_history WHERE cachedAt < :thresholdTime")
    suspend fun purgeOldCache(thresholdTime: Long)

    @Query("DELETE FROM scan_history")
    suspend fun clearAll()
}
