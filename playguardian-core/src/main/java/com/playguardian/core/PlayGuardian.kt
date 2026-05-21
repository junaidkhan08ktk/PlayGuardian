package com.playguardian.core

import android.app.Activity
import android.content.Context
import com.playguardian.audit.ManifestAuditor
import com.playguardian.audit.PolicyAuditor
import com.playguardian.audit.model.ManifestAuditReport
import com.playguardian.audit.model.PolicyReport
import com.playguardian.core.launcher.PermissionRequestLauncher
import com.playguardian.core.verifier.PermissionVerifier
import com.playguardian.oem.AppCategory
import com.playguardian.oem.PermissionConfig
import com.playguardian.oem.SpecialAccess

/**
 * # PlayGuardian
 *
 * The primary entry point for the PlayGuardian library.
 *
 * PlayGuardian orchestrates a safe, policy-aware workflow for requesting and verifying sensitive
 * Android special accesses. It combines three concerns:
 *
 * 1. **Audit** — evaluates Google Play policy risk before any UI is shown.
 * 2. **Launch** — opens the correct settings screen (with OEM fallback support).
 * 3. **Verify** — confirms whether the access was granted after the user returns.
 *
 * ## Basic usage
 *
 * ```kotlin
 * // 1. Optional: check policy risk without requesting.
 * val report = PlayGuardian.audit(
 *     access = SpecialAccess.OVERLAY,
 *     featureReason = "Show a floating translation bubble over other apps",
 *     appCategory = AppCategory.PRODUCTIVITY
 * )
 *
 * // 2. Request access (audit runs internally before launch).
 * PlayGuardian.request(
 *     activity = this,
 *     access = SpecialAccess.OVERLAY,
 *     config = PermissionConfig(
 *         featureReason = "Show a floating translation bubble over other apps",
 *         appCategory = AppCategory.PRODUCTIVITY
 *     ),
 *     callback = object : AccessCallback {
 *         override fun onGranted() { /* proceed */ }
 *         override fun onDenied() { /* inform user */ }
 *         override fun onUnsupported() { /* degrade gracefully */ }
 *         override fun onPolicyWarning(report: PolicyReport) { /* log or review */ }
 *     }
 * )
 *
 * // 3. In Activity.onResume(), confirm final state:
 * override fun onResume() {
 *     super.onResume()
 *     PlayGuardian.handleResume(this, SpecialAccess.OVERLAY, callback)
 * }
 * ```
 */
object PlayGuardian {

    // -----------------------------------------------------------------------------------------
    // Public API
    // -----------------------------------------------------------------------------------------

    /**
     * Runs a policy audit for the given [access] and returns a [PolicyReport].
     *
     * This is a pure, synchronous function — no UI is shown and no settings screens are opened.
     * Use it to surface policy risk information to developers during integration or testing.
     *
     * @param access        The [SpecialAccess] to evaluate.
     * @param featureReason A description of why the app needs this access.
     * @param appCategory   The category of the host application. Defaults to [AppCategory.OTHER].
     * @return              A [PolicyReport] with severity, warnings, and recommendations.
     */
    fun audit(
        access: SpecialAccess,
        featureReason: String,
        appCategory: AppCategory = AppCategory.OTHER
    ): PolicyReport {
        val config = PermissionConfig(
            featureReason = featureReason,
            appCategory = appCategory
        )
        return PolicyAuditor.evaluate(access, config)
    }

    /**
     * Initiates the full permission request workflow for the given [access].
     *
     * The workflow proceeds in this order:
     * 1. A policy audit is run and [AccessCallback.onPolicyWarning] is invoked.
     * 2. If already granted, [AccessCallback.onGranted] fires immediately.
     * 3. If not granted, the appropriate settings screen is opened.
     * 4. If no settings screen can be launched, [AccessCallback.onUnsupported] fires.
     *
     * After the user returns from settings, call [handleResume] in `Activity.onResume()`
     * to confirm the final granted state.
     *
     * @param activity The current foreground [Activity].
     * @param access   The [SpecialAccess] to request.
     * @param config   [PermissionConfig] describing the intended use case.
     * @param callback Receives all lifecycle events for this request.
     */
    fun request(
        activity: Activity,
        access: SpecialAccess,
        config: PermissionConfig,
        callback: AccessCallback
    ) {
        // Step 1: Run policy audit and surface to developer.
        val report = PolicyAuditor.evaluate(access, config)
        callback.onPolicyWarning(report)

        // Step 2 & 3: Launch settings or report already-granted/unsupported.
        PermissionRequestLauncher.launch(activity, access, config, callback)
    }

    /**
     * Checks whether the given [access] is currently granted.
     *
     * This is a synchronous, non-UI function. It can be called at any time to query the
     * current state without triggering any request flow.
     *
     * @param context Any [Context].
     * @param access  The [SpecialAccess] to verify.
     * @return        `true` if the access is granted, `false` otherwise.
     */
    fun isGranted(context: Context, access: SpecialAccess): Boolean {
        return PermissionVerifier.isGranted(context, access)
    }

    /**
     * Re-checks the granted state after the user returns from a settings screen.
     *
     * Call this from `Activity.onResume()` for the [access] type you requested. It will invoke
     * either [AccessCallback.onGranted] or [AccessCallback.onDenied] based on the current state.
     *
     * This function is idempotent — calling it when no prior request was made is safe and will
     * simply reflect the current permission state.
     *
     * @param context  Any [Context] (typically the Activity).
     * @param access   The [SpecialAccess] to verify.
     * @param callback Receives [AccessCallback.onGranted] or [AccessCallback.onDenied].
     */
    fun handleResume(
        context: Context,
        access: SpecialAccess,
        callback: AccessCallback
    ) {
        if (PermissionVerifier.isGranted(context, access)) {
            callback.onGranted()
        } else {
            callback.onDenied()
        }
    }

    /**
     * Scans the app's declared manifest permissions for known sensitive entries.
     *
     * Returns a [ManifestAuditReport] containing the list of sensitive permissions found and
     * human-readable warnings for each. This is intended as a developer-facing diagnostic
     * tool and does not affect runtime permission state.
     *
     * @param context Any [Context].
     * @return        A [ManifestAuditReport] describing declared sensitive permissions.
     */
    fun auditManifest(context: Context): ManifestAuditReport {
        return ManifestAuditor.scan(context)
    }
}
