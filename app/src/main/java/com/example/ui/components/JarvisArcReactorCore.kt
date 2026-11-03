package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AppleBlue
import com.example.ui.theme.AppleGreen
import com.example.ui.theme.AppleOrange
import com.example.ui.theme.AppleRed
import com.example.ui.theme.AppleTextPrimary
import com.example.ui.theme.AppleTextSecondary
import com.example.ui.theme.AppleTextTertiary

@Composable
fun JarvisArcReactorCore(
    isListening: Boolean,
    isThinking: Boolean,
    isSpeaking: Boolean,
    isAmbientWakeWordActive: Boolean,
    isKillSwitchActive: Boolean,
    rmsDb: Float,
    onCoreClick: () -> Unit,
    onToggleAmbientWakeWord: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "jarvis_reactor")

    // Rotation animations for concentric energy rings
    val outerRingRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isListening) 4000 else 12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "outer_ring"
    )

    val innerRingRotation by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isListening) 2500 else 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "inner_ring"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isSpeaking) 600 else 1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    // Dynamic color theme for core
    val coreColor = when {
        isKillSwitchActive -> AppleRed
        isListening -> Color(0xFF38BDF8) // Electric Sky Cyan
        isThinking -> AppleOrange
        isSpeaking -> Color(0xFFA855F7) // Siri Purple
        isAmbientWakeWordActive -> AppleGreen
        else -> AppleBlue
    }

    val glowGradient = Brush.radialGradient(
        colors = listOf(
            coreColor.copy(alpha = if (isListening) 0.45f else 0.25f),
            coreColor.copy(alpha = 0.08f),
            Color.Transparent
        )
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Holographic Core Container
        Box(
            modifier = Modifier
                .size(140.dp)
                .scale(if (isListening) (1f + rmsDb * 0.2f).coerceIn(1f, 1.3f) else pulseScale)
                .clip(CircleShape)
                .background(glowGradient)
                .clickable { onCoreClick() }
                .testTag("jarvis_arc_reactor_core"),
            contentAlignment = Alignment.Center
        ) {
            // Rotating Arc Reactor Canvas
            Canvas(modifier = Modifier.size(126.dp)) {
                val center = Offset(size.width / 2, size.height / 2)
                val radius = size.width / 2

                // Outer segmented circle
                drawCircle(
                    color = coreColor.copy(alpha = 0.2f),
                    radius = radius - 4.dp.toPx(),
                    style = Stroke(width = 1.5.dp.toPx())
                )

                // Outer energy arcs
                val arcColor = coreColor.copy(alpha = 0.7f)
                val startAngle = outerRingRotation
                drawArc(
                    color = arcColor,
                    startAngle = startAngle,
                    sweepAngle = 70f,
                    useCenter = false,
                    style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                )
                drawArc(
                    color = arcColor,
                    startAngle = startAngle + 120f,
                    sweepAngle = 70f,
                    useCenter = false,
                    style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                )
                drawArc(
                    color = arcColor,
                    startAngle = startAngle + 240f,
                    sweepAngle = 70f,
                    useCenter = false,
                    style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                )

                // Inner counter-rotating ring
                val innerRadius = radius * 0.65f
                drawCircle(
                    color = coreColor.copy(alpha = 0.15f),
                    radius = innerRadius,
                    style = Stroke(width = 1.dp.toPx())
                )
                drawArc(
                    color = coreColor,
                    startAngle = innerRingRotation,
                    sweepAngle = 100f,
                    useCenter = false,
                    style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                )
                drawArc(
                    color = coreColor,
                    startAngle = innerRingRotation + 180f,
                    sweepAngle = 100f,
                    useCenter = false,
                    style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                )

                // Inner core emitter
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color.White, coreColor, coreColor.copy(alpha = 0.3f)),
                        center = center,
                        radius = innerRadius * 0.5f
                    ),
                    radius = innerRadius * 0.45f
                )
            }

            // Center Icon / Status
            Icon(
                imageVector = when {
                    isKillSwitchActive -> Icons.Default.Shield
                    isListening -> Icons.Default.Mic
                    isSpeaking -> Icons.Default.VolumeUp
                    else -> Icons.Default.PowerSettingsNew
                },
                contentDescription = "Jarvis Reactor Core",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Reactor Status Label
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(coreColor)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = when {
                    isKillSwitchActive -> "KILL SWITCH ENGAGED • OFFLINE SOVEREIGN"
                    isListening -> "JARVIS LISTENING • SPEAK COMMAND"
                    isThinking -> "JARVIS COMPUTING • SYNTHESIZING"
                    isSpeaking -> "VOCALIZING RESPONSE • SIR GATEI"
                    isAmbientWakeWordActive -> "WAKE WORD ACTIVE: SAY \"HEY GA\""
                    else -> "JARVIS CORE READY • TAP OR SAY \"HEY GA\""
                },
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = AppleTextPrimary,
                letterSpacing = 0.5.sp
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Sovereign Protection Badges
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0x1F34C759),
                modifier = Modifier.padding(horizontal = 3.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "M-Pesa Safe",
                        tint = AppleGreen,
                        modifier = Modifier.size(10.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "M-Pesa Isolated",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppleGreen
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0x1F0071E3),
                modifier = Modifier.padding(horizontal = 3.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Private Media",
                        tint = AppleBlue,
                        modifier = Modifier.size(10.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "Zero Media Broadcast",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppleBlue
                    )
                }
            }

            // Wake word toggle chip
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isAmbientWakeWordActive) Color(0x1F34C759) else Color(0x1FFFFFFF),
                modifier = Modifier
                    .padding(horizontal = 3.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onToggleAmbientWakeWord() }
            ) {
                Text(
                    text = if (isAmbientWakeWordActive) "\"Shut GA\" to Sleep" else "\"Hey GA\" Listener",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isAmbientWakeWordActive) AppleGreen else AppleTextSecondary,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}
