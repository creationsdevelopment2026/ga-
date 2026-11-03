package com.example.data.repository

import com.example.data.local.ChatMessageDao
import com.example.data.local.ClientProjectDao
import com.example.data.local.CreativeScriptDao
import com.example.data.local.DecisionDao
import com.example.data.local.GateiDatabase
import com.example.data.local.MeetingDao
import com.example.data.local.TaskDao
import com.example.data.model.ChatMessage
import com.example.data.model.ClientProject
import com.example.data.model.CreativeScript
import com.example.data.model.DecisionRecord
import com.example.data.model.MeetingItem
import com.example.data.model.TaskItem
import kotlinx.coroutines.flow.Flow

class GateiRepository(private val database: GateiDatabase) {
    private val taskDao: TaskDao = database.taskDao()
    private val scriptDao: CreativeScriptDao = database.creativeScriptDao()
    private val clientDao: ClientProjectDao = database.clientProjectDao()
    private val decisionDao: DecisionDao = database.decisionDao()
    private val chatDao: ChatMessageDao = database.chatMessageDao()
    private val meetingDao: MeetingDao = database.meetingDao()
    private val transactionDao: com.example.data.local.TransactionDao = database.transactionDao()
    private val noteDao: com.example.data.local.NoteDao = database.noteDao()

    // Tasks
    val allTasks: Flow<List<TaskItem>> = taskDao.getAllTasks()
    suspend fun insertTask(task: TaskItem): Long = taskDao.insertTask(task)
    suspend fun updateTask(task: TaskItem) = taskDao.updateTask(task)
    suspend fun setTaskCompleted(id: Long, completed: Boolean) = taskDao.setTaskCompleted(id, completed)
    suspend fun deleteTask(id: Long) = taskDao.deleteTaskById(id)

    // Shilling Flow Transactions
    val allTransactions: Flow<List<com.example.data.model.TransactionItem>> = transactionDao.getAllTransactions()
    fun getTodayTransactions(startOfDay: Long): Flow<List<com.example.data.model.TransactionItem>> =
        transactionDao.getTodayTransactions(startOfDay)
    fun getTodayTotalSpent(startOfDay: Long): Flow<Double?> = transactionDao.getTodayTotalSpent(startOfDay)
    suspend fun insertTransaction(tx: com.example.data.model.TransactionItem): Long = transactionDao.insertTransaction(tx)
    suspend fun deleteTransaction(id: Long) = transactionDao.deleteTransactionById(id)

    // Notepad Notes
    val allNotes: Flow<List<com.example.data.model.NoteItem>> = noteDao.getAllNotes()
    suspend fun insertNote(note: com.example.data.model.NoteItem): Long = noteDao.insertNote(note)
    suspend fun updateNote(note: com.example.data.model.NoteItem) = noteDao.updateNote(note)
    suspend fun deleteNote(id: Long) = noteDao.deleteNoteById(id)

    // Scripts & Concepts
    val allScripts: Flow<List<CreativeScript>> = scriptDao.getAllScripts()
    suspend fun insertScript(script: CreativeScript): Long = scriptDao.insertScript(script)
    suspend fun deleteScript(id: Long) = scriptDao.deleteScriptById(id)

    // Client Projects
    val allProjects: Flow<List<ClientProject>> = clientDao.getAllProjects()
    suspend fun insertProject(project: ClientProject): Long = clientDao.insertProject(project)
    suspend fun updateProject(project: ClientProject) = clientDao.updateProject(project)
    suspend fun deleteProject(id: Long) = clientDao.deleteProjectById(id)

    // Decisions
    val allDecisions: Flow<List<DecisionRecord>> = decisionDao.getAllDecisions()
    suspend fun insertDecision(decision: DecisionRecord): Long = decisionDao.insertDecision(decision)

    // Chat
    val allMessages: Flow<List<ChatMessage>> = chatDao.getAllMessages()
    suspend fun insertMessage(message: ChatMessage): Long = chatDao.insertMessage(message)
    suspend fun clearMessages() = chatDao.clearAllMessages()

    // Meetings & Schedule
    val allMeetings: Flow<List<MeetingItem>> = meetingDao.getAllMeetings()
    suspend fun insertMeeting(meeting: MeetingItem): Long = meetingDao.insertMeeting(meeting)
    suspend fun updateMeeting(meeting: MeetingItem) = meetingDao.updateMeeting(meeting)
    suspend fun deleteMeeting(id: Long) = meetingDao.deleteMeeting(id)
}
