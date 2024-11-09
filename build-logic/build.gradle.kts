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
    implementation("net.nemerosa.versioning:net.nemerosa.versioning.gradle.plugin:3.1.0")
    implementation("de.fayard.refreshVersions:refreshVersions:0.60.5")
    compileOnly("com.android.tools.build:gradle-api:8.4.2")
//    implementation("com.android.tools.build:gradle-api:8.4.0")
}

gradlePlugin {
    plugins.register("build-logic-plugin") {
        id = "build-logic-plugin"
        implementationClass = "fe.buildlogic.BuildLogicPlugin"
    }
}
