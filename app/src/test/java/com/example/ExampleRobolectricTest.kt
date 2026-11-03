package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.ai.GateiLocalEngine
import com.example.util.AutoVocalizeMode
import com.example.util.VoiceProfile
import com.example.util.VoiceRecordingManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("GATEI AI", appName)
    }

    @Test
    fun `verify GA local engine addresses Sir Gatei and produces KFC AV concept`() {
        val response = GateiLocalEngine.processQuery("Generate KFC AV PRODUCTIONS concept")
        assertTrue(response.content.contains("Sir Gatei"))
        assertTrue(response.content.contains("KFC AV PRODUCTIONS"))
        assertEquals("CREATIVE", response.category)
    }

    @Test
    fun `verify GA executive secretary asks for details when scheduling a meeting`() {
        val response = GateiLocalEngine.processQuery("Schedule a meeting")
        assertTrue(response.content.contains("Sir Gatei"))
        assertTrue(response.content.contains("venue") || response.content.contains("objective") || response.content.contains("time"))
        assertEquals("SECRETARY", response.category)
    }

    @Test
    fun `verify GA executive secretary flags conflict and prepares first-person draft message`() {
        val response = GateiLocalEngine.processQuery("Schedule a meeting with Apex tomorrow at 2 PM")
        assertTrue(response.content.contains("Sir Gatei"))
        assertTrue(response.content.contains("CONFLICT"))
        assertTrue(response.content.contains("Titan Media"))
        assertTrue(response.content.contains("I need to adjust") || response.content.contains("Marcus"))
        assertEquals("SECRETARY", response.category)
    }

    @Test
    fun `verify natural male voice profiles are configured with fast executive velocity`() {
        val jarvis = VoiceProfile.JARVIS_BRITISH
        assertTrue(jarvis.pitch < 1.0f)
        assertTrue(jarvis.speed >= 1.25f) // Fast, non-sluggish reading speed

        val baritone = VoiceProfile.DEEP_BARITONE
        assertTrue(baritone.pitch <= 0.85f)
        assertTrue(baritone.speed >= 1.25f)
    }

    @Test
    fun `verify voice recording manager initializes sample directory`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val recordingManager = VoiceRecordingManager(context)
        assertNotNull(recordingManager.sampleFile)
        assertEquals("gatei_name_sample.m4a", recordingManager.sampleFile.name)
    }

    @Test
    fun `verify Shilling Flow parsing and M-Pesa boundary protection`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val result = com.example.util.DeviceActionDispatcher.parseAndDispatch(context, "add transaction of 50 KSh for lunch, cash, food")
        assertTrue(result.executed)
        assertEquals("SHILLING_FLOW", result.actionType)
        assertNotNull(result.parsedTransaction)
        assertEquals(50.0, result.parsedTransaction!!.amountKsh, 0.01)
        assertTrue(result.feedbackMessage.contains("M-Pesa PIN and SIM Toolkit remain strictly inaccessible"))
    }

    @Test
    fun `verify wake word Hey GA and shutdown trigger Shut GA`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val wakeResult = com.example.util.DeviceActionDispatcher.parseAndDispatch(context, "Hey GA")
        assertTrue(wakeResult.isWakeWordTrigger)
        assertTrue(wakeResult.feedbackMessage.contains("Sir Gatei"))

        val shutResult = com.example.util.DeviceActionDispatcher.parseAndDispatch(context, "Shut GA")
        assertTrue(shutResult.isKillSwitchTrigger)
        assertTrue(shutResult.feedbackMessage.contains("shutting down GA voice protocols"))
    }
}
