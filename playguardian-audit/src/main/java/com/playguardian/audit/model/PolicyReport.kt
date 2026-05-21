package com.playguardian.audit.model

/**
 * Severity level assigned to a policy audit result.
 *
 * | Level  | Meaning                                                                         |
 * |--------|---------------------------------------------------------------------------------|
 * | LOW    | Usage appears justified; minor recommendations only.                            |
 * | MEDIUM | Some concerns; review recommended before publishing.                            |
 * | HIGH   | Strong likelihood of Google Play policy violation; request strongly discouraged.|
 */
enum class Severity {
    LOW,
    MEDIUM,
    HIGH
}

/**
 * The result of a PlayGuardian policy audit for a special access request.
 *
 * @property severity        Overall risk level of this permission request in the given context.
 * @property warnings        List of specific policy concerns the developer should address.
 * @property recommendations List of concrete, actionable suggestions for safer alternatives or
 *                           required steps before publishing to Google Play.
 */
data class PolicyReport(
    val severity: Severity,
    val warnings: List<String>,
    val recommendations: List<String>
)
