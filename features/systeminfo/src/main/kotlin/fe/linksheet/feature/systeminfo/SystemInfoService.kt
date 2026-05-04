@file:Suppress("FunctionName")

package fe.linksheet.feature.systeminfo

import app.linksheet.api.BuildConstants
import app.linksheet.api.BuildInfo
import app.linksheet.api.DeviceInfo
import app.linksheet.api.RefineWrapper
import app.linksheet.api.SystemInfoService
import app.linksheet.api.SystemProperties

fun RealSystemInfoService(refineWrapper: RefineWrapper, buildInfo: BuildInfo): SystemInfoService {
    return RealSystemInfoService(
        properties = RealSystemProperties(refineWrapper = refineWrapper),
        build = StaticBuildConstants,
        buildInfo = buildInfo
    )
}

class RealSystemInfoService(
    override val properties: SystemProperties,
    override val build: BuildConstants = InjectedBuildConstants(properties),
    override val buildInfo: BuildInfo
) : SystemInfoService {

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

    private val customRom by lazy {
        knownRoms.any { properties.get(it) != null }
    }

    override val deviceInfo by lazy {
        DeviceInfo(build.release, build.manufacturer, build.model)
    }

    override fun isCustomRom(): Boolean {
        return customRom
    }

    override fun getApplicationId(): String {
        return buildInfo.applicationId
    }
}
