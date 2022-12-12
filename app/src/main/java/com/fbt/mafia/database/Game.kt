package com.fbt.mafia.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Game (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo
    var dateTime: String,
    @ColumnInfo
    var didMafiaWin: Int
)