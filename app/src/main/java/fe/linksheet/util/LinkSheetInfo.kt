package fe.linksheet.util

import android.os.Build
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import fe.std.javatime.extension.unixMillisUtc
import fe.std.javatime.time.ISO8601DateTimeFormatter
import fe.linksheet.BuildConfig

@Keep
data class BuildInfo(
    @SerializedName("version_name") val versionName: String,
    @SerializedName("version_code") val versionCode: Int,
    @SerializedName("built_at") val builtAt: String,
    @SerializedName("flavor") val flavor: String,
    @SerializedName("workflow_id") val workflowId: String? = null,
)

@Keep
data class DeviceInfo(
    @SerializedName("android_version") val androidVersion: String,
    val manufacturer: String,
    val model: String,
)

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
