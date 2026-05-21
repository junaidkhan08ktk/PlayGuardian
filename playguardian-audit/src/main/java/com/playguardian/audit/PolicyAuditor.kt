package com.playguardian.audit

import com.playguardian.audit.model.PolicyReport
import com.playguardian.audit.rules.BatteryPolicyRules
import com.playguardian.audit.rules.ExactAlarmPolicyRules
import com.playguardian.audit.rules.NotificationPolicyRules
import com.playguardian.audit.rules.OverlayPolicyRules
import com.playguardian.oem.PermissionConfig
import com.playguardian.oem.SpecialAccess

/**
 * Entry point for the policy audit subsystem.
 *
 * Delegates evaluation to the appropriate rule class for each [SpecialAccess] type and
 * returns a structured [PolicyReport] that developers can act on before requesting access.
 */
object PolicyAuditor {

    /**
     * Evaluates the Google Play policy risk for a given [access] request.
     *
     * This function is pure (no side effects, no I/O) and can be called from any thread.
     *
     * @param access The [SpecialAccess] the developer intends to request.
     * @param config The [PermissionConfig] describing the intended use case.
     * @return       A [PolicyReport] containing a severity rating, actionable warnings, and
     *               recommendations for safer or more policy-compliant alternatives.
     */
    fun evaluate(access: SpecialAccess, config: PermissionConfig): PolicyReport {
        return when (access) {
            SpecialAccess.IGNORE_BATTERY_OPTIMIZATION -> BatteryPolicyRules.evaluate(config)
            SpecialAccess.OVERLAY                     -> OverlayPolicyRules.evaluate(config)
            SpecialAccess.EXACT_ALARM                 -> ExactAlarmPolicyRules.evaluate(config)
            SpecialAccess.NOTIFICATION_LISTENER       -> NotificationPolicyRules.evaluate(config)
        }
    }
}
