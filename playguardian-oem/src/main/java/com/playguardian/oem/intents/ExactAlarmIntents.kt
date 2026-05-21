package com.playguardian.oem.intents

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings

/**
 * Provides a prioritised list of [Intent]s to open the exact-alarm scheduling permission
 * settings screen.
 *
 * [Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM] is only available on Android 12 (API 31)+.
 * On older API levels this permission does not exist and the list will contain only generic
 * app-settings fallbacks so that callers can detect unsupported state cleanly.
 *
 * ### Resolution order
 * 1. Exact alarm permission screen (API 31+)
 * 2. App details settings
 * 3. Main system settings (last resort)
 */
internal object ExactAlarmIntents {

    /**
     * Returns an ordered list of exact-alarm [Intent]s for the current device.
     */
    fun resolve(context: Context): List<Intent> {
        val packageName = context.packageName
        val intents = mutableListOf<Intent>()

        // 1. Official exact alarm settings screen (Android 12+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            safeIntent {
                Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = Uri.parse("package:$packageName")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            }?.let { intents.add(it) }
        }

        // 2. App details settings fallback (available on all supported API levels)
        safeIntent {
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:$packageName")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }?.let { intents.add(it) }

        // 3. Main settings (last resort)
        safeIntent {
            Intent(Settings.ACTION_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }?.let { intents.add(it) }

        return intents
    }

    private inline fun safeIntent(block: () -> Intent): Intent? {
        return try {
            block()
        } catch (e: Exception) {
            null
        }
    }
}
