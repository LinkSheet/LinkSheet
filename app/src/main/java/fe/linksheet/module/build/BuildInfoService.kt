package fe.linksheet.module.build

import fe.linksheet.BuildConfig
import fe.std.javatime.extension.unixMillisUtc
import fe.std.javatime.time.ISO8601DateTimeFormatter
import org.koin.dsl.module

val BuildInfoServiceModule = module {
    single { BuildInfoService(RealSystemProperties, RealBuildConstants) }
}

class BuildInfoService(
    val properties: SystemProperties = RealSystemProperties,
    val buildConstants: BuildConstants = RealBuildConstants,
) {
    val deviceInfo by lazy {
        DeviceInfo(buildConstants.release, buildConstants.manufacturer, buildConstants.model)
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
