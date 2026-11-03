package com.example.util

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class SpeechRecognitionManager(private val context: Context) {
    private var speechRecognizer: SpeechRecognizer? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _isWakeWordMode = MutableStateFlow(false)
    val isWakeWordMode: StateFlow<Boolean> = _isWakeWordMode.asStateFlow()

    private val _recognizedText = MutableStateFlow("")
    val recognizedText: StateFlow<String> = _recognizedText.asStateFlow()

    private val _rmsDb = MutableStateFlow(0f)
    val rmsDb: StateFlow<Float> = _rmsDb.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private var activeCallback: ((String) -> Unit)? = null
    private var onWakeWordCallback: (() -> Unit)? = null
    private var onShutdownCallback: (() -> Unit)? = null

    fun isRecognitionAvailable(): Boolean {
        return SpeechRecognizer.isRecognitionAvailable(context)
    }

    fun getSpeechIntent(): Intent {
        return Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Sir Gatei, speak your command...")
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }
    }

    fun startListening(onResult: (String) -> Unit) {
        activeCallback = onResult
        _error.value = null

        mainHandler.post {
            stopListeningInternal()

            if (!SpeechRecognizer.isRecognitionAvailable(context)) {
                _error.value = "Speech recognition service not present on device. Use manual input or system dialog."
                _isListening.value = false
                return@post
            }

            try {
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                    setRecognitionListener(object : RecognitionListener {
                        override fun onReadyForSpeech(params: Bundle?) {
                            _isListening.value = true
                            _recognizedText.value = ""
                            _error.value = null
                        }

                        override fun onBeginningOfSpeech() {
                            _isListening.value = true
                        }

                        override fun onRmsChanged(rmsdB: Float) {
                            _rmsDb.value = (rmsdB / 10f).coerceIn(0.1f, 1.0f)
                        }

                        override fun onBufferReceived(buffer: ByteArray?) {}

                        override fun onEndOfSpeech() {
                            _isListening.value = false
                        }

                        override fun onError(errorCode: Int) {
                            _isListening.value = false
                            val message = when (errorCode) {
                                SpeechRecognizer.ERROR_NO_MATCH -> "No speech recognized"
                                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Listening timed out"
                                SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                                SpeechRecognizer.ERROR_CLIENT -> "Client error"
                                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission required"
                                else -> "Recognition status: $errorCode"
                            }
                            _error.value = message
                        }

                        override fun onResults(results: Bundle?) {
                            _isListening.value = false
                            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                            if (!matches.isNullOrEmpty()) {
                                val text = matches[0]
                                _recognizedText.value = text
                                activeCallback?.invoke(text)
                            }
                        }

                        override fun onPartialResults(partialResults: Bundle?) {
                            val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                            if (!matches.isNullOrEmpty()) {
                                _recognizedText.value = matches[0]
                            }
                        }

                        override fun onEvent(eventType: Int, params: Bundle?) {}
                    })
                }

                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                    putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                    putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
                }

                _isListening.value = true
                speechRecognizer?.startListening(intent)
            } catch (e: Exception) {
                _isListening.value = false
                _error.value = e.message ?: "Failed to start speech recognition"
            }
        }
    }

    fun startAmbientWakeWordMonitoring(onWake: () -> Unit, onShutdown: () -> Unit) {
        _isWakeWordMode.value = true
        onWakeWordCallback = onWake
        onShutdownCallback = onShutdown

        startListening { recognized ->
            val lower = recognized.lowercase()
            if (lower.contains("shut ga")) {
                onShutdownCallback?.invoke()
            } else if (lower.contains("hey ga") || lower.contains("hello ga") || lower.contains("wake up ga")) {
                onWakeWordCallback?.invoke()
            } else {
                activeCallback?.invoke(recognized)
            }
        }
    }

    fun stopAmbientWakeWordMonitoring() {
        _isWakeWordMode.value = false
        stopListening()
    }

    fun stopListening() {
        mainHandler.post {
            stopListeningInternal()
        }
    }

    private fun stopListeningInternal() {
        try {
            speechRecognizer?.apply {
                stopListening()
                cancel()
                destroy()
            }
        } catch (_: Exception) {
        } finally {
            speechRecognizer = null
            _isListening.value = false
        }
    }
}
