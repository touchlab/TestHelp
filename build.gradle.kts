import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.konan.target.Family.LINUX

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
    iosArm32()
    watchosArm32()
    watchosArm64()
    watchosX86()
    watchosX64()
    tvosArm64()
    tvosX64()
    mingwX64("mingw")
    linuxX64()
    linuxArm32Hfp()
    linuxMips32()
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

    /* Setup dependsOn relationships */

    nativeCommonMain.dependsOn(commonMain)
    nativeCommonTest.dependsOn(commonTest)
    nativeDarwinMain.dependsOn(nativeCommonMain)
    nativeLinuxMain.dependsOn(nativeCommonMain)

    targets.withType<KotlinNativeTarget>().all {
        val mainSourceSet = compilations.getByName("main").defaultSourceSet
        val testSourceSet = compilations.getByName("test").defaultSourceSet

        mainSourceSet.dependsOn(nativeCommonMain)
        testSourceSet.dependsOn(nativeCommonTest)

        when {
            konanTarget.family == LINUX -> mainSourceSet.dependsOn(nativeLinuxMain)
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

/* Setup Publications */

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
    if (project.tasks.findByName("publish") != null) {
        setDependsOn(listOf("publishMingwPublicationToMavenRepository"))
        // dependsOn 'publishMingwX64PublicationToMavenRepository'
    }
}

/*
tasks.register('publishLinux') {
    if(project.tasks.findByName('publish')) {
        dependsOn 'publishLinuxMips32PublicationToMavenRepository'
    }
}*/
