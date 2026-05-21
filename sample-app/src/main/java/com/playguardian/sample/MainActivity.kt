package com.playguardian.sample

import android.R.attr.text
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.graphics.Typeface
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.playguardian.audit.model.PolicyReport
import com.playguardian.audit.model.Severity
import com.playguardian.core.AccessCallback
import com.playguardian.core.PlayGuardian
import com.playguardian.oem.AppCategory
import com.playguardian.oem.PermissionConfig
import com.playguardian.oem.SpecialAccess
import com.playguardian.sample.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.core.graphics.toColorInt

/**
 * Main sample activity demonstrating every PlayGuardian API.
 *
 * Sections:
 *  • Live status cards — show granted/denied for each access on resume
 *  • Policy audit — run heuristic risk evaluation per access type
 *  • Request access — full request → settings → resume verification flow
 *  • Output log — timestamped, color-coded output for all events
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    /** Tracks the most recently requested access so onResume can verify it. */
    private var pendingAccess: SpecialAccess? = null

    /** Retained callback for the active request flow. */
    private var pendingCallback: AccessCallback? = null

    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    // ─────────────────────────────────────────────────────────────────────────
    // Lifecycle
    // ─────────────────────────────────────────────────────────────────────────

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        wireButtons()
        refreshStatusCards()
    }

    override fun onResume() {
        super.onResume()
        refreshStatusCards()
        // Re-verify after user returns from settings
        val access = pendingAccess ?: return
        val callback = pendingCallback ?: return
        PlayGuardian.handleResume(this, access, callback)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Status cards
    // ─────────────────────────────────────────────────────────────────────────

    private fun refreshStatusCards() {
        binding.tvBatteryStatus.setStatus(
            PlayGuardian.isGranted(this, SpecialAccess.IGNORE_BATTERY_OPTIMIZATION)
        )
        binding.tvOverlayStatus.setStatus(
            PlayGuardian.isGranted(this, SpecialAccess.OVERLAY)
        )
        binding.tvAlarmStatus.setStatus(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                PlayGuardian.isGranted(this, SpecialAccess.EXACT_ALARM)
            else null  // not applicable below API 31
        )
        binding.tvNotifStatus.setStatus(
            PlayGuardian.isGranted(this, SpecialAccess.NOTIFICATION_LISTENER)
        )
    }

    /** null = N/A (not applicable on this API level) */
    private fun TextView.setStatus(granted: Boolean?) {
        when (granted) {
            true -> {
                text = "✓ Granted"; setTextColor("#16A34A".toColorInt())
            }

            false -> {
                text = "✗ Not Granted"; setTextColor("#DC2626".toColorInt())
            }

            null -> {
                text = "N/A (API ${Build.VERSION.SDK_INT})"; setTextColor("#6B7280".toColorInt())
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Button wiring
    // ─────────────────────────────────────────────────────────────────────────

    private fun wireButtons() {
        // Audit buttons
        binding.btnAuditBattery.setOnClickListener { auditAccess(SpecialAccess.IGNORE_BATTERY_OPTIMIZATION) }
        binding.btnAuditOverlay.setOnClickListener { auditAccess(SpecialAccess.OVERLAY) }
        binding.btnAuditAlarm.setOnClickListener { auditAccess(SpecialAccess.EXACT_ALARM) }
        binding.btnAuditNotif.setOnClickListener { auditAccess(SpecialAccess.NOTIFICATION_LISTENER) }
        binding.btnManifestAudit.setOnClickListener { runManifestAudit() }

        // Request buttons
        binding.btnRequestBattery.setOnClickListener { requestAccess(SpecialAccess.IGNORE_BATTERY_OPTIMIZATION) }
        binding.btnRequestOverlay.setOnClickListener { requestAccess(SpecialAccess.OVERLAY) }
        binding.btnRequestExactAlarm.setOnClickListener { requestAccess(SpecialAccess.EXACT_ALARM) }
        binding.btnRequestNotificationListener.setOnClickListener { requestAccess(SpecialAccess.NOTIFICATION_LISTENER) }

        // Log controls
        binding.btnClearLog.setOnClickListener { clearLog() }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Audit operations
    // ─────────────────────────────────────────────────────────────────────────

    private fun auditAccess(access: SpecialAccess) {
        val (reason, category) = accessDefaults(access)
        val report = PlayGuardian.audit(
            access = access,
            featureReason = reason,
            appCategory = category
        )
        logSection("Policy Audit — ${access.displayName()}")
        logReport(report)
    }

    private fun runManifestAudit() {
        val report = PlayGuardian.auditManifest(this)
        logSection("Manifest Audit")

        if (report.sensitivePermissions.isEmpty()) {
            logLine("No sensitive permissions declared.", LogLevel.OK)
            return
        }

        logLine("Sensitive permissions found: ${report.sensitivePermissions.size}", LogLevel.WARN)
        report.sensitivePermissions.forEach { logLine("  • $it", LogLevel.WARN) }
        logBlank()
        report.warnings.forEachIndexed { i, w ->
            logLine("[${i + 1}] $w", LogLevel.WARN)
            logBlank()
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Request operations
    // ─────────────────────────────────────────────────────────────────────────

    private fun requestAccess(access: SpecialAccess) {
        val (reason, category) = accessDefaults(access)
        val config = PermissionConfig(
            featureReason = reason,
            appCategory = category,
            autoOpenOemFallback = true
        )

        logSection("Requesting — ${access.displayName()}")
        logLine("Reason: $reason", LogLevel.INFO)
        logLine("Category: $category", LogLevel.INFO)
        logBlank()

        val callback = object : AccessCallback {
            override fun onGranted() {
                logLine("✓ GRANTED — ${access.displayName()}", LogLevel.OK)
                clearPending()
                refreshStatusCards()
            }

            override fun onDenied() {
                logLine("✗ DENIED — user did not grant access.", LogLevel.ERROR)
                clearPending()
                refreshStatusCards()
            }

            override fun onUnsupported() {
                logLine("⚠ UNSUPPORTED — cannot open settings on this device/API.", LogLevel.WARN)
                clearPending()
            }

            override fun onPolicyWarning(report: PolicyReport) {
                logLine("Policy check before request:", LogLevel.INFO)
                logReport(report)
            }
        }

        pendingAccess = access
        pendingCallback = callback
        PlayGuardian.request(this, access, config, callback)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Defaults per access type
    // ─────────────────────────────────────────────────────────────────────────

    private fun accessDefaults(access: SpecialAccess): Pair<String, AppCategory> = when (access) {
        SpecialAccess.IGNORE_BATTERY_OPTIMIZATION ->
            "Ensure user-created reminders and alarms fire on time even in Doze mode" to AppCategory.PRODUCTIVITY

        SpecialAccess.OVERLAY ->
            "Display a floating translation bubble above other apps when the user taps the translate button" to AppCategory.PRODUCTIVITY

        SpecialAccess.EXACT_ALARM ->
            "Schedule user-created alarms and calendar reminders to fire at exact times" to AppCategory.PRODUCTIVITY

        SpecialAccess.NOTIFICATION_LISTENER ->
            "Mirror notifications to a connected wearable device when Do Not Disturb is active" to AppCategory.UTILITY
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Log helpers
    // ─────────────────────────────────────────────────────────────────────────

    private enum class LogLevel { INFO, OK, WARN, ERROR }

    private fun logSection(title: String) {
        val ts = timeFormat.format(Date())
        val ssb = SpannableStringBuilder()

        ssb.append("\n")
        ssb.append(
            "[$ts] ─── $title ───",
            StyleSpan(Typeface.BOLD),
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        ssb.append("\n")

        appendToLog(ssb)
    }

    private fun logLine(text: String, level: LogLevel) {
        val color = when (level) {
            LogLevel.OK -> Color.parseColor("#4ADE80")
            LogLevel.WARN -> Color.parseColor("#FCD34D")
            LogLevel.ERROR -> Color.parseColor("#F87171")
            LogLevel.INFO -> Color.parseColor("#93C5FD")
        }
        val ssb = SpannableStringBuilder()
        ssb.append(text, ForegroundColorSpan(color), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        ssb.append("\n")
        appendToLog(ssb)
    }

    private fun logBlank() {
        appendToLog(SpannableStringBuilder("\n"))
    }

    private fun logReport(report: PolicyReport) {
        val severityColor = when (report.severity) {
            Severity.LOW -> Color.parseColor("#4ADE80")
            Severity.MEDIUM -> Color.parseColor("#FCD34D")
            Severity.HIGH -> Color.parseColor("#F87171")
        }
        val ssb = SpannableStringBuilder()
        ssb.append("Severity: ")
        ssb.append(
            report.severity.name,
            ForegroundColorSpan(severityColor),
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        ssb.append("\n")
        appendToLog(ssb)

        if (report.warnings.isNotEmpty()) {
            logLine("Warnings:", LogLevel.WARN)
            report.warnings.forEach { logLine("  ⚠ $it", LogLevel.WARN) }
        }
        if (report.recommendations.isNotEmpty()) {
            logLine("Recommendations:", LogLevel.INFO)
            report.recommendations.forEach { logLine("  ℹ $it", LogLevel.INFO) }
        }
        logBlank()
    }

    private fun appendToLog(ssb: SpannableStringBuilder) {
        val current = binding.tvOutput.text
        val isPlaceholder = current.toString().startsWith("Tap any button")
        val builder =
            if (isPlaceholder) SpannableStringBuilder() else SpannableStringBuilder(current)
        builder.append(ssb)
        binding.tvOutput.text = builder
        // Auto-scroll to bottom
        binding.scrollLog.post {
            binding.scrollLog.fullScroll(android.view.View.FOCUS_DOWN)
        }
    }

    private fun clearLog() {
        binding.tvOutput.text = "Log cleared. Tap a button to start.\n"
    }

    private fun clearPending() {
        pendingAccess = null
        pendingCallback = null
    }

    private fun SpecialAccess.displayName() = when (this) {
        SpecialAccess.IGNORE_BATTERY_OPTIMIZATION -> "Battery Optimization"
        SpecialAccess.OVERLAY -> "Overlay (Draw Over Apps)"
        SpecialAccess.EXACT_ALARM -> "Exact Alarm"
        SpecialAccess.NOTIFICATION_LISTENER -> "Notification Listener"
    }
}
