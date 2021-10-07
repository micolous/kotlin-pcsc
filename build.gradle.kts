import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJvmCompilation
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeCompilation

plugins {
    kotlin("multiplatform") version "1.3.41"
    id("org.jetbrains.dokka") version "0.10.0"
    id("maven-publish")
}

repositories {
    mavenCentral()
    mavenCentral()
}

val coroutinesVer = "1.3.0"

dependencies {
    commonMainApi(kotlin("stdlib-common"))
    commonMainApi("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVer")
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
                                dependencies {
                                    api("org.jetbrains.kotlinx:kotlinx-coroutines-core-native:$coroutinesVer")
                                }

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

afterEvaluate {
    tasks.filterIsInstance<AbstractArchiveTask>().forEach {
        it.isPreserveFileTimestamps = false
        it.isReproducibleFileOrder = true
    }
}
