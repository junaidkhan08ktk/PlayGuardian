// Top-level build file — configuration options common to all sub-projects.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
}

// Set default coordinates for publications. JitPack will normally build from a git tag
// and use that tag as the version, but having sensible defaults here is helpful for
// local publishing and for the maven-publish configuration below.
group = "com.github.junaidkhan08ktk"
version = "0.0.1-SNAPSHOT"

// Configure publishing for Android library modules. This applies the `maven-publish`
// plugin and creates a `release` Maven publication that publishes the AAR produced
// by the Android Gradle Plugin. We do this in `afterEvaluate` because the Android
// `components["release"]` is only available after the Android plugin configures the project.
subprojects {
    afterEvaluate {
        if (plugins.hasPlugin("com.android.library")) {
            apply(plugin = "maven-publish")

            publishing {
                publications {
                    create("release", org.gradle.api.publish.maven.MavenPublication::class) {
                        // Use project coordinates by default: groupId = root group, artifactId = project name
                        groupId = project.group.toString()
                        artifactId = project.name
                        version = project.version.toString()

                        // Publish the AAR produced by the Android library plugin
                        from(components["release"])
                    }
                }
            }
        }
    }
}
