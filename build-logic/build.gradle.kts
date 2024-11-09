plugins {
    kotlin("jvm") version "2.0.20"
    java
    `java-gradle-plugin`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation("de.fayard.refreshVersions:refreshVersions:0.60.5")
}

gradlePlugin {
    plugins.register("build-logic-plugin") {
        id = "build-logic-plugin"
        implementationClass = "fe.buildsrc.BuildLogicPlugin"
    }
}
