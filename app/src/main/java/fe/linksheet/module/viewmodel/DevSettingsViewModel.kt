package fe.linksheet.module.viewmodel


import android.app.Application
import androidx.lifecycle.viewModelScope
import app.linksheet.api.SystemInfoService
import app.linksheet.api.preference.AppPreferenceRepository
import app.linksheet.feature.devicecompat.miui.MiuiAuditor
import app.linksheet.feature.devicecompat.miui.MiuiCompatProvider
import app.linksheet.feature.shizuku.service.ShizukuFeatureService
import app.linksheet.feature.shizuku.service.ShizukuService
import app.linksheet.feature.shizuku.usecase.ShizukuStatusUseCase
import com.google.gson.Gson
import fe.composekit.core.AndroidVersion
import fe.linksheet.module.log.file.LogPersistService
import fe.linksheet.module.preference.experiment.ExperimentRepository
import fe.linksheet.module.preference.experiment.Experiments
import fe.linksheet.module.viewmodel.base.BaseViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DevSettingsViewModel(
    private val context: Application,
    preferenceRepository: AppPreferenceRepository,
    experimentRepository: ExperimentRepository,
    private val shizukuService: ShizukuService,
    private val shizukuFeatureService: ShizukuFeatureService,
    miuiCompatProvider: MiuiCompatProvider,
    private val gson: Gson,
    systemInfoService: SystemInfoService,
    private val logPersistService: LogPersistService,
    private val ioDispatcher: CoroutineDispatcher,
) : BaseViewModel(preferenceRepository) {
    val disableLogging = experimentRepository.asViewModelState(Experiments.disableLogging)
    private val auditor = MiuiAuditor(systemInfoService)
    val statusUseCase = ShizukuStatusUseCase(shizukuService = shizukuService)

    val miuiCompatRequired by miuiCompatProvider.isRequired

    fun enqueueResetAppLinks(resultHandler: (Int) -> Unit) {
        if (!AndroidVersion.isAtLeastApi31S()) return
        viewModelScope.launch {
            shizukuFeatureService.reset(resultHandler)
        }
    }

    fun auditMiuiEnvironment(): String {
        val audit = auditor.audit(context)
        return gson.toJson(audit)
    }

    suspend fun deleteAllLogs() = withContext(ioDispatcher) {
        logPersistService.getLogSessions().count { logPersistService.delete(it.id) }
    }
}
