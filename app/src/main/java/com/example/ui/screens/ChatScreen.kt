package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatMessage
import com.example.data.model.MessageSender
import com.example.ui.components.JarvisArcReactorCore
import com.example.ui.theme.AppleBackground
import com.example.ui.theme.AppleBlue
import com.example.ui.theme.AppleDivider
import com.example.ui.theme.AppleGreen
import com.example.ui.theme.AppleOrange
import com.example.ui.theme.AppleRed
import com.example.ui.theme.AppleSurface
import com.example.ui.theme.AppleSurfaceBorder
import com.example.ui.theme.AppleSurfaceElevated
import com.example.ui.theme.AppleSurfaceHighlight
import com.example.ui.theme.AppleTextPlaceholder
import com.example.ui.theme.AppleTextPrimary
import com.example.ui.theme.AppleTextSecondary
import com.example.ui.theme.AppleTextTertiary
import com.example.ui.theme.TitaniumAccentGradient
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatScreen(
    messages: List<ChatMessage>,
    isThinking: Boolean,
    isSpeaking: Boolean = false,
    isListening: Boolean = false,
    isAmbientWakeWordActive: Boolean = false,
    isKillSwitchActive: Boolean = false,
    rmsDb: Float = 0f,
    onSendMessage: (String) -> Unit,
    onSpeak: (String) -> Unit,
    onTriggerVoiceCommand: () -> Unit = {},
    onToggleAmbientWakeWord: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var textInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val context = LocalContext.current

    LaunchedEffect(messages.size, isThinking) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val quickDirectives = listOf(
        "Schedule Meeting" to "Schedule a meeting tomorrow at 2:00 PM with Titan Media",
        "Log 50 KSh (Cash)" to "Add transaction of 50 KSh for lunch, cash, food",
        "Log 500 KSh (M-Pesa)" to "Add transaction of 500 KSh for Uber, mpesa, transport",
        "Take Picture" to "Open camera and take a picture",
        "Record 30s Video" to "Record a video for 30 sec",
        "Play on YouTube" to "Play audio production masterclass on YouTube",
        "Check WhatsApp" to "Open WhatsApp and check recent messages",
        "Reschedule Priorities" to "Reschedule my to-do list in order of strategic priorities",
        "Edit Notepad" to "Edit my notepad: memo on client retainers",
        "Shut GA" to "Shut GA"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppleBackground)
            .testTag("chat_screen")
    ) {
        // Holographic Jarvis Arc Reactor Core Hero Header
        JarvisArcReactorCore(
            isListening = isListening,
            isThinking = isThinking,
            isSpeaking = isSpeaking,
            isAmbientWakeWordActive = isAmbientWakeWordActive,
            isKillSwitchActive = isKillSwitchActive,
            rmsDb = rmsDb,
            onCoreClick = onTriggerVoiceCommand,
            onToggleAmbientWakeWord = onToggleAmbientWakeWord
        )

        // Kill Switch Active Banner
        if (isKillSwitchActive) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(AppleRed.copy(alpha = 0.15f))
                    .border(0.5.dp, AppleRed.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(Icons.Default.Security, contentDescription = null, tint = AppleRed, modifier = Modifier.size(12.dp))
                Text(
                    text = "KILL SWITCH ENGAGED • 100% LOCAL OFFLINE (ZERO CLOUD TRANSMISSION)",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppleRed
                )
            }
        }

        // Quick Suggestion Chips Carousel
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            quickDirectives.forEach { (label, prompt) ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = AppleSurfaceElevated,
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, AppleSurfaceBorder),
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onSendMessage(prompt) }
                        .testTag("quick_directive_${label.lowercase().replace(" ", "_")}")
                ) {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppleTextPrimary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }

        // Chat & Agent Output Stream
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                MessageBubble(
                    message = msg,
                    onSpeak = { onSpeak(msg.content) },
                    onOptionSelect = { optionText -> onSendMessage(optionText) }
                )
            }

            if (isThinking) {
                item {
                    ThinkingIndicator()
                }
            }
        }

        // Bottom Input Dock
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(AppleSurface)
                .border(width = 0.5.dp, color = AppleDivider)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // High-Response Voice Mic Trigger
                IconButton(
                    onClick = onTriggerVoiceCommand,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.sweepGradient(
                                if (isListening) listOf(Color(0xFF38BDF8), Color(0xFF6366F1), Color(0xFF38BDF8))
                                else listOf(Color(0xFF1E202A), Color(0xFF282B37), Color(0xFF1E202A))
                            )
                        )
                        .border(
                            0.5.dp,
                            if (isListening) AppleBlue else AppleSurfaceBorder,
                            CircleShape
                        )
                        .testTag("voice_mic_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Voice Mic",
                        tint = if (isListening) Color.White else AppleBlue,
                        modifier = Modifier.size(22.dp)
                    )
                }

                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    placeholder = {
                        Text(
                            "Command GA (e.g. \"Log 50 KSh\", \"Take picture\")",
                            color = AppleTextPlaceholder,
                            fontSize = 13.5.sp
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_field"),
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = AppleTextPrimary,
                        unfocusedTextColor = AppleTextPrimary,
                        focusedContainerColor = AppleSurfaceElevated,
                        unfocusedContainerColor = AppleSurfaceElevated,
                        focusedBorderColor = AppleBlue,
                        unfocusedBorderColor = AppleSurfaceBorder,
                        cursorColor = AppleBlue
                    ),
                    maxLines = 4
                )

                // Send Button
                IconButton(
                    onClick = {
                        if (textInput.isNotBlank()) {
                            val text = textInput
                            textInput = ""
                            onSendMessage(text)
                        }
                    },
                    enabled = textInput.isNotBlank(),
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            if (textInput.isNotBlank()) AppleBlue else AppleSurfaceElevated
                        )
                        .testTag("send_message_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = if (textInput.isNotBlank()) Color.White else AppleTextTertiary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MessageBubble(
    message: ChatMessage,
    onSpeak: () -> Unit,
    onOptionSelect: (String) -> Unit
) {
    val isUser = message.sender == MessageSender.USER
    val context = LocalContext.current
    val timeFormatted = remember(message.timestamp) {
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(message.timestamp))
    }

    // Check if message contains interactive clarification options (A), (B), (C)
    val hasOptionA = message.content.contains("(A)")
    val hasOptionB = message.content.contains("(B)")
    val hasOptionC = message.content.contains("(C)")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(if (isUser) 0.85f else 0.95f),
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
        ) {
            Surface(
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isUser) 16.dp else 4.dp,
                    bottomEnd = if (isUser) 4.dp else 16.dp
                ),
                color = if (isUser) AppleBlue else AppleSurface,
                border = if (!isUser) androidx.compose.foundation.BorderStroke(0.5.dp, AppleSurfaceBorder) else null,
                modifier = Modifier.testTag("message_bubble_${message.id}")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    if (!isUser) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF38BDF8))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "GATEI AI • JARVIS",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFF38BDF8),
                                    letterSpacing = 0.5.sp
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = onSpeak,
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = "Vocalize",
                                        tint = AppleTextSecondary,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("GA Response", message.content)
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy",
                                        tint = AppleTextSecondary,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    Text(
                        text = message.content,
                        fontSize = 14.sp,
                        color = if (isUser) Color.White else AppleTextPrimary,
                        lineHeight = 20.sp
                    )

                    // Interactive Clarification Option Buttons
                    if (!isUser && (hasOptionA || hasOptionB || hasOptionC)) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            if (hasOptionA) {
                                Button(
                                    onClick = { onOptionSelect("Execute Option A") },
                                    colors = ButtonDefaults.buttonColors(containerColor = AppleSurfaceElevated),
                                    border = androidx.compose.foundation.BorderStroke(0.5.dp, AppleSurfaceBorder),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Option A", fontSize = 11.sp, color = AppleTextPrimary)
                                }
                            }
                            if (hasOptionB) {
                                Button(
                                    onClick = { onOptionSelect("Execute Option B") },
                                    colors = ButtonDefaults.buttonColors(containerColor = AppleSurfaceElevated),
                                    border = androidx.compose.foundation.BorderStroke(0.5.dp, AppleSurfaceBorder),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Option B", fontSize = 11.sp, color = AppleTextPrimary)
                                }
                            }
                            if (hasOptionC) {
                                Button(
                                    onClick = { onOptionSelect("Execute Option C") },
                                    colors = ButtonDefaults.buttonColors(containerColor = AppleSurfaceElevated),
                                    border = androidx.compose.foundation.BorderStroke(0.5.dp, AppleSurfaceBorder),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Option C", fontSize = 11.sp, color = AppleTextPrimary)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = timeFormatted,
                        fontSize = 10.sp,
                        color = if (isUser) Color.White.copy(alpha = 0.7f) else AppleTextTertiary,
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }
        }
    }
}

@Composable
fun ThinkingIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(tween(600, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "alpha"
    )

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = AppleSurfaceElevated,
        border = androidx.compose.foundation.BorderStroke(0.5.dp, AppleSurfaceBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.GraphicEq,
                contentDescription = "Computing",
                tint = AppleBlue.copy(alpha = alpha),
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = "JARVIS SYNTHESIZING RESPONSE...",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = AppleTextSecondary
            )
        }
    }
}
