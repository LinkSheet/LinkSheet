package fe.linksheet.composable.page.settings.apps.verifiedlinkhandlers

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Android
import androidx.compose.material.icons.outlined.FilterAltOff
import androidx.compose.material.icons.outlined.Http
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material.icons.outlined.Web
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import fe.linksheet.R

private val VlhTypeFilter.stringRes: Int
    get() = when (this) {
        VlhTypeFilter.All -> R.string.settings_verified_link_handlers__text_vlh_type_filter_all
        VlhTypeFilter.Browser -> R.string.settings_verified_link_handlers__text_vlh_type_filter_browser
        VlhTypeFilter.Native -> R.string.settings_verified_link_handlers__text_vlh_type_filter_native
    }

private val VlhTypeFilter.icon: ImageVector
    get() = when (this) {
        VlhTypeFilter.All -> Icons.Outlined.FilterAltOff
        VlhTypeFilter.Browser -> Icons.Outlined.Http
        VlhTypeFilter.Native -> Icons.Outlined.Android
    }

@Composable
internal fun VlhTypeFilter(selection: VlhTypeFilter, onSelected: (VlhTypeFilter) -> Unit) {
    BaseStateFilter(
        entries = VlhTypeFilter.entries,
        allState = VlhTypeFilter.All,
        selection = selection,
        onSelected = onSelected,
        stringRes = VlhTypeFilter::stringRes,
        icon = VlhTypeFilter::icon
    )
}
