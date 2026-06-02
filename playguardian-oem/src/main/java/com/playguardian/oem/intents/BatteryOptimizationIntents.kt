package com.playguardian.oem.intents

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import com.playguardian.oem.Manufacturer
import androidx.core.net.toUri

/**
 * Provides a prioritised list of [Intent]s that can open battery optimisation / power management
 * settings for the calling application.
 *
 * ### Resolution order
 * 1. Official Android direct-exemption request intent (API 23+)
 * 2. Official Android battery-optimisation settings list
 * 3. OEM-specific safe fallbacks (generic action-based, no hard-coded component names)
 * 4. App details settings
 * 5. Power-usage summary
 * 6. Main system settings (last resort)
 */
internal object BatteryOptimizationIntents {

    /**
     * Returns an ordered list of battery optimisation [Intent]s to attempt for the given
     * [packageName] and device [manufacturer].
     *
     * Every entry is safe to pass to [android.content.Context.startActivity] after calling
     * [android.content.pm.PackageManager.resolveActivity] — callers in [com.playguardian.oem.OemSettingsLauncher]
     * are responsible for that resolution check.
     */
    fun resolve(context: Context, manufacturer: Manufacturer): List<Intent> {
        val packageName = context.packageName
        val intents = mutableListOf<Intent>()

        // 1. Direct app-specific exemption request (official, API 23+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            safeIntent {
                Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                    data = "package:$packageName".toUri()
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            }?.let { intents.add(it) }
        }

        // 2. Official battery optimisation settings list (user picks the app)
        safeIntent {
            Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }?.let { intents.add(it) }

        // 3. OEM-specific safe fallbacks — generic action intents only.
        //    No hard-coded component/class names to avoid fragile assumptions.
        when (manufacturer) {
            Manufacturer.XIAOMI -> {
                // ACTION_POWER_USAGE_SUMMARY is a commonly handled intent on MIUI devices.
                // UNVERIFIED_FALLBACK: MIUI power manager component paths are not used here
                // because they change across MIUI versions and cannot be verified universally.
                safeIntent {
                    Intent(Intent.ACTION_POWER_USAGE_SUMMARY).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                }?.let { intents.add(it) }
            }

            Manufacturer.SAMSUNG -> {
                // Samsung Device Care uses a standard Intent action for battery settings.
                // UNVERIFIED_FALLBACK: "com.samsung.android.lool" package activities are
                // intentionally NOT used as they vary by One UI version.
                safeIntent {
                    Intent(Intent.ACTION_POWER_USAGE_SUMMARY).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                }?.let { intents.add(it) }
            }

            Manufacturer.HUAWEI -> {
                // Huawei/Honor EMUI power settings: use battery usage summary as safest option.
                safeIntent {
                    Intent(Intent.ACTION_POWER_USAGE_SUMMARY).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                }?.let { intents.add(it) }
            }

            Manufacturer.OPPO, Manufacturer.REALME -> {
                // ColorOS / realme UI: no reliable universal battery component — use generic.
                safeIntent {
                    Intent(Intent.ACTION_POWER_USAGE_SUMMARY).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                }?.let { intents.add(it) }
            }

            Manufacturer.VIVO, Manufacturer.ONEPLUS,
            Manufacturer.INFINIX, Manufacturer.TECNO,
            Manufacturer.OTHER -> {
                // No OEM-specific handling — rely on the generic fallbacks below.
            }
        }

        // 4. App details settings (always resolvable; user can adjust battery from here too)
        safeIntent {
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = "package:$packageName".toUri()
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }?.let { intents.add(it) }

        // 5. Generic power usage summary
        safeIntent {
            Intent(Intent.ACTION_POWER_USAGE_SUMMARY).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }?.let { intents.add(it) }

        // 6. Main system settings (absolute last resort)
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
