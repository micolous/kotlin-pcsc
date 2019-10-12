import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform") version "1.3.41"
}

repositories {
    mavenCentral()
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
    }
}
