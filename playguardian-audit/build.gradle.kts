plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

apply(plugin = "maven-publish")

tasks.register("sourcesJar", Jar::class) {
    archiveClassifier.set("sources")
    from(android.sourceSets.getByName("main").java.srcDirs)
}

android {
    namespace = "com.playguardian.audit"
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
    val publishingExt = project.extensions.getByType(PublishingExtension::class.java)
    val pub = publishingExt.publications.create("release", MavenPublication::class.java)
    pub.groupId = project.group.toString()
    pub.artifactId = project.name
    pub.version = project.version.toString()
    pub.from(components["release"])
    pub.artifact(tasks.named("sourcesJar").get())
}

dependencies {
    implementation(project(":playguardian-oem"))
    implementation(libs.androidx.core.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
