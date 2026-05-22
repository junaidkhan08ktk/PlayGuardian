package com.playguardian.sample.domain.model

import com.playguardian.oem.SpecialAccess

data class AccessInfo(
    val access: SpecialAccess,
    val isGranted: Boolean?,
    val displayName: String,
    val description: String
)
