package com.playguardian.sample.presentation.dashboard

import com.playguardian.sample.domain.model.AccessInfo

data class DashboardState(
    val accessStatuses: List<AccessInfo> = emptyList(),
    val isLoading: Boolean = false,
    val logs: List<LogEntry> = emptyList()
)

data class LogEntry(
    val message: String,
    val level: LogLevel = LogLevel.INFO,
    val timestamp: Long = System.currentTimeMillis()
)

enum class LogLevel { INFO, SUCCESS, WARNING, ERROR }
