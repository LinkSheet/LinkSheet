package fe.linksheet.composable.page.settings.apps.verifiedlinkhandlers

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FilterAltOff
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import fe.linksheet.R
import fe.linksheet.module.viewmodel.common.StateModeFilter

private val StateModeFilter.stringRes: Int
    get() = when (this) {
        StateModeFilter.ShowAll -> R.string.settings_verified_link_handlers__text_handling_filter_all_short
        StateModeFilter.EnabledOnly -> R.string.settings_verified_link_handlers__text_handling_filter_enabled_short
        StateModeFilter.DisabledOnly -> R.string.settings_verified_link_handlers__text_handling_filter_disabled_short
    }

private val StateModeFilter.icon: ImageVector
    get() = when (this) {
        StateModeFilter.ShowAll -> Icons.Outlined.FilterAltOff
        StateModeFilter.EnabledOnly -> Icons.Outlined.Visibility
        StateModeFilter.DisabledOnly -> Icons.Outlined.VisibilityOff
    }

@Composable
internal fun VlhStateModeFilter(selection: StateModeFilter, onSelected: (StateModeFilter) -> Unit) {
    BaseStateFilter(
        entries = StateModeFilter.entries,
        allState = StateModeFilter.ShowAll,
        selection = selection,
        onSelected = onSelected,
        stringRes = StateModeFilter::stringRes,
        icon = StateModeFilter::icon
    )
}
