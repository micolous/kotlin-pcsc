import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.plugin.mpp.DefaultCInteropSettings
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJvmCompilation
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeCompilation
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

kotlin {
    // Determine host preset.
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")

    // linuxArm32Hfp()  // Raspberry Pi
    linuxX64()
    macosX64()  // (no cross compiler)
    mingwX86()  // Windows
    mingwX64()  // Windows (no cross compiler)

    jvm("jna")

    sourceSets {
        val commonMain by getting {}
        val commonTest by getting {}

        val nativeMain by creating {}
        val nativeMacosMain by creating {
            dependsOn(nativeMain)
        }
        val nativeWindowsMain by creating {
            dependsOn(nativeMain)
        }

        jvm("jna").apply {
            compilations["main"].apply {
                dependencies {
                    api("net.java.dev.jna:jna:4.0.0")
                }
            }
        }

        // Setup common dependencies
        targets.forEach {
            val linTarget = it.preset?.name?.startsWith("linux") ?: false
            val macTarget = it.preset?.name?.startsWith("macos") ?: false
            val winTarget = it.preset?.name?.startsWith("mingw") ?: false


            it.compilations.forEach { compilation ->
                when (compilation.name) {
                    "main" -> compilation.apply {
                        defaultSourceSet {
                            if (this != commonMain) {
                                dependsOn(commonMain)
                            }
                        }

                        when (this) {
                            is KotlinJvmCompilation -> // Java
                                dependencies {
                                    api(kotlin("stdlib-jdk8"))
                                }

                            is KotlinNativeCompilation -> { // Native
                                defaultSourceSet {
                                    dependsOn(
                                        when {
                                            macTarget -> nativeMacosMain
                                            winTarget -> nativeWindowsMain
                                            else -> nativeMain
                                        }
                                    )
                                }

                                cinterops {
                                    create("winscard")
                                }
                            }
                        }
                    }


                    "test" -> compilation.apply {
                        defaultSourceSet {
                            dependsOn(commonTest)
                        }

                        if (this is KotlinJvmCompilation) {
                            // common
                            dependencies {
                                implementation(kotlin("test-junit"))
                            }
                        }
                    }
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

        configuration {
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

            platform = "common"
            includeNonPublic = false
            reportUndocumented = true
            skipEmptyPackages = true
            includes = listOf("src/module.md")
            sourceRoot {
                path = kotlin.sourceSets.getByName("commonMain").kotlin.srcDirs.first().toString()
            }
        }
    }
}
