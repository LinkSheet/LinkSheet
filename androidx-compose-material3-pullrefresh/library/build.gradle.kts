@file:Suppress("UnstableApiUsage")

import java.util.Properties

val localProperties: Properties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (!file.exists()) file.createNewFile()
    load(file.inputStream())
}

fun localProperty(key: String): String =
    localProperties[key] as? String
        ?: error("Property [$key] not found in ${file("local.properties").absolutePath}.")

plugins {
    id("com.android.library")
    kotlin("android")
}

kotlin {
    jvmToolchain(11)
}

repositories {
    google()
    mavenCentral()
}

android {
    namespace = "me.omico.compose.material3.pullrefresh"
    compileSdk = 34
    defaultConfig {
        minSdk = 21
    }
    buildFeatures {
        compose = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    composeOptions {
        kotlinCompilerExtensionVersion = localProperty("project.compose.compiler.version")
    }
}

dependencies {
    compileOnly(platform("androidx.compose:compose-bom:${localProperty("project.compose.bom.version")}"))
    compileOnly("androidx.compose.foundation:foundation")
    compileOnly("androidx.compose.material3:material3")
    compileOnly("androidx.compose.ui:ui")
}
