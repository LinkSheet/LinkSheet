package fe.linksheet.composable.page.settings.apps.verifiedlinkhandlers

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Android
import androidx.compose.material.icons.outlined.FilterAltOff
import androidx.compose.material.icons.outlined.Http
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import fe.linksheet.R
import fe.linksheet.module.viewmodel.common.applist.TypeFilter

private val TypeFilter.stringRes: Int
    get() = when (this) {
        TypeFilter.All -> R.string.settings_verified_link_handlers__text_vlh_type_filter_all
        TypeFilter.Browser -> R.string.settings_verified_link_handlers__text_vlh_type_filter_browser
        TypeFilter.Native -> R.string.settings_verified_link_handlers__text_vlh_type_filter_native
    }

private val TypeFilter.icon: ImageVector
    get() = when (this) {
        TypeFilter.All -> Icons.Outlined.FilterAltOff
        TypeFilter.Browser -> Icons.Outlined.Http
        TypeFilter.Native -> Icons.Outlined.Android
    }

@Composable
internal fun VlhTypeFilter(selection: TypeFilter, onSelected: (TypeFilter) -> Unit) {
    BaseStateFilter(
        entries = TypeFilter.entries,
        allState = TypeFilter.All,
        selection = selection,
        onSelected = onSelected,
        stringRes = TypeFilter::stringRes,
        icon = TypeFilter::icon
    )
}
