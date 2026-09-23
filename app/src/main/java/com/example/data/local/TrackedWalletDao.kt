package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackedWalletDao {
    @Query("SELECT * FROM tracked_wallets ORDER BY timestamp DESC")
    fun getAllTrackedWallets(): Flow<List<TrackedWalletEntity>>

    @Query("SELECT * FROM tracked_wallets WHERE id = :id")
    suspend fun getWalletById(id: Int): TrackedWalletEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWallet(wallet: TrackedWalletEntity): Long

    @Update
    suspend fun updateWallet(wallet: TrackedWalletEntity)

    @Delete
    suspend fun deleteWallet(wallet: TrackedWalletEntity)

    @Query("DELETE FROM tracked_wallets WHERE id = :id")
    suspend fun deleteWalletById(id: Int)
}
