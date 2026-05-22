plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

// Apply maven-publish for module-level publications (JitPack / mavenLocal)
apply(plugin = "maven-publish")

// Create a sources JAR so consumers can see sources in IDEs
tasks.register("sourcesJar", org.gradle.api.tasks.bundling.Jar::class) {
    archiveClassifier.set("sources")
    from(android.sourceSets.getByName("main").java.srcDirs)
}

android {
    namespace = "com.playguardian.core"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

afterEvaluate {
    // Configure publication after the Android plugin has created components
    val publishingExt = project.extensions.getByType(PublishingExtension::class.java)
    val pub = publishingExt.publications.create("release", MavenPublication::class.java)
    pub.groupId = project.group.toString()
    pub.artifactId = project.name
    pub.version = project.version.toString()
    pub.from(components["release"])
    pub.artifact(tasks.named("sourcesJar").get())
}

dependencies {
    api(project(":playguardian-oem"))
    api(project(":playguardian-audit"))
    implementation(libs.androidx.core.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
