package fe.linksheet.module.viewmodel.base

import android.app.Application
import fe.kotlin.extension.map.filterIf
import app.linksheet.feature.app.ActivityAppInfo
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
            items.filterIf(condition = filter.isNotEmpty()) { (info, _) ->
                info.compareLabel.contains(filter, ignoreCase = true)
            }
        }
    }

    protected open fun items(): Flow<Map<ActivityAppInfo, Boolean>> {
        return flowOfLazy { emptyMap() }
    }

    open fun save(selected: BrowserCommonSelected): Job {
        TODO()
    }
}

typealias BrowserCommonSelected = MutableMap<ActivityAppInfo, Boolean>
