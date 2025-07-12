package fe.linksheet.module.systeminfo

import fe.linksheet.BuildConfig
import fe.linksheet.feature.systeminfo.BuildInfo
import fe.linksheet.feature.systeminfo.RealSystemProperties
import fe.linksheet.feature.systeminfo.StaticBuildConstants
import fe.linksheet.feature.systeminfo.SystemInfoService
import fe.std.javatime.extension.unixMillisUtc
import fe.std.javatime.time.ISO8601DateTimeFormatter
import org.koin.dsl.module


val SystemInfoServiceModule = module {
    single {
        val buildInfo = BuildInfo(
            BuildConfig.VERSION_NAME,
            BuildConfig.VERSION_CODE,
            BuildConfig.BUILT_AT.unixMillisUtc.format(ISO8601DateTimeFormatter.FriendlyFormat),
            "${BuildConfig.FLAVOR}-${BuildConfig.BUILD_TYPE}",
            BuildConfig.GITHUB_WORKFLOW_RUN_ID
        )
        SystemInfoService(
            properties = RealSystemProperties,
            build = StaticBuildConstants,
            buildInfo = buildInfo
        )
    }
}
