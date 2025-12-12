package fe.linksheet.module.viewmodel

import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewModelScope
import app.linksheet.feature.app.applist.AppListCommon
import app.linksheet.feature.app.usecase.DomainVerificationUseCase
import dev.zwander.shared.IShizukuService
import fe.kotlin.extension.iterable.groupByNoNullKeys
import fe.linksheet.extension.kotlin.ProduceSideEffect
import fe.linksheet.extension.kotlin.mapProducingSideEffects
import fe.linksheet.module.database.entity.PreferredApp
import fe.linksheet.module.devicecompat.oneui.OneUiCompat
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.experiment.ExperimentRepository
import fe.linksheet.module.preference.experiment.Experiments
import fe.linksheet.module.repository.PreferredAppRepository
import fe.linksheet.module.shizuku.ShizukuCommand
import fe.linksheet.module.shizuku.ShizukuServiceConnection
import fe.linksheet.module.viewmodel.base.BaseViewModel
import fe.linksheet.module.viewmodel.common.handler.LinkHandlerCommon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

class VerifiedLinkHandlersViewModel(
    private val shizukuHandler: ShizukuServiceConnection,
    preferenceRepository: AppPreferenceRepository,
    experimentRepository: ExperimentRepository,
    private val preferredAppRepository: PreferredAppRepository,
    private val useCase: DomainVerificationUseCase,
    private val intentCompat: OneUiCompat,
) : BaseViewModel(preferenceRepository) {
    val newVlh = experimentRepository.asViewModelState(Experiments.newVlh)

    val lastEmitted = MutableStateFlow(0L)

    val filterDisabledOnly = MutableStateFlow(true)

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
            handleSideEffects = { packageNames -> preferredAppRepository.deleteByPackageNames(packageNames.toSet()) }
        )
        .shareIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            replay = 1
        )

    //    private fun test(): Flow<List<DomainVerificationAppInfo>> {
//        return flowOfLazy {
//            packageInfoService.getDomainVerificationAppInfos()
//        }


    //        val appsFiltered = packageInfoService.getDomainVerificationAppInfoFlow()
//        .scan(emptyList<DomainVerificationAppInfo>()) { acc, elem -> acc + elem }

    val list by lazy { AppListCommon(apps = useCase.getDomainVerificationAppInfoListFlow(), scope = viewModelScope) }
    val handler by lazy { LinkHandlerCommon(preferredAppRepository = preferredAppRepository, scope = viewModelScope) }

    fun emitLatest() {
        lastEmitted.value = System.currentTimeMillis()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun makeOpenByDefaultSettingsIntent(packageName: String): Intent {
        return intentCompat.createAppOpenByDefaultSettingsIntent(packageName)
    }

    fun <T> postShizukuCommand(delay: Long, command: IShizukuService.() -> T) {
        val cmd = ShizukuCommand(command) {
            viewModelScope.launch {
                delay(delay)
                emitLatest()
            }
        }

        shizukuHandler.enqueueCommand(cmd)
    }
}


