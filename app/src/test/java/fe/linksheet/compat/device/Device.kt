package fe.linksheet.compat.device

import fe.linksheet.module.systeminfo.SystemProperties

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


