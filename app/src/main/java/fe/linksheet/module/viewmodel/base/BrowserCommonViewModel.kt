package fe.linksheet.module.viewmodel.base

import android.app.Application
import androidx.compose.runtime.snapshots.SnapshotStateMap
import fe.kotlin.extension.map.filterIf
import fe.linksheet.module.preference.AppPreferenceRepository
import fe.linksheet.resolver.DisplayActivityInfo
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

abstract class BrowserCommonViewModel(
    val context: Application,
    preferenceRepository: AppPreferenceRepository
) : BaseViewModel(preferenceRepository) {
    val searchFilter = MutableStateFlow("")

    // TODO: This needs to be refactored
    val filteredItems by lazy {
        items().combine(searchFilter) { items, filter ->
            items.filterIf(condition = filter.isNotEmpty()) { (info, _) ->
                info.compareLabel.contains(filter, ignoreCase = true)
            }
        }
    }

    protected abstract fun items(): Flow<Map<DisplayActivityInfo, Boolean>>
    abstract fun save(selected: BrowserCommonSelected): Job
}

typealias BrowserCommonSelected = MutableMap<DisplayActivityInfo, Boolean>
