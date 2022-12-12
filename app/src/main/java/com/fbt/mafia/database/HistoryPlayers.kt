package com.fbt.mafia.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(foreignKeys = [ForeignKey(entity = Game::class,
                        parentColumns = arrayOf("id"),
                        childColumns = arrayOf("id_game"),
                        onDelete = ForeignKey.CASCADE),
                       ForeignKey(entity = Player::class,
                        parentColumns = arrayOf("id"),
                        childColumns = arrayOf("id_player"),
                        onDelete = ForeignKey.CASCADE)])
data class HistoryPlayers (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(index = true)
    val id_game: Int,
    @ColumnInfo(index = true)
    val id_player: Int,
    @ColumnInfo
    var role: String
)