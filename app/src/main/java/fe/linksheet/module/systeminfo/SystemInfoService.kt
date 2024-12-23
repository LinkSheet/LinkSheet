package fe.linksheet.module.systeminfo

import fe.linksheet.BuildConfig
import fe.std.javatime.extension.unixMillisUtc
import fe.std.javatime.time.ISO8601DateTimeFormatter
import org.koin.dsl.module

val SystemInfoServiceModule = module {
    single { SystemInfoService(RealSystemProperties, StaticBuildConstants) }
}

class SystemInfoService(
    val properties: SystemProperties = RealSystemProperties,
    val build: BuildConstants = InjectedBuildConstants(properties),
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

    val buildInfo by lazy {
        BuildInfo(
            BuildConfig.VERSION_NAME,
            BuildConfig.VERSION_CODE,
            BuildConfig.BUILT_AT.unixMillisUtc.format(ISO8601DateTimeFormatter.DefaultFormat),
            "${BuildConfig.FLAVOR}-${BuildConfig.BUILD_TYPE}",
            BuildConfig.GITHUB_WORKFLOW_RUN_ID
        )
    }
}
