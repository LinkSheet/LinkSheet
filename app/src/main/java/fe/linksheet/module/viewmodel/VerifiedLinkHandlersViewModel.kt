package fe.linksheet.module.viewmodel

import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewModelScope
import app.linksheet.api.preference.AppPreferenceRepository
import app.linksheet.feature.app.applist.AppListModel
import app.linksheet.feature.app.core.IAppInfo
import app.linksheet.feature.app.usecase.DomainVerificationUseCase
import app.linksheet.feature.devicecompat.oneui.OneUiCompat
import fe.kotlin.extension.iterable.groupByNoNullKeys
import fe.linksheet.composable.dialog.HostState
import fe.linksheet.extension.kotlin.ProduceSideEffect
import fe.linksheet.extension.kotlin.mapProducingSideEffects
import fe.linksheet.module.database.entity.PreferredApp
import fe.linksheet.module.preference.experiment.ExperimentRepository
import fe.linksheet.module.preference.experiment.Experiments
import fe.linksheet.module.repository.PreferredAppRepository
import fe.linksheet.module.viewmodel.base.BaseViewModel
import fe.linksheet.module.viewmodel.common.handler.LinkHandlerCommon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

class VerifiedLinkHandlersViewModel(
    preferenceRepository: AppPreferenceRepository,
    experimentRepository: ExperimentRepository,
    private val preferredAppRepository: PreferredAppRepository,
    private val useCase: DomainVerificationUseCase,
    private val intentCompat: OneUiCompat,
) : BaseViewModel(preferenceRepository) {
    val newVlh = experimentRepository.asViewModelState(Experiments.newVlh)

    private fun groupHosts(
        preferredApps: List<PreferredApp>,
        sideEffect: ProduceSideEffect<String>,
    ): Map<String, Collection<String>> {
        return preferredApps.groupByNoNullKeys(
            keySelector = { preferredApp ->
                preferredApp.pkg

//                with(packageInfoService) {
//                    getLauncherOrNull(preferredApp.pkg)?.let { toAppInfo(it, false) }
//                }
            },
            nullKeyHandler = { app -> app.pkg?.let { sideEffect(it) } },
            cacheIndexSelector = { it.pkg },
            valueTransform = { it.host }
        )
    }

    val preferredApps = preferredAppRepository.getAllAlwaysPreferred()
        .mapProducingSideEffects(
            sideEffectContext = Dispatchers.IO,
            transform = ::groupHosts,
            handleSideEffects = { packageNames ->
                preferredAppRepository.deleteByPackageNames(
                    packageNames.toSet()
                )
            }
        )
        .shareIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            replay = 1
        )

    val appListModel by lazy { AppListModel(queryApps = useCase::getDomainVerificationAppInfoList, scope = viewModelScope) }
    private val handler by lazy {
        LinkHandlerCommon(
            preferredAppRepository = preferredAppRepository,
        )
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun makeOpenByDefaultSettingsIntent(packageName: String): Intent {
        return intentCompat.createAppOpenByDefaultSettingsIntent(packageName)
    }

    fun updateHostState(info: IAppInfo, hostStates: List<HostState>) {
        viewModelScope.launch {
            handler.updateHostState(info, hostStates)
        }
    }
//
//    fun <T> postShizukuCommand(delay: Long, command: IShizukuService.() -> T) {
//        val cmd = ShizukuCommand(command) {
//            viewModelScope.launch {
//                delay(delay)
//                emitLatest()
//            }
//        }
//
//        shizukuHandler.enqueueCommand(cmd)
//    }
}


