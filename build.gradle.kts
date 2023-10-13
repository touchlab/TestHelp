import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension
import org.jetbrains.kotlin.gradle.targets.js.npm.tasks.KotlinNpmInstallTask
import org.jetbrains.kotlin.konan.target.Family.ANDROID
import org.jetbrains.kotlin.konan.target.Family.LINUX
import org.jetbrains.kotlin.konan.target.Family.MINGW

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.mavenPublish)
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
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        // browser()
        nodejs()
        binaries.executable()
    }

    applyDefaultHierarchyTemplate()

    sourceSets {
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val jsAndWasmJsMain by creating {
            dependsOn(commonMain.get())
            jsMain.get().dependsOn(this)
        }
        val jsAndWasmJsTest by creating {
            dependsOn(commonTest)
            jsTest.get().dependsOn(this)
        }
        val wasmJsMain by getting {
            dependsOn(jsAndWasmJsMain)
        }
        val wasmJsTest by getting {
            dependsOn(jsAndWasmJsTest)
        }

        val nativeCommonMain by creating {
            dependsOn(commonMain.get())
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
                konanTarget.family == LINUX ||
                    konanTarget.family == ANDROID -> mainSourceSet.dependsOn(nativeLinuxMain)

                konanTarget.family.isAppleFamily -> mainSourceSet.dependsOn(nativeDarwinMain)
                else -> mainSourceSet.dependsOn(nativeCommonMain)
            }
        }
    }
}

// https://github.com/Kotlin/kotlin-wasm-examples/commit/701a051d6ee869abcabebff702b3ccd98d51c38d
rootProject.the<NodeJsRootExtension>().apply {
    nodeVersion = "21.0.0-v8-canary202309143a48826a08"
    nodeDownloadBaseUrl = "https://nodejs.org/download/v8-canary"
}

// https://github.com/Kotlin/kotlin-wasm-examples/commit/52445ffc561d1694babf6d9484f8df0807b9ed53
tasks.withType<KotlinNpmInstallTask>().configureEach {
    args.add("--ignore-engines")
}