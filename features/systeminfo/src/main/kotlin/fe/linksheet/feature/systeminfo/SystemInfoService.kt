package fe.linksheet.feature.systeminfo

import app.linksheet.api.SystemProperties

class SystemInfoService(
    val properties: SystemProperties = RealSystemProperties,
    val build: BuildConstants = InjectedBuildConstants(properties),
    val buildInfo: BuildInfo
) {
    // Via https://github.com/godotengine/godot/blob/master/platform/android/os_android.cpp#L297
    private val knownRoms = setOf(
        "ro.havoc.version",
        "org.pex.version",
        "org.pixelexperience.version",
        "ro.potato.version",
        "ro.xtended.version",
        "org.evolution.version",
        "ro.corvus.version",
        "ro.pa.version",
        "ro.crdroid.version",
        "ro.syberia.version",
        "ro.arrow.version",
        "ro.droidx.version",
        "ro.lineage.version",
        "ro.modversion"
    )

    val isCustomRom by lazy {
        knownRoms.any { properties.get(it) != null }
    }

    val deviceInfo by lazy {
        DeviceInfo(build.release, build.manufacturer, build.model)
    }
}
