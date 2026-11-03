package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.MeetingItem
import kotlinx.coroutines.flow.Flow

@Dao
interface MeetingDao {
    @Query("SELECT * FROM meetings ORDER BY id DESC")
    fun getAllMeetings(): Flow<List<MeetingItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeeting(meeting: MeetingItem): Long

    @Update
    suspend fun updateMeeting(meeting: MeetingItem)

    @Query("DELETE FROM meetings WHERE id = :id")
    suspend fun deleteMeeting(id: Long)
}
