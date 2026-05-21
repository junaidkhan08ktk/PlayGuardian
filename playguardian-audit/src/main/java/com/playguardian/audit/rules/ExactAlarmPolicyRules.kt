package com.playguardian.audit.rules

import com.playguardian.audit.model.PolicyReport
import com.playguardian.audit.model.Severity
import com.playguardian.oem.AppCategory
import com.playguardian.oem.PermissionConfig

/**
 * Evaluates Google Play policy risk for [com.playguardian.oem.SpecialAccess.EXACT_ALARM].
 *
 * From Android 12 (API 31), apps must declare `SCHEDULE_EXACT_ALARM` or `USE_EXACT_ALARM`
 * and obtain user approval to schedule exact alarms. Google Play restricts this permission to
 * apps with a legitimate time-critical use case visible to the user.
 */
internal object ExactAlarmPolicyRules {

    private val JUSTIFIED_KEYWORDS = setOf(
        "alarm", "reminder", "calendar", "event", "schedule", "wake up", "timer",
        "countdown", "medication", "appointment", "meeting", "clock"
    )

    private val UNJUSTIFIED_KEYWORDS = setOf(
        "analytics", "tracking", "telemetry", "ping", "poll", "sync", "refresh",
        "background", "periodic", "batch"
    )

    private val JUSTIFIED_CATEGORIES = setOf(
        AppCategory.PRODUCTIVITY, AppCategory.HEALTH, AppCategory.UTILITY, AppCategory.COMMUNICATION
    )

    /**
     * Produces a [PolicyReport] for exact alarm permission requests.
     *
     * @param config The [PermissionConfig] supplied by the developer.
     * @return       A [PolicyReport] with severity, warnings, and recommendations.
     */
    fun evaluate(config: PermissionConfig): PolicyReport {
        val reasonLower = config.featureReason.lowercase()
        val warnings = mutableListOf<String>()
        val recommendations = mutableListOf<String>()

        val hasJustifiedKeyword = JUSTIFIED_KEYWORDS.any { reasonLower.contains(it) }
        val hasUnjustifiedKeyword = UNJUSTIFIED_KEYWORDS.any { reasonLower.contains(it) }
        val isJustifiedCategory = config.appCategory in JUSTIFIED_CATEGORIES

        val severity: Severity = when {
            hasUnjustifiedKeyword -> {
                warnings.add(
                    "Your featureReason suggests the exact alarm is used for non-user-visible " +
                    "background tasks (e.g. sync, polling, analytics). This is not a permitted " +
                    "use case for SCHEDULE_EXACT_ALARM under Google Play policy."
                )
                warnings.add(
                    "Use WorkManager with setExpedited() or inexact alarms (setWindow()) for " +
                    "background work. Exact alarms are reserved for user-visible, time-critical events."
                )
                Severity.HIGH
            }
            hasJustifiedKeyword && isJustifiedCategory -> {
                recommendations.add(
                    "Your app category (${config.appCategory}) and stated reason appear to align " +
                    "with accepted use cases for exact alarms. Ensure the alarm maps to an event " +
                    "the user explicitly created or requested."
                )
                Severity.LOW
            }
            hasJustifiedKeyword -> {
                recommendations.add(
                    "Your reason contains alarm/reminder keywords. Verify that your Play Store " +
                    "listing accurately describes this time-critical feature."
                )
                Severity.LOW
            }
            !isJustifiedCategory -> {
                warnings.add(
                    "Apps in the ${config.appCategory} category do not typically require exact " +
                    "alarm scheduling. Consider whether inexact alarms or WorkManager would suffice."
                )
                Severity.MEDIUM
            }
            else -> {
                warnings.add(
                    "No clear justification for exact alarms was found in your featureReason. " +
                    "Provide a specific description of the user-facing, time-critical feature."
                )
                Severity.MEDIUM
            }
        }

        recommendations.add(
            "On Android 13+, prefer USE_EXACT_ALARM (no user approval required) if your app " +
            "is an alarm clock or calendar app. Otherwise use SCHEDULE_EXACT_ALARM with " +
            "AlarmManager.canScheduleExactAlarms() checks at runtime."
        )
        recommendations.add(
            "Always handle the case where canScheduleExactAlarms() returns false gracefully — " +
            "fall back to setWindow() or WorkManager rather than crashing or blocking the user."
        )

        return PolicyReport(
            severity = severity,
            warnings = warnings,
            recommendations = recommendations
        )
    }
}
