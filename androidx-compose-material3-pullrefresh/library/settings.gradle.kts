rootProject.name = "material3-pullrefresh"

pluginManagement {
    val localProperties = java.util.Properties().apply {
        val file = file("local.properties")
        if (!file.exists()) file.createNewFile()
        load(file.inputStream())
    }

    fun localProperty(key: String): String =
        localProperties[key] as? String
             ?: error("Property [$key] not found in ${file("local.properties").absolutePath}.")

    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        id("com.android.application") version localProperty("project.android.gradle.plugin.version")
        id("com.android.library") version localProperty("project.android.gradle.plugin.version")
        kotlin("android") version localProperty("project.kotlin.version")
    }
}
