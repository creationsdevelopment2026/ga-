package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meetings")
data class MeetingItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val attendee: String,
    val dateTime: String,
    val venue: String,
    val purpose: String,
    val status: String = "CONFIRMED", // CONFIRMED, RESCHEDULED, PENDING
    val draftMessage: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
