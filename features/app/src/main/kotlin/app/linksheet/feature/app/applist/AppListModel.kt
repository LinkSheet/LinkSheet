package app.linksheet.feature.app.applist

import app.linksheet.feature.app.core.DomainVerificationAppInfo
import app.linksheet.feature.app.core.IAppInfo
import app.linksheet.util.RefreshableDataFlow
import fe.kotlin.extension.iterable.filterIf
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class AppListModel<T : IAppInfo>(
    queryApps: () -> List<T>,
    private val scope: CoroutineScope,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val _sortState = MutableStateFlow(SortByState.Default)
    val sortState = _sortState.asStateFlow()
    private val _filterState = MutableStateFlow(FilterState.Default)
    val filterState = _filterState.asStateFlow()
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val appDataFlow = RefreshableDataFlow<AppListState<T>, RefreshTrigger>(
        dispatcher = dispatcher,
        initialState = AppListState(isLoading = true, apps = null),
        loadData = {
            val apps = queryApps()
            emit(AppListState(isLoading = false, apps))
        },
        refreshData = { current, params ->
            emit(current.copy(isLoading = true))
            val apps = queryApps()
            emit(AppListState(isLoading = false, apps))
        }
    )
    val appsFiltered = combine(
        flow = appDataFlow.flow,
        flow2 = _filterState,
        flow3 = _searchQuery,
        flow4 = _sortState
    ) { appState, filter, searchQuery, sort ->
        return@combine AppListState(
            isLoading = appState.isLoading,
            apps = appState.apps
                ?.filter { filter.matches(it) }
                ?.filterIf(searchQuery.isNotEmpty()) { it.matches(searchQuery) }
                ?.sortedWith(sort.toComparator())
        )
    }
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
//            initialValue = emptyList()
        )

    fun refresh() = scope.launch {
        appDataFlow.refresh(RefreshTrigger.User)
    }

    fun search(query: String?) {
        _searchQuery.value = query ?: ""
    }

    private fun T.matches(query: String): Boolean {
        if (compareLabel.contains(query, ignoreCase = true)) {
            return true
        }
        if (packageName.contains(query, ignoreCase = true)) {
            return true
        }
        if (this is DomainVerificationAppInfo && compareHostSet.contains(query.lowercase())) {
            return true
        }

        return false
    }

    fun updateState(sortByState: SortByState, filterState: FilterState) {
        _sortState.value = sortByState
        _filterState.value = filterState
    }
}

data class AppListState<T : IAppInfo>(
    val isLoading: Boolean,
    val apps: List<T>?
)

sealed interface RefreshTrigger {
    object User : RefreshTrigger
}
