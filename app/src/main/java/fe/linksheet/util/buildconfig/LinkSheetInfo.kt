package fe.linksheet.util.buildconfig

import fe.linksheet.BuildConfig
import fe.linksheet.module.systeminfo.BuildInfo
import fe.std.javatime.extension.unixMillisUtc
import fe.std.javatime.time.ISO8601DateTimeFormatter


object LinkSheetInfo {
    @Deprecated("Switch to SystemInfoService#buildInfo")
    val buildInfo = BuildInfo(
        BuildConfig.VERSION_NAME,
        BuildConfig.VERSION_CODE,
        BuildConfig.BUILT_AT.unixMillisUtc.format(ISO8601DateTimeFormatter.FriendlyFormat),
        "${BuildConfig.FLAVOR}-${BuildConfig.BUILD_TYPE}",
        BuildConfig.GITHUB_WORKFLOW_RUN_ID
    )
}
