// Top-level build file — configuration options common to all sub-projects.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.kapt) apply false
}

// Set default coordinates for publications. JitPack will normally build from a git tag
// and use that tag as the version, but having sensible defaults here is helpful for
// local publishing and for the maven-publish configuration below.
group = "com.github.junaidkhan08ktk"
version = "0.0.1-SNAPSHOT"

// NOTE: Publishing is configured per-module inside each library module's
// `build.gradle.kts` (this avoids timing issues with AGP components and keeps
// the module-specific sourcesJar configuration close to the module sources).
