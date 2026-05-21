package com.playguardian.audit.rules

import com.playguardian.audit.model.PolicyReport
import com.playguardian.audit.model.Severity
import com.playguardian.oem.AppCategory
import com.playguardian.oem.PermissionConfig

/**
 * Evaluates Google Play policy risk for [com.playguardian.oem.SpecialAccess.NOTIFICATION_LISTENER].
 *
 * Notification listener services receive the full content of every notification on the device,
 * including from other apps. Google Play classifies this as a high-sensitivity permission that
 * requires explicit policy approval and a clearly declared, user-visible purpose.
 */
internal object NotificationPolicyRules {

    private val HIGH_RISK_KEYWORDS = setOf(
        "read all", "monitor", "harvest", "collect", "aggregate", "scrape",
        "track", "analytics", "spy", "surveillance"
    )

    private val JUSTIFIED_KEYWORDS = setOf(
        "do not disturb", "dnd", "filter", "mirror", "watch", "wearable", "smart watch",
        "auto reply", "notification manager", "custom notification", "notification center",
        "driving mode", "focus mode", "smart reply", "assistant"
    )

    private val JUSTIFIED_CATEGORIES = setOf(
        AppCategory.PRODUCTIVITY, AppCategory.UTILITY, AppCategory.COMMUNICATION, AppCategory.TOOLS
    )

    /**
     * Produces a [PolicyReport] for notification listener permission requests.
     *
     * @param config The [PermissionConfig] supplied by the developer.
     * @return       A [PolicyReport] with severity, warnings, and recommendations.
     */
    fun evaluate(config: PermissionConfig): PolicyReport {
        val reasonLower = config.featureReason.lowercase()
        val warnings = mutableListOf<String>()
        val recommendations = mutableListOf<String>()

        val hasHighRiskKeyword = HIGH_RISK_KEYWORDS.any { reasonLower.contains(it) }
        val hasJustifiedKeyword = JUSTIFIED_KEYWORDS.any { reasonLower.contains(it) }
        val isJustifiedCategory = config.appCategory in JUSTIFIED_CATEGORIES

        val severity: Severity = when {
            hasHighRiskKeyword -> {
                warnings.add(
                    "Your featureReason contains language associated with notification harvesting, " +
                    "monitoring, or surveillance. This will likely result in app rejection or removal " +
                    "from Google Play."
                )
                warnings.add(
                    "Notification listener access must not be used to collect user data from other " +
                    "apps' notifications. Only the notification content directly relevant to your " +
                    "declared feature may be processed."
                )
                Severity.HIGH
            }
            hasJustifiedKeyword && isJustifiedCategory -> {
                recommendations.add(
                    "Your stated reason and app category suggest an accepted use case for notification " +
                    "listener access (e.g. DND management, notification mirroring, smart replies). " +
                    "Ensure you disclose this access clearly in your Privacy Policy and Play listing."
                )
                Severity.LOW
            }
            hasJustifiedKeyword -> {
                recommendations.add(
                    "Your reason appears reasonable. Make sure the Play Store listing and Privacy " +
                    "Policy clearly describe what notification data is accessed and why."
                )
                Severity.MEDIUM
            }
            !isJustifiedCategory -> {
                warnings.add(
                    "Apps in the ${config.appCategory} category rarely have a legitimate need for " +
                    "notification listener access. Reconsider whether this access is truly required."
                )
                warnings.add(
                    "If notification listener is needed, be prepared to provide detailed justification " +
                    "to Google Play during review, including what notification data is read and how it " +
                    "is used, stored, and protected."
                )
                Severity.HIGH
            }
            else -> {
                warnings.add(
                    "Notification listener access is high-sensitivity. Your featureReason does not " +
                    "clearly indicate an accepted use case. Provide a detailed, user-facing explanation."
                )
                Severity.MEDIUM
            }
        }

        recommendations.add(
            "Always require the minimum notification data needed. Do not log, store, or transmit " +
            "notification content from other apps unless it is essential to your core feature."
        )
        recommendations.add(
            "Update your Privacy Policy to explicitly disclose notification listener access, what " +
            "data is read, how it is processed, and how long it is retained."
        )
        recommendations.add(
            "Provide a clear, in-app explanation to the user before directing them to the " +
            "Notification Listener settings screen. Users must understand what access they are granting."
        )

        return PolicyReport(
            severity = severity,
            warnings = warnings,
            recommendations = recommendations
        )
    }
}
