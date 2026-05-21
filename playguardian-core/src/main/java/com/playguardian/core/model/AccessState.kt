package com.playguardian.core.model

/**
 * Represents the possible states of a special access after verification.
 *
 * Use [com.playguardian.core.verifier.PermissionVerifier.checkState] to obtain the current
 * [AccessState] for a given [com.playguardian.oem.SpecialAccess].
 */
sealed class AccessState {

    /** The special access has been granted by the user. */
    object Granted : AccessState()

    /** The special access has not been granted, but the user can grant it via settings. */
    object Denied : AccessState()

    /**
     * The special access is not supported on this device or API level.
     *
     * For example, [com.playguardian.oem.SpecialAccess.EXACT_ALARM] returns [Unsupported] on
     * devices running Android 11 or earlier.
     */
    object Unsupported : AccessState()
}
