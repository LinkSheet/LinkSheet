package fe.linksheet.module.viewmodel.util

import android.content.Context
import android.os.Build
import com.google.gson.Gson
import fe.gson.dsl.jsonObject
import fe.gson.extension.json.`object`.plus
import fe.linksheet.extension.android.getCurrentLanguageTag
import fe.linksheet.module.log.file.entry.LogEntry
import fe.linksheet.module.redactor.Redactor
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.preference.SensitivePreference
import fe.linksheet.module.preference.experiment.ExperimentRepository
import fe.linksheet.module.preference.experiment.Experiments
import fe.linksheet.util.AppInfo
import fe.linksheet.util.BuildInfo


class LogViewCommon(
    val preferenceRepository: AppPreferenceRepository,
    val experimentRepository: ExperimentRepository,
    val gson: Gson,
    private val redactor: Redactor,
) {
    @OptIn(SensitivePreference::class)
    private fun logPreferences(redact: Boolean): Map<String, String?> {
        val preferences = preferenceRepository.exportPreferences(AppPreferences.sensitivePreferences)
        val packages = AppPreferences.logPackages(if (redact) redactor else Redactor.NoOp, preferenceRepository)

        return preferences + packages
    }

    data class ExportSettings(
        val fingerprint: Boolean,
        val preferences: Boolean,
        val redact: Boolean,
        val throwable: Boolean,
    )

    fun buildClipboardText(
        context: Context,
        settings: ExportSettings,
        logEntries: List<LogEntry>,
    ): String {
        val (fingerprint, preferences, redact, throwable) = settings

        return gson.toJson(jsonObject {
            "build_info" += AppInfo.buildInfo
            "device_info" += AppInfo.deviceInfo

            "locale" += context.getCurrentLanguageTag()

            if (fingerprint) {
                "fingerprint" += Build.FINGERPRINT
            }

            "active_experiments" += Experiments.getActive(experimentRepository)
            if (preferences) {
                "preferences" += AppPreferences.toJsonArray(logPreferences(redact))
            }

            "log" += logEntries.map { it.toCopyLogJson(redact, throwable) }
        })
    }
}
