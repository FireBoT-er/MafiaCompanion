package com.fbt.mafia.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Player (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo
    var nickname: String,
    @ColumnInfo
    var info: String?
)