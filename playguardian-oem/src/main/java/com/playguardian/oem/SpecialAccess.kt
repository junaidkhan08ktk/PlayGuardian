package com.playguardian.oem

/**
 * Enumerates the special Android system accesses that PlayGuardian supports.
 *
 * Each value maps to a distinct Android permission or access mechanism that requires
 * a non-standard user-facing approval flow beyond runtime permissions.
 */
enum class SpecialAccess {

    /**
     * Battery optimisation exemption — [android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS].
     *
     * Requires the `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` permission in the manifest.
     * **Google Play policy:** Only permitted for apps with a genuine need for unrestricted
     * background execution (e.g. VoIP, real-time messaging). Abuse may result in rejection.
     */
    IGNORE_BATTERY_OPTIMIZATION,

    /**
     * System alert window / "draw over other apps" — [android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION].
     *
     * Requires `SYSTEM_ALERT_WINDOW` in the manifest.
     * **Google Play policy:** High-scrutiny permission. Apps must demonstrate that the overlay
     * serves a clear user benefit.
     */
    OVERLAY,

    /**
     * Exact alarm scheduling — [android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM].
     *
     * Available from Android 12 (API 31). On older versions this permission does not exist.
     * Requires `SCHEDULE_EXACT_ALARM` or `USE_EXACT_ALARM` in the manifest.
     * **Google Play policy:** Must be justified with a user-visible time-critical use case
     * (e.g. alarms, calendar reminders).
     */
    EXACT_ALARM,

    /**
     * Notification listener service access — [android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS].
     *
     * Requires a `<service>` component with `BIND_NOTIFICATION_LISTENER_SERVICE` in the manifest.
     * **Google Play policy:** Sensitive — apps gain read access to all incoming notifications.
     * Requires explicit Play policy approval.
     */
    NOTIFICATION_LISTENER
}
