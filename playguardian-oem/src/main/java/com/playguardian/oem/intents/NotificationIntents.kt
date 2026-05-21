package com.playguardian.oem.intents

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

/**
 * Provides a prioritised list of [Intent]s to open the Notification Listener access settings.
 *
 * ### Resolution order
 * 1. Official notification listener access settings ([Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS])
 * 2. App details settings (user can check notification permissions from here)
 * 3. Main system settings (last resort)
 *
 * No OEM-specific notification panels are used as [Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS]
 * is consistently handled across all supported Android versions and OEMs.
 */
internal object NotificationIntents {

    /**
     * Returns an ordered list of notification listener [Intent]s.
     */
    fun resolve(context: Context): List<Intent> {
        val packageName = context.packageName
        val intents = mutableListOf<Intent>()

        // 1. Official notification listener settings
        safeIntent {
            Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }?.let { intents.add(it) }

        // 2. App details settings fallback
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
