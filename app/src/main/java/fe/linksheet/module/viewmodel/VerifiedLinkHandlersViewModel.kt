package fe.linksheet.module.viewmodel

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import app.linksheet.api.preference.AppPreferenceRepository
import app.linksheet.feature.app.applist.AppListModel
import app.linksheet.feature.app.core.AppInfo
import app.linksheet.feature.app.core.AppInfoCreator
import app.linksheet.feature.app.core.DomainVerificationAppInfo
import app.linksheet.feature.app.core.IAppInfo
import app.linksheet.feature.app.core.LinkHandling
import app.linksheet.feature.app.usecase.DomainVerificationUseCase
import app.linksheet.feature.devicecompat.oneui.OneUiCompat
import fe.composekit.flag.ApplicationInfoFlags
import fe.kotlin.extension.iterable.groupByNoNullKeys
import fe.kotlin.extension.iterable.mapToSet
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
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

class VerifiedLinkHandlersViewModel(
    preferenceRepository: AppPreferenceRepository,
    experimentRepository: ExperimentRepository,
    private val preferredAppRepository: PreferredAppRepository,
    private val useCase: DomainVerificationUseCase,
    private val intentCompat: OneUiCompat,
    private val compatHelper: PreferredAppCompatHelper,
) : BaseViewModel(preferenceRepository) {
    val newVlh = experimentRepository.asViewModelState(Experiments.newVlh)
    val mergeWithPreferred = experimentRepository.asViewModelState(Experiments.mergeWithPreferred)

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

    private val _preferredApps = preferredAppRepository.getAllAlwaysPreferred()
        .mapProducingSideEffects(
            sideEffectContext = Dispatchers.IO,
            transform = ::groupHosts,
            handleSideEffects = { packageNames ->
                preferredAppRepository.deleteByPackageNames(packageNames.toSet())
            }
        )
    val preferredApps = _preferredApps.shareIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        replay = 1
    )

    val appListModel by lazy {
        AppListModel(
            queryApps = {
                val realApps = useCase.getDomainVerificationAppInfoList()
                if (!mergeWithPreferred.value) {
                    return@AppListModel realApps
                }

                val realAppPackages = realApps.mapToSet { it.packageName }
                val compatApps = _preferredApps.firstOrNull()?.let { preferredAppMap ->
                    preferredAppMap.filter { it.key !in realAppPackages }
                        .mapNotNull { (pkg, hosts) ->
                            compatHelper.toDomainVerificationAppInfo(pkg, hosts)
                        }
                }
                if (compatApps != null) {
                    return@AppListModel realApps + compatApps
                }
                return@AppListModel realApps
            },
            scope = viewModelScope
        )
    }
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
            if (info is PreferredAppCompatInfo) {
                appListModel.refreshOnChange()
            }
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

// TODO: Remove once we have a proper implementation
class PreferredAppCompatHelper(
    private val creator: AppInfoCreator,
    private val getApplicationInfoOrNull: (String, ApplicationInfoFlags) -> ApplicationInfo?,
) {
    fun toDomainVerificationAppInfo(
        packageName: String,
        hosts: Collection<String>
    ): DomainVerificationAppInfo? {
        val app = getApplicationInfoOrNull(packageName, ApplicationInfoFlags.EMPTY) ?: return null
        val appInfo = creator.toAppInfo(app, null)

        return PreferredAppCompatInfo(appInfo = appInfo, hosts.toMutableList())
    }
}

@Parcelize
@Immutable
class PreferredAppCompatInfo(
    appInfo: AppInfo,
    stateSelected: MutableList<String>,
) : DomainVerificationAppInfo(
    appInfo,
    LinkHandling.Allowed,
    mutableListOf(),
    stateSelected,
    mutableListOf()
) {

}
