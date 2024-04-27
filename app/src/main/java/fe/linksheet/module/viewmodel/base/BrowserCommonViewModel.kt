package fe.linksheet.module.viewmodel.base

import android.app.Application
import fe.kotlin.extension.map.filterIf
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.resolver.DisplayActivityInfo
import fe.linksheet.util.flowOfLazy
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*

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

    protected open fun items(): Flow<Map<DisplayActivityInfo, Boolean>> {
        return flowOfLazy { emptyMap() }
    }

    open fun save(selected: BrowserCommonSelected): Job {
        TODO()
    }
}

typealias BrowserCommonSelected = MutableMap<DisplayActivityInfo, Boolean>
