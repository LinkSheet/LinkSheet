package fe.linksheet.module.viewmodel.util

import android.content.Context
import android.os.Parcelable
import com.akuleshov7.ktoml.Toml
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
import fe.linksheet.util.ExportImportData
import fe.linksheet.util.ExportImportUseCase
import fe.linksheet.util.ExportImportUseCase.Format
import fe.linksheet.util.extension.android.getCurrentLanguageTag
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.encodeToString


class LogViewCommon(
    val preferenceRepository: DefaultAppPreferenceRepository,
    private val experimentRepository: ExperimentRepository,
    private val pasteService: PasteService<*>,
    val gson: Gson,
    val toml: Toml,
    private val systemInfoService: SystemInfoService,
    private val useCase: ExportImportUseCase,
) {
    @OptIn(SensitivePreference::class)
    private fun logPreferences(redact: Boolean): Map<String, String> {
        val preferences = useCase.export(!redact)
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
        format: Format,
        settings: ExportSettings,
        logEntries: List<LogEntry>,
    ): String {
        val (fingerprint, preferences, redact, throwable) = settings
        return when (format) {
            Format.Toml -> toml.encodeToString(
                ExportImportData(
                    buildInfo = systemInfoService.buildInfo,
                    deviceInfo = systemInfoService.deviceInfo,
                    locale = context.getCurrentLanguageTag(),
                    fingerprint = if (fingerprint) systemInfoService.build.fingerprint else null,
                    activeExperiments = Experiments.getActive(experimentRepository),
                    preferences = if (preferences) useCase.export(!redact) else null,
                    log = logEntries.map { it.toSerializable(redact, throwable) }
                )
            )
            Format.Json -> gson.toJson(jsonObject {
                "build_info" += systemInfoService.buildInfo
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
}
