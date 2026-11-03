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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AppleBackground
import com.example.ui.theme.AppleBlue
import com.example.ui.theme.AppleDivider
import com.example.ui.theme.AppleGreen
import com.example.ui.theme.AppleIndigo
import com.example.ui.theme.AppleRed
import com.example.ui.theme.AppleSurfaceBorder
import com.example.ui.theme.AppleSurfaceElevated
import com.example.ui.theme.AppleTextPrimary
import com.example.ui.theme.AppleTextSecondary
import com.example.ui.theme.AppleTextTertiary
import com.example.ui.theme.TitaniumAccentGradient

@Composable
fun TacticalHudBar(
    isSpeaking: Boolean,
    isVoiceEnabled: Boolean,
    onToggleVoice: () -> Unit,
    activeEngineTitle: String = "THINKING ENGINE",
    onOpenVoiceCalibration: () -> Unit = {},
    onTriggerVoiceCommand: () -> Unit = {},
    isKillSwitchActive: Boolean = false,
    onToggleKillSwitch: () -> Unit = {},
    isLightTheme: Boolean = false,
    onToggleTheme: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(AppleBackground)
            .border(width = 0.5.dp, color = AppleDivider, shape = RoundedCornerShape(bottomStart = 0.dp, bottomEnd = 0.dp))
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .testTag("tactical_hud_bar")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Identity & Status (Clickable to open Voice Studio)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.clickable { onOpenVoiceCalibration() }
            ) {
                // Apple Intelligence Siri-Inspired Orb Button
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.sweepGradient(TitaniumAccentGradient)
                        )
                        .clickable { onTriggerVoiceCommand() }
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0F1015))
                    ) {
                        Icon(Icons.Default.Mic, contentDescription = "Voice Command", tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "GA Executive",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = AppleTextPrimary,
                            letterSpacing = (-0.3).sp
                        )

                        // Kill Switch or Online Capsule
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isKillSwitchActive) AppleRed.copy(alpha = 0.2f) else Color(0x1F34C759))
                                .clickable { onToggleKillSwitch() }
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .clip(CircleShape)
                                    .background(if (isKillSwitchActive) AppleRed else AppleGreen)
                            )
                            Text(
                                text = if (isKillSwitchActive) "OFFLINE" else "READY",
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isKillSwitchActive) AppleRed else AppleGreen
                            )
                        }
                    }

                    Text(
                        text = "Sir Gatei • Voice & Assistant Agent",
                        fontSize = 11.5.sp,
                        color = AppleTextSecondary,
                        letterSpacing = (-0.2).sp
                    )
                }
            }

            // Right-side Actions & Speaking Indicator
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (isSpeaking) {
                    MinimalistVoicePill()
                }

                // Voice Command Mic Action Button
                IconButton(
                    onClick = onTriggerVoiceCommand,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(0x2638BDF8))
                        .border(0.5.dp, Color(0x6638BDF8), CircleShape)
                        .testTag("top_bar_mic_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Voice Command",
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Theme Switcher (Dark Titanium / Light Pearl)
                IconButton(
                    onClick = onToggleTheme,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(AppleSurfaceElevated)
                        .border(0.5.dp, AppleSurfaceBorder, CircleShape)
                        .testTag("theme_toggle_button")
                ) {
                    Icon(
                        imageVector = if (isLightTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                        contentDescription = "Toggle Color Scheme",
                        tint = AppleTextSecondary,
                        modifier = Modifier.size(15.dp)
                    )
                }

                // Voice Tuning & Calibration Studio Button
                IconButton(
                    onClick = onOpenVoiceCalibration,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(AppleSurfaceElevated)
                        .border(0.5.dp, AppleSurfaceBorder, CircleShape)
                        .testTag("voice_calibration_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = "Voice Studio",
                        tint = AppleTextSecondary,
                        modifier = Modifier.size(15.dp)
                    )
                }

                // Voice Mute / Unmute Button
                IconButton(
                    onClick = onToggleVoice,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(if (isVoiceEnabled) Color(0x260071E3) else AppleSurfaceElevated)
                        .border(
                            0.5.dp,
                            if (isVoiceEnabled) Color(0x660071E3) else AppleSurfaceBorder,
                            CircleShape
                        )
                        .testTag("voice_toggle_button")
                ) {
                    Icon(
                        imageVector = if (isVoiceEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                        contentDescription = if (isVoiceEnabled) "Mute Voice" else "Enable Voice",
                        tint = if (isVoiceEnabled) AppleBlue else AppleTextTertiary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MinimalistVoicePill() {
    val transition = rememberInfiniteTransition(label = "wave")
    val h1 by transition.animateFloat(
        initialValue = 4f,
        targetValue = 14f,
        animationSpec = infiniteRepeatable(tween(300, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "h1"
    )
    val h2 by transition.animateFloat(
        initialValue = 12f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(tween(380, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "h2"
    )
    val h3 by transition.animateFloat(
        initialValue = 6f,
        targetValue = 16f,
        animationSpec = infiniteRepeatable(tween(340, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "h3"
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(2.5.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x1F0071E3))
            .padding(horizontal = 7.dp, vertical = 4.dp)
            .height(18.dp)
    ) {
        Box(modifier = Modifier.width(2.5.dp).height(h1.dp).clip(RoundedCornerShape(1.dp)).background(AppleBlue))
        Box(modifier = Modifier.width(2.5.dp).height(h2.dp).clip(RoundedCornerShape(1.dp)).background(AppleIndigo))
        Box(modifier = Modifier.width(2.5.dp).height(h3.dp).clip(RoundedCornerShape(1.dp)).background(AppleBlue))
    }
}
