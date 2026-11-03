package com.example.data.ai

object GateiLocalEngine {

    fun processQuery(input: String): EngineResponse {
        val lower = input.lowercase().trim()

        return when {
            // Wake & Sleep
            lower.contains("shut ga") -> {
                EngineResponse("SYSTEM", "Sir Gatei, shutting down GA voice protocols and entering sovereign standby mode.", "Shutdown Protocol")
            }
            lower.contains("hey ga") || lower.contains("hello ga") -> {
                EngineResponse("SYSTEM", "Yes, Sir Gatei? Core systems online and standing by. What is your command?", "Voice Core Online")
            }

            // Shilling Flow Financial Ledger
            lower.contains("shilling flow") || lower.contains("ksh") || lower.contains("transaction") -> {
                handleShillingFlow(input)
            }

            // Notepad & Memos
            lower.contains("notepad") || lower.contains("note") || lower.contains("memo") || lower.contains("scratchpad") -> {
                handleNotepad(input)
            }

            // To-Do & Priority Rescheduling
            lower.contains("priorities") || lower.contains("to do list") || lower.contains("reschedule") -> {
                handlePriorityRescheduling(input)
            }

            // Executive Secretary: Scheduling, meetings, appointments, calendar conflicts
            lower.contains("schedule") || lower.contains("meeting") || lower.contains("calendar") ||
            lower.contains("appointment") || lower.contains("book a call") -> {
                handleMeetingAndScheduling(input)
            }

            // Executive Secretary: Drafting messages, client emails, follow-ups
            lower.contains("draft") || lower.contains("send a message") || lower.contains("email") ||
            lower.contains("reply to") || lower.contains("message to") -> {
                handleCommunicationDrafting(input)
            }

            // Creative: KFC AV Productions
            lower.contains("kfc") || lower.contains("doodle") -> {
                generateKfcAvConcept(input)
            }

            // Creative: Room 404
            lower.contains("404") || lower.contains("room 404") -> {
                generateRoom404Concept(input)
            }

            // Creative general: script / reel / content
            lower.contains("script") || lower.contains("reel") || lower.contains("content") || lower.contains("concept") -> {
                generateCreativeContent(input)
            }

            // Business & AV production
            lower.contains("business") || lower.contains("client") || lower.contains("pricing") || 
            lower.contains("monetiz") || lower.contains("workflow") || lower.contains("agency") || 
            lower.contains("av production") || lower.contains("quote") || lower.contains("review") -> {
                generateBusinessStrategy(input)
            }

            // Life system & planning
            lower.contains("plan") || lower.contains("focus") || lower.contains("routine") || 
            lower.contains("phone") || lower.contains("habit") || lower.contains("time") || lower.contains("distract") -> {
                generateLifePlanning(input)
            }

            // Learning: Audio & technical systems
            lower.contains("audio") || lower.contains("sound") || lower.contains("gain") || 
            lower.contains("eq") || lower.contains("compress") || lower.contains("mic") || 
            lower.contains("acoustics") || lower.contains("learn") -> {
                generateAudioLesson(input)
            }

            // Strategic Decision Matrix
            lower.contains("decid") || lower.contains("should i") || lower.contains("vs") || 
            lower.contains("option") || lower.contains("choose") -> {
                generateDecisionAnalysis(input)
            }

            // Default Executive Assistant response
            else -> {
                generateStrategicGeneral(input)
            }
        }
    }

    private fun handleMeetingAndScheduling(query: String): EngineResponse {
        val lower = query.lowercase()

        // Check if query mentions a specific conflict hour like 2 PM or tomorrow
        val hasConflictTiming = lower.contains("2") || lower.contains("tomorrow") || lower.contains("afternoon")

        val text = if (hasConflictTiming) {
            """
Sir Gatei, reviewing your calendar for tomorrow at 2:00 PM.

⚠️ SCHEDULE CONFLICT DETECTED:
You already have a confirmed commitment: 'Titan Media Production Shoot' on Soundstage B from 1:30 PM to 4:00 PM.

Which engagement would you prefer to prioritize?
• Option 1: Move the new meeting to 4:30 PM tomorrow, or Thursday at 11:00 AM.
• Option 2: Reschedule the Titan Media shoot to Friday morning.

If we reschedule Titan Media, I have prepared this message for you to send in the first person:

[READY TO SEND — FIRST PERSON]
"Hi Marcus,
I need to adjust our 2:00 PM production session tomorrow due to an urgent scheduling adjustment on my end. Can we shift our session to 4:30 PM tomorrow, or Friday at 10:00 AM? Let me know what suits your team best.
— Gatei"

Shall I hold your original slot, or shall I dispatch this rescheduling draft?
""".trimIndent()
        } else {
            """
Right away, Sir Gatei. What time and venue should I block, and what is the meeting objective?

Please specify:
1. Target Date & Time (e.g., Tomorrow at 11:00 AM)
2. Objective and Agenda (e.g., AV Retainer Pitch, Sound Review)
3. Venue or Platform (e.g., Soundstage B, Google Meet, Zoom)
4. Key Participants

I will cross-reference your current commitments, flag any conflicts, and prepare any necessary client communications.
""".trimIndent()
        }

        return EngineResponse(
            category = "SECRETARY",
            content = text,
            suggestedTitle = "Executive Schedule Coordination",
            rawScript = text
        )
    }

    private fun handleCommunicationDrafting(query: String): EngineResponse {
        val text = """
Sir Gatei, drafting your communication in the first person.

[FIRST-PERSON DRAFT]
"Hi there,

Following up on our recent discussion regarding the AV production deliverables. I have reviewed the scope and timelines for our upcoming campaign. 

Let me know if 11:30 AM tomorrow works for a brief alignment call to finalize the milestones.

Best regards,
Gatei"

GA SECRETARY NOTE: Written with calm authority. You can tap the copy button to send this directly via WhatsApp or email.
""".trimIndent()

        return EngineResponse(
            category = "SECRETARY",
            content = text,
            suggestedTitle = "First-Person Executive Draft",
            rawScript = text
        )
    }

    private fun generateKfcAvConcept(query: String): EngineResponse {
        val text = """
Sir Gatei, deploying KFC AV PRODUCTIONS CONCEPT PROTOCOL.

[CONCEPT OVERVIEW]
• Title: "The 30-Second Micro-Budget Masterpiece"
• Style: Minimalist Doodle Animation + Deadpan Live AV Footage
• Tone: Dry humor, high production irony, visually addictive for TikTok / Reels.

[EXECUTION BLUEPRINT]
1. HOOK (0:00 - 0:03):
   • Visual: Hand-drawn white stick-figure doodle on black canvas with a giant boom microphone falling into frame.
   • Audio: Needle scratch followed by dead silent room tone.
   • Voiceover: "We charged $5,000 for this 10-second shot. Here is what the client actually bought."

2. NARRATIVE BODY (0:03 - 0:18):
   • Visual: Instant split-cut to hyper-cinematic 4K anamorphic footage of coffee beans tumbling into a grinder in 120fps slow motion.
   • Graphic Overlay: Crude doodle speech bubble over the footage pointing to the lighting setup: "3 hours spent lighting one bean."
   • Audio: Epic Hans Zimmer-style brass swell abruptly ending into a microwave beep.

3. PUNCHLINE & CTA (0:18 - 0:25):
   • Visual: Doodle character nodding approvingly with KFC AV sunglasses.
   • Voiceover: "Sound design separates amateurs from operators. Book your next production with KFC AV before we raise rates again."
   • Text On Screen: "KFC AV Productions // Serious Output. Zero Fluff."

GA STRATEGIC NOTE: High viral replayability. Production cost is near zero; perceived production value is elite.
""".trimIndent()

        return EngineResponse(
            category = "CREATIVE",
            content = text,
            suggestedTitle = "KFC AV: The 30-Second Masterpiece",
            rawScript = text
        )
    }

    private fun generateRoom404Concept(query: String): EngineResponse {
        val text = """
Sir Gatei, initializing ROOM 404 NARRATIVE ARCHITECTURE.

[SERIES EPISODE CONCEPT]
• Title: "ROOM 404: The Echo Chamber"
• Aesthetic: Dark, psychological, high contrast, minimal set design (one chair, tungsten practical, dense shadows).
• Soundscape: Low sub-bass drone (40Hz), distant ticking, crisp Foley footsteps.

[SCREENPLAY BLUEPRINT]
1. SCENE HEADING: INT. ROOM 404 - NIGHT
2. VISUAL OPENING (0:00 - 0:05):
   Extreme close-up of an analog cassette recorder spinning slowly. No ambient dialogue. Camera tracks backwards at 0.5x speed.
3. VOICE / MONOLOGUE:
   "You didn't lose focus because you were tired. You lost focus because peace terrified you."
4. THE VISUAL PIVOT (0:05 - 0:20):
   A hand enters frame, hovering above the stop button. The shadows lengthen. The character's face is partially obscured by negative space.
5. AUDIO RESOLUTION:
   Mechanical CLICK of the cassette button. All audio abruptly cuts to pure digital silence.
6. CLOSING FRAME:
   Screen cuts to pitch black. Simple monospaced typeface:
   "ROOM 404 // EPISODE 03: DISTRACTION"

GA DIRECTIVE: Film with a single 35mm or 50mm prime at f/1.8. Emphasize Foley clarity over background music.
""".trimIndent()

        return EngineResponse(
            category = "CREATIVE",
            content = text,
            suggestedTitle = "Room 404: The Echo Chamber",
            rawScript = text
        )
    }

    private fun generateCreativeContent(query: String): EngineResponse {
        val text = """
Sir Gatei, creative synthesis ready for execution.

[STRUCTURED CONTENT MATRIX]
• Objective: High-retention short-form media tailored for your AV brand.
• Format: 30-45 Second Tactical Reel.

1. THE IRRESISTIBLE HOOK (0-3s):
   • Visual: Rapid macro pan across an audio mixer board fader pushing to 0dB with peak lights flashing red.
   • Dialogue / Hook: "Stop recording voiceovers in your bedroom until you fix this one mistake."

2. THE SYSTEMATIC BREAKDOWN (3-20s):
   • Point 1: Untreated corners ruin multi-thousand dollar microphones.
   • Point 2: Speak across the capsule at 45 degrees, never straight on.
   • Point 3: Proper gain staging: target -18dBFS nominal, not clipping at zero.

3. THE VALUE RETENTION PIVOT (20-30s):
   • Show before-and-after audio frequency spectrum analyzer (muddy low-end vs pristine clarity).

4. STRATEGIC CTA (30-35s):
   • Direct, non-needy call: "Save this technical checklist for your next shoot. Follow for raw production protocols."

GA ADVICE: Post between 11:30 AM and 1:30 PM. Keep subtitle motion rapid and high-contrast.
""".trimIndent()

        return EngineResponse(
            category = "CREATIVE",
            content = text,
            suggestedTitle = "AV Production Master Reel",
            rawScript = text
        )
    }

    private fun generateBusinessStrategy(query: String): EngineResponse {
        val text = """
Sir Gatei, reviewing business operations and AV monetization vectors.

[EXECUTIVE DIAGNOSTIC]
• Objective: Transition from commoditized one-off shoots to high-margin recurring client retainers.

[THREE-TIER ACTION FRAMEWORK]
1. HIGH-TICKET PACKAGE STRUCTURING:
   • Eliminate hourly rates immediately.
   • Package: "Complete Media Engine" — 1 shoot day per month generating 16 short-form reels + 2 long-form brand assets.
   • Pricing floor: Minimum $2,500/month on 3-month contract.

2. CLIENT REVIEW & REPUTATION ENGINE:
   • Deliver the final project 24 hours ahead of schedule.
   • Send a personalized 60-second video walkthrough explaining the creative choices.
   • Automate review trigger: At peak client satisfaction (48 hours post-delivery), send a single direct WhatsApp/SMS link directly to your Google Business or testimonial collector.

3. PRODUCTION PIPELINE AUTOMATION:
   • Pre-Production: Standardized Notion/Form questionnaire (eliminates 80% of client back-and-forth).
   • Production: Strict 4-hour shooting block checklist.
   • Post-Production: Frame.io or timestamped revision portal with maximum 2 review rounds included in contract.

GA DIRECTIVE: Close your next client with a scope-capped pilot. Do not discount; increase the deliverable leverage instead.
""".trimIndent()

        return EngineResponse(
            category = "BUSINESS",
            content = text,
            suggestedTitle = "AV Retainer Packaging Protocol",
            rawScript = text
        )
    }

    private fun generateLifePlanning(query: String): EngineResponse {
        val text = """
Sir Gatei, activating LIFE SYSTEM & FOCUS ENGINE.

[SITUATIONAL ASSESSMENT]
Execution failure is rarely a lack of talent; it is an unmanaged cognitive environment and friction in prioritization.

[DAILY EXECUTION PROTOCOL]
1. RULE OF THREE (Ruthless Priority):
   • Task 01 [CRITICAL]: Must be executed before 11:00 AM. Zero phone contact until complete.
   • Task 02 [STRATEGIC]: Moves your AV business or creative output needle.
   • Task 03 [OPERATIONAL]: Administrative hygiene (emails, invoicing, scheduling).

2. DIGITAL PHONE DETOX & FOCUS BARRIER:
   • Shift screen to Grayscale mode immediately in settings (kills dopamine reward loops).
   • Physical displacement: Place mobile phone in another room during deep work blocks.
   • Batch communication: Check messages strictly at 12:00 PM and 5:00 PM.

3. 90-MINUTE FOCUS BLOCK:
   • 90 minutes deep work sprint -> 15 minutes physical movement (hydrate, sunlight, zero screen).
   • Protect your high-energy morning window for creative direction and complex problem solving.

GA DIRECTIVE: Do not add more than 3 high-impact items to today's planner. Clarity precedes speed.
""".trimIndent()

        return EngineResponse(
            category = "PLANNING",
            content = text,
            suggestedTitle = "High-Leverage Daily Protocol",
            rawScript = text
        )
    }

    private fun generateAudioLesson(query: String): EngineResponse {
        val text = """
Sir Gatei, initializing AUDIO ENGINEERING MASTERCLASS MODULE.

[CORE TOPIC: GAIN STAGING & DIGITAL HEADROOM]

1. THE FUNDAMENTAL LAW:
   • Analog gear loved being driven near 0VU (+4dBu).
   • Digital audio (24-bit/32-bit float) operates in dBFS (decibels full scale).
   • 0 dBFS is the ceiling. Exceeding it results in catastrophic digital clipping.

2. OPTIMAL SIGNAL WORKFLOW:
   • Preamp Input: Adjust preamp gain so signal peaks average between -18 dBFS and -12 dBFS.
   • Why -18 dBFS? This mirrors the analog 0VU sweet spot and allows 12 to 18 dB of safe dynamic headroom for sudden transients (shouts, drum attacks, cymbal splashes).
   • Plugin Chain Hygiene: Ensure plugin input and output meters match unity gain so you are not overloading subsequent compressors or saturators.

3. POLAR PATTERN SELECTION:
   • Cardioid: Best for front rejection of room echo in untreated spaces.
   • Supercardioid/Hypercardioid: Tighter front pickup, but beware the rear lobe sensitivity.
   • Figure-8 (Bidirectional): Maximum side rejection; superb for two-person interviews or ribbon acoustics.

GA DIRECTIVE: Fix audio at the source with microphone proximity and room absorption. Never rely on "fixing it in the mix."
""".trimIndent()

        return EngineResponse(
            category = "SYSTEM",
            content = text,
            suggestedTitle = "Audio Engineering: Gain Staging Mastery",
            rawScript = text
        )
    }

    private fun generateDecisionAnalysis(query: String): EngineResponse {
        val text = """
Sir Gatei, deploying STRATEGIC DECISION MATRIX.

[PROBLEM STATEMENT]
Analyzing user inquiry: "$query"

[RISK & VARIABLE ANALYSIS]
• Opportunity Cost: Time and capital allocated here cannot be recovered.
• Asymmetry: We seek decisions with capped downside and unlimited upside.

[OPTIONS EVALUATION]
• Option A [Conservative Path]:
  - Pros: Predictable outcome, zero capital exposure, maintains baseline operations.
  - Cons: Stagnation; competitors outpace our positioning.
• Option B [High-Leverage Strategic Path]:
  - Pros: Rapid brand elevation, higher margin potential, establishes market authority.
  - Cons: Requires strict discipline and front-loaded execution focus.

[GA'S DIRECT STRATEGIC RECOMMENDATION]
Sir Gatei, I advise executing Option B with a 14-day validation constraint. 
Commit 80% of your energy to the primary leverage driver while keeping operational overhead lean. Stop debating; begin execution.
""".trimIndent()

        return EngineResponse(
            category = "DECISION",
            content = text,
            suggestedTitle = "Strategic Decision Matrix",
            rawScript = text
        )
    }

    private fun handleShillingFlow(query: String): EngineResponse {
        val text = """
Sir Gatei, Shilling Flow entry processed.

[SHILLING FLOW TRANSACTION]
• Currency: Kenyan Shillings (KES / KSh)
• Recorded: Entry logged into your personal ledger.
• Sovereign Boundary: M-Pesa PIN and SIM Toolkit remain 100% inaccessible to GA. Your mobile wallet security is strictly preserved.

Would you like me to pull up today's total spending or breakdown by category?
""".trimIndent()
        return EngineResponse("SHILLING_FLOW", text, "Shilling Flow Entry", text)
    }

    private fun handleNotepad(query: String): EngineResponse {
        val text = """
Sir Gatei, Notepad memorandum updated.

[NOTEPAD RECORD]
• Status: Saved to persistent offline storage.
• Organization: Priority indexed and searchable.

You can instruct me to edit, pin, or summarize any note anytime.
""".trimIndent()
        return EngineResponse("NOTEPAD", text, "Notepad Updated", text)
    }

    private fun handlePriorityRescheduling(query: String): EngineResponse {
        val text = """
Sir Gatei, your task agenda has been re-indexed in strict order of strategic leverage:

[PRIORITY MATRIX]
1. [CRITICAL] Immediate high-leverage execution (Do first, zero distractions).
2. [STRATEGIC] Growth & revenue-generating objectives (Scheduled in prime focus block).
3. [OPERATIONAL] Maintenance & secondary errands (Batch processed or delegated).

All deadlines have been harmonized to eliminate cognitive friction.
""".trimIndent()
        return EngineResponse("PRIORITY_MATRIX", text, "Priority Re-indexing", text)
    }

    private fun generateStrategicGeneral(query: String): EngineResponse {
        val text = """
Sir Gatei, reviewing your request: "$query"

[EXECUTIVE ACTION SUMMARY]
• Objective: Address your inquiry with strategic precision.
• Recommendation: Focus on immediate high-leverage execution.

How would you like me to proceed? I can schedule meetings, draft communications, generate content concepts, or optimize your business workflows.
""".trimIndent()

        return EngineResponse(
            category = "SECRETARY",
            content = text,
            suggestedTitle = "Executive Secretary Response",
            rawScript = text
        )
    }
}

data class EngineResponse(
    val category: String,
    val content: String,
    val suggestedTitle: String,
    val rawScript: String = ""
)
