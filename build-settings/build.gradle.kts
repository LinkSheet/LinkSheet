plugins {
    kotlin("jvm")
    java
    `java-gradle-plugin`
}

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
    maven(url = "https://plugins.gradle.org/m2")
    gradlePluginPortal()
    google()
}

kotlin {
    jvmToolchain(21)
}

dependencies {
//    compileOnly("net.nemerosa.versioning:net.nemerosa.versioning.gradle.plugin:_")
//    compileOnly("de.fayard.refreshVersions:refreshVersions:_")
//    compileOnly("com.android.tools.build:gradle-api:_")
//    implementation("com.android.tools.build:gradle-api:_")
}

gradlePlugin {
    plugins.register("build-settings-plugin") {
        id = "build-settings-plugin"
        implementationClass = "fe.buildsettings.BuildSettingsPlugin"
    }
}
