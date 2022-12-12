package com.fbt.mafia.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface HistoryActionsDao {
    @Query("SELECT * FROM historyactions")
    fun getAll(): List<HistoryActions>

    @Query("SELECT * FROM historyactions WHERE id_game IN (:gamesIds)")
    fun loadAllByGamesIds(gamesIds: IntArray): List<HistoryActions>

    @Insert
    fun insert(historyactions: HistoryActions)

    @Update
    fun update(historyactions: HistoryActions)

    @Delete
    fun delete(historyactions: HistoryActions)
}