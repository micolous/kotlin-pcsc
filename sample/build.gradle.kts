import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget

plugins {
    kotlin("multiplatform")
    id("com.gradleup.shadow") version "9.2.2"
}

repositories {
    mavenCentral()
}

dependencies {
    commonMainImplementation(kotlin("stdlib-common"))
    commonMainImplementation(rootProject)
}

kotlin {
    linuxX64()
    macosX64()
    mingwX64()
    jvm {
        mainRun {
            mainClass = "SampleKt"
        }
    }

    applyDefaultHierarchyTemplate()

    targets.filterIsInstance<KotlinNativeTarget>().forEach {
        it.binaries {
            executable("pcsc_sample")
        }
    }

    targets.filterIsInstance<KotlinJvmTarget>().forEach {
        it.compilations["main"].apply {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
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
