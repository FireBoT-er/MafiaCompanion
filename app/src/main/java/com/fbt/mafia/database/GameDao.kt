package com.fbt.mafia.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface GameDao {
    @Query("SELECT * FROM game")
    fun getAll(): List<Game>

    @Query("SELECT * FROM game WHERE id IN (:gamesIds)")
    fun loadAllByIds(gamesIds: IntArray): List<Game>

    @Query("SELECT * FROM game WHERE dateTime LIKE :dateTime LIMIT 1")
    fun findByTime(dateTime: String): Game

    @Insert
    fun insert(game: Game)

    @Update
    fun update(game: Game)

    @Delete
    fun delete(game: Game)
}