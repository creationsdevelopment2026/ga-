package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.GeminiClient
import com.example.data.ai.GateiLocalEngine
import com.example.data.local.GateiDatabase
import com.example.data.model.Category
import com.example.data.model.ChatMessage
import com.example.data.model.ClientProject
import com.example.data.model.CreativeScript
import com.example.data.model.DecisionRecord
import com.example.data.model.MeetingItem
import com.example.data.model.MessageSender
import com.example.data.model.NoteItem
import com.example.data.model.PaymentMethod
import com.example.data.model.Priority
import com.example.data.model.ProjectStatus
import com.example.data.model.TaskItem
import com.example.data.model.TransactionCategory
import com.example.data.model.TransactionItem
import com.example.data.repository.GateiRepository
import com.example.ui.theme.AppThemeStyle
import com.example.util.ActionResult
import com.example.util.AutoVocalizeMode
import com.example.util.DeviceActionDispatcher
import com.example.util.SpeechRecognitionManager
import com.example.util.TtsManager
import com.example.util.VoiceProfile
import com.example.util.VoiceRecordingManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

enum class GateiSection(val title: String) {
    AGENT("JARVIS AGENT"),
    DESK("EXECUTIVE DESK"),
    RESEARCH("INTELLIGENCE VAULT")
}

class GateiViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs: SharedPreferences = application.getSharedPreferences("ga_executive_prefs", Context.MODE_PRIVATE)
    private val repository: GateiRepository
    val ttsManager: TtsManager = TtsManager(application)
    val voiceRecordingManager: VoiceRecordingManager = VoiceRecordingManager(application)
    val speechRecognitionManager: SpeechRecognitionManager = SpeechRecognitionManager(application)

    // Voice & Speech State
    val isSpeaking: StateFlow<Boolean> = ttsManager.isSpeaking
    val isVoiceEnabled: StateFlow<Boolean> = ttsManager.isVoiceEnabled
    val vocalizeMode: StateFlow<AutoVocalizeMode> = ttsManager.vocalizeMode
    val currentVoiceProfile: StateFlow<VoiceProfile> = ttsManager.currentProfile
    val voicePitch: StateFlow<Float> = ttsManager.pitch
    val voiceSpeed: StateFlow<Float> = ttsManager.speed
    val phoneticName: StateFlow<String> = ttsManager.phoneticPronunciation

    // Speech-to-Text Voice Command State
    val isListeningToVoiceCommand: StateFlow<Boolean> = speechRecognitionManager.isListening
    val recognizedVoiceText: StateFlow<String> = speechRecognitionManager.recognizedText
    val speechRmsDb: StateFlow<Float> = speechRecognitionManager.rmsDb
    val speechError: StateFlow<String?> = speechRecognitionManager.error

    // Ambient Wake Word Mode ("Hey GA" / "Shut GA")
    private val _isAmbientWakeWordActive = MutableStateFlow(false)
    val isAmbientWakeWordActive: StateFlow<Boolean> = _isAmbientWakeWordActive.asStateFlow()

    // User Voice Recording & Training State
    val isRecordingUserVoice: StateFlow<Boolean> = voiceRecordingManager.isRecording
    val isPlayingUserVoice: StateFlow<Boolean> = voiceRecordingManager.isPlayingSample
    val hasRecordedUserVoice: StateFlow<Boolean> = voiceRecordingManager.hasRecordedSample
    val recordingAmplitude: StateFlow<Float> = voiceRecordingManager.amplitude
    val recordingDurationSeconds: StateFlow<Int> = voiceRecordingManager.recordingDurationSeconds

    // Privacy & Sovereign Kill Switch
    private val _isKillSwitchActive = MutableStateFlow(prefs.getBoolean("kill_switch", false))
    val isKillSwitchActive: StateFlow<Boolean> = _isKillSwitchActive.asStateFlow()

    // Visual Theme Style
    private val _currentThemeStyle = MutableStateFlow(
        AppThemeStyle.entries.find { it.name == prefs.getString("theme_style", AppThemeStyle.TITANIUM_LUXE.name) }
            ?: AppThemeStyle.TITANIUM_LUXE
    )
    val currentThemeStyle: StateFlow<AppThemeStyle> = _currentThemeStyle.asStateFlow()

    private val _currentSection = MutableStateFlow(GateiSection.AGENT)
    val currentSection: StateFlow<GateiSection> = _currentSection.asStateFlow()

    private val _isThinking = MutableStateFlow(false)
    val isThinking: StateFlow<Boolean> = _isThinking.asStateFlow()

    // Data streams
    val messages: StateFlow<List<ChatMessage>>
    val tasks: StateFlow<List<TaskItem>>
    val meetings: StateFlow<List<MeetingItem>>
    val transactions: StateFlow<List<TransactionItem>>
    val todayTotalSpent: StateFlow<Double>
    val notes: StateFlow<List<NoteItem>>

    // Focus state
    private val _isFocusActive = MutableStateFlow(false)
    val isFocusActive: StateFlow<Boolean> = _isFocusActive.asStateFlow()

    private val _focusSecondsRemaining = MutableStateFlow(0)
    val focusSecondsRemaining: StateFlow<Int> = _focusSecondsRemaining.asStateFlow()

    private var focusTimerJob: Job? = null

    init {
        val db = GateiDatabase.getDatabase(application)
        repository = GateiRepository(db)

        messages = repository.allMessages.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        tasks = repository.allTasks.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        meetings = repository.allMeetings.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        transactions = repository.allTransactions.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        val startOfDay = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        todayTotalSpent = repository.getTodayTotalSpent(startOfDay).map { it ?: 0.0 }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            0.0
        )

        notes = repository.allNotes.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        seedInitialDataIfEmpty()
    }

    private fun seedInitialDataIfEmpty() {
        viewModelScope.launch {
            val existingMessages = repository.allMessages.first()
            if (existingMessages.isEmpty()) {
                val welcome = ChatMessage(
                    sender = MessageSender.GA,
                    content = "Good day, Sir Gatei. Jarvis protocols and executive systems are online.\n\nI am your all-round personal companion, executive secretary, and autonomous agent. You can instruct me by voice or text for anything you need:\n\n• Say \"Hey GA\" to wake me, or \"Shut GA\" to silence & enter sleep mode.\n• \"Schedule a meeting tomorrow at 2 PM\" (with conflict detection & drafts).\n• \"Log 50 KSh in Shilling Flow for lunch, cash\" (M-Pesa PIN is strictly protected).\n• \"Take a picture\" or \"Record a 30s video\" (saved privately, never posted).\n• \"Play music on YouTube / Spotify / Phoenix\".\n• \"Reschedule my to-do list in order of priorities\".\n• \"Edit my notepad: memo on marketing strategy\".\n\nStanding by for your command, Sir Gatei.",
                    category = "SYSTEM"
                )
                repository.insertMessage(welcome)
            }

            val existingMeetings = repository.allMeetings.first()
            if (existingMeetings.isEmpty()) {
                repository.insertMeeting(
                    MeetingItem(
                        title = "Titan Media Commercial Shoot",
                        attendee = "Marcus (Titan Director)",
                        dateTime = "Tomorrow, 2:00 PM - 4:00 PM",
                        venue = "Soundstage B",
                        purpose = "Principal 4K photography and commercial sound capture",
                        status = "CONFIRMED"
                    )
                )
            }

            val existingTasks = repository.allTasks.first()
            if (existingTasks.isEmpty()) {
                repository.insertTask(
                    TaskItem(
                        title = "Review high-leverage client retainers & contracts",
                        priority = Priority.CRITICAL,
                        category = Category.BUSINESS,
                        deadline = "10:00 AM"
                    )
                )
                repository.insertTask(
                    TaskItem(
                        title = "Audit Shilling Flow weekly cash outflow & personal ledger",
                        priority = Priority.STRATEGIC,
                        category = Category.LIFE,
                        deadline = "2:00 PM"
                    )
                )
            }

            val existingNotes = repository.allNotes.first()
            if (existingNotes.isEmpty()) {
                repository.insertNote(
                    NoteItem(
                        title = "Executive Directive",
                        content = "Focus 80% of energy on highest strategic leverage. Zero distraction. GA handles operational friction.",
                        priority = Priority.CRITICAL,
                        isPinned = true
                    )
                )
            }

            val existingTransactions = repository.allTransactions.first()
            if (existingTransactions.isEmpty()) {
                repository.insertTransaction(
                    TransactionItem(
                        amountKsh = 50.0,
                        purpose = "Coffee & Refreshment",
                        paymentMethod = PaymentMethod.CASH,
                        category = TransactionCategory.FOOD
                    )
                )
            }
        }
    }

    fun setThemeStyle(style: AppThemeStyle) {
        _currentThemeStyle.value = style
        prefs.edit().putString("theme_style", style.name).apply()
    }

    fun toggleKillSwitch(forceState: Boolean? = null) {
        val newState = forceState ?: !_isKillSwitchActive.value
        _isKillSwitchActive.value = newState
        prefs.edit().putBoolean("kill_switch", newState).apply()
        if (newState) {
            speechRecognitionManager.stopListening()
            _isAmbientWakeWordActive.value = false
            ttsManager.stop()
            voiceRecordingManager.stopPlayback()
            voiceRecordingManager.stopRecording()
        }
    }

    fun toggleAmbientWakeWord() {
        val newState = !_isAmbientWakeWordActive.value
        _isAmbientWakeWordActive.value = newState

        if (newState) {
            speechRecognitionManager.startAmbientWakeWordMonitoring(
                onWake = {
                    speak("Yes, Sir Gatei? Core systems online and standing by.")
                    startVoiceRecognition()
                },
                onShutdown = {
                    speak("Shutting down voice core, Sir Gatei. Standing by.")
                    toggleKillSwitch(true)
                }
            )
        } else {
            speechRecognitionManager.stopAmbientWakeWordMonitoring()
        }
    }

    fun setSection(section: GateiSection) {
        _currentSection.value = section
    }

    fun toggleVoice() {
        ttsManager.toggleVoiceEnabled()
    }

    fun setVocalizeMode(mode: AutoVocalizeMode) {
        ttsManager.setVocalizeMode(mode)
    }

    fun speak(text: String) {
        ttsManager.speakExplicit(text)
    }

    fun stopSpeaking() {
        ttsManager.stop()
    }

    // Voice recognition
    fun startVoiceRecognition() {
        if (_isKillSwitchActive.value) return
        speechRecognitionManager.startListening { transcribedText ->
            executeVoiceCommand(transcribedText)
        }
    }

    fun stopVoiceRecognition() {
        speechRecognitionManager.stopListening()
    }

    fun getSpeechIntent(): Intent {
        return speechRecognitionManager.getSpeechIntent()
    }

    // Command Dispatcher: Handles BOTH Voice and Typed Text
    fun executeVoiceCommand(command: String) {
        processCommandOrChat(command.trim(), isVoiceSource = true)
    }

    fun sendMessage(userText: String) {
        processCommandOrChat(userText.trim(), isVoiceSource = false)
    }

    private fun processCommandOrChat(rawInput: String, isVoiceSource: Boolean) {
        if (rawInput.isBlank()) return

        // 1. Check for system device actions & commands
        val actionResult = DeviceActionDispatcher.parseAndDispatch(getApplication(), rawInput)

        if (actionResult.isKillSwitchTrigger) {
            toggleKillSwitch(true)
            postMessagesAndVocalize(rawInput, actionResult.feedbackMessage, "KILL_SWITCH", isVoiceSource)
            return
        }

        if (actionResult.isWakeWordTrigger) {
            postMessagesAndVocalize(rawInput, actionResult.feedbackMessage, "WAKE_WORD", isVoiceSource)
            return
        }

        if (actionResult.parsedTransaction != null) {
            val tx = actionResult.parsedTransaction
            addTransaction(tx.amountKsh, tx.purpose, tx.paymentMethod, tx.category)
            postMessagesAndVocalize(rawInput, actionResult.feedbackMessage, "SHILLING_FLOW", isVoiceSource)
            return
        }

        if (actionResult.actionType == "RESCHEDULE_PRIORITIES") {
            reschedulePriorities()
            postMessagesAndVocalize(rawInput, actionResult.feedbackMessage, "RESCHEDULE", isVoiceSource)
            return
        }

        if (actionResult.executed) {
            postMessagesAndVocalize(rawInput, actionResult.feedbackMessage, actionResult.actionType, isVoiceSource)
            return
        }

        // 2. Natural language AI response
        viewModelScope.launch {
            val userMsg = ChatMessage(
                sender = MessageSender.USER,
                content = rawInput,
                category = "USER"
            )
            repository.insertMessage(userMsg)

            _isThinking.value = true

            val replyText = if (_isKillSwitchActive.value) {
                val localResp = GateiLocalEngine.processQuery(rawInput)
                "[SOVEREIGN OFFLINE MODE — ZERO NETWORK TRANSMISSION]\n\n${localResp.content}"
            } else {
                val history = messages.value.takeLast(6).map {
                    (if (it.sender == MessageSender.USER) "user" else "model") to it.content
                }
                GeminiClient.generateResponse(rawInput, history)
            }

            val gaMsg = ChatMessage(
                sender = MessageSender.GA,
                content = replyText,
                category = detectCategory(rawInput)
            )
            repository.insertMessage(gaMsg)
            _isThinking.value = false

            // Vocalize concise briefing
            ttsManager.speakAutoResponse(replyText)
        }
    }

    private fun postMessagesAndVocalize(userInput: String, gaReply: String, category: String, isVoiceSource: Boolean) {
        viewModelScope.launch {
            val userMsg = ChatMessage(
                sender = MessageSender.USER,
                content = userInput,
                category = if (isVoiceSource) "VOICE_ACTION" else "USER"
            )
            repository.insertMessage(userMsg)

            val gaMsg = ChatMessage(
                sender = MessageSender.GA,
                content = gaReply,
                category = category
            )
            repository.insertMessage(gaMsg)

            ttsManager.speakAutoResponse(gaReply)
        }
    }

    // Shilling Flow Operations
    fun addTransaction(amount: Double, purpose: String, method: PaymentMethod, category: TransactionCategory) {
        viewModelScope.launch {
            repository.insertTransaction(
                TransactionItem(
                    amountKsh = amount,
                    purpose = purpose,
                    paymentMethod = method,
                    category = category
                )
            )
        }
    }

    fun deleteTransaction(id: Long) {
        viewModelScope.launch {
            repository.deleteTransaction(id)
        }
    }

    // Notepad Operations
    fun addNote(title: String, content: String, priority: Priority) {
        viewModelScope.launch {
            repository.insertNote(
                NoteItem(
                    title = title,
                    content = content,
                    priority = priority
                )
            )
        }
    }

    fun deleteNote(id: Long) {
        viewModelScope.launch {
            repository.deleteNote(id)
        }
    }

    // Task & Priority Matrix Operations
    fun addTask(title: String, priority: Priority, deadline: String) {
        viewModelScope.launch {
            repository.insertTask(
                TaskItem(
                    title = title,
                    priority = priority,
                    deadline = deadline
                )
            )
        }
    }

    fun toggleTaskCompleted(id: Long, completed: Boolean) {
        viewModelScope.launch {
            repository.setTaskCompleted(id, completed)
        }
    }

    fun deleteTask(id: Long) {
        viewModelScope.launch {
            repository.deleteTask(id)
        }
    }

    fun reschedulePriorities() {
        viewModelScope.launch {
            // Re-sort existing tasks by priority level
            val currentTasks = tasks.value
            currentTasks.sortedBy { it.priority.level }.forEachIndexed { idx, task ->
                val adjustedDeadline = when (task.priority) {
                    Priority.CRITICAL -> "Immediate Priority 1"
                    Priority.STRATEGIC -> "Strategic Window 2"
                    Priority.OPERATIONAL -> "Delegated / End of Day"
                }
                repository.updateTask(task.copy(deadline = adjustedDeadline))
            }
        }
    }

    // Voice calibration controls
    fun setVoiceProfile(profile: VoiceProfile) {
        ttsManager.setProfile(profile)
    }

    fun updateVoicePitchAndSpeed(pitch: Float, speed: Float) {
        ttsManager.updatePitchAndSpeed(pitch, speed)
    }

    fun setPhoneticName(pronunciation: String) {
        ttsManager.setPhoneticPronunciation(pronunciation)
    }

    fun auditionVoice() {
        ttsManager.auditionVoice()
    }

    // User voice recording & calibration
    fun startRecordingUserVoice(): Boolean {
        return voiceRecordingManager.startRecording()
    }

    fun stopRecordingUserVoice() {
        voiceRecordingManager.stopRecording()
    }

    fun playUserVoiceSample() {
        voiceRecordingManager.playSample()
    }

    fun stopUserVoicePlayback() {
        voiceRecordingManager.stopPlayback()
    }

    fun getExportVoiceIntent(): Intent? {
        return voiceRecordingManager.exportRecordingIntent()
    }

    private fun detectCategory(prompt: String): String {
        val lower = prompt.lowercase()
        return when {
            lower.contains("meeting") || lower.contains("schedule") -> "SECRETARY"
            lower.contains("ksh") || lower.contains("shilling") || lower.contains("mpesa") -> "SHILLING_FLOW"
            lower.contains("note") || lower.contains("memo") -> "NOTEPAD"
            lower.contains("task") || lower.contains("priority") -> "PLANNER"
            lower.contains("camera") || lower.contains("video") || lower.contains("photo") -> "CAMERA"
            lower.contains("youtube") || lower.contains("spotify") || lower.contains("music") -> "MEDIA"
            lower.contains("whatsapp") -> "COMMUNICATION"
            lower.contains("research") || lower.contains("find") -> "RESEARCH"
            else -> "ASSISTANT"
        }
    }
}
