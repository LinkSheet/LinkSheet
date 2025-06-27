package fe.linksheet.feature.systeminfo.device

import fe.linksheet.feature.systeminfo.SystemProperties

// https://github.com/pytorch/cpuinfo/blob/main/test/mock/xiaomi-mi-5c.h
// https://github.com/godotengine/godot/blob/master/platform/android/os_android.cpp
sealed class Device(
    val codename: String? = null,
    val fingerprint: String? = null,
    val buildProperties: Map<String, String>,
) : SystemProperties {

    override fun get(key: String): String? {
        return buildProperties[key]
    }

    override fun getAllProperties(): Map<String, String> {
        return buildProperties
    }
}

fun parseTestBuildProperties(buildProperties: String): Map<String, String> {
    val lines = buildProperties.split("\n")

    return buildMap {
        for (line in lines) {
            var mutLine = line.trim()
            if (mutLine.isBlank() || mutLine.startsWith("#") || !mutLine.contains("=")) continue

            val (name, value) = mutLine.split("=")
            put(name, value)
        }
    }
}


