package fe.linksheet.module.viewmodel.util

import android.os.Build
import fe.gson.dsl.jsonArray
import fe.gson.dsl.jsonObject
import fe.kotlin.extension.unixMillisAtUtc
import fe.kotlin.util.ISO8601DateTimeFormatOption
import fe.linksheet.BuildConfig
import fe.linksheet.module.log.Logger
import fe.linksheet.module.preference.AppPreferenceRepository
import fe.linksheet.module.preference.AppPreferences


class LogViewCommon(
    val preferenceRepository: AppPreferenceRepository,
    private val logger: Logger
) {
    fun logPreferences(redact: Boolean): Map<String, String?> {
        return AppPreferences.loggablePreferences.associate {
            it.key to preferenceRepository.getAnyAsString(it)
        } + AppPreferences.logPackages(redact, logger, preferenceRepository)
    }

    fun buildClipboardText(
        includeFingerprint: Boolean,
        includePreferences: Boolean,
        redactLog: Boolean,
    ): String {
        return preferenceRepository.gson.toJson(jsonObject {
            if (includeFingerprint) {
                "device_fingerprint" += jsonObject {
                    "fingerprint" += Build.FINGERPRINT
                    "manufacturer" += Build.MANUFACTURER
                    "model" += Build.MODEL
                }
            }

            "android_version" += Build.VERSION.RELEASE
            "full_identifier" += BuildConfig.VERSION_NAME
            "built_at" += BuildConfig.BUILT_AT.unixMillisAtUtc.value.format(
                ISO8601DateTimeFormatOption.DefaultFormat
            )
            "commit" += BuildConfig.COMMIT.substring(0, 7)
            "branch" += BuildConfig.BRANCH
            "flavor" += BuildConfig.FLAVOR
            "type" += BuildConfig.BUILD_TYPE

            if (BuildConfig.GITHUB_WORKFLOW_RUN_ID != null) {
                "workflow_id" += BuildConfig.GITHUB_WORKFLOW_RUN_ID
            }

            if (includePreferences) {
                "preferences" += jsonArray {
                    logPreferences(redactLog).forEach { (key, value) ->
                        +jsonObject {
                            "name" += key
                            "value" += value
                        }
                    }
                }
            }
        })
    }
}
