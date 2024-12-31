package fe.linksheet.module.viewmodel

import android.app.Application
import android.content.Intent
import android.net.Uri
import com.google.gson.Gson
import fe.gson.dsl.jsonObject
import fe.linksheet.util.intent.buildIntent
import fe.linksheet.module.preference.SensitivePreference
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.preference.permission.PermissionBoundPreference
import fe.linksheet.module.viewmodel.base.BaseViewModel
import fe.linksheet.util.ImportExportService

class ExportSettingsViewModel(
    val context: Application,
    val preferenceRepository: AppPreferenceRepository,
    val gson: Gson,
) : BaseViewModel(preferenceRepository) {
    val importIntent = buildIntent(Intent.ACTION_OPEN_DOCUMENT) {
        addCategory(Intent.CATEGORY_OPENABLE)
        type = "application/json"
    }

    private val importExportService = ImportExportService(context)


    fun importPreferences(uri: Uri): Result<List<PermissionBoundPreference>> {
        val result = importExportService.importPreferencesFromUri(uri)
        if (result.isFailure) {
            return Result.failure(result.exceptionOrNull()!!)
        }

        return Result.success(preferenceRepository.importPreferences(result.getOrNull()!!))
    }

    @OptIn(SensitivePreference::class)
    fun exportPreferences(uri: Uri, includeLogHashKey: Boolean) {
        val set = AppPreferences.sensitivePreferences.toMutableSet()
        if (includeLogHashKey) {
            set.remove(AppPreferences.logKey)
        }

        val preferences = preferenceRepository.exportPreferences(set)

        val fileContent = jsonObject {
            "preferences" += AppPreferences.toJsonArray(preferences)
        }

        importExportService.exportPreferencesToUri(uri, gson.toJson(fileContent))
    }
}
