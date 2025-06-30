package fe.linksheet.composable.page.settings.apps.verifiedlinkhandlers

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FilterAltOff
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import fe.linksheet.R

private val VlhStateModeFilter.stringRes: Int
    get() = when (this) {
        VlhStateModeFilter.ShowAll -> R.string.settings_verified_link_handlers__text_handling_filter_all_short
        VlhStateModeFilter.EnabledOnly -> R.string.settings_verified_link_handlers__text_handling_filter_enabled_short
        VlhStateModeFilter.DisabledOnly -> R.string.settings_verified_link_handlers__text_handling_filter_disabled_short
    }

private val VlhStateModeFilter.icon: ImageVector
    get() = when (this) {
        VlhStateModeFilter.ShowAll -> Icons.Outlined.FilterAltOff
        VlhStateModeFilter.EnabledOnly -> Icons.Outlined.Visibility
        VlhStateModeFilter.DisabledOnly -> Icons.Outlined.VisibilityOff
    }

@Composable
internal fun VlhStateModeFilter(selection: VlhStateModeFilter, onSelected: (VlhStateModeFilter) -> Unit) {
    BaseStateFilter(
        entries = VlhStateModeFilter.entries,
        allState = VlhStateModeFilter.ShowAll,
        selection = selection,
        onSelected = onSelected,
        stringRes = VlhStateModeFilter::stringRes,
        icon = VlhStateModeFilter::icon
    )
}
