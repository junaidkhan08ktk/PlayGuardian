package com.playguardian.oem

import android.content.Context
import android.content.Intent
import com.playguardian.oem.intents.BatteryOptimizationIntents
import com.playguardian.oem.intents.ExactAlarmIntents
import com.playguardian.oem.intents.NotificationIntents
import com.playguardian.oem.intents.OverlayIntents

/**
 * Central resolver that returns a prioritised, safe list of [Intent]s for a given
 * special-access type and the current device's manufacturer.
 *
 * Callers should attempt each intent in order and stop at the first one that resolves
 * successfully (see [OemSettingsLauncher]).
 *
 * This object never throws — all exceptions from intent construction are absorbed internally.
 */
object OemIntentResolver {

    /**
     * Returns a prioritised fallback list of [Intent]s for the specified [access] type.
     *
     * The list is always non-empty (at minimum a main settings intent is included) but
     * individual entries may not be resolvable on every device. Callers must check
     * [android.content.pm.PackageManager.resolveActivity] before starting each intent.
     *
     * @param context Application or Activity context.
     * @param access  The special access type to resolve intents for.
     * @return        Ordered list of intents to attempt, from most specific to most generic.
     */
    fun resolve(context: Context, access: SpecialAccess): List<Intent> {
        val manufacturer = ManufacturerDetector.detect()
        return when (access) {
            SpecialAccess.IGNORE_BATTERY_OPTIMIZATION ->
                BatteryOptimizationIntents.resolve(context, manufacturer)

            SpecialAccess.OVERLAY ->
                OverlayIntents.resolve(context)

            SpecialAccess.EXACT_ALARM ->
                ExactAlarmIntents.resolve(context)

            SpecialAccess.NOTIFICATION_LISTENER ->
                NotificationIntents.resolve(context)
        }
    }
}
