package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "decision_records")
data class DecisionRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val query: String,
    val problem: String,
    val analysis: String,
    val recommendation: String,
    val actionSteps: String,
    val createdAt: Long = System.currentTimeMillis()
)
