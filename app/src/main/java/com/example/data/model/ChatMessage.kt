package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sender: MessageSender,
    val content: String,
    val category: String = "SYSTEM", // "PLANNING", "BUSINESS", "CREATIVE", "SYSTEM", "DECISION"
    val timestamp: Long = System.currentTimeMillis()
)

enum class MessageSender {
    USER,
    GA
}
