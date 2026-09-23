package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.bip39.Bip39WordList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        TrackedWalletEntity::class,
        TrackedAddressEntity::class,
        ScanHistoryEntity::class,
        Bip39WordEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trackedWalletDao(): TrackedWalletDao
    abstract fun trackedAddressDao(): TrackedAddressDao
    abstract fun scanHistoryDao(): ScanHistoryDao
    abstract fun bip39WordDao(): Bip39WordDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "crypto_recovery_db"
                ).fallbackToDestructiveMigration(dropAllTables = true)
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Pre-populate BIP-39 official 2048 words into Room database
                            CoroutineScope(Dispatchers.IO).launch {
                                getInstance(context).prepopulateBip39Words()
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    suspend fun prepopulateBip39Words() {
        val dao = bip39WordDao()
        if (dao.getWordCount() == 0) {
            val entities = Bip39WordList.words.mapIndexed { index, word ->
                Bip39WordEntity(wordIndex = index, word = word)
            }
            dao.insertAll(entities)
        }
    }
}
