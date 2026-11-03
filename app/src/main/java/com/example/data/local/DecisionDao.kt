package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.DecisionRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface DecisionDao {
    @Query("SELECT * FROM decision_records ORDER BY createdAt DESC")
    fun getAllDecisions(): Flow<List<DecisionRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDecision(decision: DecisionRecord): Long

    @Delete
    suspend fun deleteDecision(decision: DecisionRecord)
}
