package com.fbt.mafia.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface HistoryPlayersDao {
    @Query("SELECT * FROM historyplayers")
    fun getAll(): List<HistoryPlayers>

    @Query("SELECT * FROM historyplayers WHERE id_game IN (:gamesIds)")
    fun loadAllByGamesIds(gamesIds: IntArray): List<HistoryPlayers>

    @Query("SELECT * FROM historyplayers WHERE id_player IN (:playersIds)")
    fun loadAllByPLayersIds(playersIds: IntArray): List<HistoryPlayers>

    @Insert
    fun insert(historyplayers: HistoryPlayers)

    @Update
    fun update(historyplayers: HistoryPlayers)

    @Delete
    fun delete(historyplayers: HistoryPlayers)
}