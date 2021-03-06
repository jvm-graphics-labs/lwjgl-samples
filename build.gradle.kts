/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Java Library project to get you started.
 * For more details take a look at the Java Libraries chapter in the Gradle
 * User Manual available at https://docs.gradle.org/5.2.1/userguide/java_library_plugin.html
 */

import org.gradle.internal.os.OperatingSystem.*

plugins {
    // Apply the java-library plugin to add support for Java Library
    `java-library`
    id("org.openjfx.javafxplugin") version "0.0.8"
}

repositories {
    // Use jcenter for resolving your dependencies.
    // You can declare any Maven/Ivy/file repository here.
    jcenter()
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://maven.onehippo.com/maven2/")
}

dependencies {

    lwjgl.run { testImplementation() }

    // https://mvnrepository.com/artifact/org.testng/testng
    testImplementation("org.testng:testng:7.1.0")

    // https://mvnrepository.com/artifact/org.openjdk.jmh/jmh-core
    testImplementation("org.openjdk.jmh:jmh-core:1.23")

    // https://mvnrepository.com/artifact/org.openjfx/javafx-base
    testImplementation("org.openjfx:javafx-base:11.0.2")

    testImplementation("org.joml:joml:1.9.19")

    // https://mvnrepository.com/artifact/javax.annotation/javax.annotation-api
//    implementation("javax.annotation:javax.annotation-api:1.2")
    // https://mvnrepository.com/artifact/javax.annotation/jsr305
    testImplementation("javax.annotation:jsr305:1.0")
}

javafx {
    modules("javafx.controls", "javafx.fxml")
}

object lwjgl {
    val name = "org.lwjgl:lwjgl"
    val version = "3.2.4-SNAPSHOT"
    val mod = listOf("", "-assimp", "-bgfx", "-bullet", "-cuda", "-driftfx", "-egl", "-glfw", "-jawt",
            "-jemalloc", "-libdivide", "-llvm", "-lmdb", "-lz4", "-meow", "-meshoptimizer", "-nanovg", "-nfd",
            "-nuklear", "-odbc", "-openal", "-opencl", "-opengl", "-opengles", "-openvr", "-opus", "-ovr", "-par",
            "-remotery", "-rpmalloc", "-shaderc", "-spvc", "-sse", "-stb", "-tinyexr", "-tinyfd", "-tootle", "-vma",
            "-vulkan", "-xxhash", "-yoga", "-zstd", "-opengl")
    val plain = listOf("-cuda", "-egl", "-jawt", "-odbc", "-opencl", "-vulkan")
    val platform = when (current()) {
        WINDOWS -> "windows"
        LINUX -> "linux"
        else -> "macos"
    }
    fun DependencyHandlerScope.testImplementation() {
        mod.forEach {
            val base = "$name$it:$version"
            testImplementation(base)
            if (it !in plain)
                runtimeOnly("$base:natives-$platform")
        }
    }
}
