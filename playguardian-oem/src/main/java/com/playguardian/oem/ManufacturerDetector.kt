package com.playguardian.oem

import android.os.Build

/**
 * Utility object that detects the device manufacturer at runtime by reading [Build.MANUFACTURER].
 *
 * Detection is intentionally conservative — if the manufacturer string cannot be confidently
 * mapped to a known OEM, [Manufacturer.OTHER] is returned so that safe generic fallbacks
 * are used instead of potentially incorrect OEM-specific intents.
 */
object ManufacturerDetector {

    /**
     * Returns the [Manufacturer] enum value that corresponds to the current device.
     *
     * Matching is case-insensitive and uses substring/prefix checks to accommodate
     * variant brand names (e.g. "Redmi" maps to [Manufacturer.XIAOMI]).
     */
    fun detect(): Manufacturer {
        val raw = Build.MANUFACTURER.lowercase().trim()
        return when {
            raw.contains("xiaomi") || raw.contains("redmi") || raw.contains("poco") -> Manufacturer.XIAOMI
            raw.contains("oppo") -> Manufacturer.OPPO
            raw.contains("vivo") || raw.contains("iqoo") -> Manufacturer.VIVO
            raw.contains("realme") -> Manufacturer.REALME
            raw.contains("huawei") || raw.contains("honor") -> Manufacturer.HUAWEI
            raw.contains("samsung") -> Manufacturer.SAMSUNG
            raw.contains("oneplus") -> Manufacturer.ONEPLUS
            raw.contains("infinix") -> Manufacturer.INFINIX
            raw.contains("tecno") -> Manufacturer.TECNO
            else -> Manufacturer.OTHER
        }
    }
}
