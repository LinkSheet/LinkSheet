package fe.linksheet.module.viewmodel.util

import android.content.Context
import com.google.gson.Gson
import fe.gson.dsl.jsonObject
import fe.gson.extension.json.`object`.plus
import fe.linksheet.module.log.entry.LogEntry
import fe.linksheet.module.log.Logger
import fe.linksheet.module.preference.AppPreferenceRepository
import fe.linksheet.module.preference.AppPreferences
import fe.linksheet.util.AppInformation


class LogViewCommon(
    val preferenceRepository: AppPreferenceRepository,
    val gson: Gson,
    private val logger: Logger
) {
    private fun logPreferences(redact: Boolean): Map<String, String?> {
        val preferences = preferenceRepository.exportPreferences(AppPreferences.sensitivePreferences)
        val packages = AppPreferences.logPackages(redact, logger, preferenceRepository)

        return preferences + packages
    }

    data class ExportSettings(
        val fingerprint: Boolean,
        val preferences: Boolean,
        val redact: Boolean,
        val throwable: Boolean
    )

    fun buildClipboardText(
        context: Context,
        settings: ExportSettings,
        logEntries: List<LogEntry>,
    ): String {
        val (fingerprint, preferences, redact, throwable) = settings
        return gson.toJson(jsonObject {
            "device_basics" += AppInformation.getDeviceBasics(context)
            if (fingerprint) {
                "device_info" += (AppInformation.deviceInfo + AppInformation.androidFingerprint)
            }

            "app_info" += AppInformation.appInfo
            if (preferences) {
                "preferences" += AppPreferences.toJsonArray(logPreferences(redact))
            }

            "log" += logEntries.map { it.toCopyLogJson(redact, throwable) }
        })
    }
}
