package com.playguardian.oem.intents

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.core.net.toUri

/**
 * Provides a prioritised list of [Intent]s to open the "Draw over other apps" (overlay)
 * permission settings screen.
 *
 * ### Resolution order
 * 1. Official per-app overlay permission screen ([Settings.ACTION_MANAGE_OVERLAY_PERMISSION])
 * 2. App details settings (user can navigate to overlay from here on most ROMs)
 * 3. All-apps overlay list ([Settings.ACTION_MANAGE_OVERLAY_PERMISSION] without package)
 * 4. Main system settings (last resort)
 *
 * No OEM-specific hidden overlay panels are used; the standard API is universally available
 * on API 23+ and is the only safe option.
 */
internal object OverlayIntents {

    /**
     * Returns an ordered list of overlay permission [Intent]s.
     */
    fun resolve(context: Context): List<Intent> {
        val packageName = context.packageName
        val intents = mutableListOf<Intent>()

        // 1. Official per-app overlay permission screen (API 23+)
        safeIntent {
            Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
                data = "package:$packageName".toUri()
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }?.let { intents.add(it) }

        // 2. App details settings fallback
        safeIntent {
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = "package:$packageName".toUri()
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }?.let { intents.add(it) }

        // 3. Overlay permission list (no package filter — user selects app)
        safeIntent {
            Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }?.let { intents.add(it) }

        // 4. Main settings (absolute last resort)
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
        } catch (_: Exception) {
            null
        }
    }
}
