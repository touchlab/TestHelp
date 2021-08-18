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
    val knTargets = listOf(
        macosX64(),
        iosX64(),
        iosArm64(),
        iosArm32(),
        watchosArm32(),
        watchosArm64(),
        watchosX86(),
        watchosX64(),
        tvosArm64(),
        tvosX64(),
        mingwX64(),
        linuxX64()
        // ,
        // linuxArm32Hfp(),
        // linuxMips32()
    )

    jvm()
    js {
        browser()
        nodejs()
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib-common")
            }
        }
        commonTest {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-test-common")
                implementation("org.jetbrains.kotlin:kotlin-test-annotations-common")
            }
        }

        sourceSets.maybeCreate("jvmMain").apply {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib")
            }
        }
        sourceSets.maybeCreate("jvmTest").apply {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-test")
                implementation("org.jetbrains.kotlin:kotlin-test-junit")
            }
        }

        sourceSets.maybeCreate("jsMain").apply {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib-js")
            }
        }
        sourceSets.maybeCreate("jsTest").apply {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-test-js")
            }
        }

        val nativeCommonMain = sourceSets.maybeCreate("nativeCommonMain")
        val nativeCommonTest = sourceSets.maybeCreate("nativeCommonTest")

        val appleMain = sourceSets.maybeCreate("nativeDarwinMain").apply {
            dependsOn(nativeCommonMain)
        }
        val linuxMain = sourceSets.maybeCreate("nativeLinuxMain").apply {
            dependsOn(nativeCommonMain)
        }
        val mingwMain = sourceSets.maybeCreate("nativeMingwMain").apply {
            dependsOn(nativeCommonMain)
        }

        knTargets.forEach { target ->
            when {
                target.name.startsWith("mingw") -> {
                    target.compilations.getByName("test").defaultSourceSet.dependsOn(nativeCommonTest)
                }
                target.name.startsWith("linux") -> {
                    target.compilations.getByName("main").defaultSourceSet.dependsOn(linuxMain)
                    target.compilations.getByName("test").defaultSourceSet.dependsOn(nativeCommonTest)
                }
                else -> {
                    target.compilations.getByName("main").defaultSourceSet.dependsOn(appleMain)
                    target.compilations.getByName("test").defaultSourceSet.dependsOn(nativeCommonTest)
                }
            }

        }
    }
}

/*if (!HostManager.hostIsLinux) {
    tasks.findByName("linuxX64Test")?.enabled = false
    tasks.findByName("linkDebugTestLinuxX64")?.enabled = false
    tasks.findByName("publishLinuxX64PublicationToMavenRepository")?.enabled = false
}*/

apply(from = "gradle/gradle-mvn-mpp-push.gradle")

/*
tasks.register("publishMac") {
    setDependsOn(tasks.filter { t ->
        t.name.startsWith("publish") && t.name.endsWith("ToMavenRepository") && !t.name.contains(
            "Linux"
        )
    }.map { it.name })
}
*/

tasks.register("publishWindows") {
    if(project.tasks.findByName("publish") != null) {
        setDependsOn(listOf("publishMingwX64PublicationToMavenRepository"))
        // dependsOn 'publishMingwX64PublicationToMavenRepository'
    }
}

/*
tasks.register('publishLinux') {
    if(project.tasks.findByName('publish')) {
        dependsOn 'publishLinuxMips32PublicationToMavenRepository'
    }
}*/
