package app.linksheet.feature.app.applist

import app.linksheet.feature.app.core.DomainVerificationAppInfo
import app.linksheet.feature.app.core.IAppInfo
import app.linksheet.feature.app.core.LinkHandling
import fe.kotlin.extension.iterable.filterIf
import fe.linksheet.extension.android.SYSTEM_APP_FLAGS
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

class AppListCommon<T : IAppInfo>(
    apps: Flow<List<T>>,
    private val scope: CoroutineScope,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    val sortState = MutableStateFlow(SortByState(SortType.AZ, true))
    val filterState = MutableStateFlow(FilterState(StateModeFilter.ShowAll, TypeFilter.All, true))
    val searchQuery = MutableStateFlow("")

    val appsFiltered = apps
        .flowOn(dispatcher)
        .combine(filterState) { apps, state ->
            apps.filter { state.matches(it) }
        }
        .combine(searchQuery) { apps, searchQuery ->
            apps.filterIf(searchQuery.isNotEmpty()) { it.matches(searchQuery) }
        }
        .combine(sortState) { apps, state ->
            apps.sortedWith(state.toComparator())
        }
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(0),
            initialValue = null
//            initialValue = emptyList()
        )

    fun search(query: String?) {
        searchQuery.value = query ?: ""
    }

    private fun IAppInfo.matches(query: String): Boolean {
        return compareLabel.contains(query, ignoreCase = true) || packageName.contains(
            query,
            ignoreCase = true
        )
    }

    private val sortComparators = mapOf(
        SortType.InstallTime to IAppInfo.InstallTime,
        SortType.AZ to IAppInfo.Label
    )

    private fun SortByState.toComparator(): Comparator<IAppInfo> {
        val (asc, desc) = sortComparators[sort]!!
        val comp = if (ascending) asc else desc
        return comp as Comparator<IAppInfo>
    }

    private fun StateModeFilter.matches(info: DomainVerificationAppInfo): Boolean {
        return when (this) {
            StateModeFilter.ShowAll -> true
            StateModeFilter.EnabledOnly -> info.enabled
            StateModeFilter.DisabledOnly -> !info.enabled
        }
    }

    private fun TypeFilter.matches(info: DomainVerificationAppInfo): Boolean {
        return when (this) {
            TypeFilter.All -> true
            TypeFilter.Browser -> info.linkHandling == LinkHandling.Browser
            TypeFilter.Native -> info.linkHandling != LinkHandling.Browser
        }
    }

    private fun FilterState.matches(info: IAppInfo): Boolean {
        if (!systemApps && info.flags in SYSTEM_APP_FLAGS) return false
        if (info is DomainVerificationAppInfo) {
            if (!mode.matches(info)) return false
            if (!type.matches(info)) return false
        }

        return true
    }
}
