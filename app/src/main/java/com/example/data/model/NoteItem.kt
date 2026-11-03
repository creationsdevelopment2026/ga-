package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val isPinned: Boolean = false,
    val priority: Priority = Priority.STRATEGIC,
    val category: String = "General",
    val updatedAt: Long = System.currentTimeMillis()
)
