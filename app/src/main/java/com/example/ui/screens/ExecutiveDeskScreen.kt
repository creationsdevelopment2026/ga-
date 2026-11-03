package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Reorder
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MeetingItem
import com.example.data.model.NoteItem
import com.example.data.model.PaymentMethod
import com.example.data.model.Priority
import com.example.data.model.TaskItem
import com.example.data.model.TransactionCategory
import com.example.data.model.TransactionItem
import com.example.ui.theme.AppleBackground
import com.example.ui.theme.AppleBlue
import com.example.ui.theme.AppleDivider
import com.example.ui.theme.AppleGreen
import com.example.ui.theme.AppleOrange
import com.example.ui.theme.AppleRed
import com.example.ui.theme.AppleSurface
import com.example.ui.theme.AppleSurfaceBorder
import com.example.ui.theme.AppleSurfaceElevated
import com.example.ui.theme.AppleTextPrimary
import com.example.ui.theme.AppleTextSecondary
import com.example.ui.theme.AppleTextTertiary

@Composable
fun ExecutiveDeskScreen(
    tasks: List<TaskItem>,
    notes: List<NoteItem>,
    transactions: List<TransactionItem>,
    meetings: List<MeetingItem>,
    todayTotalSpent: Double,
    onAddTask: (String, Priority, String) -> Unit,
    onToggleTaskCompleted: (Long, Boolean) -> Unit,
    onDeleteTask: (Long) -> Unit,
    onReschedulePriorities: () -> Unit,
    onAddNote: (String, String, Priority) -> Unit,
    onDeleteNote: (Long) -> Unit,
    onAddTransaction: (Double, String, PaymentMethod, TransactionCategory) -> Unit,
    onDeleteTransaction: (Long) -> Unit,
    onSendDraftMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Shilling Flow", "To-Do & Priorities", "Notepad", "Meetings")

    var showAddTxDialog by remember { mutableStateOf(false) }
    var showAddNoteDialog by remember { mutableStateOf(false) }
    var showAddTaskDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppleBackground)
    ) {
        // Tab Header
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = AppleSurface,
            contentColor = AppleBlue,
            edgePadding = 16.dp,
            divider = { Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(AppleDivider)) },
            indicator = { tabPositions ->
                if (selectedTab < tabPositions.size) {
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        height = 2.dp,
                        color = AppleBlue
                    )
                }
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 13.sp,
                            fontWeight = if (selectedTab == index) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (selectedTab == index) AppleBlue else AppleTextSecondary
                        )
                    }
                )
            }
        }

        // Tab Content
        Box(modifier = Modifier.fillMaxSize()) {
            when (selectedTab) {
                0 -> ShillingFlowPane(
                    transactions = transactions,
                    todayTotalSpent = todayTotalSpent,
                    onOpenAddDialog = { showAddTxDialog = true },
                    onDeleteTransaction = onDeleteTransaction
                )
                1 -> ToDoPrioritiesPane(
                    tasks = tasks,
                    onOpenAddDialog = { showAddTaskDialog = true },
                    onToggleCompleted = onToggleTaskCompleted,
                    onDeleteTask = onDeleteTask,
                    onReschedulePriorities = onReschedulePriorities
                )
                2 -> NotepadPane(
                    notes = notes,
                    onOpenAddDialog = { showAddNoteDialog = true },
                    onDeleteNote = onDeleteNote
                )
                3 -> MeetingsPane(
                    meetings = meetings,
                    onSendDraftMessage = onSendDraftMessage
                )
            }
        }
    }

    // Add Transaction Dialog
    if (showAddTxDialog) {
        AddTransactionDialog(
            onDismiss = { showAddTxDialog = false },
            onConfirm = { amount, purpose, method, cat ->
                onAddTransaction(amount, purpose, method, cat)
                showAddTxDialog = false
            }
        )
    }

    // Add Note Dialog
    if (showAddNoteDialog) {
        AddNoteDialog(
            onDismiss = { showAddNoteDialog = false },
            onConfirm = { title, content, priority ->
                onAddNote(title, content, priority)
                showAddNoteDialog = false
            }
        )
    }

    // Add Task Dialog
    if (showAddTaskDialog) {
        AddTaskDialog(
            onDismiss = { showAddTaskDialog = false },
            onConfirm = { title, priority, deadline ->
                onAddTask(title, priority, deadline)
                showAddTaskDialog = false
            }
        )
    }
}

// -------------------------------------------------------------
// SHILLING FLOW PANE
// -------------------------------------------------------------
@Composable
fun ShillingFlowPane(
    transactions: List<TransactionItem>,
    todayTotalSpent: Double,
    onOpenAddDialog: () -> Unit,
    onDeleteTransaction: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Today's Total Spent Hero Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AppleSurfaceElevated),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, AppleSurfaceBorder),
                modifier = Modifier.fillMaxWidth().testTag("shilling_flow_hero_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "SHILLING FLOW LEDGER",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = AppleTextTertiary,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "KES ${"%,.0f".format(todayTotalSpent)}",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppleTextPrimary
                            )
                            Text(
                                text = "Today's Logged Outflow",
                                fontSize = 12.sp,
                                color = AppleTextSecondary
                            )
                        }

                        Button(
                            onClick = onOpenAddDialog,
                            colors = ButtonDefaults.buttonColors(containerColor = AppleBlue),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                            modifier = Modifier.testTag("add_shilling_transaction_btn")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Log KSh", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Sovereign Security Notice
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x1F34C759))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "M-Pesa Safe",
                            tint = AppleGreen,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "M-Pesa PIN & SIM Toolkit isolated. GA only records your ledger.",
                            fontSize = 11.sp,
                            color = AppleGreen,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "RECENT TRANSACTIONS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = AppleTextTertiary,
                letterSpacing = 1.sp
            )
        }

        if (transactions.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No Shilling Flow transactions yet.\nSay: \"Add transaction of 50 KSh for lunch, cash, food\"",
                        fontSize = 13.sp,
                        color = AppleTextTertiary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            items(transactions, key = { it.id }) { tx ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = AppleSurface),
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, AppleSurfaceBorder),
                    modifier = Modifier.fillMaxWidth().testTag("transaction_item_${tx.id}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0x1F38BDF8)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Payments,
                                    contentDescription = "Payment",
                                    tint = AppleBlue,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = tx.purpose,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AppleTextPrimary
                                )
                                Text(
                                    text = "${tx.category.label} • ${tx.paymentMethod.label} • ${tx.timeFormatted}",
                                    fontSize = 11.sp,
                                    color = AppleTextTertiary
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "-${tx.amountKsh.toInt()} KSh",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppleRed
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            IconButton(
                                onClick = { onDeleteTransaction(tx.id) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = AppleTextTertiary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TO-DO & PRIORITIES PANE
// -------------------------------------------------------------
@Composable
fun ToDoPrioritiesPane(
    tasks: List<TaskItem>,
    onOpenAddDialog: () -> Unit,
    onToggleCompleted: (Long, Boolean) -> Unit,
    onDeleteTask: (Long) -> Unit,
    onReschedulePriorities: () -> Unit
) {
    // Sort tasks in order of priority: Critical -> Strategic -> Operational
    val sortedTasks = remember(tasks) {
        tasks.sortedWith(compareBy<TaskItem> { it.isCompleted }.thenBy { it.priority.level })
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "PRIORITY MATRIX",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = AppleTextTertiary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Ordered by Strategic Leverage",
                        fontSize = 12.sp,
                        color = AppleTextSecondary
                    )
                }

                Row {
                    Button(
                        onClick = onReschedulePriorities,
                        colors = ButtonDefaults.buttonColors(containerColor = AppleSurfaceElevated),
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, AppleSurfaceBorder),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Reorder, contentDescription = "Reschedule", tint = AppleBlue, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Re-order", fontSize = 12.sp, color = AppleTextPrimary)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = onOpenAddDialog,
                        colors = ButtonDefaults.buttonColors(containerColor = AppleBlue),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Task", fontSize = 12.sp)
                    }
                }
            }
        }

        if (sortedTasks.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("No agenda items. Instruct GA: \"Add to-do: Audit contracts\"", fontSize = 13.sp, color = AppleTextTertiary)
                }
            }
        } else {
            items(sortedTasks, key = { it.id }) { task ->
                val priorityColor = when (task.priority) {
                    Priority.CRITICAL -> AppleRed
                    Priority.STRATEGIC -> AppleOrange
                    Priority.OPERATIONAL -> AppleBlue
                }

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = if (task.isCompleted) AppleSurface.copy(alpha = 0.6f) else AppleSurface),
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, AppleSurfaceBorder),
                    modifier = Modifier.fillMaxWidth().testTag("task_item_${task.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { onToggleCompleted(task.id, !task.isCompleted) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (task.isCompleted) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircleOutline,
                                contentDescription = "Toggle Complete",
                                tint = if (task.isCompleted) AppleGreen else AppleTextTertiary
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = task.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (task.isCompleted) AppleTextTertiary else AppleTextPrimary,
                                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = priorityColor.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = task.priority.label.uppercase(),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = priorityColor,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Due: ${task.deadline}",
                                    fontSize = 11.sp,
                                    color = AppleTextTertiary
                                )
                            }
                        }

                        IconButton(
                            onClick = { onDeleteTask(task.id) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = AppleTextTertiary, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// NOTEPAD PANE
// -------------------------------------------------------------
@Composable
fun NotepadPane(
    notes: List<NoteItem>,
    onOpenAddDialog: () -> Unit,
    onDeleteNote: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "EXECUTIVE NOTEPAD",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = AppleTextTertiary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Memos & Strategic Notes",
                        fontSize = 12.sp,
                        color = AppleTextSecondary
                    )
                }

                Button(
                    onClick = onOpenAddDialog,
                    colors = ButtonDefaults.buttonColors(containerColor = AppleBlue),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.EditNote, contentDescription = "Add", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Note", fontSize = 12.sp)
                }
            }
        }

        if (notes.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("Notepad is empty.\nInstruct GA: \"Edit notepad: Note on marketing campaign\"", fontSize = 13.sp, color = AppleTextTertiary, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                }
            }
        } else {
            items(notes, key = { it.id }) { note ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = AppleSurface),
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, AppleSurfaceBorder),
                    modifier = Modifier.fillMaxWidth().testTag("note_item_${note.id}")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = note.title,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppleTextPrimary
                            )
                            IconButton(onClick = { onDeleteNote(note.id) }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = AppleTextTertiary, modifier = Modifier.size(16.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = note.content,
                            fontSize = 13.sp,
                            color = AppleTextSecondary,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// MEETINGS PANE
// -------------------------------------------------------------
@Composable
fun MeetingsPane(
    meetings: List<MeetingItem>,
    onSendDraftMessage: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "EXECUTIVE CALENDAR & MEETINGS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = AppleTextTertiary,
                letterSpacing = 1.sp
            )
        }

        items(meetings, key = { it.id }) { meeting ->
            Card(
                shape = RoundedCornerShape(12.dp),
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
                        Text(meeting.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = AppleTextPrimary)
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = AppleGreen.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = meeting.status,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppleGreen,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "With: ${meeting.attendee} • ${meeting.dateTime}", fontSize = 12.sp, color = AppleBlue)
                    Text(text = "Venue: ${meeting.venue}", fontSize = 12.sp, color = AppleTextSecondary)
                    Text(text = "Purpose: ${meeting.purpose}", fontSize = 12.sp, color = AppleTextTertiary)

                    if (!meeting.draftMessage.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(AppleSurfaceElevated)
                                .padding(10.dp)
                        ) {
                            Column {
                                Text("First-Person Reschedule Draft:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AppleOrange)
                                Text(meeting.draftMessage, fontSize = 12.sp, color = AppleTextPrimary)
                                Spacer(modifier = Modifier.height(6.dp))
                                Button(
                                    onClick = { onSendDraftMessage(meeting.draftMessage) },
                                    colors = ButtonDefaults.buttonColors(containerColor = AppleBlue),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Icon(Icons.Outlined.Send, contentDescription = "Send", modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Send Draft", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// DIALOGS
// -------------------------------------------------------------
@Composable
fun AddTransactionDialog(
    onDismiss: () -> Unit,
    onConfirm: (Double, String, PaymentMethod, TransactionCategory) -> Unit
) {
    var amountText by remember { mutableStateOf("50") }
    var purposeText by remember { mutableStateOf("") }
    var selectedMethod by remember { mutableStateOf(PaymentMethod.CASH) }
    var selectedCategory by remember { mutableStateOf(TransactionCategory.FOOD) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Shilling Flow Entry", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Amount (KSh)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = purposeText,
                    onValueChange = { purposeText = it },
                    label = { Text("Purpose / Use") },
                    placeholder = { Text("e.g. Lunch, Uber, Camera gear") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Payment Method:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedMethod == PaymentMethod.CASH,
                        onClick = { selectedMethod = PaymentMethod.CASH }
                    )
                    Text("Cash", fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    RadioButton(
                        selected = selectedMethod == PaymentMethod.MPESA,
                        onClick = { selectedMethod = PaymentMethod.MPESA }
                    )
                    Text("M-PESA (Ledger)", fontSize = 13.sp)
                }

                Text("Category:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    TransactionCategory.values().take(3).forEach { cat ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (selectedCategory == cat) AppleBlue else AppleSurfaceElevated,
                            modifier = Modifier.clickable { selectedCategory = cat }
                        ) {
                            Text(
                                text = cat.label.split(" ").first(),
                                fontSize = 11.sp,
                                color = if (selectedCategory == cat) Color.White else AppleTextPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull() ?: 50.0
                    onConfirm(amount, purposeText.ifBlank { "Personal item" }, selectedMethod, selectedCategory)
                },
                colors = ButtonDefaults.buttonColors(containerColor = AppleBlue)
            ) {
                Text("Log Entry")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AddNoteDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, Priority) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Executive Note", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Content") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() || content.isNotBlank()) {
                        onConfirm(title.ifBlank { "Untitled Note" }, content, Priority.STRATEGIC)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AppleBlue)
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Priority, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf(Priority.CRITICAL) }
    var deadline by remember { mutableStateOf("Today") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Agenda Task", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = deadline,
                    onValueChange = { deadline = it },
                    label = { Text("Deadline / Focus Window") },
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Strategic Priority:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Priority.values().forEach { prio ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (selectedPriority == prio) AppleBlue else AppleSurfaceElevated,
                            modifier = Modifier.clickable { selectedPriority = prio }
                        ) {
                            Text(
                                text = prio.label,
                                fontSize = 11.sp,
                                color = if (selectedPriority == prio) Color.White else AppleTextPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(title, selectedPriority, deadline)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AppleBlue)
            ) {
                Text("Add Task")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
