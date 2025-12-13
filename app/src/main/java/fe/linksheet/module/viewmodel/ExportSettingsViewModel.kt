@file:OptIn(ExperimentalTime::class)

package fe.linksheet.module.viewmodel

import android.app.Application
import android.content.Intent
import android.net.Uri
import com.google.gson.Gson
import fe.gson.dsl.jsonObject
import fe.linksheet.module.preference.SensitivePreference
import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.preference.app.DefaultAppPreferenceRepository
import fe.linksheet.module.preference.permission.PermissionBoundPreference
import fe.linksheet.module.viewmodel.base.BaseViewModel
import fe.linksheet.util.ImportExportService
import fe.std.result.IResult
import fe.std.result.StdResult
import fe.std.result.isFailure
import fe.std.result.unaryPlus
import java.time.ZoneId
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class ExportSettingsViewModel(
    val context: Application,
    val preferenceRepository: DefaultAppPreferenceRepository,
    val gson: Gson,
    clock: Clock,
    zoneId: ZoneId,
) : BaseViewModel(preferenceRepository) {

    private val importExportService = ImportExportService(context, clock, zoneId)

    fun createImportIntent(): Intent {
        return ImportExportService.ImportIntent
    }

    fun createExportIntent(): Intent {
        return importExportService.createExportIntent()
    }

    suspend fun importPreferences(uri: Uri): StdResult<List<PermissionBoundPreference>> {
        val result = importExportService.importPreferencesFromUri(uri)
        if (result.isFailure()) {
            return +result
        }

        val value = result.value
        return +preferenceRepository.importPreferences(value)
    }

    @OptIn(SensitivePreference::class)
    suspend fun exportPreferences(uri: Uri, includeLogHashKey: Boolean): IResult<Unit> {
        val set = AppPreferences.sensitivePreferences.toMutableSet()
        if (includeLogHashKey) {
        }

        val preferences = preferenceRepository.exportPreferences(set)

        val fileContent = jsonObject {
            "preferences" += AppPreferences.toJsonArray(preferences)
        }

        return importExportService.exportPreferencesToUri(uri, gson.toJson(fileContent))
    }
}
