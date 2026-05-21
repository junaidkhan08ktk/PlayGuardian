# PlayGuardian

**PlayGuardian** is a production-ready, open-source Android library that helps developers safely request, verify, and audit sensitive Android special accesses — while staying compliant with Google Play policies and handling the fragmented OEM ecosystem gracefully.

---

## Why PlayGuardian?

Requesting special Android permissions (battery optimisation exemption, overlays, exact alarms, notification listeners) is deceptively hard:

- Each access requires a different settings screen and a different verification API.
- OEM customisations (Xiaomi MIUI, Samsung One UI, Huawei EMUI, etc.) break standard intents.
- Google Play has strict, evolving policies about *which apps* may request each access and *why*.
- There is no built-in Android API that combines audit, launch, and verify into a single workflow.

PlayGuardian solves all three problems in one library.

---

## Features

- ✅ **Policy Audit** — heuristic risk evaluation against Google Play policy before any UI is shown
- ✅ **OEM-Safe Intent Resolution** — prioritised fallback chain across 9 OEM brands + generic Android
- ✅ **Safe Settings Launcher** — no crashes; catches all `ActivityNotFoundException` and security exceptions
- ✅ **Permission Verifier** — canonical per-access API (PowerManager, AlarmManager, Settings, etc.)
- ✅ **Manifest Auditor** — scans declared permissions and surfaces sensitive entries with explanations
- ✅ **Full Callback Lifecycle** — `onGranted`, `onDenied`, `onUnsupported`, `onPolicyWarning`
- ✅ **Multi-module architecture** — core / oem / audit modules are independently reusable
- ✅ **Zero runtime crashes** — all OEM intent launches are wrapped in defensive try/catch

---

## Supported Special Accesses

| Access | Android API | Notes |
|---|---|---|
| `IGNORE_BATTERY_OPTIMIZATION` | API 23+ | Requires `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` in manifest |
| `OVERLAY` | API 23+ | Requires `SYSTEM_ALERT_WINDOW` in manifest |
| `EXACT_ALARM` | API 31+ | Requires `SCHEDULE_EXACT_ALARM` or `USE_EXACT_ALARM` in manifest |
| `NOTIFICATION_LISTENER` | All | Requires `BIND_NOTIFICATION_LISTENER_SERVICE` service in manifest |

---

## OEM Support

PlayGuardian includes safe, defensive intent fallbacks for:

| OEM | Brands Detected |
|---|---|
| Xiaomi | Xiaomi, Redmi, Poco |
| Samsung | Samsung |
| Huawei | Huawei, Honor |
| Oppo | Oppo |
| Realme | Realme |
| Vivo | Vivo, iQOO |
| OnePlus | OnePlus |
| Infinix | Infinix |
| Tecno | Tecno |
| Other | All other brands → safe generic fallback |

> **Safety principle:** PlayGuardian never guesses hidden OEM component names. All OEM fallbacks use standard Android intent actions. Fragile MIUI/EMUI component paths are intentionally excluded.

---

## Installation

> Publishing to Maven Central is in progress. Until then, clone and include as a local module.

**Step 1.** Clone the repository:
```bash
git clone https://github.com/your-org/PlayGuardian.git
```

**Step 2.** Add to your root `settings.gradle.kts`:
```kotlin
include(":playguardian-core")
include(":playguardian-oem")
include(":playguardian-audit")
project(":playguardian-core").projectDir = File("../PlayGuardian/playguardian-core")
project(":playguardian-oem").projectDir = File("../PlayGuardian/playguardian-oem")
project(":playguardian-audit").projectDir = File("../PlayGuardian/playguardian-audit")
```

**Step 3.** Add the core dependency to your app's `build.gradle.kts`:
```kotlin
dependencies {
    implementation(project(":playguardian-core"))
}
```

---

## Quick Usage

### 1. Audit Policy Risk (no UI)

```kotlin
val report = PlayGuardian.audit(
    access = SpecialAccess.OVERLAY,
    featureReason = "Show a floating translation bubble above other apps",
    appCategory = AppCategory.PRODUCTIVITY
)

when (report.severity) {
    Severity.HIGH   -> Log.w("PlayGuardian", "High policy risk: ${report.warnings}")
    Severity.MEDIUM -> Log.i("PlayGuardian", "Review recommended: ${report.warnings}")
    Severity.LOW    -> Log.d("PlayGuardian", "Looks good: ${report.recommendations}")
}
```

---

### 2. Request a Special Access

```kotlin
PlayGuardian.request(
    activity = this,
    access = SpecialAccess.OVERLAY,
    config = PermissionConfig(
        featureReason = "Show a floating translation bubble above other apps",
        appCategory = AppCategory.PRODUCTIVITY,
        autoOpenOemFallback = true
    ),
    callback = object : AccessCallback {
        override fun onGranted() {
            // Access confirmed — proceed with feature
        }
        override fun onDenied() {
            // User returned without granting — show rationale or degrade gracefully
        }
        override fun onUnsupported() {
            // Cannot open settings on this device — degrade gracefully
        }
        override fun onPolicyWarning(report: PolicyReport) {
            // Inspect report.severity, report.warnings, report.recommendations
            // The request flow continues — you decide whether to abort
        }
    }
)
```

---

### 3. Handle Return from Settings

In your `Activity.onResume()`, call `handleResume` to verify the final state after the user returns from the settings screen:

```kotlin
override fun onResume() {
    super.onResume()
    PlayGuardian.handleResume(
        context = this,
        access = SpecialAccess.OVERLAY,
        callback = myCallback
    )
}
```

---

### 4. Check Current State Without Requesting

```kotlin
val granted: Boolean = PlayGuardian.isGranted(context, SpecialAccess.EXACT_ALARM)
```

---

### 5. Scan Manifest for Sensitive Permissions

```kotlin
val manifestReport = PlayGuardian.auditManifest(context)

manifestReport.sensitivePermissions.forEach { permission ->
    Log.w("ManifestAudit", "Sensitive: $permission")
}
manifestReport.warnings.forEach { warning ->
    Log.w("ManifestAudit", warning)
}
```

Detected sensitive permissions include:
- `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS`
- `SYSTEM_ALERT_WINDOW`
- `SCHEDULE_EXACT_ALARM` / `USE_EXACT_ALARM`
- `BIND_NOTIFICATION_LISTENER_SERVICE`
- `QUERY_ALL_PACKAGES`
- `MANAGE_EXTERNAL_STORAGE`

---

## Module Architecture

```
PlayGuardian/
│
├── playguardian-core/          ← Public API. Depends on oem + audit.
│   ├── PlayGuardian.kt         ← Singleton entry point
│   ├── AccessCallback.kt       ← Callback interface
│   ├── AppCategory.kt          ← Typealias (source in oem)
│   ├── PermissionConfig.kt     ← Typealias (source in oem)
│   ├── model/AccessState.kt    ← Sealed class: Granted / Denied / Unsupported
│   ├── verifier/               ← PermissionVerifier (canonical per-access checks)
│   └── launcher/               ← PermissionRequestLauncher (orchestration)
│
├── playguardian-oem/           ← OEM intent resolution. No dependency on core/audit.
│   ├── SpecialAccess.kt        ← Enum of supported accesses
│   ├── AppCategory.kt          ← Enum of app categories
│   ├── PermissionConfig.kt     ← Config data class
│   ├── Manufacturer.kt         ← OEM enum
│   ├── ManufacturerDetector.kt ← Build.MANUFACTURER → Manufacturer
│   ├── OemIntentResolver.kt    ← Returns prioritised Intent list
│   ├── OemSettingsLauncher.kt  ← Launches first resolvable intent
│   └── intents/                ← Per-access intent builders
│
├── playguardian-audit/         ← Policy audit engine. Depends on oem only.
│   ├── PolicyAuditor.kt        ← Delegates to per-access rule classes
│   ├── ManifestAuditor.kt      ← Scans PackageManager for sensitive permissions
│   ├── model/PolicyReport.kt   ← Result: severity + warnings + recommendations
│   ├── model/ManifestAuditReport.kt
│   └── rules/                  ← Per-access heuristic rule classes
│
└── sample-app/                 ← Demonstrates all library features
```

---

## Dependency Graph

```
sample-app
    └── playguardian-core
            ├── playguardian-audit
            │       └── playguardian-oem
            └── playguardian-oem
```

---

## Google Play Policy Guidance

PlayGuardian surfaces policy warnings but **does not block requests** — the final decision is yours. Key principles:

| Access | Play Policy Summary |
|---|---|
| Battery Optimisation | Only for apps requiring strict real-time delivery (VoIP, alarms). Background sync is not sufficient justification. |
| Overlay | Must be user-initiated. Cannot display ads or interfere with other apps. |
| Exact Alarm | Restricted to alarm clocks and calendar apps. Background polling/analytics is not permitted. |
| Notification Listener | Requires explicit Play approval. Must disclose data access in Privacy Policy. |

> Always consult the [Google Play Developer Policy Center](https://play.google.com/about/developer-content-policy/) for the authoritative and current policy text.

---

## Minimum Requirements

- Android minSdk: **24** (Android 7.0)
- Kotlin: **1.9+**
- Android Gradle Plugin: **8.x**

---

## Contributing

Pull requests are welcome. Please open an issue first for significant changes. All new access types must include:
1. A `SpecialAccess` enum entry in `playguardian-oem`
2. Intent resolution logic in `playguardian-oem/intents/`
3. A verification implementation in `PermissionVerifier`
4. A policy rule class in `playguardian-audit/rules/`

---

## License

```
Apache License 2.0

Copyright 2024 PlayGuardian Contributors

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0
```
