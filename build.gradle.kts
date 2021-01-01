import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJvmCompilation
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeCompilation
import java.io.ByteArrayOutputStream

plugins {
    kotlin("multiplatform") version "1.4.21"
    id("org.jetbrains.dokka") version "1.4.20"
    id("maven-publish")
}

repositories {
    mavenCentral()
    jcenter()
}

val coroutinesVer = "1.4.2"

dependencies {
    commonMainApi("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVer")
    commonTestImplementation(kotlin("test-common"))
    commonTestImplementation(kotlin("test-annotations-common"))
}

group = "au.id.micolous.kotlin.pcsc"
version = "0.0.1"

/** The macOS SDK path, or null if it is not available. */
val macOSSDKPath : String? = run {
    try {
        val o = ByteArrayOutputStream()
        project.exec {
            commandLine("xcrun", "--sdk", "macosx", "--show-sdk-path")
            standardOutput = o
        }
        o.toString("UTF-8").trim()
    } catch (_: Exception) {
        null
    }
}

kotlin {
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

        // Setup common dependencies
        targets.forEach {
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
                                    api("net.java.dev.jna:jna:4.0.0")
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
                                    create("winscard") {
                                        when {
                                            macTarget && macOSSDKPath != null ->
                                                includeDirs("${macOSSDKPath}/System/Library/Frameworks/PCSC.framework/Versions/Current/Headers")
                                        }
                                    }
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

publishing {
    publications {
        val kotlinMultiplatform by getting {
        //    artifactId = "kotlin-pcsc"
        }
    }
}

tasks.withType<DokkaTask>().configureEach {
    outputDirectory.set(buildDir.resolve("dokka"))

    dokkaSourceSets {
        named("commonMain") {
            includeNonPublic.set(false)
            reportUndocumented.set(true)
            skipEmptyPackages.set(true)
            includes.from("src/module.md")
            sourceRoot(kotlin.sourceSets.getByName("commonMain").kotlin.srcDirs.first())
            platform.set(org.jetbrains.dokka.Platform.common)
            perPackageOption {
                matchingRegex.set("au\\.id\\.micolous\\.kotlin\\.pcsc\\.(jna|internal|native)(\$|\\\\.).*")
                suppress.set(true)
            }
        }

        // There are source sets for each platform-specific target. Our API is only the `common`
        // source set, so we intentionally don't generate docs for the other targets. Also,
        // building docs for those targets requires a working (cross-)compiler... which is hard. :)
        configureEach {
            suppress.set(name != "commonMain")
        }
    }
}

afterEvaluate {
    tasks.filterIsInstance<AbstractArchiveTask>().forEach {
        it.isPreserveFileTimestamps = false
        it.isReproducibleFileOrder = true
    }
}
