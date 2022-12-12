package com.fbt.mafia.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface PlayerDao {
    @Query("SELECT * FROM player")
    fun getAll(): List<Player>

    @Query("SELECT id FROM player LIMIT 1")
    fun getFirstID(): Int

    @Query("SELECT * FROM player WHERE id IN (:playerIds)")
    fun loadAllByIds(playerIds: IntArray): List<Player>

    @Query("SELECT * FROM player WHERE id LIKE :id LIMIT 1")
    fun findByID(id: Int): Player

    @Query("SELECT * FROM player WHERE nickname LIKE :nickname LIMIT 1")
    fun findByNickname(nickname: String): Player

    @Insert
    fun insert(player: Player)

    @Update
    fun update(player: Player)

    @Delete
    fun delete(player: Player)
}