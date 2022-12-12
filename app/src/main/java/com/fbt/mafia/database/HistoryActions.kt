package com.fbt.mafia.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * @param action:
 * 1 - выставлен на голосование;
 * 2 - выбыл по голосованию;
 * 3 - убит мафией;
 * 4 - мафия промахнулась.
 */
@Entity(foreignKeys = [ForeignKey(entity = Game::class,
                        parentColumns = arrayOf("id"),
                        childColumns = arrayOf("id_game"),
                        onDelete = ForeignKey.CASCADE),
                       ForeignKey(entity = Player::class,
                        parentColumns = arrayOf("id"),
                        childColumns = arrayOf("id_player"),
                        onDelete = ForeignKey.CASCADE)])
data class HistoryActions (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(index = true)
    var id_game: Int,
    @ColumnInfo
    var round: Int,
    @ColumnInfo
    var action: Int,
    @ColumnInfo(index = true)
    val id_player: Int
)