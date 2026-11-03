package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.TransactionItem
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM shilling_flow_transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<TransactionItem>>

    @Query("SELECT * FROM shilling_flow_transactions WHERE timestamp >= :startOfDayTimestamp ORDER BY timestamp DESC")
    fun getTodayTransactions(startOfDayTimestamp: Long): Flow<List<TransactionItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionItem): Long

    @Update
    suspend fun updateTransaction(transaction: TransactionItem)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionItem)

    @Query("DELETE FROM shilling_flow_transactions WHERE id = :id")
    suspend fun deleteTransactionById(id: Long)

    @Query("SELECT SUM(amountKsh) FROM shilling_flow_transactions WHERE timestamp >= :startOfDayTimestamp")
    fun getTodayTotalSpent(startOfDayTimestamp: Long): Flow<Double?>
}
