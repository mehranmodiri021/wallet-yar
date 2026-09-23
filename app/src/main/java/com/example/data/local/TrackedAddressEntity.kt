package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * ذخیره آدرس‌های عمومی در فهرست تحت‌نظر (Watchlist)
 * هشدار: فقط آدرس عمومی ذخیره می‌شود، هرگز کلید خصوصی یا Seed ذخیره نمی‌شود!
 */
@Entity(tableName = "tracked_addresses")
data class TrackedAddressEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val label: String,
    val address: String,
    val blockchain: String,
    val lastKnownBalance: String = "0.0",
    val status: String = "در حال بررسی", // دارای موجودی، بدون تراکنش، در حال بررسی
    val notes: String = "",
    val addedAt: Long = System.currentTimeMillis()
)

@Dao
interface TrackedAddressDao {
    @Query("SELECT * FROM tracked_addresses ORDER BY addedAt DESC")
    fun getAllTrackedAddresses(): Flow<List<TrackedAddressEntity>>

    @Query("SELECT COUNT(*) FROM tracked_addresses")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(address: TrackedAddressEntity): Long

    @Update
    suspend fun update(address: TrackedAddressEntity)

    @Delete
    suspend fun delete(address: TrackedAddressEntity)

    @Query("DELETE FROM tracked_addresses")
    suspend fun clearAll()
}
