package com.playguardian.oem

/**
 * Classifies the category of the host application.
 *
 * Used by [com.playguardian.audit.PolicyAuditor] to apply heuristic policy rules that account
 * for the expected use cases of different app types. Selecting the most accurate category
 * improves the quality of audit warnings and recommendations.
 */
enum class AppCategory {

    /** Productivity tools: task managers, note apps, document editors, workflow tools. */
    PRODUCTIVITY,

    /** Communication apps: messaging, email, VoIP, video calling. */
    COMMUNICATION,

    /** Health and fitness: workout trackers, medication reminders, sleep monitors. */
    HEALTH,

    /** General utilities: calculators, file managers, system tools. */
    UTILITY,

    /** Media apps: video players, music, streaming, podcasts. */
    MEDIA,

    /** Developer/technical tools: IDE companions, logcat viewers, ADB tools. */
    TOOLS,

    /** Finance apps: banking, budgeting, payment, investment. */
    FINANCE,

    /** Does not fit neatly into another category. Highest default policy scrutiny applies. */
    OTHER
}
