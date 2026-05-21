package com.playguardian.audit.rules

import com.playguardian.audit.model.PolicyReport
import com.playguardian.audit.model.Severity
import com.playguardian.oem.AppCategory
import com.playguardian.oem.PermissionConfig

/**
 * Evaluates Google Play policy risk for [com.playguardian.oem.SpecialAccess.OVERLAY].
 *
 * `SYSTEM_ALERT_WINDOW` is one of the most scrutinised permissions on Google Play. Apps must
 * demonstrate that overlays serve a clear, user-beneficial purpose and must not use them to
 * display disruptive content or interfere with other applications.
 */
internal object OverlayPolicyRules {

    private val HIGH_RISK_KEYWORDS = setOf(
        "ad", "advertisement", "promo", "earn", "monetize", "click", "reward",
        "floating button", "always on top", "persistent overlay"
    )

    private val ACCEPTED_USE_KEYWORDS = setOf(
        "bubble", "chat head", "screen filter", "blue light", "accessibility", "translation",
        "floating window", "clipboard", "color filter", "brightness", "magnifier"
    )

    private val HIGH_RISK_CATEGORIES = setOf(
        AppCategory.FINANCE, AppCategory.MEDIA, AppCategory.OTHER
    )

    /**
     * Produces a [PolicyReport] for overlay permission requests.
     *
     * @param config The [PermissionConfig] supplied by the developer.
     * @return       A [PolicyReport] with severity, warnings, and recommendations.
     */
    fun evaluate(config: PermissionConfig): PolicyReport {
        val reasonLower = config.featureReason.lowercase()
        val warnings = mutableListOf<String>()
        val recommendations = mutableListOf<String>()

        val hasHighRiskKeyword = HIGH_RISK_KEYWORDS.any { reasonLower.contains(it) }
        val hasAcceptedKeyword = ACCEPTED_USE_KEYWORDS.any { reasonLower.contains(it) }
        val isHighRiskCategory = config.appCategory in HIGH_RISK_CATEGORIES

        val severity: Severity = when {
            hasHighRiskKeyword -> {
                warnings.add(
                    "Your featureReason contains keywords associated with advertising, monetisation, " +
                    "or disruptive overlay patterns. Google Play will very likely reject this usage."
                )
                warnings.add(
                    "SYSTEM_ALERT_WINDOW must not be used to display unsolicited content, ads, " +
                    "or overlays that interfere with other apps. This violates Play policy."
                )
                Severity.HIGH
            }
            isHighRiskCategory && !hasAcceptedKeyword -> {
                warnings.add(
                    "Apps in the ${config.appCategory} category requesting overlay permission " +
                    "without a clear user-triggered use case are high-risk for policy rejection."
                )
                warnings.add(
                    "Ensure that overlays are only shown in direct response to a user action, " +
                    "not proactively or on a timer."
                )
                Severity.HIGH
            }
            hasAcceptedKeyword -> {
                recommendations.add(
                    "Your stated reason aligns with commonly accepted overlay use cases. " +
                    "Ensure the overlay is dismissible, non-disruptive, and user-initiated."
                )
                Severity.LOW
            }
            else -> {
                warnings.add(
                    "Overlay permission is high-sensitivity. Your featureReason does not clearly " +
                    "indicate an accepted use case. Review Play policy before shipping."
                )
                Severity.MEDIUM
            }
        }

        recommendations.add(
            "Consider using in-app floating windows (e.g. Android 11 Bubbles API) instead of " +
            "SYSTEM_ALERT_WINDOW where possible — they offer similar UX with fewer policy risks."
        )
        recommendations.add(
            "Only request SYSTEM_ALERT_WINDOW when your core feature genuinely requires drawing " +
            "outside your own app. Add a clear in-app explanation before opening the settings screen."
        )
        recommendations.add(
            "Test that your overlay respects the back button, is dismissible at all times, " +
            "and does not obscure system UI or other app content unexpectedly."
        )

        return PolicyReport(
            severity = severity,
            warnings = warnings,
            recommendations = recommendations
        )
    }
}
