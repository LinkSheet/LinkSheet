package fe.linksheet.module.viewmodel

import android.app.Application
import android.net.Uri
import com.google.gson.Gson
import fe.gson.dsl.jsonObject
import fe.linksheet.module.preference.AppPreferenceRepository
import fe.linksheet.module.preference.AppPreferences

import fe.linksheet.module.viewmodel.base.BaseViewModel
import fe.linksheet.util.ImportExportService

class ExportSettingsViewModel(
    val context: Application,
    val preferenceRepository: AppPreferenceRepository,
    val gson: Gson
) : BaseViewModel(preferenceRepository) {
    private val importExportService = ImportExportService(context)
    fun importPreferences(uri: Uri): String? {
        val result = importExportService.importPreferencesFromUri(uri)
        if (result.isFailure) {
            return result.exceptionOrNull()!!.message
        }

        preferenceRepository.importPreferences(result.getOrNull()!!)
        return null
    }

    fun exportPreferences(uri: Uri, includeLogHashKey: Boolean) {
        val preferences = preferenceRepository.dumpPreferences(
            if (!includeLogHashKey) listOf(AppPreferences.logKey) else listOf()
        )

        val fileContent = jsonObject {
            "preferences" += AppPreferences.toJsonArray(preferences)
        }

        importExportService.exportPreferencesToUri(uri, gson.toJson(fileContent))
    }
}