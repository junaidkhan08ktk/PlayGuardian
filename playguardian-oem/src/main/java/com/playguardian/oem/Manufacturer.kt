package com.playguardian.oem

/**
 * Enum representing known Android device manufacturers.
 *
 * Used internally by [ManufacturerDetector] and [OemIntentResolver] to
 * select appropriate settings intent fallback strategies.
 */
enum class Manufacturer {
    XIAOMI,
    OPPO,
    VIVO,
    REALME,
    HUAWEI,
    SAMSUNG,
    ONEPLUS,
    INFINIX,
    TECNO,
    OTHER
}
