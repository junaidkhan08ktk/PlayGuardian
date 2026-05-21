package com.playguardian.core.launcher

import android.app.Activity
import com.playguardian.core.AccessCallback
import com.playguardian.oem.PermissionConfig
import com.playguardian.core.verifier.PermissionVerifier
import com.playguardian.oem.OemIntentResolver
import com.playguardian.oem.OemSettingsLauncher
import com.playguardian.oem.SpecialAccess

/**
 * Handles the settings-launch phase of a permission request flow.
 *
 * This class is an internal implementation detail of [com.playguardian.core.PlayGuardian].
 * It resolves the correct settings intent(s) for the requested [SpecialAccess] and attempts
 * to launch the first one that is resolvable on the current device.
 */
internal object PermissionRequestLauncher {

    /**
     * Attempts to open the appropriate settings screen for [access].
     *
     * If already granted, [AccessCallback.onGranted] is called immediately.
     * If no settings screen could be launched, [AccessCallback.onUnsupported] is called.
     *
     * @param activity The current foreground [Activity].
     * @param access   The [SpecialAccess] to request.
     * @param config   Configuration controlling OEM fallback behaviour.
     * @param callback Receives the outcome of the launch attempt.
     */
    fun launch(
        activity: Activity,
        access: SpecialAccess,
        config: PermissionConfig,
        callback: AccessCallback
    ) {
        // Guard: already granted — no need to open settings.
        if (PermissionVerifier.isGranted(activity, access)) {
            callback.onGranted()
            return
        }

        // Resolve the prioritised list of candidate intents.
        val intents = OemIntentResolver.resolve(activity, access)

        if (intents.isEmpty()) {
            callback.onUnsupported()
            return
        }

        val intentList = if (config.autoOpenOemFallback) {
            intents
        } else {
            // When OEM fallback is disabled, use only the first (most specific) intent.
            listOf(intents.first())
        }

        val launched = OemSettingsLauncher.launch(activity, intentList)

        if (!launched) {
            callback.onUnsupported()
        }
        // If launched = true, the result is handled in PlayGuardian.handleResume()
        // after the user returns from the settings screen.
    }
}
