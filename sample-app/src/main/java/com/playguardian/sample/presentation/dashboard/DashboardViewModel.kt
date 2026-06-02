package com.playguardian.sample.presentation.dashboard

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playguardian.audit.model.PolicyReport
import com.playguardian.core.AccessCallback
import com.playguardian.oem.AppCategory
import com.playguardian.oem.SpecialAccess
import com.playguardian.sample.domain.usecase.GetAccessStatusesUseCase
import com.playguardian.sample.domain.repository.PlayGuardianRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getAccessStatusesUseCase: GetAccessStatusesUseCase,
    private val repository: PlayGuardianRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardState())
    val uiState: StateFlow<DashboardState> = _uiState.asStateFlow()

    init {
        refreshStatuses()
        addLog("Welcome to PlayGuardian Audit. Tap a button to start.")
    }

    fun refreshStatuses() {
        _uiState.update { it.copy(accessStatuses = getAccessStatusesUseCase()) }
    }

    fun auditAccess(access: SpecialAccess, reason: String, category: AppCategory) {
        addLog("Auditing ${access.name}...", LogLevel.INFO)
        val report = repository.audit(access, reason, category)
        addLog("Audit complete for ${access.name}. Severity: ${report.severity}",
            if (report.warnings.isEmpty()) LogLevel.SUCCESS else LogLevel.WARNING)
        report.warnings.forEach { addLog("  Warning: $it", LogLevel.WARNING) }
        report.recommendations.forEach { addLog("  Info: $it", LogLevel.INFO) }
    }

    fun requestAccess(activity: Activity, access: SpecialAccess, reason: String, category: AppCategory) {
        addLog("Requesting ${access.name}...", LogLevel.INFO)
        repository.requestAccess(activity, access, reason, category, object : AccessCallback {
            override fun onGranted() {
                addLog("✓ GRANTED: ${access.name}", LogLevel.SUCCESS)
                refreshStatuses()
            }

            override fun onDenied() {
                addLog("✗ DENIED: ${access.name}", LogLevel.ERROR)
                refreshStatuses()
            }

            override fun onUnsupported() {
                addLog("⚠ UNSUPPORTED: ${access.name}", LogLevel.WARNING)
            }

            override fun onPolicyWarning(report: PolicyReport) {
                addLog("Policy check for ${access.name}: Severity ${report.severity}", LogLevel.INFO)
            }
        })
    }

    fun auditManifest() {
        addLog("Running Manifest Audit...", LogLevel.INFO)
        val report = repository.auditManifest()
        if (report.sensitivePermissions.isEmpty()) {
            addLog("No sensitive permissions found in manifest.", LogLevel.SUCCESS)
        } else {
            addLog("Found ${report.sensitivePermissions.size} sensitive permissions.", LogLevel.WARNING)
            report.sensitivePermissions.forEach { addLog("  • $it", LogLevel.WARNING) }
            report.warnings.forEach { addLog("  ⚠ $it", LogLevel.WARNING) }
        }
    }

    fun handleResume(activity: Activity, access: SpecialAccess) {
        repository.handleResume(activity, access, object : AccessCallback {
            override fun onGranted() {
                addLog("✓ VERIFIED GRANTED: ${access.name}", LogLevel.SUCCESS)
                refreshStatuses()
            }
            override fun onDenied() {
                addLog("✗ VERIFIED DENIED: ${access.name}", LogLevel.ERROR)
                refreshStatuses()
            }
            override fun onUnsupported() {}
            override fun onPolicyWarning(report: PolicyReport) {}
        })
    }

    fun clearLogs() {
        _uiState.update { it.copy(logs = emptyList()) }
    }

    private fun addLog(message: String, level: LogLevel = LogLevel.INFO) {
        _uiState.update {
            it.copy(logs = it.logs + LogEntry(message, level))
        }
    }
}
