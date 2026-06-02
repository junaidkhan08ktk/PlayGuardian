package com.playguardian.audit

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.playguardian.audit.model.ManifestAuditReport

/**
 * Inspects the app's declared permissions (as listed in the merged AndroidManifest.xml) and
 * flags any known sensitive permissions with human-readable explanations.
 *
 * This auditor reads the currently installed app's permission list from [PackageManager] —
 * it does not parse raw XML. The information is suitable for surfacing to developers during
 * integration testing or pre-release verification.
 */
object ManifestAuditor {

    /**
     * Scans the calling app's declared permissions for known sensitive entries.
     *
     * @param context Any [Context]; the package name is derived from it.
     * @return A [ManifestAuditReport] listing all detected sensitive permissions with warnings.
     */
    fun scan(context: Context): ManifestAuditReport {
        val packageName = context.packageName
        val declared: Array<String> = try {
            val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                PackageManager.GET_PERMISSIONS
            } else {
                @Suppress("DEPRECATION")
                PackageManager.GET_PERMISSIONS
            }
            context.packageManager
                .getPackageInfo(packageName, flags)
                .requestedPermissions
                ?: emptyArray()
        } catch (_: PackageManager.NameNotFoundException) {
            emptyArray()
        }

        val sensitivePermissions = mutableListOf<String>()
        val warnings = mutableListOf<String>()

        for (permission in declared) {
            when (permission) {
                "android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS" -> {
                    sensitivePermissions.add(permission)
                    warnings.add(
                        "REQUEST_IGNORE_BATTERY_OPTIMIZATIONS is declared. This permission allows " +
                        "your app to request exemption from Android's battery optimisation. Google " +
                        "Play requires a legitimate, user-visible reason (e.g. VoIP, real-time alarms). " +
                        "Unjustified use may result in policy rejection."
                    )
                }
                "android.permission.SYSTEM_ALERT_WINDOW" -> {
                    sensitivePermissions.add(permission)
                    warnings.add(
                        "SYSTEM_ALERT_WINDOW (Draw over other apps) is declared. This is a high-scrutiny " +
                        "Google Play permission. Ensure overlays are user-initiated, dismissible, and do " +
                        "not interfere with other apps or system UI. Automated rejection is common for " +
                        "apps that cannot justify this permission."
                    )
                }
                "android.permission.SCHEDULE_EXACT_ALARM" -> {
                    sensitivePermissions.add(permission)
                    warnings.add(
                        "SCHEDULE_EXACT_ALARM is declared (Android 12+). This requires user approval " +
                        "at runtime and is restricted to apps with user-visible, time-critical features " +
                        "such as alarm clocks and calendar apps. Always handle canScheduleExactAlarms() " +
                        "returning false gracefully."
                    )
                }
                "android.permission.USE_EXACT_ALARM" -> {
                    sensitivePermissions.add(permission)
                    warnings.add(
                        "USE_EXACT_ALARM is declared (Android 13+). This is pre-granted for eligible " +
                        "app categories (alarm clock, calendar). Google Play enforces that only apps in " +
                        "these categories may declare it. Misuse may result in rejection."
                    )
                }
                "android.permission.BIND_NOTIFICATION_LISTENER_SERVICE" -> {
                    sensitivePermissions.add(permission)
                    warnings.add(
                        "BIND_NOTIFICATION_LISTENER_SERVICE is declared. This grants your service " +
                        "access to all notifications on the device. Google Play requires explicit policy " +
                        "approval for this access. Update your Privacy Policy and Play Store listing to " +
                        "disclose how notification data is used, stored, and protected."
                    )
                }
                "android.permission.QUERY_ALL_PACKAGES" -> {
                    sensitivePermissions.add(permission)
                    warnings.add(
                        "QUERY_ALL_PACKAGES is declared. This allows your app to see all installed apps, " +
                        "which Google Play classifies as sensitive. It should only be declared if your core " +
                        "functionality genuinely requires a full app inventory (e.g. launcher, security apps). " +
                        "Unjustified use may result in removal from Play."
                    )
                }
                "android.permission.MANAGE_EXTERNAL_STORAGE" -> {
                    sensitivePermissions.add(permission)
                    warnings.add(
                        "MANAGE_EXTERNAL_STORAGE is declared. This is one of the most restricted Google " +
                        "Play permissions and requires a specific approved use case (e.g. file manager, backup). " +
                        "Apps cannot publish with this permission without completing a Declaration Form and " +
                        "passing manual review."
                    )
                }
            }
        }

        return ManifestAuditReport(
            sensitivePermissions = sensitivePermissions,
            warnings = warnings
        )
    }
}
