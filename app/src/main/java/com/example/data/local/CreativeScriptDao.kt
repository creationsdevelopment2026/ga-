package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.CreativeScript
import kotlinx.coroutines.flow.Flow

@Dao
interface CreativeScriptDao {
    @Query("SELECT * FROM creative_scripts ORDER BY createdAt DESC")
    fun getAllScripts(): Flow<List<CreativeScript>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScript(script: CreativeScript): Long

    @Delete
    suspend fun deleteScript(script: CreativeScript)

    @Query("DELETE FROM creative_scripts WHERE id = :id")
    suspend fun deleteScriptById(id: Long)
}
