package fe.linksheet.composable.page.settings.browser.mode

import android.content.ComponentName
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
import fe.android.compose.text.ComposableTextContent.Companion.content
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.component.list.column.SaneLazyColumnLayout
import fe.composekit.component.list.item.ContentPosition
import fe.composekit.component.list.item.type.RadioButtonListItem
import fe.composekit.component.page.SaneSettingsScaffold
import fe.composekit.preference.collectAsStateWithLifecycle
import fe.linksheet.R
import fe.linksheet.module.viewmodel.PreferredBrowserViewModel
import fe.linksheet.module.viewmodel.SingleBrowserViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun SingleBrowserSettingsRoute(
    type: PreferredBrowserViewModel.BrowserType,
    onBackPressed: () -> Unit,
    viewModel: SingleBrowserViewModel = koinViewModel(parameters = { parametersOf(type) }),
) {
    val items by viewModel.list.appsFiltered.collectAsStateWithLifecycle()
    val searchFilter by viewModel.list.searchQuery.collectAsStateWithLifecycle()
    val selectedBrowser by viewModel.selectedBrowser.collectAsStateWithLifecycle()
    val selectedCmp = remember(selectedBrowser) {
        selectedBrowser?.let { ComponentName.unflattenFromString(it) }
    }

    val listState = remember(items?.size, searchFilter) {
        listState(items, searchFilter)
    }

    SaneSettingsScaffold(
        topBar = {
            AppFilterSearchTopAppBar(
                appListCommon = viewModel.list,
                titleContent = textContent(R.string.settings_apps_browsers_mode__title_selected),
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
                val selected = remember(selectedCmp, item) {
                    (selectedCmp != null && selectedCmp == item.componentName) || (selectedBrowser == item.packageName)
                }
                RadioButtonListItem(
                    selected = selected,
                    onSelect = {
                        viewModel.updateSelectedBrowser(item)
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

