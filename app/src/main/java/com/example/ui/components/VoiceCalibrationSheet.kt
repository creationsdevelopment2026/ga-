package com.example.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.example.ui.theme.AppleBlue
import com.example.ui.theme.AppleBlueSubtle
import com.example.ui.theme.AppleDivider
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
import com.example.util.AutoVocalizeMode
import com.example.util.VoiceProfile

@Composable
fun VoiceCalibrationDialog(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    currentProfile: VoiceProfile,
    pitch: Float,
    speed: Float,
    vocalizeMode: AutoVocalizeMode,
    phoneticName: String,
    onSelectProfile: (VoiceProfile) -> Unit,
    onSelectVocalizeMode: (AutoVocalizeMode) -> Unit,
    onUpdatePitchAndSpeed: (Float, Float) -> Unit,
    onUpdatePhoneticName: (String) -> Unit,
    onAudition: () -> Unit,
    isRecordingUserVoice: Boolean,
    isPlayingUserVoice: Boolean,
    hasRecordedUserVoice: Boolean,
    recordingAmplitude: Float,
    recordingDurationSeconds: Int,
    onStartRecording: () -> Boolean,
    onStopRecording: () -> Unit,
    onPlayUserSample: () -> Unit,
    onStopUserPlayback: () -> Unit,
    onExportRecording: () -> Unit
) {
    if (!isOpen) return

    val context = LocalContext.current
    var customPhoneticInput by remember(phoneticName) { mutableStateOf(phoneticName) }
    var localPitch by remember(pitch) { mutableFloatStateOf(pitch) }
    var localSpeed by remember(speed) { mutableFloatStateOf(speed) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val started = onStartRecording()
            if (!started) {
                Toast.makeText(context, "Microphone initialization failed", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Audio recording permission is required to calibrate your voice", Toast.LENGTH_LONG).show()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(24.dp))
                .border(0.5.dp, AppleSurfaceBorder, RoundedCornerShape(24.dp)),
            color = AppleSurfaceElevated
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Voice & Speech Behavior",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppleTextPrimary,
                            letterSpacing = (-0.3).sp
                        )
                        Text(
                            text = "Control auto-speech, rapid speed, and voice cloning",
                            fontSize = 12.sp,
                            color = AppleTextSecondary
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(AppleSurface)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = AppleTextSecondary, modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // SECTION 0: Auto-Vocalize Behavior (Concise vs Silent vs Full)
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(AppleSurface)
                                .border(0.5.dp, AppleSurfaceBorder, RoundedCornerShape(16.dp))
                                .padding(16.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(
                                    text = "READOUT BEHAVIOR",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AppleTextTertiary,
                                    letterSpacing = 0.5.sp
                                )

                                Text(
                                    text = "Prevent GA from slowly reading out long messages. Choose how GA speaks:",
                                    fontSize = 12.sp,
                                    color = AppleTextSecondary,
                                    lineHeight = 16.sp
                                )

                                AutoVocalizeMode.entries.forEach { mode ->
                                    val isSelected = vocalizeMode == mode
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (isSelected) AppleSurfaceHighlight else Color.Transparent)
                                            .border(
                                                width = if (isSelected) 1.dp else 0.5.dp,
                                                color = if (isSelected) AppleBlue else AppleDivider,
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .clickable { onSelectVocalizeMode(mode) }
                                            .padding(horizontal = 12.dp, vertical = 10.dp)
                                    ) {
                                        Column {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = mode.label,
                                                    fontSize = 13.5.sp,
                                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                                                    color = if (isSelected) AppleBlue else AppleTextPrimary
                                                )
                                                if (isSelected) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(8.dp)
                                                            .clip(CircleShape)
                                                            .background(AppleBlue)
                                                    )
                                                }
                                            }
                                            Text(
                                                text = mode.description,
                                                fontSize = 11.5.sp,
                                                color = AppleTextSecondary,
                                                modifier = Modifier.padding(top = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // SECTION 1: Natural Male Voice Profiles & Speed
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(AppleSurface)
                                .border(0.5.dp, AppleSurfaceBorder, RoundedCornerShape(16.dp))
                                .padding(16.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "ASSISTANT MALE VOICE & SPEED",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = AppleTextTertiary,
                                        letterSpacing = 0.5.sp
                                    )

                                    Button(
                                        onClick = onAudition,
                                        colors = ButtonDefaults.buttonColors(containerColor = AppleBlueSubtle, contentColor = AppleBlue),
                                        shape = RoundedCornerShape(14.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        modifier = Modifier.height(28.dp).testTag("audition_voice_button")
                                    ) {
                                        Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Audition", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }

                                // Voice Profile Selector Cards
                                VoiceProfile.entries.forEach { profile ->
                                    val isSelected = currentProfile == profile
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (isSelected) AppleSurfaceHighlight else Color.Transparent)
                                            .border(
                                                width = if (isSelected) 1.dp else 0.5.dp,
                                                color = if (isSelected) AppleBlue else AppleDivider,
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .clickable {
                                                onSelectProfile(profile)
                                                localPitch = profile.pitch
                                                localSpeed = profile.speed
                                            }
                                            .padding(horizontal = 12.dp, vertical = 10.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = profile.label,
                                                fontSize = 13.sp,
                                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                                color = if (isSelected) AppleTextPrimary else AppleTextSecondary
                                            )
                                            if (isSelected) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(8.dp)
                                                        .clip(CircleShape)
                                                        .background(AppleBlue)
                                                )
                                            }
                                        }
                                    }
                                }

                                // Fast Cadence Speed Adjuster
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Speech Velocity (Fast & Clear)", fontSize = 12.sp, color = AppleTextPrimary, fontWeight = FontWeight.Medium)
                                        Text(String.format("%.2fx", localSpeed), fontSize = 12.sp, color = AppleBlue, fontWeight = FontWeight.Bold)
                                    }
                                    Slider(
                                        value = localSpeed,
                                        onValueChange = {
                                            localSpeed = it
                                            onUpdatePitchAndSpeed(localPitch, it)
                                        },
                                        valueRange = 1.0f..1.60f,
                                        colors = SliderDefaults.colors(
                                            thumbColor = AppleBlue,
                                            activeTrackColor = AppleBlue,
                                            inactiveTrackColor = AppleSurfaceHighlight
                                        ),
                                        modifier = Modifier.height(28.dp)
                                    )

                                    // Quick Speed Presets
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        listOf(1.15f to "Normal", 1.30f to "Fast Executive", 1.45f to "Rapid").forEach { (speedPreset, label) ->
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(if (kotlin.math.abs(localSpeed - speedPreset) < 0.06f) AppleBlueSubtle else AppleSurfaceHighlight)
                                                    .clickable {
                                                        localSpeed = speedPreset
                                                        onUpdatePitchAndSpeed(localPitch, speedPreset)
                                                    }
                                                    .padding(vertical = 6.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = label,
                                                    fontSize = 10.5.sp,
                                                    color = if (kotlin.math.abs(localSpeed - speedPreset) < 0.06f) AppleBlue else AppleTextSecondary,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // SECTION 2: Name Pronunciation Calibration
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(AppleSurface)
                                .border(0.5.dp, AppleSurfaceBorder, RoundedCornerShape(16.dp))
                                .padding(16.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(
                                    text = "NAME PRONUNCIATION CALIBRATION",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AppleTextTertiary,
                                    letterSpacing = 0.5.sp
                                )

                                Text(
                                    text = "How GA addresses you as 'Sir Gatei'. Select phonetic cadence or enter custom spelling:",
                                    fontSize = 12.sp,
                                    color = AppleTextSecondary,
                                    lineHeight = 17.sp
                                )

                                // Preset Pronunciations
                                val presets = listOf("Guh-tay", "GAH-tay", "GAY-tee", "Gah-teh")
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    presets.forEach { preset ->
                                        val isSelected = customPhoneticInput == preset
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(if (isSelected) AppleBlue else AppleSurfaceHighlight)
                                                .clickable {
                                                    customPhoneticInput = preset
                                                    onUpdatePhoneticName(preset)
                                                }
                                                .padding(vertical = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = preset,
                                                fontSize = 11.5.sp,
                                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                                color = if (isSelected) Color.White else AppleTextSecondary
                                            )
                                        }
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = customPhoneticInput,
                                        onValueChange = {
                                            customPhoneticInput = it
                                            onUpdatePhoneticName(it)
                                        },
                                        placeholder = { Text("Custom phonetic spelling", color = AppleTextPlaceholder) },
                                        modifier = Modifier.weight(1f).height(50.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedContainerColor = AppleSurfaceElevated,
                                            unfocusedContainerColor = AppleSurfaceElevated,
                                            focusedBorderColor = AppleBlue,
                                            unfocusedBorderColor = AppleSurfaceBorder,
                                            focusedTextColor = AppleTextPrimary,
                                            unfocusedTextColor = AppleTextPrimary,
                                            cursorColor = AppleBlue
                                        )
                                    )

                                    Button(
                                        onClick = onAudition,
                                        colors = ButtonDefaults.buttonColors(containerColor = AppleSurfaceHighlight, contentColor = AppleTextPrimary),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.height(50.dp)
                                    ) {
                                        Text("Test", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }

                    // SECTION 3: User Voice Recording & Training Export
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(AppleSurface)
                                .border(0.5.dp, AppleSurfaceBorder, RoundedCornerShape(16.dp))
                                .padding(16.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    text = "RECORD YOUR VOICE & CLONING PIPELINE",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AppleTextTertiary,
                                    letterSpacing = 0.5.sp
                                )

                                Text(
                                    text = "Record yourself speaking naturally and saying your name. GA captures this audio sample for vocal timbre calibration and model training.",
                                    fontSize = 12.sp,
                                    color = AppleTextSecondary,
                                    lineHeight = 17.sp
                                )

                                // Live Waveform / Amplitude Meter during Recording
                                if (isRecordingUserVoice) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(AppleRed.copy(alpha = 0.15f))
                                            .padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(10.dp)
                                                    .clip(CircleShape)
                                                    .background(AppleRed)
                                            )
                                            Text(
                                                text = "Recording Voice Sample: ${recordingDurationSeconds}s",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = AppleRed
                                            )
                                        }

                                        // Amplitude meter
                                        Box(
                                            modifier = Modifier
                                                .width(40.dp)
                                                .height((8 + (recordingAmplitude * 20)).dp)
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(AppleRed)
                                        )
                                    }
                                }

                                // Recording Controls
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    if (isRecordingUserVoice) {
                                        Button(
                                            onClick = onStopRecording,
                                            colors = ButtonDefaults.buttonColors(containerColor = AppleRed, contentColor = Color.White),
                                            shape = RoundedCornerShape(14.dp),
                                            modifier = Modifier.weight(1f).height(44.dp)
                                        ) {
                                            Icon(Icons.Default.Stop, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Stop Recording", fontWeight = FontWeight.SemiBold)
                                        }
                                    } else {
                                        Button(
                                            onClick = {
                                                val hasPermission = ContextCompat.checkSelfPermission(
                                                    context,
                                                    Manifest.permission.RECORD_AUDIO
                                                ) == PackageManager.PERMISSION_GRANTED

                                                if (hasPermission) {
                                                    val started = onStartRecording()
                                                    if (!started) {
                                                        Toast.makeText(context, "Microphone start failed", Toast.LENGTH_SHORT).show()
                                                    }
                                                } else {
                                                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = AppleBlue, contentColor = Color.White),
                                            shape = RoundedCornerShape(14.dp),
                                            modifier = Modifier.weight(1f).height(44.dp)
                                        ) {
                                            Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(if (hasRecordedUserVoice) "Re-Record Voice" else "Record My Voice", fontWeight = FontWeight.SemiBold)
                                        }
                                    }

                                    if (hasRecordedUserVoice && !isRecordingUserVoice) {
                                        Button(
                                            onClick = {
                                                if (isPlayingUserVoice) onStopUserPlayback() else onPlayUserSample()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = AppleSurfaceHighlight, contentColor = AppleTextPrimary),
                                            shape = RoundedCornerShape(14.dp),
                                            modifier = Modifier.weight(1f).height(44.dp)
                                        ) {
                                            Icon(if (isPlayingUserVoice) Icons.Default.Stop else Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(if (isPlayingUserVoice) "Pause" else "Play Sample", fontWeight = FontWeight.Medium)
                                        }
                                    }
                                }

                                if (hasRecordedUserVoice && !isRecordingUserVoice) {
                                    Button(
                                        onClick = onExportRecording,
                                        colors = ButtonDefaults.buttonColors(containerColor = AppleSurfaceElevated, contentColor = AppleBlue),
                                        shape = RoundedCornerShape(14.dp),
                                        border = androidx.compose.foundation.BorderStroke(0.5.dp, AppleBlueSubtle),
                                        modifier = Modifier.fillMaxWidth().height(42.dp)
                                    ) {
                                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Export Audio to Train Cloned Voice (ElevenLabs/XTTS)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }

                                Text(
                                    text = "💡 How to clone your voice: Your audio file is saved in studio 44.1kHz AAC format. Tap 'Export' to send it to ElevenLabs VoiceLab, Cartesia, or OpenAI Voice Engine to create a cloned Voice ID.",
                                    fontSize = 11.5.sp,
                                    color = AppleTextTertiary,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
