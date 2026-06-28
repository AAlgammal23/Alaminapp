package com.alamin.pharma.ui.screens

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import com.alamin.pharma.data.MedicineReminder
import java.util.*

@Composable
fun MedicineReminderScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var reminders by remember { mutableStateOf(listOf<MedicineReminder>()) }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        reminders = loadReminders(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("منبه الدواء") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "رجوع")
                    }
                },
                actions = {
                    IconButton(onClick = { showDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "إضافة منبه")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (reminders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("لا توجد منبهات", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                items(reminders) { reminder ->
                    ReminderItem(
                        reminder = reminder,
                        onToggle = {
                            val newReminder = reminder.copy(isActive = !reminder.isActive)
                            updateReminder(context, newReminder)
                            reminders = reminders.map { if (it.id == newReminder.id) newReminder else it }
                            scheduleReminder(context, newReminder)
                        },
                        onDelete = {
                            deleteReminder(context, reminder.id)
                            reminders = reminders.filter { it.id != reminder.id }
                            cancelReminder(context, reminder.id)
                        }
                    )
                    Divider()
                }
            }
        }

        if (showDialog) {
            AddReminderDialog(
                onDismiss = { showDialog = false },
                onAdd = { name, time, days ->
                    val newReminder = MedicineReminder(
                        id = UUID.randomUUID().toString(),
                        name = name,
                        time = time,
                        days = days,
                        isActive = true
                    )
                    reminders = reminders + newReminder
                    saveReminder(context, newReminder)
                    scheduleReminder(context, newReminder)
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun ReminderItem(
    reminder: MedicineReminder,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(reminder.name, style = MaterialTheme.typography.bodyLarge)
            Text(reminder.time, style = MaterialTheme.typography.bodySmall)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Switch(checked = reminder.isActive, onCheckedChange = { onToggle() })
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "حذف")
            }
        }
    }
}

@Composable
fun AddReminderDialog(
    onDismiss: () -> Unit,
    onAdd: (name: String, time: String, days: List<Int>) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var hour by remember { mutableStateOf(8) }
    var minute by remember { mutableStateOf(0) }
    val daysOfWeek = listOf("الاثنين", "الثلاثاء", "الأربعاء", "الخميس", "الجمعة", "السبت", "الأحد")
    var selectedDays by remember { mutableStateOf(listOf<Int>()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("إضافة منبه جديد") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("اسم الدواء") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("الوقت: ")
                    NumberPicker(
                        value = hour,
                        onValueChange = { hour = it },
                        range = 0..23,
                        modifier = Modifier.weight(1f)
                    )
                    Text(" : ")
                    NumberPicker(
                        value = minute,
                        onValueChange = { minute = it },
                        range = 0..59,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("أيام التكرار:")
                LazyRow {
                    items(daysOfWeek.indices.toList()) { index ->
                        FilterChip(
                            selected = selectedDays.contains(index + 1),
                            onClick = {
                                if (selectedDays.contains(index + 1)) {
                                    selectedDays = selectedDays.filter { it != index + 1 }
                                } else {
                                    selectedDays = selectedDays + (index + 1)
                                }
                            },
                            label = { Text(daysOfWeek[index]) },
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotEmpty() && selectedDays.isNotEmpty()) {
                        val time = String.format("%02d:%02d", hour, minute)
                        onAdd(name, time, selectedDays)
                    }
                },
                enabled = name.isNotEmpty() && selectedDays.isNotEmpty()
            ) {
                Text("إضافة")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}

@Composable
fun NumberPicker(
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { if (value > range.first) onValueChange(value - 1) }) {
            Icon(Icons.Default.Remove, contentDescription = "نقص")
        }
        Text(text = value.toString(), style = MaterialTheme.typography.titleLarge)
        IconButton(onClick = { if (value < range.last) onValueChange(value + 1) }) {
            Icon(Icons.Default.Add, contentDescription = "زيادة")
        }
    }
}

// --- دوال التخزين والإدارة ---
fun saveReminder(context: Context, reminder: MedicineReminder) {
    val all = loadReminders(context).toMutableList()
    all.add(reminder)
    saveAllReminders(context, all)
}

fun loadReminders(context: Context): List<MedicineReminder> {
    val prefs = context.getSharedPreferences("reminders", Context.MODE_PRIVATE)
    val json = prefs.getString("list", "") ?: ""
    if (json.isEmpty()) return emptyList()
    return json.split("|").mapNotNull { part ->
        val parts = part.split(",")
        if (parts.size >= 5) {
            try {
                MedicineReminder(
                    id = parts[0],
                    name = parts[1],
                    time = parts[2],
                    days = parts[3].split(",").map { it.toInt() },
                    isActive = parts[4].toBoolean()
                )
            } catch (e: Exception) { null }
        } else null
    }
}

fun saveAllReminders(context: Context, reminders: List<MedicineReminder>) {
    val prefs = context.getSharedPreferences("reminders", Context.MODE_PRIVATE)
    val json = reminders.joinToString("|") {
        "${it.id},${it.name},${it.time},${it.days.joinToString(",")},${it.isActive}"
    }
    prefs.edit().putString("list", json).apply()
}

fun updateReminder(context: Context, reminder: MedicineReminder) {
    val all = loadReminders(context).toMutableList()
    val index = all.indexOfFirst { it.id == reminder.id }
    if (index != -1) {
        all[index] = reminder
        saveAllReminders(context, all)
    }
}

fun deleteReminder(context: Context, id: String) {
    val all = loadReminders(context).toMutableList()
    all.removeAll { it.id == id }
    saveAllReminders(context, all)
}

fun scheduleReminder(context: Context, reminder: MedicineReminder) {
    if (!reminder.isActive) return
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, ReminderReceiver::class.java).apply {
        putExtra("name", reminder.name)
        putExtra("id", reminder.id)
    }
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        reminder.id.hashCode(),
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val timeParts = reminder.time.split(":").map { it.toInt() }
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, timeParts[0])
        set(Calendar.MINUTE, timeParts[1])
        set(Calendar.SECOND, 0)
    }
    if (calendar.timeInMillis < System.currentTimeMillis()) {
        calendar.add(Calendar.DAY_OF_YEAR, 1)
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    } else {
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }
}

fun cancelReminder(context: Context, id: String) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, ReminderReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        id.hashCode(),
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    alarmManager.cancel(pendingIntent)
}

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val name = intent.getStringExtra("name") ?: "دواء"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("reminder", "منبهات الأدوية", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }
        val notification = NotificationCompat.Builder(context, "reminder")
            .setContentTitle("⏰ موعد تناول الدواء")
            .setContentText("حان وقت تناول $name")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(intent.getStringExtra("id")?.hashCode() ?: 0, notification)
    }
}
