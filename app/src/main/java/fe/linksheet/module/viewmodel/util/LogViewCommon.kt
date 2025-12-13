package fe.linksheet.module.viewmodel.util

import android.content.Context
import android.os.Parcelable
import com.google.gson.Gson
import fe.gson.dsl.jsonObject
import fe.linksheet.feature.systeminfo.SystemInfoService
import fe.linksheet.module.log.file.entry.LogEntry
import fe.linksheet.module.paste.PasteService
import fe.linksheet.module.preference.SensitivePreference
import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.preference.app.DefaultAppPreferenceRepository
import fe.linksheet.module.preference.experiment.ExperimentRepository
import fe.linksheet.module.preference.experiment.Experiments
import fe.linksheet.util.buildconfig.LinkSheetInfo
import fe.linksheet.util.extension.android.getCurrentLanguageTag
import kotlinx.parcelize.Parcelize


class LogViewCommon(
    val preferenceRepository: DefaultAppPreferenceRepository,
    private val experimentRepository: ExperimentRepository,
    private val pasteService: PasteService<*>,
    val gson: Gson,
    private val systemInfoService: SystemInfoService,
) {
    @OptIn(SensitivePreference::class)
    private fun logPreferences(redact: Boolean): Map<String, String?> {
        val preferences = preferenceRepository.exportPreferences(AppPreferences.sensitivePreferences)

        return preferences
    }

    @Parcelize
    data class ExportSettings(
        val fingerprint: Boolean,
        val preferences: Boolean,
        val redact: Boolean,
        val throwable: Boolean,
    ) : Parcelable

    fun createPaste(text: String): String? {
        val paste = pasteService.createPaste(text)
        return paste.getOrNull()?.url
    }

    fun buildExportText(
        context: Context,
        settings: ExportSettings,
        logEntries: List<LogEntry>,
    ): String {
        val (fingerprint, preferences, redact, throwable) = settings

        return gson.toJson(jsonObject {
            "build_info" += LinkSheetInfo.buildInfo
            "device_info" += systemInfoService.deviceInfo

            "locale" += context.getCurrentLanguageTag()

            if (fingerprint) {
                "fingerprint" += systemInfoService.build.fingerprint
            }

            "active_experiments" += Experiments.getActive(experimentRepository)
            if (preferences) {
                "preferences" += AppPreferences.toJsonArray(logPreferences(redact))
            }

            "log" += logEntries.map { it.toCopyLogJson(redact, throwable) }
        })
    }
}
