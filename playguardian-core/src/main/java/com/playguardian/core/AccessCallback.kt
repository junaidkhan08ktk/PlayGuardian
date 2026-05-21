package com.playguardian.core

import com.playguardian.audit.model.PolicyReport

/**
 * Callback interface for receiving the result of a [PlayGuardian.request] flow.
 *
 * Implement this interface and pass it to [PlayGuardian.request] to be notified of every
 * meaningful state transition during the permission request lifecycle.
 *
 * **Threading:** All callbacks are invoked on the main thread.
 */
interface AccessCallback {

    /**
     * Called when the special access has been confirmed as granted.
     *
     * This may be called immediately if the permission was already granted before the request
     * was initiated, or after the user returns from the settings screen having granted access.
     */
    fun onGranted()

    /**
     * Called when the special access is confirmed as not granted after the user returns from
     * the settings screen, or when the request flow was completed without the user granting access.
     */
    fun onDenied()

    /**
     * Called when the special access cannot be requested on the current device or API level.
     *
     * For example, [com.playguardian.oem.SpecialAccess.EXACT_ALARM] is only meaningful on
     * Android 12+. On older devices, this callback fires instead of launching any settings screen.
     *
     * Also called when no resolvable settings intent could be found after exhausting all
     * fallback options (including OEM-specific intents, if enabled).
     */
    fun onUnsupported()

    /**
     * Called synchronously during [PlayGuardian.request] with the result of the pre-request
     * policy audit. The request flow continues regardless of the severity level — it is the
     * developer's responsibility to decide whether to abort based on [PolicyReport.severity].
     *
     * @param report The [PolicyReport] from [com.playguardian.audit.PolicyAuditor].
     */
    fun onPolicyWarning(report: PolicyReport)
}
