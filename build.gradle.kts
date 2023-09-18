import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.konan.target.Family.ANDROID
import org.jetbrains.kotlin.konan.target.Family.LINUX
import org.jetbrains.kotlin.konan.target.Family.MINGW

buildscript {
    dependencies {
        classpath("com.vanniktech:gradle-maven-publish-plugin:0.25.3")
    }
}

plugins {
    kotlin("multiplatform")
}

repositories {
    mavenCentral()
    google()
}

val GROUP: String by project
val VERSION_NAME: String by project

group = GROUP
version = VERSION_NAME

kotlin {
    targetHierarchy.default()
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
    linuxArm64()

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
    wasm {
        browser()
        binaries.executable()
    }
    
    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val jsWasmMain by creating {
            dependsOn(commonMain)
        }
        val jsWasmTest by creating {
            dependsOn(commonTest)
        }
        val jsMain by getting {
            dependsOn(jsWasmMain)
        }
        val jsTest by getting {
            dependsOn(jsWasmTest)
        }
        val wasmMain by getting {
            dependsOn(jsWasmMain)
        }
        val wasmTest by getting {
            dependsOn(jsWasmTest)
        }

        val nativeCommonMain by creating {
            dependsOn(commonMain)
        }
        val nativeCommonTest by creating {
            dependsOn(commonTest)
        }

        val nativeDarwinMain by creating {
            dependsOn(nativeCommonMain)
        }
        val nativeLinuxMain by creating {
            dependsOn(nativeCommonMain)
        }
        val mingwMain by getting {
            dependsOn(nativeCommonMain)
        }
        
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
    }
}

apply(plugin = "com.vanniktech.maven.publish")
