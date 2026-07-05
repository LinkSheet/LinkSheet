package app.linksheet.feature.browser.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.linksheet.feature.app.ui.AppInfoIcon
import app.linksheet.feature.app.ui.AppListPage
import app.linksheet.feature.browser.R
import app.linksheet.feature.browser.viewmodel.PrivateBrowsingBrowserSettingsViewModel
import fe.android.compose.text.ComposableTextContent.Companion.content
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.component.list.item.ContentPosition
import fe.composekit.component.list.item.type.CheckboxListItem
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun PrivateBrowsingBrowsersSettings(
    onBackPressed: () -> Unit,
    viewModel: PrivateBrowsingBrowserSettingsViewModel = koinViewModel(),
) {
    val all by viewModel.allowedBrowsers.collectAsStateWithLifecycle(initialValue = emptySet())

    AppListPage(
        titleContent = textContent(R.string.settings_private_browsing_browsers__title_browsers),
        appListModel = viewModel.appListModel,
        onBackPressed = onBackPressed
    ) { item, padding, shape ->
        val isSelected = remember(all, item) {
            item.flatComponentName in all
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
                Text(text = item.packageName, overflow = TextOverflow.Ellipsis, maxLines = 1)
            },
            otherContent = {
                AppInfoIcon(appInfo = item)
            }
        )
    }
}

