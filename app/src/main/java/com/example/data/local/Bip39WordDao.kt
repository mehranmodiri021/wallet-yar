package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface Bip39WordDao {
    @Query("SELECT * FROM bip39_words ORDER BY wordIndex ASC")
    fun getAllWords(): Flow<List<Bip39WordEntity>>

    @Query("SELECT * FROM bip39_words ORDER BY wordIndex ASC")
    suspend fun getAllWordsList(): List<Bip39WordEntity>

    @Query("SELECT COUNT(*) FROM bip39_words")
    suspend fun getWordCount(): Int

    @Query("SELECT * FROM bip39_words WHERE word = :word LIMIT 1")
    suspend fun findWord(word: String): Bip39WordEntity?

    @Query("SELECT word FROM bip39_words WHERE word LIKE :query || '%' ORDER BY word ASC LIMIT :limit")
    suspend fun searchPrefix(query: String, limit: Int = 10): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(words: List<Bip39WordEntity>)
}
