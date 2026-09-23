package com.example.data.repository

import com.example.data.local.TrackedWalletDao
import com.example.data.local.TrackedWalletEntity
import kotlinx.coroutines.flow.Flow

class WalletRepository(private val dao: TrackedWalletDao) {
    val allWallets: Flow<List<TrackedWalletEntity>> = dao.getAllTrackedWallets()

    suspend fun insertWallet(wallet: TrackedWalletEntity): Long = dao.insertWallet(wallet)

    suspend fun updateWallet(wallet: TrackedWalletEntity) = dao.updateWallet(wallet)

    suspend fun deleteWallet(wallet: TrackedWalletEntity) = dao.deleteWallet(wallet)

    suspend fun deleteWalletById(id: Int) = dao.deleteWalletById(id)

    suspend fun getWalletById(id: Int) = dao.getWalletById(id)
}
