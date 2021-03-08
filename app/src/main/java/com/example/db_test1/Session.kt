package com.example.db_test1

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*
import java.sql.Date

@Entity(tableName = "sessions")
data class Session(
    val count: Int,
    val timestamp: Long,
    @PrimaryKey(autoGenerate = true) val id:Long=0
)