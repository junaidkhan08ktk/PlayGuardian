package com.playguardian.sample.data.repository

import android.app.Activity
import android.content.Context
import com.playguardian.audit.model.ManifestAuditReport
import com.playguardian.audit.model.PolicyReport
import com.playguardian.core.AccessCallback
import com.playguardian.core.PlayGuardian
import com.playguardian.oem.AppCategory
import com.playguardian.oem.PermissionConfig
import com.playguardian.oem.SpecialAccess
import com.playguardian.sample.domain.repository.PlayGuardianRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayGuardianRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : PlayGuardianRepository {

    override fun isGranted(access: SpecialAccess): Boolean {
        return PlayGuardian.isGranted(context, access)
    }

    override fun audit(access: SpecialAccess, reason: String, category: AppCategory): PolicyReport {
        return PlayGuardian.audit(access, reason, category)
    }

    override fun auditManifest(): ManifestAuditReport {
        return PlayGuardian.auditManifest(context)
    }

    override fun requestAccess(
        activity: Activity,
        access: SpecialAccess,
        reason: String,
        category: AppCategory,
        callback: AccessCallback
    ) {
        val config = PermissionConfig(
            featureReason = reason,
            appCategory = category,
            autoOpenOemFallback = true
        )
        PlayGuardian.request(activity, access, config, callback)
    }

    override fun handleResume(activity: Activity, access: SpecialAccess, callback: AccessCallback) {
        PlayGuardian.handleResume(activity, access, callback)
    }
}
