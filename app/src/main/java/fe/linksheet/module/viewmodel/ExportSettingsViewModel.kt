@file:OptIn(ExperimentalTime::class)

package fe.linksheet.module.viewmodel

import android.app.Application
import android.content.Intent
import android.net.Uri
import com.google.gson.Gson
import fe.linksheet.extension.android.bufferedSink
import fe.linksheet.extension.android.bufferedSource
import app.linksheet.api.SensitivePreference
import fe.linksheet.module.preference.app.DefaultAppPreferenceRepository
import fe.linksheet.module.preference.permission.PermissionBoundPreference
import fe.linksheet.module.viewmodel.base.BaseViewModel
import fe.linksheet.util.ExportImportUseCase
import fe.linksheet.util.ImportExportService
import fe.std.result.Failure
import fe.std.result.IResult
import fe.std.result.StdResult
import fe.std.result.Success
import fe.std.result.isFailure
import fe.std.result.tryCatch
import fe.std.result.unaryPlus
import java.time.ZoneId
import kotlin.contracts.ExperimentalContracts
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class ExportSettingsViewModel(
    val context: Application,
    val preferenceRepository: DefaultAppPreferenceRepository,
    val gson: Gson,
    clock: Clock,
    zoneId: ZoneId,
    private val useCase: ExportImportUseCase
) : BaseViewModel(preferenceRepository) {

    private val importExportService = ImportExportService(context, clock, zoneId)

    fun createImportIntent(): Intent {
        return importExportService.createImportIntent(ExportImportUseCase.Format.Json)
    }

    fun createExportIntent(): Intent {
        return importExportService.createExportIntent(ExportImportUseCase.Format.Json)
    }

    fun importPreferences(uri: Uri): StdResult<List<PermissionBoundPreference>> {
        val result = tryCatch {
            importExportService.openDescriptor(uri, "r")?.bufferedSource()?.let {
                useCase.import(ExportImportUseCase.Format.Json, it)
            }
        }.unpack()
        if (result.isFailure()) {
            return +result
        }

        val value = result.value
        val mappedPreferences = useCase.importPreferences(value)
        return +preferenceRepository.refreshPostImport(mappedPreferences)
    }

    @OptIn(SensitivePreference::class)
    fun exportPreferences(uri: Uri, includeLogHashKey: Boolean): IResult<Unit> {
        val preferences = useCase.exportToString(ExportImportUseCase.Format.Json, includeLogHashKey)
        return tryCatch {
            importExportService.openDescriptor(uri, "w")?.bufferedSink()?.use {
                it.writeUtf8(preferences)
            }
        }
    }
}

@OptIn(ExperimentalContracts::class)
public fun <T> IResult<IResult<T>?>.unpack(): IResult<T> {
//    contract {
//        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
//    }
    when (this) {
        is Success<*> -> {
            @Suppress("UNCHECKED_CAST")
            return this.value as Success<T>
        }
        is Failure<*> -> {
            @Suppress("UNCHECKED_CAST")
            return this as Failure<T>
        }
    }
}
