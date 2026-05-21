package com.playguardian.core.verifier

import android.app.AlarmManager
import android.content.Context
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import com.playguardian.core.model.AccessState
import com.playguardian.oem.SpecialAccess

/**
 * Verifies whether a given [SpecialAccess] has been granted on the current device.
 *
 * Each access type uses the canonical Android API for verification. There is no reliance on
 * undocumented or OEM-specific APIs — if the standard API is not available, [AccessState.Unsupported]
 * is returned rather than guessing.
 */
object PermissionVerifier {

    /**
     * Returns `true` if the given [access] has been granted, `false` otherwise.
     *
     * For [SpecialAccess.EXACT_ALARM] on pre-Android-12 devices, this returns `true` because
     * the permission does not exist and all apps can schedule exact alarms without restriction.
     *
     * @param context Any [Context].
     * @param access  The [SpecialAccess] to check.
     */
    fun isGranted(context: Context, access: SpecialAccess): Boolean {
        return when (checkState(context, access)) {
            is AccessState.Granted     -> true
            is AccessState.Denied      -> false
            is AccessState.Unsupported -> false
        }
    }

    /**
     * Returns the full [AccessState] for the given [access].
     *
     * Prefer this over [isGranted] when you need to distinguish between denied and unsupported.
     *
     * @param context Any [Context].
     * @param access  The [SpecialAccess] to check.
     */
    fun checkState(context: Context, access: SpecialAccess): AccessState {
        return when (access) {
            SpecialAccess.IGNORE_BATTERY_OPTIMIZATION -> checkBatteryOptimization(context)
            SpecialAccess.OVERLAY                     -> checkOverlay(context)
            SpecialAccess.EXACT_ALARM                 -> checkExactAlarm(context)
            SpecialAccess.NOTIFICATION_LISTENER       -> checkNotificationListener(context)
        }
    }

    // -----------------------------------------------------------------------------------------
    // Private verification logic
    // -----------------------------------------------------------------------------------------

    private fun checkBatteryOptimization(context: Context): AccessState {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            if (pm.isIgnoringBatteryOptimizations(context.packageName)) {
                AccessState.Granted
            } else {
                AccessState.Denied
            }
        } else {
            // Battery optimisation does not exist below API 23; apps are always unrestricted.
            AccessState.Granted
        }
    }

    private fun checkOverlay(context: Context): AccessState {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(context)) {
                AccessState.Granted
            } else {
                AccessState.Denied
            }
        } else {
            // SYSTEM_ALERT_WINDOW is pre-granted below API 23.
            AccessState.Granted
        }
    }

    private fun checkExactAlarm(context: Context): AccessState {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (am.canScheduleExactAlarms()) {
                AccessState.Granted
            } else {
                AccessState.Denied
            }
        } else {
            // Exact alarm restriction does not exist below Android 12; always accessible.
            AccessState.Granted
        }
    }

    private fun checkNotificationListener(context: Context): AccessState {
        return try {
            val flat = Settings.Secure.getString(
                context.contentResolver,
                "enabled_notification_listeners"
            ) ?: return AccessState.Denied

            val packageName = context.packageName
            if (flat.split(":").any { component ->
                    component.startsWith("$packageName/") || component == packageName
                }) {
                AccessState.Granted
            } else {
                AccessState.Denied
            }
        } catch (e: Exception) {
            AccessState.Denied
        }
    }
}
