package fe.linksheet.util

import android.os.Build
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import fe.std.javatime.extension.unixMillisUtc
import fe.std.javatime.time.ISO8601DateTimeFormatter
import fe.linksheet.BuildConfig
import fe.linksheet.module.build.BuildInfo
import fe.linksheet.module.build.DeviceInfo


object LinkSheetInfo {
    val buildInfo = BuildInfo(
        BuildConfig.VERSION_NAME,
        BuildConfig.VERSION_CODE,
        BuildConfig.BUILT_AT.unixMillisUtc.format(ISO8601DateTimeFormatter.DefaultFormat),
        "${BuildConfig.FLAVOR}-${BuildConfig.BUILD_TYPE}",
        BuildConfig.GITHUB_WORKFLOW_RUN_ID
    )

    val deviceInfo = DeviceInfo(Build.VERSION.RELEASE, Build.MANUFACTURER, Build.MODEL)
}
