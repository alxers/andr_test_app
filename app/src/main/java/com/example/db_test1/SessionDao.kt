package com.example.db_test1

import androidx.room.*

@Dao
@TypeConverters(DateConverter::class)
interface SessionDao {
//    @Query("SELECT * FROM sessions")
    // SQL select all sessions and count their "count" field
    @Query("SELECT SUM(count) FROM sessions")
    fun getAllSessionsCount(): Int

    @Query("SELECT EXISTS(SELECT * FROM sessions WHERE timestamp = :ts)")
    fun isRowIsExist(ts : Long) : Boolean

    @Query("SELECT * FROM sessions WHERE timestamp LIKE (:v)")
    fun getByTimestamp(v: Long): Session

    @Insert
    fun insertSession(newSession: Session)

    @Query("DELETE FROM sessions")
    fun clearDB()

    @Update
    fun updateSession(session: Session)

    @Query("UPDATE sessions SET count = :c WHERE id = :tid")
    fun updateCount(tid: Long, c: Int): Int
}
