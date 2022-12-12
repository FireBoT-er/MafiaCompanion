package com.fbt.mafia.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [Player::class, Game::class, HistoryPlayers::class, HistoryActions::class], version = 9)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playerDao(): PlayerDao
    abstract fun gameDao(): GameDao
    abstract fun historyPlayersDao(): HistoryPlayersDao
    abstract fun historyActionsDao(): HistoryActionsDao

    companion object{
        private var db : AppDatabase? = null

        fun getDB(applicationContext: Context) : AppDatabase{
            if (db == null){
                db = Room.databaseBuilder(
                    applicationContext,
                    AppDatabase::class.java, "mafia-db"
                ).allowMainThreadQueries()
//                .fallbackToDestructiveMigration()
                .build()
            }

            return db as AppDatabase
        }
    }
}