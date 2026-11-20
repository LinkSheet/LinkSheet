package app.linksheet.feature.shizuku.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.style.TextOverflow
import app.linksheet.feature.app.ui.AppInfoIcon
import app.linksheet.feature.app.ui.AppListPage
import app.linksheet.feature.shizuku.R
import app.linksheet.feature.shizuku.service.ORIGINAL_MANAGER_PACKAGE
import app.linksheet.feature.shizuku.viewmodel.ShizukuManagerViewModel
import fe.android.compose.text.ComposableTextContent.Companion.content
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.component.list.item.ContentPosition
import fe.composekit.component.list.item.type.RadioButtonListItem
import fe.composekit.preference.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun ShizukuManagerSettings(
    onBackPressed: () -> Unit,
    viewModel: ShizukuManagerViewModel = koinViewModel(),
) {
    val selectedPackage by viewModel.selectedPackage.collectAsStateWithLifecycle()

    AppListPage(
        titleContent = textContent(R.string.settings_shizuku__title_select_manager),
        appListModel = viewModel.appListModel,
        onBackPressed = onBackPressed
    ) { item, padding, shape ->
        val selected = (selectedPackage ?: ORIGINAL_MANAGER_PACKAGE) == item.packageName
        RadioButtonListItem(
            selected = selected,
            onSelect = { viewModel.select(item) },
            padding = padding,
            shape = shape,
            position = ContentPosition.Trailing,
            headlineContent = content {
                Text(text = item.label, overflow = TextOverflow.Ellipsis, maxLines = 1)
            },
            supportingContent = content {
                Text(text = item.packageName, overflow = TextOverflow.Ellipsis, maxLines = 1)
            },
            otherContent = {
                AppInfoIcon(appInfo = item)
            }
        )
    }
}
