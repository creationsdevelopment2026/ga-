package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.AlarmClock
import android.provider.CalendarContract
import android.provider.MediaStore
import com.example.data.model.PaymentMethod
import com.example.data.model.TransactionCategory
import com.example.data.model.TransactionItem
import java.net.URLEncoder

data class ParsedTransaction(
    val amountKsh: Double,
    val purpose: String,
    val paymentMethod: PaymentMethod,
    val category: TransactionCategory
)

data class ActionResult(
    val executed: Boolean,
    val actionType: String,
    val feedbackMessage: String,
    val intent: Intent? = null,
    val parsedTransaction: ParsedTransaction? = null,
    val isKillSwitchTrigger: Boolean = false,
    val isWakeWordTrigger: Boolean = false
)

object DeviceActionDispatcher {

    fun parseAndDispatch(context: Context, rawCommand: String): ActionResult {
        val query = rawCommand.lowercase().trim()

        // 1. Wake & Shutdown Triggers
        if (query.startsWith("shut ga") || query == "shut ga" || query.contains("kill switch") || query == "turn off ga" || query == "sleep ga") {
            return ActionResult(
                executed = true,
                actionType = "KILL_SWITCH",
                feedbackMessage = "Sir Gatei, shutting down GA voice protocols and engaging sovereign mode. Disconnecting active sensors.",
                isKillSwitchTrigger = true
            )
        }

        if (query.startsWith("hey ga") || query == "hey ga" || query.startsWith("hello ga") || query == "hello ga" || query == "ga") {
            val stripped = rawCommand.replace(Regex("(?i)^(hey ga|hello ga|ga)[,\\s]*"), "").trim()
            if (stripped.isBlank()) {
                return ActionResult(
                    executed = true,
                    actionType = "WAKE_WORD",
                    feedbackMessage = "Yes, Sir Gatei? Core systems online and standing by. What is your command?",
                    isWakeWordTrigger = true
                )
            }
            // If there's an actual command after "Hey GA", re-evaluate stripped query
            return parseAndDispatch(context, stripped)
        }

        // 2. Shilling Flow (Kenyan Shilling Ledger)
        // e.g. "go to shilling flow and add a new transaction of 50 KSh for lunch, mpesa, food"
        if (query.contains("shilling flow") || query.contains("ksh") || query.contains("shilling") ||
            (query.contains("transaction") && query.contains("add"))
        ) {
            val parsedTx = parseShillingTransaction(rawCommand)
            if (parsedTx != null) {
                val methodStr = if (parsedTx.paymentMethod == PaymentMethod.MPESA) "M-PESA" else "Cash"
                return ActionResult(
                    executed = true,
                    actionType = "SHILLING_FLOW",
                    feedbackMessage = "Sir Gatei, logged ${parsedTx.amountKsh.toInt()} KSh for '${parsedTx.purpose}' via $methodStr under ${parsedTx.category.label}. (Sovereign Safety: M-Pesa PIN and SIM Toolkit remain strictly inaccessible to GA).",
                    parsedTransaction = parsedTx
                )
            }
        }

        // 3. Camera (Photo & Video) - Strict rule: NEVER post photos or videos
        if (query.contains("take a picture") || query.contains("take a photo") || query.contains("open camera") || query.contains("snap")) {
            return handleCameraCapture(context, isVideo = false)
        }

        if (query.contains("record a video") || query.contains("video record") || query.contains("record video")) {
            val duration = if (query.contains("30")) 30 else 60
            return handleCameraCapture(context, isVideo = true, durationSeconds = duration)
        }

        // 4. YouTube & Videos
        if (query.contains("youtube") || (query.contains("play") && query.contains("video"))) {
            return handleYouTubePlayback(context, rawCommand)
        }

        // 5. Music: Spotify, Phoenix, YouTube
        if (query.contains("spotify") || query.contains("phoenix") || query.startsWith("play ") || query.contains("play music") || query.contains("play song")) {
            return handleMusicPlayback(context, rawCommand)
        }

        // 6. WhatsApp Messaging & Reading
        if (query.contains("whatsapp")) {
            return handleWhatsAppAction(context, rawCommand)
        }

        // 7. Email / Gmail Actions
        if (query.contains("email") || query.contains("gmail") || query.contains("inbox")) {
            return handleEmailAction(context, rawCommand)
        }

        // 8. Alarm & Timers
        if (query.contains("alarm") || query.contains("wake me up at") || query.contains("set a timer")) {
            return handleSetAlarm(context, rawCommand)
        }

        // 9. Calendar & Scheduling
        if (query.contains("calendar") || query.contains("open calendar") || query.contains("my schedule")) {
            return handleOpenCalendar(context)
        }

        // 10. Phone Calls
        if (query.startsWith("call ") || query.contains("make a call to") || query.contains("dial ")) {
            return handlePhoneCall(context, rawCommand)
        }

        // 11. Files & Documents
        if (query.contains("files") || query.contains("pull up a file") || query.contains("show me a file") || query.contains("open file")) {
            return handleOpenFiles(context, rawCommand)
        }

        // 12. Research on Internet
        if (query.contains("research") || query.contains("search google") || query.contains("search internet") || query.contains("find information on")) {
            return handleInternetResearch(context, rawCommand)
        }

        // 13. To-Do / Priority Rescheduling
        if (query.contains("priorities") || query.contains("reschedule my to-do") || query.contains("order of priorities")) {
            return ActionResult(
                executed = true,
                actionType = "RESCHEDULE_PRIORITIES",
                feedbackMessage = "Sir Gatei, auditing your agenda and reorganizing your task matrix in strict order of strategic and critical priorities."
            )
        }

        return ActionResult(
            executed = false,
            actionType = "NONE",
            feedbackMessage = ""
        )
    }

    private fun parseShillingTransaction(command: String): ParsedTransaction? {
        val lower = command.lowercase()

        // Extract amount (e.g., 50 KSh, 1000 ksh, 250 bob, 500)
        val amountRegex = Regex("(\\d+(?:\\.\\d+)?)\\s*(?:ksh|shillings?|bob)?")
        val match = amountRegex.findAll(lower).firstOrNull { it.groupValues[1].toDoubleOrNull() != null }
        val amount = match?.groupValues?.get(1)?.toDoubleOrNull() ?: 50.0

        // Payment method: cash or mpesa
        val method = if (lower.contains("mpesa") || lower.contains("m-pesa")) PaymentMethod.MPESA else PaymentMethod.CASH

        // Category
        val category = when {
            lower.contains("food") || lower.contains("lunch") || lower.contains("dinner") || lower.contains("breakfast") || lower.contains("coffee") -> TransactionCategory.FOOD
            lower.contains("transport") || lower.contains("uber") || lower.contains("matatu") || lower.contains("fuel") || lower.contains("fare") -> TransactionCategory.TRANSPORT
            lower.contains("production") || lower.contains("av") || lower.contains("gear") || lower.contains("camera") || lower.contains("mic") -> TransactionCategory.PRODUCTION
            lower.contains("bill") || lower.contains("token") || lower.contains("wifi") || lower.contains("rent") -> TransactionCategory.OPERATIONS
            else -> TransactionCategory.PERSONAL
        }

        // Purpose description
        var purpose = "Personal expense"
        val forMatch = Regex("(?i)(?:for|use of|on)\\s+([a-zA-Z0-9\\s]{2,30})").find(command)
        if (forMatch != null) {
            purpose = forMatch.groupValues[1].replace(Regex("(?i)\\s+(mpesa|cash|ksh).*"), "").trim()
        }

        return ParsedTransaction(
            amountKsh = amount,
            purpose = purpose.ifBlank { "Personal item" },
            paymentMethod = method,
            category = category
        )
    }

    private fun handleCameraCapture(context: Context, isVideo: Boolean, durationSeconds: Int = 30): ActionResult {
        // Try camera app intent first (STILL_IMAGE_CAMERA or VIDEO_CAMERA) so user has real viewfinder
        val action = if (isVideo) {
            MediaStore.INTENT_ACTION_VIDEO_CAMERA
        } else {
            MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA
        }

        val intent = Intent(action).apply {
            if (isVideo) {
                putExtra(MediaStore.EXTRA_DURATION_LIMIT, durationSeconds)
            }
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        val message = if (isVideo) {
            "Launching camera for 30-second video recording, Sir Gatei. (Notice: Video is saved privately to device; social posting is strictly blocked)."
        } else {
            "Opening camera viewfinder, Sir Gatei. (Notice: Photos are stored privately; GA never broadcasts media)."
        }

        return try {
            context.startActivity(intent)
            ActionResult(executed = true, actionType = "CAMERA", feedbackMessage = message, intent = intent)
        } catch (_: Exception) {
            // Fallback to capture intent
            val fallback = Intent(if (isVideo) MediaStore.ACTION_VIDEO_CAPTURE else MediaStore.ACTION_IMAGE_CAPTURE).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            try {
                context.startActivity(fallback)
                ActionResult(executed = true, actionType = "CAMERA", feedbackMessage = message, intent = fallback)
            } catch (_: Exception) {
                ActionResult(executed = false, actionType = "CAMERA", feedbackMessage = "Camera application could not be reached.")
            }
        }
    }

    private fun handleYouTubePlayback(context: Context, command: String): ActionResult {
        val query = command
            .replace(Regex("(?i)^(play|watch|open|find)\\s+"), "")
            .replace(Regex("(?i)\\s+(on|using|in)\\s+youtube.*"), "")
            .replace(Regex("(?i)\\s+video.*"), "")
            .trim()

        val encoded = URLEncoder.encode(query.ifBlank { "Trending" }, "UTF-8")
        val ytIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query=$encoded")).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        return try {
            context.startActivity(ytIntent)
            ActionResult(
                executed = true,
                actionType = "YOUTUBE",
                feedbackMessage = "Opening YouTube for \"$query\", Sir Gatei.",
                intent = ytIntent
            )
        } catch (_: Exception) {
            ActionResult(executed = false, actionType = "YOUTUBE", feedbackMessage = "Unable to launch YouTube.")
        }
    }

    private fun handleMusicPlayback(context: Context, command: String): ActionResult {
        val lower = command.lowercase()
        val queryCleaned = command
            .replace(Regex("(?i)^(play|listen to|put on)\\s+"), "")
            .replace(Regex("(?i)\\s+(on|using|in)\\s+(spotify|youtube|phoenix)"), "")
            .trim()

        return when {
            lower.contains("spotify") -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("spotify:search:$queryCleaned")).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                try {
                    context.startActivity(intent)
                    ActionResult(executed = true, actionType = "MUSIC_SPOTIFY", feedbackMessage = "Playing $queryCleaned on Spotify, Sir Gatei.")
                } catch (_: Exception) {
                    val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://open.spotify.com/search/$queryCleaned")).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(webIntent)
                    ActionResult(executed = true, actionType = "MUSIC_SPOTIFY", feedbackMessage = "Opening Spotify for $queryCleaned, Sir Gatei.")
                }
            }

            lower.contains("phoenix") -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=$queryCleaned")).apply {
                    setPackage("com.transsion.phoenix")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                try {
                    context.startActivity(intent)
                    ActionResult(executed = true, actionType = "PHOENIX", feedbackMessage = "Launching Phoenix browser for $queryCleaned, Sir Gatei.")
                } catch (_: Exception) {
                    val fallback = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=$queryCleaned")).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(fallback)
                    ActionResult(executed = true, actionType = "BROWSER", feedbackMessage = "Opening web player for $queryCleaned, Sir Gatei.")
                }
            }

            else -> {
                handleYouTubePlayback(context, command)
            }
        }
    }

    private fun handleWhatsAppAction(context: Context, command: String): ActionResult {
        val lower = command.lowercase()

        // Check if query is checking messages or opening
        if (lower.contains("tell me if") || lower.contains("has texted") || lower.contains("read that text") || lower.contains("check whatsapp")) {
            val launchIntent = context.packageManager.getLaunchIntentForPackage("com.whatsapp")
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(launchIntent)
                return ActionResult(
                    executed = true,
                    actionType = "WHATSAPP",
                    feedbackMessage = "Opening WhatsApp to check recent chats and unread messages for you, Sir Gatei."
                )
            }
        }

        // Draft message
        var messageText = "Hello, this is Gatei."
        val tellMatch = Regex("(?i)(tell|saying|text)\\s+(?:him|her|them)?\\s*(.*)").find(command)
        if (tellMatch != null && tellMatch.groupValues[2].isNotBlank()) {
            messageText = tellMatch.groupValues[2].trim()
        }

        val encoded = try {
            URLEncoder.encode(messageText, "UTF-8")
        } catch (_: Exception) {
            messageText
        }

        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://api.whatsapp.com/send?text=$encoded")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        return try {
            context.startActivity(intent)
            ActionResult(
                executed = true,
                actionType = "WHATSAPP",
                feedbackMessage = "WhatsApp launched with your pre-drafted message, Sir Gatei: \"$messageText\"",
                intent = intent
            )
        } catch (_: Exception) {
            val launchIntent = context.packageManager.getLaunchIntentForPackage("com.whatsapp")
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(launchIntent)
                ActionResult(executed = true, actionType = "WHATSAPP", feedbackMessage = "WhatsApp opened, Sir Gatei.")
            } else {
                ActionResult(executed = false, actionType = "WHATSAPP", feedbackMessage = "WhatsApp application is not installed on this device.")
            }
        }
    }

    private fun handleEmailAction(context: Context, command: String): ActionResult {
        val launchIntent = context.packageManager.getLaunchIntentForPackage("com.google.android.gm")
            ?: Intent(Intent.ACTION_VIEW, Uri.parse("mailto:")).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }

        return try {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(launchIntent)
            ActionResult(
                executed = true,
                actionType = "EMAIL",
                feedbackMessage = "Opening your primary email inbox, Sir Gatei. Ready to sort and triage communications.",
                intent = launchIntent
            )
        } catch (_: Exception) {
            ActionResult(executed = false, actionType = "EMAIL", feedbackMessage = "Unable to launch email client.")
        }
    }

    private fun handleInternetResearch(context: Context, command: String): ActionResult {
        val topic = command
            .replace(Regex("(?i)^(do a research from the internet and reliable sources and give me findings on|research|find info on|search google for)\\s*"), "")
            .trim()

        val searchIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=${URLEncoder.encode(topic, "UTF-8")}")).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        return try {
            context.startActivity(searchIntent)
            ActionResult(
                executed = true,
                actionType = "RESEARCH",
                feedbackMessage = "Conducting rapid intelligence sweep on \"$topic\", Sir Gatei. Launching trusted search results and synthesizing findings.",
                intent = searchIntent
            )
        } catch (_: Exception) {
            ActionResult(executed = false, actionType = "RESEARCH", feedbackMessage = "Unable to launch web intelligence browser.")
        }
    }

    private fun handleSetAlarm(context: Context, command: String): ActionResult {
        val lower = command.lowercase()
        var hour = 7
        var minutes = 0

        val timeRegex = Regex("(\\d{1,2})(?::(\\d{2}))?\\s*(am|pm)?")
        val match = timeRegex.find(lower)
        if (match != null) {
            val parsedHour = match.groupValues[1].toIntOrNull() ?: 7
            val parsedMin = match.groupValues[2].toIntOrNull() ?: 0
            val amPm = match.groupValues[3]

            hour = when {
                amPm == "pm" && parsedHour < 12 -> parsedHour + 12
                amPm == "am" && parsedHour == 12 -> 0
                else -> parsedHour
            }
            minutes = parsedMin
        }

        val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
            putExtra(AlarmClock.EXTRA_MESSAGE, "Sir Gatei Executive Alarm")
            putExtra(AlarmClock.EXTRA_HOUR, hour)
            putExtra(AlarmClock.EXTRA_MINUTES, minutes)
            putExtra(AlarmClock.EXTRA_SKIP_UI, false)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        val timeStr = String.format("%02d:%02d", hour, minutes)
        return try {
            context.startActivity(intent)
            ActionResult(
                executed = true,
                actionType = "SET_ALARM",
                feedbackMessage = "Alarm set for $timeStr, Sir Gatei.",
                intent = intent
            )
        } catch (_: Exception) {
            ActionResult(executed = false, actionType = "SET_ALARM", feedbackMessage = "Unable to set system alarm.")
        }
    }

    private fun handleOpenCalendar(context: Context): ActionResult {
        val builder = CalendarContract.CONTENT_URI.buildUpon().appendPath("time")
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = builder.build()
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        return try {
            context.startActivity(intent)
            ActionResult(executed = true, actionType = "OPEN_CALENDAR", feedbackMessage = "Opening your executive calendar, Sir Gatei.", intent = intent)
        } catch (_: Exception) {
            val fallback = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_APP_CALENDAR)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            try {
                context.startActivity(fallback)
                ActionResult(executed = true, actionType = "OPEN_CALENDAR", feedbackMessage = "Opening calendar, Sir Gatei.")
            } catch (_: Exception) {
                ActionResult(executed = false, actionType = "OPEN_CALENDAR", feedbackMessage = "Calendar app not found.")
            }
        }
    }

    private fun handlePhoneCall(context: Context, command: String): ActionResult {
        val cleaned = command
            .replace(Regex("(?i)^(call|make a call to|dial)\\s+"), "")
            .trim()

        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = if (cleaned.any { it.isDigit() }) {
                Uri.parse("tel:${cleaned.filter { it.isDigit() || it == '+' }}")
            } else {
                Uri.parse("tel:")
            }
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        return try {
            context.startActivity(intent)
            ActionResult(executed = true, actionType = "PHONE_CALL", feedbackMessage = "Dialer initiated for $cleaned, Sir Gatei.", intent = intent)
        } catch (_: Exception) {
            ActionResult(executed = false, actionType = "PHONE_CALL", feedbackMessage = "Unable to launch phone dialer.")
        }
    }

    private fun handleOpenFiles(context: Context, command: String): ActionResult {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
            addCategory(Intent.CATEGORY_OPENABLE)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        return try {
            context.startActivity(Intent.createChooser(intent, "Pull Up File"))
            ActionResult(executed = true, actionType = "FILES", feedbackMessage = "Pulling up file directory, Sir Gatei.")
        } catch (_: Exception) {
            ActionResult(executed = false, actionType = "FILES", feedbackMessage = "Unable to access file directory.")
        }
    }
}
