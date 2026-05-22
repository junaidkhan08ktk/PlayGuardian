package com.playguardian.sample.domain.usecase

import android.os.Build
import com.playguardian.oem.SpecialAccess
import com.playguardian.sample.domain.model.AccessInfo
import com.playguardian.sample.domain.repository.PlayGuardianRepository
import javax.inject.Inject

class GetAccessStatusesUseCase @Inject constructor(
    private val repository: PlayGuardianRepository
) {
    operator fun invoke(): List<AccessInfo> {
        return listOf(
            AccessInfo(
                access = SpecialAccess.IGNORE_BATTERY_OPTIMIZATION,
                isGranted = repository.isGranted(SpecialAccess.IGNORE_BATTERY_OPTIMIZATION),
                displayName = "Battery Optimization",
                description = "Ensure reminders and alarms fire on time in Doze mode"
            ),
            AccessInfo(
                access = SpecialAccess.OVERLAY,
                isGranted = repository.isGranted(SpecialAccess.OVERLAY),
                displayName = "Overlay (Draw Over Apps)",
                description = "Display floating UI above other applications"
            ),
            AccessInfo(
                access = SpecialAccess.EXACT_ALARM,
                isGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    repository.isGranted(SpecialAccess.EXACT_ALARM)
                } else null,
                displayName = "Exact Alarm",
                description = "Schedule precise alarms and calendar notifications"
            ),
            AccessInfo(
                access = SpecialAccess.NOTIFICATION_LISTENER,
                isGranted = repository.isGranted(SpecialAccess.NOTIFICATION_LISTENER),
                displayName = "Notification Listener",
                description = "Read and interact with system notifications"
            )
        )
    }
}
