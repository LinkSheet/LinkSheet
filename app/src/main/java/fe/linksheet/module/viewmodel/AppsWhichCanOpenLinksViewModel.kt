package fe.linksheet.module.viewmodel

import android.content.Context
import android.content.Intent
import android.content.pm.getInstalledPackagesCompat
import android.content.pm.verify.domain.DomainVerificationManager
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
import fe.kotlin.extension.iterable.filterIf
import fe.linksheet.R
import fe.linksheet.experiment.ui.overhaul.composable.AppListItemData
import fe.linksheet.extension.android.isUserApp
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.experiment.ExperimentRepository
import fe.linksheet.module.preference.experiment.Experiments
import fe.linksheet.module.shizuku.ShizukuCommand
import fe.linksheet.module.shizuku.ShizukuHandler
import fe.linksheet.module.viewmodel.base.BaseViewModel
import fe.linksheet.resolver.DisplayActivityInfo
import fe.linksheet.util.AndroidVersion
import fe.linksheet.util.VerifiedDomainUtil
import fe.linksheet.util.flowOfLazy
import fe.linksheet.util.getAppOpenByDefaultIntent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class AppsWhichCanOpenLinksViewModel(
    private val context: Context,
    private val shizukuHandler: ShizukuHandler,
    preferenceRepository: AppPreferenceRepository,
    experimentRepository: ExperimentRepository,
) : BaseViewModel(preferenceRepository) {

    private val uiOverhaul = experimentRepository.asState(Experiments.uiOverhaul)

    private val domainVerificationManager by lazy {
        if (AndroidVersion.AT_LEAST_API_31_S) {
            context.getSystemService<DomainVerificationManager>()
        } else null
    }

    val lastEmitted = MutableStateFlow(0L)
    val searchFilter = MutableStateFlow("")

    //    val pagerState = PagerState { 2 }
//    val linkHandlingAllowed = snapshotFlow { pagerState.currentPage == 0 }s
    val filterDisabledOnly = MutableStateFlow(true)
    val userApps = MutableStateFlow(true)

    val filterMode = MutableStateFlow<FilterMode>(FilterMode.EnabledOnly)

    @RequiresApi(Build.VERSION_CODES.S)
    private val installedPackages = flowOfLazy {
        context.packageManager.getInstalledPackagesCompat().mapNotNull {
            VerifiedDomainUtil.getStatus(
                domainVerificationManager!!,
                it.applicationInfo,
                it.applicationInfo.loadLabel(context.packageManager)
            )
        }
    }
//        .combine(lastEmitted) { installedPackages, _ -> installedPackages }

    @RequiresApi(Build.VERSION_CODES.S)
    private val baseApps = installedPackages.combine(userApps) { apps, userApps ->
        apps.filter { !userApps || it.applicationInfo.isUserApp() }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private val apps = if (!uiOverhaul()) baseApps.combine(filterDisabledOnly) { apps, filterDisabledOnly ->
        apps.filter { !(filterDisabledOnly && it.enabled) }
    } else baseApps.combine(filterMode) { apps, filterMode ->
        if (filterMode == FilterMode.ShowAll) return@combine apps

        val enabledMode = filterMode == FilterMode.EnabledOnly
        apps.filter { if (enabledMode) it.enabled else !it.enabled }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    val appsFiltered = apps.combine(searchFilter) { apps, query ->
        apps.filterIf(query.isNotEmpty()) { it.matches(query) }.sortedWith(AppListItemData.labelComparator)
    }

    fun emitLatest() {
        lastEmitted.value = System.currentTimeMillis()
    }

    fun search(query: String?) {
        searchFilter.value = query ?: ""
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
