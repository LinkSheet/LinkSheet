package fe.linksheet.util

import android.content.Context
import android.os.Build
import fe.gson.dsl.jsonObject
import fe.gson.dsl.lazyJsonObject
import fe.kotlin.extension.primitive.unixMillisUtc
import fe.kotlin.time.ISO8601DateTimeFormatter
import fe.linksheet.BuildConfig
import fe.linksheet.extension.android.getCurrentLocale

object AppInfo {
    val appInfo by lazyJsonObject {
        "full_identifier" += BuildConfig.VERSION_NAME
        "built_at" += BuildConfig.BUILT_AT.unixMillisUtc.format(ISO8601DateTimeFormatter.DefaultFormat)
        "commit" += BuildConfig.COMMIT.substring(0, 7)
        "branch" += BuildConfig.BRANCH
        "flavor" += BuildConfig.FLAVOR
        "type" += BuildConfig.BUILD_TYPE

        BuildConfig.GITHUB_WORKFLOW_RUN_ID?.let { runId ->
            "workflow_id" += runId
        }
    }

    val deviceInfo by lazyJsonObject {
        "manufacturer" += Build.MANUFACTURER
        "model" += Build.MODEL
    }

    val androidFingerprint by lazyJsonObject {
        "fingerprint" += Build.FINGERPRINT
    }

    fun getDeviceBasics(context: Context) = jsonObject {
        "android_version" += Build.VERSION.RELEASE
        "locale" += context.getCurrentLocale().toLanguageTag()
    }
}
