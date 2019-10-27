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

val kotlinNativeDataPath = System.getenv("KONAN_DATA_DIR")?.let { File(it) }
    ?: File(System.getProperty("user.home")).resolve(".konan")

dependencies {
    commonMainApi(kotlin("stdlib-common"))
    commonTestImplementation(kotlin("test-common"))
    commonTestImplementation(kotlin("test-annotations-common"))
}

/**
 * Runs `pkg-config`:
 * https://github.com/JetBrains/kotlin-native/issues/1534#issuecomment-384894431
 */
fun pkgConfig(
    vararg packageNames: String,
    cflags: Boolean = false,
    includes: Boolean = false,
    libs: Boolean = false): List<String> {
    val p = ProcessBuilder(*(listOfNotNull(
        "pkg-config",
        if (cflags) "--cflags-only-other" else null,
        if (includes) "--variable=includedir" else null,
        if (libs) "--libs" else null
    ).toTypedArray() + packageNames))
        .start()
        .also { it.waitFor(10, TimeUnit.SECONDS) }


    if (p.exitValue() != 0) {
        throw Exception("Error executing pkg-config: ${p.errorStream.bufferedReader().readText()}")
    }

    return p.inputStream.bufferedReader().readText().split(" ").map{ it.trim() }
}

kotlin {
    // Determine host preset.
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")

    // linuxArm32Hfp()  // Raspberry Pi
    linuxX64()
    macosX64()  // (no cross compiler)
    // mingwX86()  // Windows
    mingwX64()  // Windows (no cross compiler)

    jvm("jna")

    sourceSets {
        val nativeMain by creating {
            dependsOn(getByName("commonMain"))
        }

        val nativeMacosMain by creating {
            dependsOn(nativeMain)
        }

        val nativeWindowsMain by creating {
            dependsOn(nativeMain)
        }

        fun KotlinNativeTarget.buildNative() {
            compilations["main"].apply {
                defaultSourceSet {
                    dependsOn(when (preset) {
                        presets["macosX64"] -> nativeMacosMain
                        presets["mingwX64"],
                        presets["mingwX86"] -> nativeWindowsMain
                        else -> nativeMain
                    })
                }

                cinterops {
                    val winscard by creating {
//                        when (preset) {
//                            presets["linuxX64"] -> {
//                                includeDirs("/usr/include", "/usr/include/PCSC")
//                            }
//                        }
                    }
                }
            }

            compilations["test"].apply {
                defaultSourceSet {
                    dependsOn(getByName("commonTest"))
                }
            }
        }

        // linuxArm32Hfp().buildNative()
        linuxX64().buildNative()
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

    sourceSets.all {
        languageSettings.useExperimentalAnnotation("kotlin.ExperimentalStdlibApi")
    }
}

tasks {
    val dokka by getting(DokkaTask::class) {
        outputFormat = "html"
        outputDirectory = "$buildDir/dokka"
        multiplatform {
            val global by creating {
                perPackageOption {
                    prefix = "au.id.micolous.kotlin.pcsc.jna"
                    suppress = true
                }
                perPackageOption {
                    prefix = "au.id.micolous.kotlin.pcsc.native"
                    suppress = true
                }
                perPackageOption {
                    prefix = "au.id.micolous.kotlin.pcsc.internal"
                    suppress = true
                }
            }

            register("common") {
                targets = listOf("all")
                platform = "common"
            }


            // val common by creating {}
            //val linuxX64 by creating {}
            //val macosX64 by creating {}

        }
    }
}
