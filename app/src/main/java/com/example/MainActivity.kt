package com.example

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.TacticalHudBar
import com.example.ui.components.VoiceCalibrationDialog
import com.example.ui.components.VoiceCommandOverlay
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.ExecutiveDeskScreen
import com.example.ui.screens.ResearchScreen
import com.example.ui.theme.AppThemeStyle
import com.example.ui.theme.AppleBackground
import com.example.ui.theme.AppleBlue
import com.example.ui.theme.AppleDivider
import com.example.ui.theme.AppleTextPrimary
import com.example.ui.theme.AppleTextSecondary
import com.example.ui.theme.AppleTextTertiary
import com.example.ui.theme.GateiAiTheme
import com.example.ui.viewmodel.GateiSection
import com.example.ui.viewmodel.GateiViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: GateiViewModel = viewModel()
            val themeStyle by viewModel.currentThemeStyle.collectAsStateWithLifecycle()
            GateiAiTheme(themeStyle = themeStyle) {
                GateiApp(viewModel)
            }
        }
    }
}

@Composable
fun GateiApp(viewModel: GateiViewModel = viewModel()) {
    val context = LocalContext.current
    val currentSection by viewModel.currentSection.collectAsStateWithLifecycle()
    val isSpeaking by viewModel.isSpeaking.collectAsStateWithLifecycle()
    val isVoiceEnabled by viewModel.isVoiceEnabled.collectAsStateWithLifecycle()
    val isThinking by viewModel.isThinking.collectAsStateWithLifecycle()

    val currentThemeStyle by viewModel.currentThemeStyle.collectAsStateWithLifecycle()
    val isKillSwitchActive by viewModel.isKillSwitchActive.collectAsStateWithLifecycle()
    val isListeningToVoiceCommand by viewModel.isListeningToVoiceCommand.collectAsStateWithLifecycle()
    val recognizedVoiceText by viewModel.recognizedVoiceText.collectAsStateWithLifecycle()
    val speechRmsDb by viewModel.speechRmsDb.collectAsStateWithLifecycle()
    val speechError by viewModel.speechError.collectAsStateWithLifecycle()
    val isAmbientWakeWordActive by viewModel.isAmbientWakeWordActive.collectAsStateWithLifecycle()

    val vocalizeMode by viewModel.vocalizeMode.collectAsStateWithLifecycle()
    val currentVoiceProfile by viewModel.currentVoiceProfile.collectAsStateWithLifecycle()
    val voicePitch by viewModel.voicePitch.collectAsStateWithLifecycle()
    val voiceSpeed by viewModel.voiceSpeed.collectAsStateWithLifecycle()
    val phoneticName by viewModel.phoneticName.collectAsStateWithLifecycle()

    val isRecordingUserVoice by viewModel.isRecordingUserVoice.collectAsStateWithLifecycle()
    val isPlayingUserVoice by viewModel.isPlayingUserVoice.collectAsStateWithLifecycle()
    val hasRecordedUserVoice by viewModel.hasRecordedUserVoice.collectAsStateWithLifecycle()
    val recordingAmplitude by viewModel.recordingAmplitude.collectAsStateWithLifecycle()
    val recordingDurationSeconds by viewModel.recordingDurationSeconds.collectAsStateWithLifecycle()

    var showVoiceCalibrationDialog by remember { mutableStateOf(false) }

    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val meetings by viewModel.meetings.collectAsStateWithLifecycle()
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val todayTotalSpent by viewModel.todayTotalSpent.collectAsStateWithLifecycle()
    val notes by viewModel.notes.collectAsStateWithLifecycle()

    // System Speech Recognition Fallback Launcher
    val systemSpeechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val spokenMatches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!spokenMatches.isNullOrEmpty()) {
                val command = spokenMatches[0]
                viewModel.executeVoiceCommand(command)
            }
        }
    }

    // Audio Recording Permission Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.startVoiceRecognition()
        } else {
            Toast.makeText(context, "Microphone permission is required for voice commands", Toast.LENGTH_SHORT).show()
        }
    }

    val triggerVoiceWithPermission = {
        val permission = Manifest.permission.RECORD_AUDIO
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            viewModel.startVoiceRecognition()
        } else {
            permissionLauncher.launch(permission)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(AppleBackground),
        topBar = {
            Column(modifier = Modifier.statusBarsPadding()) {
                TacticalHudBar(
                    isSpeaking = isSpeaking,
                    isVoiceEnabled = isVoiceEnabled,
                    onToggleVoice = { viewModel.toggleVoice() },
                    activeEngineTitle = currentSection.title,
                    onOpenVoiceCalibration = { showVoiceCalibrationDialog = true },
                    onTriggerVoiceCommand = triggerVoiceWithPermission,
                    isKillSwitchActive = isKillSwitchActive,
                    onToggleKillSwitch = { viewModel.toggleKillSwitch() },
                    isLightTheme = currentThemeStyle.isLight,
                    onToggleTheme = {
                        viewModel.setThemeStyle(
                            if (currentThemeStyle.isLight) AppThemeStyle.TITANIUM_LUXE else AppThemeStyle.PEARL_MINIMAL
                        )
                    }
                )
            }
        },
        bottomBar = {
            GateiBottomNavigation(
                currentSection = currentSection,
                onSelectSection = { viewModel.setSection(it) },
                modifier = Modifier.navigationBarsPadding()
            )
        },
        containerColor = AppleBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentSection) {
                GateiSection.AGENT -> {
                    ChatScreen(
                        messages = messages,
                        isThinking = isThinking,
                        isSpeaking = isSpeaking,
                        isListening = isListeningToVoiceCommand,
                        isAmbientWakeWordActive = isAmbientWakeWordActive,
                        isKillSwitchActive = isKillSwitchActive,
                        rmsDb = speechRmsDb,
                        onSendMessage = { viewModel.sendMessage(it) },
                        onSpeak = { viewModel.speak(it) },
                        onTriggerVoiceCommand = triggerVoiceWithPermission,
                        onToggleAmbientWakeWord = { viewModel.toggleAmbientWakeWord() }
                    )
                }

                GateiSection.DESK -> {
                    ExecutiveDeskScreen(
                        tasks = tasks,
                        notes = notes,
                        transactions = transactions,
                        meetings = meetings,
                        todayTotalSpent = todayTotalSpent,
                        onAddTask = { title, priority, deadline ->
                            viewModel.addTask(title, priority, deadline)
                        },
                        onToggleTaskCompleted = { id, comp ->
                            viewModel.toggleTaskCompleted(id, comp)
                        },
                        onDeleteTask = { id ->
                            viewModel.deleteTask(id)
                        },
                        onReschedulePriorities = {
                            viewModel.reschedulePriorities()
                        },
                        onAddNote = { title, content, priority ->
                            viewModel.addNote(title, content, priority)
                        },
                        onDeleteNote = { id ->
                            viewModel.deleteNote(id)
                        },
                        onAddTransaction = { amount, purpose, method, cat ->
                            viewModel.addTransaction(amount, purpose, method, cat)
                        },
                        onDeleteTransaction = { id ->
                            viewModel.deleteTransaction(id)
                        },
                        onSendDraftMessage = { draft ->
                            viewModel.sendMessage("Draft message prepared: $draft")
                        }
                    )
                }

                GateiSection.RESEARCH -> {
                    ResearchScreen(
                        onTriggerResearchQuery = { prompt ->
                            viewModel.sendMessage(prompt)
                            viewModel.setSection(GateiSection.AGENT)
                        }
                    )
                }
            }
        }
    }

    // Voice Command Listening Siri Overlay
    VoiceCommandOverlay(
        isListening = isListeningToVoiceCommand,
        recognizedText = recognizedVoiceText,
        errorMessage = speechError,
        rmsDb = speechRmsDb,
        onStopListening = { viewModel.stopVoiceRecognition() },
        onSubmitCommand = { viewModel.executeVoiceCommand(it) },
        onLaunchSystemSpeech = {
            viewModel.stopVoiceRecognition()
            systemSpeechLauncher.launch(viewModel.getSpeechIntent())
        },
        onDismiss = { viewModel.stopVoiceRecognition() }
    )

    // Voice & Speech Behavior Dialog
    VoiceCalibrationDialog(
        isOpen = showVoiceCalibrationDialog,
        onDismiss = { showVoiceCalibrationDialog = false },
        currentProfile = currentVoiceProfile,
        pitch = voicePitch,
        speed = voiceSpeed,
        vocalizeMode = vocalizeMode,
        phoneticName = phoneticName,
        onSelectProfile = { viewModel.setVoiceProfile(it) },
        onSelectVocalizeMode = { viewModel.setVocalizeMode(it) },
        onUpdatePitchAndSpeed = { p, s -> viewModel.updateVoicePitchAndSpeed(p, s) },
        onUpdatePhoneticName = { viewModel.setPhoneticName(it) },
        onAudition = { viewModel.auditionVoice() },
        isRecordingUserVoice = isRecordingUserVoice,
        isPlayingUserVoice = isPlayingUserVoice,
        hasRecordedUserVoice = hasRecordedUserVoice,
        recordingAmplitude = recordingAmplitude,
        recordingDurationSeconds = recordingDurationSeconds,
        onStartRecording = { viewModel.startRecordingUserVoice() },
        onStopRecording = { viewModel.stopRecordingUserVoice() },
        onPlayUserSample = { viewModel.playUserVoiceSample() },
        onStopUserPlayback = { viewModel.stopUserVoicePlayback() },
        onExportRecording = {
            val exportIntent = viewModel.getExportVoiceIntent()
            if (exportIntent != null) {
                context.startActivity(Intent.createChooser(exportIntent, "Export Voice Calibration Sample"))
            } else {
                Toast.makeText(context, "No recorded voice sample found to export", Toast.LENGTH_SHORT).show()
            }
        }
    )
}

data class NavigationItemConfig(
    val section: GateiSection,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
fun GateiBottomNavigation(
    currentSection: GateiSection,
    onSelectSection: (GateiSection) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        NavigationItemConfig(GateiSection.AGENT, "Jarvis Agent", Icons.Filled.Forum, Icons.Outlined.Forum),
        NavigationItemConfig(GateiSection.DESK, "Executive Desk", Icons.Filled.Inventory2, Icons.Outlined.Inventory2),
        NavigationItemConfig(GateiSection.RESEARCH, "Intelligence", Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome)
    )

    NavigationBar(
        containerColor = Color(0xFF0C0C0E),
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .border(width = 0.5.dp, color = AppleDivider)
            .testTag("gatei_bottom_navigation")
    ) {
        items.forEach { item ->
            val isSelected = currentSection == item.section
            NavigationBarItem(
                selected = isSelected,
                onClick = { onSelectSection(item.section) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label,
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        letterSpacing = (-0.2).sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AppleBlue,
                    selectedTextColor = AppleBlue,
                    unselectedIconColor = AppleTextTertiary,
                    unselectedTextColor = AppleTextTertiary,
                    indicatorColor = Color.Transparent
                ),
                modifier = Modifier.testTag("nav_${item.section.name.lowercase()}")
            )
        }
    }
}
