package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AppleBackground
import com.example.ui.theme.AppleBlue
import com.example.ui.theme.AppleGreen
import com.example.ui.theme.AppleOrange
import com.example.ui.theme.AppleSurface
import com.example.ui.theme.AppleSurfaceBorder
import com.example.ui.theme.AppleSurfaceElevated
import com.example.ui.theme.AppleTextPrimary
import com.example.ui.theme.AppleTextSecondary
import com.example.ui.theme.AppleTextTertiary

data class ResearchTopic(
    val title: String,
    val domain: String,
    val summary: String,
    val sources: List<String>,
    val searchQuery: String
)

@Composable
fun ResearchScreen(
    onTriggerResearchQuery: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var queryText by remember { mutableStateOf("") }

    val presetTopics = remember {
        listOf(
            ResearchTopic(
                title = "Next-Gen Audio Engineering Protocols",
                domain = "Acoustics & Production",
                summary = "Mastering -14 LUFS streaming normalization targets, 32-bit float recording headroom to eliminate digital clipping, and binaural spatial audio panning for immersive media.",
                sources = listOf("AES (Audio Engineering Society)", "Sound on Sound", "ITU-R BS.1770-4"),
                searchQuery = "32 bit float audio recording LUFS standards"
            ),
            ResearchTopic(
                title = "High-Ticket AV Production Retainers",
                domain = "Business & Strategy",
                summary = "Structuring monthly recurring content retainers ($3,000 - $10,000/mo) for commercial brands rather than per-project billing. Includes guaranteed monthly deliverables, revision limits, and automated review acquisition.",
                sources = listOf("Harvard Business Review", "Commercial Production Guild", "Forbes Agency Council"),
                searchQuery = "creative agency retainer business model pricing strategy"
            ),
            ResearchTopic(
                title = "Kenyan Digital Economy & Micro-Ledger Systems",
                domain = "Finance & Tech",
                summary = "Analysis of sovereign mobile accounting, cashless SME transaction velocity, and personal cashflow management in Kenyan Shillings.",
                sources = listOf("Central Bank of Kenya Reports", "TechCrunch Africa", "World Bank Economic Prospect"),
                searchQuery = "Kenya mobile money micro enterprise financial management"
            )
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(AppleBackground),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Search & Intelligence Sweep Bar
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AppleSurfaceElevated),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, AppleSurfaceBorder),
                modifier = Modifier.fillMaxWidth().testTag("research_sweep_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Language, contentDescription = null, tint = AppleBlue, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "INTERNET RESEARCH ENGINE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = AppleTextTertiary,
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Verified Sources & Executive Findings",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppleTextPrimary
                    )
                    Text(
                        text = "Instruct GA to research any topic, verify facts, or extract strategic takeaways.",
                        fontSize = 12.sp,
                        color = AppleTextSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = queryText,
                            onValueChange = { queryText = it },
                            placeholder = { Text("Enter topic to research...", fontSize = 13.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = AppleTextPrimary,
                                unfocusedTextColor = AppleTextPrimary,
                                focusedBorderColor = AppleBlue,
                                unfocusedBorderColor = AppleSurfaceBorder
                            )
                        )

                        Button(
                            onClick = {
                                if (queryText.isNotBlank()) {
                                    val q = queryText
                                    queryText = ""
                                    onTriggerResearchQuery("Do research from internet and reliable sources on: $q")
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AppleBlue),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Icon(Icons.Default.Search, contentDescription = "Search", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Research", fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "CURATED INTELLIGENCE BRIEFS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = AppleTextTertiary,
                letterSpacing = 1.sp
            )
        }

        items(presetTopics) { topic ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = AppleSurface),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, AppleSurfaceBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(topic.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = AppleTextPrimary)
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = AppleBlue.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = topic.domain,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppleBlue,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(topic.summary, fontSize = 13.sp, color = AppleTextSecondary, lineHeight = 18.sp)

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Verified Sources:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AppleTextTertiary)
                    Text(topic.sources.joinToString(" • "), fontSize = 11.sp, color = AppleTextSecondary)

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                onTriggerResearchQuery("Provide an in-depth executive briefing on ${topic.title} based on verified sources.")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AppleSurfaceElevated),
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, AppleSurfaceBorder),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AppleBlue, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Synthesize in GA", fontSize = 11.sp, color = AppleTextPrimary)
                        }

                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=${Uri.encode(topic.searchQuery)}")).apply {
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AppleSurfaceElevated),
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, AppleSurfaceBorder),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.OpenInBrowser, contentDescription = null, tint = AppleGreen, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Open Web Search", fontSize = 11.sp, color = AppleTextPrimary)
                        }
                    }
                }
            }
        }
    }
}
