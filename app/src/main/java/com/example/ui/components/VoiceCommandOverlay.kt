package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AppleBlue
import com.example.ui.theme.AppleGreen
import com.example.ui.theme.AppleOrange
import com.example.ui.theme.AppleRed
import com.example.ui.theme.AppleSurfaceBorder
import com.example.ui.theme.AppleSurfaceElevated
import com.example.ui.theme.AppleTextPrimary
import com.example.ui.theme.AppleTextSecondary
import com.example.ui.theme.AppleTextTertiary
import com.example.ui.theme.TitaniumAccentGradient

@Composable
fun VoiceCommandOverlay(
    isListening: Boolean,
    recognizedText: String,
    errorMessage: String? = null,
    rmsDb: Float,
    onStopListening: () -> Unit,
    onSubmitCommand: (String) -> Unit,
    onLaunchSystemSpeech: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!isListening && recognizedText.isEmpty() && errorMessage == null) return

    val infiniteTransition = rememberInfiniteTransition(label = "orb_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(tween(900, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulse"
    )

    var manualEditText by remember(recognizedText) { mutableStateOf(recognizedText) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xE60A0B0E))
            .clickable(onClick = onDismiss)
            .testTag("voice_command_overlay")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .clickable(enabled = false) {}, // prevent click-through
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "JARVIS VOICE AGENT",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = AppleTextTertiary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Sir Gatei Executive Interface",
                        fontSize = 11.sp,
                        color = AppleTextSecondary
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0x1FFFFFFF))
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Center: Glowing Animated Voice Orb
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .scale(if (isListening) (1f + rmsDb * 0.25f).coerceIn(1f, 1.35f) else pulseScale)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF6366F1).copy(alpha = 0.45f),
                                    Color(0xFF38BDF8).copy(alpha = 0.35f),
                                    Color(0xFFA855F7).copy(alpha = 0.15f),
                                    Color.Transparent
                                )
                            )
                        )
                        .border(
                            width = 2.dp,
                            brush = Brush.sweepGradient(TitaniumAccentGradient),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isListening) Icons.Default.Mic else Icons.Default.Stop,
                        contentDescription = "Mic Indicator",
                        tint = Color.White,
                        modifier = Modifier.size(44.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = when {
                        isListening -> "Jarvis is listening to your voice..."
                        errorMessage != null -> errorMessage
                        recognizedText.isNotBlank() -> "Command recognized"
                        else -> "Standing by, Sir Gatei..."
                    },
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (errorMessage != null) AppleOrange else AppleTextPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Editable or Live Recognized Text
                OutlinedTextField(
                    value = if (isListening) recognizedText.ifBlank { "Speak or edit your command..." } else manualEditText,
                    onValueChange = { manualEditText = it },
                    readOnly = isListening,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = AppleSurfaceElevated,
                        unfocusedContainerColor = AppleSurfaceElevated,
                        focusedBorderColor = AppleBlue,
                        unfocusedBorderColor = AppleSurfaceBorder
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = onLaunchSystemSpeech,
                        colors = ButtonDefaults.buttonColors(containerColor = AppleBlue),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Mic, contentDescription = "System Speech", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Launch System Voice Dialog")
                    }
                }
            }

            // Quick Voice Action Chips & Controls
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "QUICK EXECUTIVE DISPATCH",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = AppleTextTertiary,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                val quickActions = listOf(
                    "Schedule meeting tomorrow at 2 PM",
                    "Add transaction of 50 KSh for lunch, cash, food",
                    "Take a picture with camera",
                    "Record a video for 30 sec",
                    "Play on YouTube",
                    "Check WhatsApp messages",
                    "Reschedule priorities",
                    "Shut GA"
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    quickActions.take(2).forEach { action ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0x1F38BDF8),
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, Color(0x3338BDF8)),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    onStopListening()
                                    onSubmitCommand(action)
                                }
                        ) {
                            Text(
                                text = action,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = AppleTextPrimary,
                                maxLines = 1,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bottom Action Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (isListening) {
                        Button(
                            onClick = onStopListening,
                            colors = ButtonDefaults.buttonColors(containerColor = AppleRed),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Stop, contentDescription = "Stop", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Stop Listening")
                        }
                    }

                    Button(
                        onClick = {
                            val textToSubmit = if (manualEditText.isNotBlank()) manualEditText else recognizedText
                            if (textToSubmit.isNotBlank()) {
                                onStopListening()
                                onSubmitCommand(textToSubmit)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AppleBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Execute", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Execute Command")
                    }
                }
            }
        }
    }
}
