import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
}

repositories {
    mavenCentral()
}

dependencies {
    commonMainApi(kotlin("stdlib-common"))
    commonMainApi(rootProject)
    commonTestImplementation(kotlin("test-common"))
    commonTestImplementation(kotlin("test-annotations-common"))
}

kotlin {
    macosX64()

    targets.filterIsInstance<KotlinNativeTarget>().forEach {
        it.binaries {
            executable("pcsc_sample") { }
        }
    }
}
