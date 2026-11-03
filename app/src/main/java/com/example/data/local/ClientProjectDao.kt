package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ClientProject
import kotlinx.coroutines.flow.Flow

@Dao
interface ClientProjectDao {
    @Query("SELECT * FROM client_projects ORDER BY updatedAt DESC")
    fun getAllProjects(): Flow<List<ClientProject>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ClientProject): Long

    @Update
    suspend fun updateProject(project: ClientProject)

    @Delete
    suspend fun deleteProject(project: ClientProject)

    @Query("DELETE FROM client_projects WHERE id = :id")
    suspend fun deleteProjectById(id: Long)
}
