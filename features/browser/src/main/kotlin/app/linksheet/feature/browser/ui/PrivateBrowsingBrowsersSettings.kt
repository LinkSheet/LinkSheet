package app.linksheet.feature.browser.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.linksheet.compose.util.listState
import app.linksheet.feature.app.ui.AppFilterSearchTopAppBar
import app.linksheet.feature.app.ui.AppInfoIcon
import app.linksheet.feature.app.ui.appList
import app.linksheet.feature.browser.R
import app.linksheet.feature.browser.viewmodel.PrivateBrowsingBrowserSettingsViewModel
import fe.android.compose.text.ComposableTextContent.Companion.content
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.component.list.column.SaneLazyColumnLayout
import fe.composekit.component.list.item.ContentPosition
import fe.composekit.component.list.item.type.CheckboxListItem
import fe.composekit.component.page.SaneSettingsScaffold
import org.koin.androidx.compose.koinViewModel

@Composable
fun PrivateBrowsingBrowsersSettings(
    onBackPressed: () -> Unit,
    viewModel: PrivateBrowsingBrowserSettingsViewModel = koinViewModel(),
) {
    val items by viewModel.list.appsFiltered.collectAsStateWithLifecycle()
    val searchFilter by viewModel.list.searchQuery.collectAsStateWithLifecycle()
    val all by viewModel.allowedBrowsers.collectAsStateWithLifecycle(initialValue = emptySet())

    val listState = remember(items?.size, searchFilter) {
        listState(items, searchFilter)
    }

    SaneSettingsScaffold(
        topBar = {
            AppFilterSearchTopAppBar(
                appListCommon = viewModel.list,
                titleContent = textContent(R.string.settings_private_browsing_browsers__title_browsers),
                onBackPressed = onBackPressed
            )
        }
    ) { padding ->
        SaneLazyColumnLayout(padding = padding) {
            appList(
                listState = listState,
                list = items,
                listKey = { it.flatComponentName }
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
    }
}

