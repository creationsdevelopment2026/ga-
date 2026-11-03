package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "client_projects")
data class ClientProject(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val clientName: String,
    val projectTitle: String,
    val status: ProjectStatus = ProjectStatus.PRE_PRODUCTION,
    val budget: String = "$1,500",
    val nextAction: String = "Deliver initial cut",
    val notes: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)

enum class ProjectStatus(val label: String, val stepIndex: Int) {
    LEAD("Lead / Inquiry", 0),
    PITCH_QUOTE("Pitch & Quote", 1),
    PRE_PRODUCTION("Pre-Production", 2),
    PRODUCTION("Production / Shoot", 3),
    POST_PRODUCTION("Post / Edit", 4),
    CLIENT_REVIEW("Review & Revision", 5),
    COMPLETED_PAID("Completed & Paid", 6)
}
