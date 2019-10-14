import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform") version "1.3.41"
    id("org.jetbrains.dokka") version "0.10.0"
}

repositories {
    mavenCentral()
    jcenter()
}

val kotlinVersion = "1.3.41"
val mingwPath = File(System.getenv("MINGW64_DIR") ?: "C:/msys64/mingw64")

dependencies {
    commonMainApi(kotlin("stdlib-common"))
    commonTestImplementation(kotlin("test-common"))
    commonTestImplementation(kotlin("test-annotations-common"))
}

kotlin {
    // Determine host preset.
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")

    // linuxArm32Hfp()  // Raspberry Pi
    // linuxX64()
    macosX64()  // (no cross compiler)
    // mingwX86()  // Windows
    // mingwX64()  // Windows x64 (no cross compiler)

    jvm("jna")

    sourceSets {
        val nativeMain by creating {
            dependsOn(getByName("commonMain"))
        }

        fun KotlinNativeTarget.buildNative() {
            compilations["main"].apply {
                defaultSourceSet {
                    dependsOn(nativeMain)
                }

                cinterops {
                    val winscard by creating { }
                }
            }

            compilations["test"].apply {
                defaultSourceSet {
                    dependsOn(getByName("commonTest"))
                }
            }
        }

        // linuxArm32Hfp().buildNative()
        // linuxX64().buildNative()
        macosX64().buildNative()
        // mingwX86().buildNative()
        // mingwX64().buildNative()

        jvm("jna").apply {
            compilations["main"].apply {
                defaultSourceSet {
                    dependsOn(getByName("commonMain"))
                }
                dependencies {
                    api(kotlin("stdlib-jdk8"))
                    api("net.java.dev.jna:jna:4.0.0")
                }
            }

            compilations["test"].apply {
                defaultSourceSet {
                    dependsOn(getByName("commonTest"))
                }
                dependencies {
                    implementation(kotlin("test-junit"))
                }
            }
        }
    }
}

tasks {
    val dokka by getting(DokkaTask::class) {
        outputFormat = "html"
        outputDirectory = "$buildDir/dokka"
    }
}
