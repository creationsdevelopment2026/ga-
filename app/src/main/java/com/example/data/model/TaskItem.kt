package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val priority: Priority = Priority.STRATEGIC,
    val category: Category = Category.BUSINESS,
    val deadline: String = "Today",
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

enum class Priority(val label: String, val level: Int) {
    CRITICAL("Critical", 1),
    STRATEGIC("Strategic", 2),
    OPERATIONAL("Operational", 3)
}

enum class Category(val label: String) {
    LIFE("Life System"),
    BUSINESS("AV Business"),
    CREATIVE("Creative"),
    SYSTEM("Systems/Learning")
}
