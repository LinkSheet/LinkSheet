package fe.linksheet.module.viewmodel.util

import android.content.Context
import com.google.gson.Gson
import fe.gson.dsl.jsonObject
import fe.gson.extension.json.`object`.plus
import fe.linksheet.module.log.entry.LogEntry
import fe.linksheet.module.log.Logger
import fe.linksheet.module.preference.AppPreferenceRepository
import fe.linksheet.module.preference.AppPreferences
import fe.linksheet.util.LinkSheetAppInfo


class LogViewCommon(
    val preferenceRepository: AppPreferenceRepository,
    val gson: Gson,
    private val logger: Logger
) {
    private fun logPreferences(redact: Boolean): Map<String, String?> {
        return preferenceRepository.dumpPreferences(AppPreferences.sensitivePreferences) + AppPreferences.logPackages(
            redact,
            logger,
            preferenceRepository
        )
    }

    fun buildClipboardText(
        context: Context,
        includeFingerprint: Boolean,
        includePreferences: Boolean,
        redactLog: Boolean,
        includeThrowable: Boolean,
        logEntries: List<LogEntry>,
    ): String {
        return gson.toJson(jsonObject {
            "device_basics" += LinkSheetAppInfo.getDeviceBasics(context)

            if (includeFingerprint) {
                "device_info" += (LinkSheetAppInfo.deviceInfo + LinkSheetAppInfo.androidFingerprint)
            }

            "app_info" += LinkSheetAppInfo.appInfo

            if (includePreferences) {
                "preferences" += AppPreferences.toJsonArray(logPreferences(redactLog))
            }

            "log" += logEntries.map { it.toCopyLogJson(redactLog, includeThrowable) }
        })
    }
}
