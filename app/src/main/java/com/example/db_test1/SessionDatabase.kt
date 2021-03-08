package com.example.db_test1

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(Session::class), version = 1)
abstract class SessionDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDao
}
