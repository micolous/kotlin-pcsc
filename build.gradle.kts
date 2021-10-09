import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJvmCompilation
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeCompilation

plugins {
    kotlin("multiplatform") version "1.5.31"
    id("org.jetbrains.dokka") version "1.5.30"
    id("maven-publish")
}

repositories {
    mavenCentral()
}

val coroutinesVer = "1.5.2"

dependencies {
    commonMainApi(kotlin("stdlib-common"))
    commonMainImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVer")
    commonTestImplementation(kotlin("test-common"))
    commonTestImplementation(kotlin("test-annotations-common"))
}

group = "au.id.micolous.kotlin.pcsc"
version = "0.0.1"

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

        jvm("jna").apply {
            compilations["main"].apply {
                dependencies {
                    api("net.java.dev.jna:jna:4.0.0")
                }
            }
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
