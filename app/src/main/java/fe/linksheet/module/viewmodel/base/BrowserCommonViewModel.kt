package fe.linksheet.module.viewmodel.base

import android.app.Application
import app.linksheet.feature.app.ActivityAppInfoStatus
import fe.kotlin.extension.iterable.filterIf
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.util.flowOfLazy
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

abstract class BrowserCommonViewModel(
    val context: Application,
    preferenceRepository: AppPreferenceRepository,
) : BaseViewModel(preferenceRepository) {
    open val filter = MutableStateFlow("")

    // TODO: This needs to be refactored
    open val filteredItems by lazy {
        items().combine(filter) { items, filter ->
            items.filterIf(filter = filter.isNotEmpty()) { (info, _) ->
                info.compareLabel.contains(filter, ignoreCase = true)
            }.toList()
        }
    }

    protected open fun items(): Flow<List<ActivityAppInfoStatus>> {
        return flowOfLazy { emptyList() }
    }

    open fun save(items: List<ActivityAppInfoStatus>?, selected: MutableMap<ActivityAppInfoStatus, Boolean>): Job {
        TODO()
    }
}
