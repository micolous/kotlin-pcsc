import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJvmCompilation
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeCompilation

plugins {
    kotlin("multiplatform") version "2.0.0"
    id("org.jetbrains.dokka") version "2.0.0"
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
version = "0.0.2"

kotlin {
    // linuxArm32Hfp()  // Raspberry Pi
    linuxX64 {
        compilations.getByName("main") {
            cinterops {
                val winscard by creating
            }

            kotlinOptions {
                // Workaround https://youtrack.jetbrains.com/issue/KT-65217/
                freeCompilerArgs += listOf("-linker-option", "--allow-shlib-undefined")
            }
        }

        compilations.getByName("test") {
            kotlinOptions {
                // Workaround https://youtrack.jetbrains.com/issue/KT-65217/
                freeCompilerArgs += listOf("-linker-option", "--allow-shlib-undefined")
            }
        }
    }

    macosArm64 {
        // macOS on Apple Silicon (no cross-OS compiler)
        compilations.getByName("main") {
            cinterops {
                val winscard by creating
            }
        }
    }

    macosX64 {
        // macOS on Intel (no cross-OS compiler)
        compilations.getByName("main") {
            cinterops {
                val winscard by creating
            }
        }
    }

    mingwX64 {
        // Windows (no cross compiler)
        compilations.getByName("main") {
            cinterops {
                val winscard by creating
            }
        }
    }

    jvm()

    sourceSets {
        jvmMain.dependencies {
            api("net.java.dev.jna:jna:5.9.0")
        }

        jvmTest.dependencies {
            implementation(kotlin("test-junit"))
        }
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
