package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.Category
import com.example.data.model.MessageSender
import com.example.data.model.Priority
import com.example.data.model.ProjectStatus

class Converters {
    @TypeConverter
    fun fromPriority(priority: Priority): String = priority.name

    @TypeConverter
    fun toPriority(value: String): Priority = runCatching { Priority.valueOf(value) }.getOrDefault(Priority.STRATEGIC)

    @TypeConverter
    fun fromCategory(category: Category): String = category.name

    @TypeConverter
    fun toCategory(value: String): Category = runCatching { Category.valueOf(value) }.getOrDefault(Category.BUSINESS)

    @TypeConverter
    fun fromProjectStatus(status: ProjectStatus): String = status.name

    @TypeConverter
    fun toProjectStatus(value: String): ProjectStatus = runCatching { ProjectStatus.valueOf(value) }.getOrDefault(ProjectStatus.PRE_PRODUCTION)

    @TypeConverter
    fun fromMessageSender(sender: MessageSender): String = sender.name

    @TypeConverter
    fun toMessageSender(value: String): MessageSender = runCatching { MessageSender.valueOf(value) }.getOrDefault(MessageSender.USER)

    @TypeConverter
    fun fromPaymentMethod(method: com.example.data.model.PaymentMethod): String = method.name

    @TypeConverter
    fun toPaymentMethod(value: String): com.example.data.model.PaymentMethod =
        runCatching { com.example.data.model.PaymentMethod.valueOf(value) }.getOrDefault(com.example.data.model.PaymentMethod.CASH)

    @TypeConverter
    fun fromTransactionCategory(cat: com.example.data.model.TransactionCategory): String = cat.name

    @TypeConverter
    fun toTransactionCategory(value: String): com.example.data.model.TransactionCategory =
        runCatching { com.example.data.model.TransactionCategory.valueOf(value) }.getOrDefault(com.example.data.model.TransactionCategory.PERSONAL)
}
