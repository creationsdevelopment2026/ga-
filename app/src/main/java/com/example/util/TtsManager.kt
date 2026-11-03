package com.example.util

import android.content.Context
import android.content.SharedPreferences
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

enum class AutoVocalizeMode(val label: String, val description: String) {
    CONCISE_PUNCHY("Concise Briefing (Fast)", "Speaks only the key 1-2 sentence response rapidly without reading long lists"),
    SILENT_ON_DEMAND("Silent / On-Demand Only", "Does not auto-speak responses. Speaks only when you tap the speaker icon"),
    FULL_TEXT("Full Readout", "Reads the entire generated response")
}

enum class VoiceProfile(
    val label: String,
    val locale: Locale,
    val pitch: Float,
    val speed: Float,
    val voiceKeywords: List<String>
) {
    JARVIS_BRITISH(
        label = "Jarvis (British Fast & Deep)",
        locale = Locale.UK,
        pitch = 0.88f,
        speed = 1.30f,
        voiceKeywords = listOf("en-gb", "male", "rjs", "m0")
    ),
    EXECUTIVE_US(
        label = "Executive (Natural US Male)",
        locale = Locale.US,
        pitch = 0.92f,
        speed = 1.32f,
        voiceKeywords = listOf("en-us", "male", "iom", "sfg", "m0")
    ),
    DEEP_BARITONE(
        label = "Deep Baritone (Low Resonance)",
        locale = Locale.US,
        pitch = 0.82f,
        speed = 1.28f,
        voiceKeywords = listOf("male", "en-us")
    ),
    CALM_STRATEGIST(
        label = "High Velocity (Rapid Delivery)",
        locale = Locale.UK,
        pitch = 0.94f,
        speed = 1.45f,
        voiceKeywords = listOf("en-gb", "male")
    )
}

class TtsManager(private val context: Context) : TextToSpeech.OnInitListener {
    private val prefs: SharedPreferences = context.getSharedPreferences("ga_voice_settings", Context.MODE_PRIVATE)
    private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    private var isInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _isVoiceEnabled = MutableStateFlow(true)
    val isVoiceEnabled: StateFlow<Boolean> = _isVoiceEnabled.asStateFlow()

    private val _vocalizeMode = MutableStateFlow(
        AutoVocalizeMode.entries.find { it.name == prefs.getString("vocalize_mode", AutoVocalizeMode.CONCISE_PUNCHY.name) }
            ?: AutoVocalizeMode.CONCISE_PUNCHY
    )
    val vocalizeMode: StateFlow<AutoVocalizeMode> = _vocalizeMode.asStateFlow()

    private val _currentProfile = MutableStateFlow(
        VoiceProfile.entries.find { it.name == prefs.getString("profile", VoiceProfile.JARVIS_BRITISH.name) }
            ?: VoiceProfile.JARVIS_BRITISH
    )
    val currentProfile: StateFlow<VoiceProfile> = _currentProfile.asStateFlow()

    // Default fast executive speed (1.30x)
    private val _pitch = MutableStateFlow(prefs.getFloat("pitch", 0.88f))
    val pitch: StateFlow<Float> = _pitch.asStateFlow()

    private val _speed = MutableStateFlow(prefs.getFloat("speed", 1.30f))
    val speed: StateFlow<Float> = _speed.asStateFlow()

    // How "Gatei" should be phonetically spoken
    private val _phoneticPronunciation = MutableStateFlow(
        prefs.getString("phonetic_name", "Guh-tay") ?: "Guh-tay"
    )
    val phoneticPronunciation: StateFlow<String> = _phoneticPronunciation.asStateFlow()

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.let { engine ->
                applyVoiceSettings(engine, _currentProfile.value, _pitch.value, _speed.value)
                isInitialized = true

                engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        _isSpeaking.value = true
                    }

                    override fun onDone(utteranceId: String?) {
                        _isSpeaking.value = false
                    }

                    override fun onError(utteranceId: String?) {
                        _isSpeaking.value = false
                    }
                })
            }
        }
    }

    private fun applyVoiceSettings(engine: TextToSpeech, profile: VoiceProfile, pitchVal: Float, speedVal: Float) {
        engine.language = profile.locale
        engine.setPitch(pitchVal)
        engine.setSpeechRate(speedVal)

        // Select the most natural male voice available on device
        try {
            val availableVoices = engine.voices
            if (!availableVoices.isNullOrEmpty()) {
                val bestVoice = availableVoices
                    .filter { voice ->
                        voice.locale.language == profile.locale.language &&
                                !voice.isNetworkConnectionRequired &&
                                (voice.name.contains("male", ignoreCase = true) ||
                                        !voice.name.contains("female", ignoreCase = true))
                    }
                    .maxByOrNull { voice ->
                        var score = 0
                        if (voice.name.contains("male", ignoreCase = true)) score += 10
                        if (voice.name.contains("local", ignoreCase = true)) score += 5
                        if (voice.quality >= Voice.QUALITY_HIGH) score += 5
                        profile.voiceKeywords.forEach { kw ->
                            if (voice.name.contains(kw, ignoreCase = true)) score += 3
                        }
                        score
                    }

                bestVoice?.let {
                    engine.voice = it
                }
            }
        } catch (_: Exception) {
            // Fallback to default locale configuration
        }
    }

    fun setProfile(profile: VoiceProfile) {
        _currentProfile.value = profile
        _pitch.value = profile.pitch
        _speed.value = profile.speed

        prefs.edit()
            .putString("profile", profile.name)
            .putFloat("pitch", profile.pitch)
            .putFloat("speed", profile.speed)
            .apply()

        tts?.let { applyVoiceSettings(it, profile, profile.pitch, profile.speed) }
    }

    fun setVocalizeMode(mode: AutoVocalizeMode) {
        _vocalizeMode.value = mode
        prefs.edit().putString("vocalize_mode", mode.name).apply()
    }

    fun updatePitchAndSpeed(newPitch: Float, newSpeed: Float) {
        _pitch.value = newPitch
        _speed.value = newSpeed

        prefs.edit()
            .putFloat("pitch", newPitch)
            .putFloat("speed", newSpeed)
            .apply()

        tts?.setPitch(newPitch)
        tts?.setSpeechRate(newSpeed)
    }

    fun setPhoneticPronunciation(pronunciation: String) {
        _phoneticPronunciation.value = pronunciation
        prefs.edit().putString("phonetic_name", pronunciation).apply()
    }

    fun toggleVoiceEnabled() {
        val newState = !_isVoiceEnabled.value
        _isVoiceEnabled.value = newState
        if (!newState) {
            stop()
        }
    }

    fun auditionVoice() {
        val speedVal = _speed.value
        val speedLabel = if (speedVal >= 1.3f) "fast and concise" else "calibrated"
        speakExplicit("Good day, Sir Gatei. Executive voice response is active, $speedLabel.")
    }

    /**
     * Automatic vocalization for newly generated messages.
     * Respects user preference:
     * - CONCISE_PUNCHY: Speaks only the initial 1-2 punchy sentences or direct question rapidly.
     * - SILENT_ON_DEMAND: Does not auto-speak.
     * - FULL_TEXT: Speaks the full cleaned text.
     */
    fun speakAutoResponse(fullText: String) {
        if (!_isVoiceEnabled.value || !isInitialized || fullText.isBlank()) return

        when (_vocalizeMode.value) {
            AutoVocalizeMode.SILENT_ON_DEMAND -> return
            AutoVocalizeMode.CONCISE_PUNCHY -> {
                val concise = extractConcisePunchline(fullText)
                if (concise.isNotBlank()) {
                    speakInternal(concise)
                }
            }
            AutoVocalizeMode.FULL_TEXT -> {
                speakInternal(fullText)
            }
        }
    }

    /**
     * Explicit on-demand speech triggered by the user tapping a specific message's speak button.
     */
    fun speakExplicit(text: String) {
        if (!isInitialized || text.isBlank()) return
        speakInternal(text)
    }

    private fun speakInternal(text: String) {
        stop()
        val phonetic = _phoneticPronunciation.value

        var preparedText = text
            .replace(Regex("(?i)\\bGatei\\b"), phonetic)
            .replace(Regex("[#*`_~>\\[\\]()•]"), "")
            .replace(Regex("[-]{3,}"), "")
            .trim()

        val utteranceId = "GA_TTS_${System.currentTimeMillis()}"
        tts?.speak(preparedText, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

    /**
     * Extracts only the key executive sentence/question so GA doesn't bore the user
     * by slowly reading 5 paragraphs of text.
     */
    private fun extractConcisePunchline(text: String): String {
        val lines = text.lines()
            .map { it.trim() }
            .filter { line ->
                line.isNotEmpty() &&
                        !line.startsWith("#") &&
                        !line.startsWith("[") &&
                        !line.startsWith("•") &&
                        !line.startsWith("-") &&
                        !line.startsWith("1.") &&
                        !line.startsWith("2.")
            }

        val firstMeaningful = lines.firstOrNull { it.length > 15 } ?: text.take(120)

        // If it contains a question mark, cut up to the question mark to give the immediate prompt
        val questionIdx = firstMeaningful.indexOf('?')
        if (questionIdx != -1 && questionIdx < 160) {
            return firstMeaningful.substring(0, questionIdx + 1).trim()
        }

        // Otherwise return the first sentence
        val periodIdx = firstMeaningful.indexOf(". ")
        return if (periodIdx != -1 && periodIdx > 20) {
            firstMeaningful.substring(0, periodIdx + 1).trim()
        } else {
            firstMeaningful.take(140).trim()
        }
    }

    fun stop() {
        if (isInitialized) {
            tts?.stop()
            _isSpeaking.value = false
        }
    }

    fun shutdown() {
        stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
    }
}
