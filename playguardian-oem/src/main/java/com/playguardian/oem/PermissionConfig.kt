package com.playguardian.oem

/**
 * Configuration object that describes the context in which a special access is being requested.
 *
 * @property featureReason       A human-readable explanation of *why* the app needs this access.
 * @property appCategory         The category of the host application. Defaults to [AppCategory.OTHER].
 * @property autoOpenOemFallback When `true`, the request flow will attempt OEM-specific fallback
 *   intents if the standard settings intent cannot be resolved.
 */
data class PermissionConfig(
    val featureReason: String,
    val appCategory: AppCategory = AppCategory.OTHER,
    val autoOpenOemFallback: Boolean = true
)
