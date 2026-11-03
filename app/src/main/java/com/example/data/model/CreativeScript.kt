package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "creative_scripts")
data class CreativeScript(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val style: String, // "KFC AV PRODUCTIONS", "ROOM 404", "REEL", "COMMERCIAL"
    val hook: String,
    val concept: String,
    val visualPlan: String,
    val audioCue: String,
    val scriptText: String,
    val callToAction: String,
    val createdAt: Long = System.currentTimeMillis()
)
