import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget

plugins {
    kotlin("multiplatform")
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
    jvm("jna")

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

    sourceSets.all {
        languageSettings.optIn("kotlin.ExperimentalUnsignedTypes")
    }
}

afterEvaluate {
    val jnaFatJar by tasks.creating(Jar::class) {
        dependsOn("jnaJar")
        group = "jar"
        manifest.attributes["Main-Class"] = "SampleKt"
        val deps = configurations["jnaRuntimeClasspath"].filter {
            it.name.endsWith(".jar") } +
                project.tasks["jnaJar"].outputs.files
        deps.forEach { from(zipTree(it)) }
        exclude(
            // Added by JDK9, and exists in multiple dependency JARs (which conflict).
            // Fat executable JARs don't need modules support anyway.
            "META-INF/versions/9/module-info.class",
        )
    }

    tasks.filterIsInstance<AbstractArchiveTask>().forEach {
        it.isPreserveFileTimestamps = false
        it.isReproducibleFileOrder = true
    }
}
