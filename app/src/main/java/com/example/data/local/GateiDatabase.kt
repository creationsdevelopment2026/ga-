package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.model.ChatMessage
import com.example.data.model.ClientProject
import com.example.data.model.CreativeScript
import com.example.data.model.DecisionRecord
import com.example.data.model.MeetingItem
import com.example.data.model.NoteItem
import com.example.data.model.TaskItem
import com.example.data.model.TransactionItem

@Database(
    entities = [
        TaskItem::class,
        CreativeScript::class,
        ClientProject::class,
        DecisionRecord::class,
        ChatMessage::class,
        MeetingItem::class,
        TransactionItem::class,
        NoteItem::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class GateiDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun creativeScriptDao(): CreativeScriptDao
    abstract fun clientProjectDao(): ClientProjectDao
    abstract fun decisionDao(): DecisionDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun meetingDao(): MeetingDao
    abstract fun transactionDao(): TransactionDao
    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile
        private var INSTANCE: GateiDatabase? = null

        fun getDatabase(context: Context): GateiDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GateiDatabase::class.java,
                    "gatei_ai_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
