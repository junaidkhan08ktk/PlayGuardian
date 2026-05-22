package com.playguardian.sample.domain.repository

import android.app.Activity
import com.playguardian.audit.model.ManifestAuditReport
import com.playguardian.audit.model.PolicyReport
import com.playguardian.core.AccessCallback
import com.playguardian.oem.AppCategory
import com.playguardian.oem.SpecialAccess

interface PlayGuardianRepository {
    fun isGranted(access: SpecialAccess): Boolean
    fun audit(access: SpecialAccess, reason: String, category: AppCategory): PolicyReport
    fun auditManifest(): ManifestAuditReport
    fun requestAccess(activity: Activity, access: SpecialAccess, reason: String, category: AppCategory, callback: AccessCallback)
    fun handleResume(activity: Activity, access: SpecialAccess, callback: AccessCallback)
}
