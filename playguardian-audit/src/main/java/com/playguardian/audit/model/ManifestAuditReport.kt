package com.playguardian.audit.model

/**
 * The result of scanning an app's declared permissions for known sensitive entries.
 *
 * @property sensitivePermissions Fully-qualified permission strings that were found in the
 *   app's manifest and are considered sensitive by Google Play policy.
 * @property warnings Human-readable developer-facing warnings explaining why each detected
 *   permission warrants attention.
 */
data class ManifestAuditReport(
    val sensitivePermissions: List<String>,
    val warnings: List<String>
)
