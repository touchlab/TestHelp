rootProject.name = "testhelp"

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
    }
    val KOTLIN_VERSION: String by settings
    plugins {
        kotlin("multiplatform") version KOTLIN_VERSION
    }
}

enableFeaturePreview("GRADLE_METADATA")
