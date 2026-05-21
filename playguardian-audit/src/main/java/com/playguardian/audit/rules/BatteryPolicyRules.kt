package com.playguardian.audit.rules

import com.playguardian.audit.model.PolicyReport
import com.playguardian.audit.model.Severity
import com.playguardian.oem.AppCategory
import com.playguardian.oem.PermissionConfig

/**
 * Evaluates Google Play policy risk for [SpecialAccess.IGNORE_BATTERY_OPTIMIZATION] based on
 * the app's [AppCategory] and the declared [PermissionConfig.featureReason].
 *
 * Google Play restricts battery optimisation exemptions to apps with a genuine need for
 * continuous background operation. Misuse can result in app rejection or removal.
 */
internal object BatteryPolicyRules {

    private val CRITICAL_BACKGROUND_KEYWORDS = setOf(
        "sync", "background", "update", "refresh", "fetch", "polling", "data sync",
        "auto sync", "background task", "periodic"
    )

    private val JUSTIFIED_KEYWORDS = setOf(
        "alarm", "reminder", "call", "voip", "realtime", "real-time", "delivery",
        "notification delivery", "critical", "emergency", "schedule", "wake"
    )

    private val HIGH_RISK_CATEGORIES = setOf(
        AppCategory.MEDIA, AppCategory.OTHER, AppCategory.TOOLS
    )

    /**
     * Produces a [PolicyReport] describing the policy risk level and actionable guidance for
     * battery optimisation exemption requests.
     *
     * @param config The [PermissionConfig] supplied by the developer.
     * @return       A [PolicyReport] with severity, warnings, and recommendations.
     */
    fun evaluate(config: PermissionConfig): PolicyReport {
        val reasonLower = config.featureReason.lowercase()
        val warnings = mutableListOf<String>()
        val recommendations = mutableListOf<String>()

        val hasJustifiedKeyword = JUSTIFIED_KEYWORDS.any { reasonLower.contains(it) }
        val hasCriticalKeyword = CRITICAL_BACKGROUND_KEYWORDS.any { reasonLower.contains(it) }
        val isHighRiskCategory = config.appCategory in HIGH_RISK_CATEGORIES

        val severity: Severity = when {
            hasCriticalKeyword && isHighRiskCategory -> {
                warnings.add(
                    "Your featureReason mentions background/sync keywords and your app category " +
                    "(${config.appCategory}) is not a recognised use case for battery exemption. " +
                    "Google Play will likely reject this permission request during review."
                )
                warnings.add(
                    "Apps in the ${config.appCategory} category should prefer WorkManager, " +
                    "JobScheduler, or Android's Doze-compatible APIs for background work."
                )
                Severity.HIGH
            }
            hasCriticalKeyword && !hasJustifiedKeyword -> {
                warnings.add(
                    "Your featureReason suggests generic background/sync work without a clear " +
                    "real-time justification. Google Play may flag this during policy review."
                )
                warnings.add(
                    "Battery optimisation exemption is intended for apps with strict delivery SLAs " +
                    "(VoIP, real-time messaging, alarm clocks). Ensure your use case qualifies."
                )
                Severity.MEDIUM
            }
            hasJustifiedKeyword -> {
                recommendations.add(
                    "Your stated reason (\"${config.featureReason}\") appears to align with " +
                    "accepted use cases such as alarms, reminders, or real-time delivery. " +
                    "Ensure this is accurately reflected in your Play Store listing."
                )
                Severity.LOW
            }
            else -> {
                warnings.add(
                    "No clear justification keywords were detected in your featureReason. " +
                    "Make sure your stated reason clearly explains why the app cannot function " +
                    "correctly without being exempt from battery optimisation."
                )
                Severity.MEDIUM
            }
        }

        recommendations.add(
            "Always test your app in Doze mode and App Standby before requesting exemption. " +
            "Use WorkManager with setExpedited() as the primary solution where possible."
        )
        recommendations.add(
            "Declare REQUEST_IGNORE_BATTERY_OPTIMIZATIONS in your manifest only if your app " +
            "qualifies under Google Play's Acceptable Use Policy for battery exemptions."
        )

        return PolicyReport(
            severity = severity,
            warnings = warnings,
            recommendations = recommendations
        )
    }
}
