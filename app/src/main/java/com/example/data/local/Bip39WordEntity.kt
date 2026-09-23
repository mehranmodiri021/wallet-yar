package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bip39_words")
data class Bip39WordEntity(
    @PrimaryKey val wordIndex: Int,
    val word: String
)
