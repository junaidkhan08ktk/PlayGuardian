package com.playguardian.sample.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playguardian.oem.AppCategory
import com.playguardian.oem.SpecialAccess
import com.playguardian.sample.domain.model.AccessInfo
import com.playguardian.sample.presentation.theme.SuccessGreen
import com.playguardian.sample.presentation.theme.WarningYellow
import com.playguardian.sample.presentation.theme.ErrorRed
import com.playguardian.sample.presentation.theme.InfoBlue
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PlayGuardian Audit") },
                actions = {
                    IconButton(onClick = { viewModel.refreshStatuses() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                    IconButton(onClick = { viewModel.auditManifest() }) {
                        Icon(Icons.Default.Info, contentDescription = "Audit Manifest")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Special Access Status",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Status Cards
            uiState.accessStatuses.forEach { info ->
                AccessStatusCard(
                    info = info,
                    onAudit = {
                        val (reason, category) = getDefaults(info.access)
                        viewModel.auditAccess(info.access, reason, category)
                    },
                    onRequest = {
                        val (reason, category) = getDefaults(info.access)
                        viewModel.requestAccess(context as android.app.Activity, info.access, reason, category)
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Logs Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Output Log",
                    style = MaterialTheme.typography.titleMedium
                )
                TextButton(onClick = { viewModel.clearLogs() }) {
                    Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Clear")
                }
            }

            LogPanel(
                logs = uiState.logs,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun AccessStatusCard(
    info: AccessInfo,
    onAudit: () -> Unit,
    onRequest: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = info.displayName, fontWeight = FontWeight.Bold)
                    Text(text = info.description, style = MaterialTheme.typography.bodySmall)
                }
                StatusIndicator(isGranted = info.isGranted)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = onAudit,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Audit", fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onRequest,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Request", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun StatusIndicator(isGranted: Boolean?) {
    val (text, color) = when (isGranted) {
        true -> "Granted" to SuccessGreen
        false -> "Not Granted" to ErrorRed
        null -> "N/A" to Color.Gray
    }
    Surface(
        color = color.copy(alpha = 0.2f),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = text,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun LogPanel(
    logs: List<LogEntry>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Black, RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        if (logs.isEmpty()) {
            Text("No logs yet...", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
        } else {
            LazyColumn(
                reverseLayout = false,
                modifier = Modifier.fillMaxSize()
            ) {
                items(logs) { entry ->
                    LogItem(entry)
                }
            }
        }
    }
}

@Composable
fun LogItem(entry: LogEntry) {
    val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    val timestamp = timeFormat.format(Date(entry.timestamp))
    
    val color = when (entry.level) {
        LogLevel.INFO -> InfoBlue
        LogLevel.SUCCESS -> SuccessGreen
        LogLevel.WARNING -> WarningYellow
        LogLevel.ERROR -> ErrorRed
    }
    
    Row(modifier = Modifier.padding(vertical = 2.dp)) {
        Text(
            text = "[$timestamp] ",
            color = Color.Gray,
            style = MaterialTheme.typography.bodySmall,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
        )
        Text(
            text = entry.message,
            color = color,
            style = MaterialTheme.typography.bodySmall,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
        )
    }
}

private fun getDefaults(access: SpecialAccess): Pair<String, AppCategory> = when (access) {
    SpecialAccess.IGNORE_BATTERY_OPTIMIZATION ->
        "Ensure user-created reminders and alarms fire on time even in Doze mode" to AppCategory.PRODUCTIVITY

    SpecialAccess.OVERLAY ->
        "Display a floating translation bubble above other apps when the user taps the translate button" to AppCategory.PRODUCTIVITY

    SpecialAccess.EXACT_ALARM ->
        "Schedule user-created alarms and calendar reminders to fire at exact times" to AppCategory.PRODUCTIVITY

    SpecialAccess.NOTIFICATION_LISTENER ->
        "Mirror notifications to a connected wearable device when Do Not Disturb is active" to AppCategory.UTILITY
}
