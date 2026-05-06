package fe.linksheet.module.systeminfo

import app.linksheet.api.BuildInfo
import app.linksheet.api.SystemInfoService
import fe.linksheet.BuildConfig
import fe.linksheet.feature.systeminfo.RealSystemInfoService
import fe.std.javatime.extension.unixMillisUtc
import fe.std.javatime.time.ISO8601DateTimeFormatter
import org.koin.dsl.module

val SystemInfoServiceModule = module {
    single<SystemInfoService> {
        RealSystemInfoService(
            refineWrapper = get(),
            buildInfo = BuildInfo(
                versionName = BuildConfig.VERSION_NAME,
                versionCode = BuildConfig.VERSION_CODE,
                builtAt = BuildConfig.BUILT_AT.unixMillisUtc.format(ISO8601DateTimeFormatter.FriendlyFormat),
                flavor = "${BuildConfig.FLAVOR}-${BuildConfig.BUILD_TYPE}",
                workflowId = BuildConfig.GITHUB_WORKFLOW_RUN_ID,
                applicationId = BuildConfig.APPLICATION_ID
            )
        )
    }
}
