package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "shilling_flow_transactions")
data class TransactionItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amountKsh: Double,
    val purpose: String,
    val paymentMethod: PaymentMethod = PaymentMethod.CASH,
    val category: TransactionCategory = TransactionCategory.PERSONAL,
    val timestamp: Long = System.currentTimeMillis(),
    val timeFormatted: String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
)

enum class PaymentMethod(val label: String) {
    CASH("Cash"),
    MPESA("M-PESA (Recorded Ledger Only)")
}

enum class TransactionCategory(val label: String) {
    FOOD("Food & Dining"),
    TRANSPORT("Transport"),
    PRODUCTION("Production & Gear"),
    PERSONAL("Personal / Lifestyle"),
    OPERATIONS("Operations & Bills"),
    OTHER("Other")
}
