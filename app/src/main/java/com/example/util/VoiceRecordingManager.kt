package com.example.util

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import androidx.core.content.FileProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File

class VoiceRecordingManager(private val context: Context) {
    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    private var amplitudeJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _isPlayingSample = MutableStateFlow(false)
    val isPlayingSample: StateFlow<Boolean> = _isPlayingSample.asStateFlow()

    private val _hasRecordedSample = MutableStateFlow(false)
    val hasRecordedSample: StateFlow<Boolean> = _hasRecordedSample.asStateFlow()

    private val _amplitude = MutableStateFlow(0f)
    val amplitude: StateFlow<Float> = _amplitude.asStateFlow()

    private val _recordingDurationSeconds = MutableStateFlow(0)
    val recordingDurationSeconds: StateFlow<Int> = _recordingDurationSeconds.asStateFlow()

    private val voiceDir = File(context.filesDir, "voice_samples").apply { if (!exists()) mkdirs() }
    val sampleFile = File(voiceDir, "gatei_name_sample.m4a")

    init {
        _hasRecordedSample.value = sampleFile.exists() && sampleFile.length() > 0
    }

    fun startRecording(): Boolean {
        try {
            stopPlayback()
            val recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }

            recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            recorder.setAudioSamplingRate(44100)
            recorder.setAudioEncodingBitRate(128000)
            recorder.setOutputFile(sampleFile.absolutePath)
            recorder.prepare()
            recorder.start()

            mediaRecorder = recorder
            _isRecording.value = true
            _recordingDurationSeconds.value = 0

            amplitudeJob?.cancel()
            amplitudeJob = scope.launch {
                var seconds = 0
                while (isActive && _isRecording.value) {
                    delay(100)
                    mediaRecorder?.let {
                        try {
                            val max = it.maxAmplitude
                            _amplitude.value = (max / 32767f).coerceIn(0.05f, 1f)
                        } catch (_: Exception) {
                            _amplitude.value = 0.1f
                        }
                    }
                    seconds++
                    if (seconds % 10 == 0) {
                        _recordingDurationSeconds.value = seconds / 10
                    }
                }
            }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            _isRecording.value = false
            return false
        }
    }

    fun stopRecording() {
        if (!_isRecording.value) return
        try {
            amplitudeJob?.cancel()
            mediaRecorder?.apply {
                stop()
                release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            mediaRecorder = null
            _isRecording.value = false
            _amplitude.value = 0f
            _hasRecordedSample.value = sampleFile.exists() && sampleFile.length() > 0
        }
    }

    fun playSample() {
        if (!sampleFile.exists()) return
        stopPlayback()
        try {
            val player = MediaPlayer().apply {
                setDataSource(sampleFile.absolutePath)
                prepare()
                setOnCompletionListener {
                    _isPlayingSample.value = false
                    it.release()
                    mediaPlayer = null
                }
                start()
            }
            mediaPlayer = player
            _isPlayingSample.value = true
        } catch (e: Exception) {
            e.printStackTrace()
            _isPlayingSample.value = false
        }
    }

    fun stopPlayback() {
        try {
            mediaPlayer?.apply {
                if (isPlaying) stop()
                release()
            }
        } catch (_: Exception) {
        } finally {
            mediaPlayer = null
            _isPlayingSample.value = false
        }
    }

    fun exportRecordingIntent(): Intent? {
        if (!sampleFile.exists()) return null
        return try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                sampleFile
            )
            Intent(Intent.ACTION_SEND).apply {
                type = "audio/mp4"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Sir Gatei Voice Calibration Sample")
                putExtra(Intent.EXTRA_TEXT, "Acoustic training sample for Gatei AI voice cloning.")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun clearSample() {
        stopPlayback()
        if (sampleFile.exists()) {
            sampleFile.delete()
        }
        _hasRecordedSample.value = false
    }
}
