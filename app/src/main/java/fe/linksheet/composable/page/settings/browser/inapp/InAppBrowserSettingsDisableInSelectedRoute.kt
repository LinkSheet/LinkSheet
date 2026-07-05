package fe.linksheet.composable.page.settings.browser.inapp

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.linksheet.feature.app.ui.AppInfoIcon
import app.linksheet.feature.app.ui.AppListPage
import fe.android.compose.text.ComposableTextContent.Companion.content
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.component.list.item.ContentPosition
import fe.composekit.component.list.item.type.CheckboxListItem
import fe.linksheet.R
import fe.linksheet.module.viewmodel.InAppBrowserSettingsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun InAppBrowserSettingsDisableInSelectedRoute(
    onBackPressed: () -> Unit,
    viewModel: InAppBrowserSettingsViewModel = koinViewModel(),
) {
    val disabledPackages by viewModel.disabledPackages.collectAsStateWithLifecycle(initialValue = emptySet())

    AppListPage(
        titleContent = textContent(R.string.disable_in_selected),
        appListModel = viewModel.appListModel,
        onBackPressed = onBackPressed
    ) {  item, padding, shape ->
        val isSelected = remember(disabledPackages, item) {
            item.packageName in disabledPackages
        }
        CheckboxListItem(
            checked = isSelected,
            onCheckedChange = {
                viewModel.save(item, it)
            },
            padding = padding,
            shape = shape,
            position = ContentPosition.Trailing,
            headlineContent = content {
                Text(text = item.label, overflow = TextOverflow.Ellipsis, maxLines = 1)
            },
            supportingContent = content {
                Text(
                    text = item.packageName,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            },
            otherContent = {
                AppInfoIcon(appInfo = item)
            }
        )
    }
}


