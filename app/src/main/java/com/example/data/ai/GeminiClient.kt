package com.example.data.ai

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiClient {
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun generateResponse(
        prompt: String,
        conversationHistory: List<Pair<String, String>> = emptyList()
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            // Local engine fallback
            return@withContext GateiLocalEngine.processQuery(prompt).content
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
            
            // Build contents array
            val contentsArray = JSONArray()
            
            // Add conversation history
            for ((role, text) in conversationHistory.takeLast(6)) {
                val turnObj = JSONObject()
                turnObj.put("role", if (role == "user") "user" else "model")
                val partsArray = JSONArray()
                val partObj = JSONObject()
                partObj.put("text", text)
                partsArray.put(partObj)
                turnObj.put("parts", partsArray)
                contentsArray.put(turnObj)
            }
            
            // Add current turn
            val currentTurn = JSONObject()
            currentTurn.put("role", "user")
            val currentParts = JSONArray()
            val currentPart = JSONObject()
            currentPart.put("text", prompt)
            currentParts.put(currentPart)
            currentTurn.put("parts", currentParts)
            contentsArray.put(currentTurn)

            // Build system instruction
            val systemInstruction = JSONObject()
            val sysParts = JSONArray()
            val sysPart = JSONObject()
            sysPart.put("text", GateiSystemPrompt.MASTER_PROMPT)
            sysParts.put(sysPart)
            systemInstruction.put("parts", sysParts)

            // Main request body
            val requestJson = JSONObject()
            requestJson.put("contents", contentsArray)
            requestJson.put("systemInstruction", systemInstruction)

            val requestBody = requestJson.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrBlank()) {
                val jsonResponse = JSONObject(responseBody)
                val candidates = jsonResponse.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val candidate = candidates.getJSONObject(0)
                    val content = candidate.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        val text = parts.getJSONObject(0).optString("text")
                        if (text.isNotBlank()) {
                            return@withContext text
                        }
                    }
                }
            }

            // If response was empty or error, fallback to local engine
            GateiLocalEngine.processQuery(prompt).content
        } catch (e: Exception) {
            // Graceful fallback to local engine
            GateiLocalEngine.processQuery(prompt).content
        }
    }
}
