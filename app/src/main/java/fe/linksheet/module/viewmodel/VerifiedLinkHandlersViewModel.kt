package fe.linksheet.module.viewmodel

import android.app.Application
import android.content.Intent
import android.content.pm.verify.domain.DomainVerificationManager
import android.content.pm.verify.domain.DomainVerificationUserState
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FilterAltOff
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.content.getSystemService
import androidx.lifecycle.viewModelScope
import dev.zwander.shared.IShizukuService
import fe.android.compose.version.AndroidVersion
import fe.kotlin.extension.iterable.filterIf
import fe.linksheet.R
import fe.linksheet.composable.AppListItemData
import fe.linksheet.extension.android.SYSTEM_APP_FLAGS
import fe.linksheet.module.app.PackageDomainVerificationStatus
import fe.linksheet.module.app.PackageInfoService
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.experiment.ExperimentRepository
import fe.linksheet.module.shizuku.ShizukuCommand
import fe.linksheet.module.shizuku.ShizukuHandler
import fe.linksheet.module.viewmodel.base.BaseViewModel
import fe.linksheet.resolver.DisplayActivityInfo
import fe.linksheet.util.getAppOpenByDefaultIntent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class VerifiedLinkHandlersViewModel(
    private val context: Application,
    private val shizukuHandler: ShizukuHandler,
    preferenceRepository: AppPreferenceRepository,
    experimentRepository: ExperimentRepository,
    private val packageInfoService: PackageInfoService,
) : BaseViewModel(preferenceRepository) {

    private val domainVerificationManager by lazy {
        if (AndroidVersion.AT_LEAST_API_31_S) {
            context.getSystemService<DomainVerificationManager>()
        } else null
    }

    val lastEmitted = MutableStateFlow(0L)

    val filterDisabledOnly = MutableStateFlow(true)

    val userAppFilter = MutableStateFlow(true)
    val filterMode = MutableStateFlow<FilterMode>(FilterMode.ShowAll)
    val searchQuery = MutableStateFlow("")
    private val sorting = MutableStateFlow(AppListItemData.labelComparator)

    private fun createDomainVerificationFlow(): Flow<PackageDomainVerificationStatus> = flow {
        val packages = packageInfoService.getInstalledPackages()
        for (packageInfo in packages) {
            val applicationInfo = packageInfo.applicationInfo ?: continue
            val verificationState = packageInfoService.getVerificationState(applicationInfo) ?: continue
            val label = packageInfoService.findBestLabel(packageInfo)

            val stateNone = mutableListOf<String>()
            val stateSelected = mutableListOf<String>()
            val stateVerified = mutableListOf<String>()

            for ((domain, state) in verificationState.hostToStateMap) {
                when (state) {
                    DomainVerificationUserState.DOMAIN_STATE_NONE -> stateNone.add(domain)
                    DomainVerificationUserState.DOMAIN_STATE_SELECTED -> stateSelected.add(domain)
                    DomainVerificationUserState.DOMAIN_STATE_VERIFIED -> stateVerified.add(domain)
                }
            }

            val status = PackageDomainVerificationStatus(
                packageInfo.packageName,
                label,
                applicationInfo.flags,
                verificationState.isLinkHandlingAllowed,
                stateNone,
                stateSelected,
                stateVerified
            )

            emit(status)
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    val appsFiltered = createDomainVerificationFlow()
        .scan(emptyList<PackageDomainVerificationStatus>()) { acc, elem -> acc + elem }
        .flowOn(Dispatchers.IO)
        .combine(userAppFilter) { apps, userAppFilter ->
            apps.filter { !userAppFilter || it.flags !in SYSTEM_APP_FLAGS }
        }
        .combine(filterMode) { apps, filterMode ->
            if (filterMode == FilterMode.ShowAll) return@combine apps

            val enabledMode = filterMode == FilterMode.EnabledOnly
            apps.filter { if (enabledMode) it.enabled else !it.enabled }
        }
        .combine(searchQuery) { apps, searchQuery ->
            apps.filterIf(searchQuery.isNotEmpty()) { it.matches(searchQuery) }
        }
        .combine(sorting) { apps, sorting ->
            apps.sortedWith(sorting)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(0),
            initialValue = emptyList()
        )

    fun emitLatest() {
        lastEmitted.value = System.currentTimeMillis()
    }

    fun search(query: String?) {
        searchQuery.value = query ?: ""
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun makeOpenByDefaultSettingsIntent(activityInfo: DisplayActivityInfo): Intent {
        return makeOpenByDefaultSettingsIntent(activityInfo.packageName)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun makeOpenByDefaultSettingsIntent(packageName: String): Intent {
        return getAppOpenByDefaultIntent(packageName)
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

sealed class FilterMode(
    @StringRes val shortStringRes: Int,
    @StringRes val stringRes: Int,
    val icon: ImageVector,
//    private val createIcon: (Context) -> ImageVector,
) {
//    private var icon: ImageVector? = null
//
//    fun loadIcon(context: Context): ImageVector {
//        if (icon == null) icon = createIcon(context)
//        return icon!!
//    }

    data object ShowAll : FilterMode(
        R.string.settings_verified_link_handlers__text_handling_filter_all_short,
        R.string.settings_verified_link_handlers__text_handling_filter_all_short,
//        {
        Icons.Outlined.FilterAltOff
//}
    )

    data object EnabledOnly : FilterMode(
        R.string.settings_verified_link_handlers__text_handling_filter_enabled_short,
        R.string.settings_verified_link_handlers__text_handling_filter_enabled_short,
//        {
        Icons.Outlined.Visibility
//}
    )

    data object DisabledOnly : FilterMode(
        R.string.settings_verified_link_handlers__text_handling_filter_disabled_short,
        R.string.settings_verified_link_handlers__text_handling_filter_disabled_short,
        Icons.Outlined.VisibilityOff
//        {
//            Icons.Outlined.VisibilityOff
//            ImageVector.vectorResource(res = it.resources, resId = R.drawable.domain_verification_off_24)
//        }
    )

    companion object {
        val Modes by lazy { arrayOf(ShowAll, EnabledOnly, DisabledOnly) }
    }
}
