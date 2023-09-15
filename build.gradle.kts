import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.konan.target.Family.ANDROID
import org.jetbrains.kotlin.konan.target.Family.LINUX
import org.jetbrains.kotlin.konan.target.Family.MINGW

buildscript {
    dependencies {
        classpath("com.vanniktech:gradle-maven-publish-plugin:0.23.1")
    }
}

plugins {
    kotlin("multiplatform")
}

repositories {
    mavenLocal()
    mavenCentral()
    google()
    jcenter()
}

val GROUP: String by project
val VERSION_NAME: String by project

group = GROUP
version = VERSION_NAME

kotlin {

    macosX64()
    iosX64()
    iosArm64()
    watchosArm32()
    watchosArm64()
    watchosX64()
    tvosArm64()
    tvosX64()
    mingwX64()
    linuxX64()

    macosArm64()
    iosSimulatorArm64()
    watchosSimulatorArm64()
    tvosSimulatorArm64()

    watchosDeviceArm64()

    androidNativeArm32()
    androidNativeArm64()
    androidNativeX86()
    androidNativeX64()

    jvm()
    js {
        browser()
        nodejs()
    }

    val commonMain by sourceSets.getting
    val commonTest by sourceSets.getting

    val jvmMain by sourceSets.getting
    val jvmTest by sourceSets.getting
    val jsMain by sourceSets.getting
    val jsTest by sourceSets.getting

    val nativeCommonMain by sourceSets.creating
    val nativeCommonTest by sourceSets.creating

    val nativeDarwinMain by sourceSets.creating
    val nativeLinuxMain by sourceSets.creating
    val mingwMain by sourceSets.creating

    /* Setup dependsOn relationships */

    nativeCommonMain.dependsOn(commonMain)
    nativeCommonTest.dependsOn(commonTest)
    nativeDarwinMain.dependsOn(nativeCommonMain)
    nativeLinuxMain.dependsOn(nativeCommonMain)
    mingwMain.dependsOn(nativeCommonMain)

    targets.withType<KotlinNativeTarget>().all {
        val mainSourceSet = compilations.getByName("main").defaultSourceSet
        val testSourceSet = compilations.getByName("test").defaultSourceSet

        mainSourceSet.dependsOn(nativeCommonMain)
        testSourceSet.dependsOn(nativeCommonTest)

        when {
            konanTarget.family == MINGW -> mainSourceSet.dependsOn(mingwMain)
            konanTarget.family == LINUX || konanTarget.family == ANDROID -> mainSourceSet.dependsOn(nativeLinuxMain)
            konanTarget.family.isAppleFamily -> mainSourceSet.dependsOn(nativeDarwinMain)
            else -> mainSourceSet.dependsOn(nativeCommonMain)
        }
    }

    /* Setup dependencies */

    commonMain.dependencies {
        implementation("org.jetbrains.kotlin:kotlin-stdlib-common")
    }

    commonTest.dependencies {
        implementation("org.jetbrains.kotlin:kotlin-test-common")
        implementation("org.jetbrains.kotlin:kotlin-test-annotations-common")
    }

    jvmMain.dependencies {
        implementation("org.jetbrains.kotlin:kotlin-stdlib")
    }

    jvmTest.dependencies {
        implementation("org.jetbrains.kotlin:kotlin-test")
        implementation("org.jetbrains.kotlin:kotlin-test-junit")
    }

    jsMain.dependencies {
        implementation("org.jetbrains.kotlin:kotlin-stdlib-js")
    }

    jsTest.dependencies {
        implementation("org.jetbrains.kotlin:kotlin-test-js")
    }
}

tasks.register("publishLinux") {
    if (project.tasks.findByName("publish") != null) {
        setDependsOn(listOf(
            "publishLinuxMips32PublicationToMavenRepository"
        ))
    }
}

apply(plugin = "com.vanniktech.maven.publish")
