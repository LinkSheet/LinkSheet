package fe.linksheet.composable.page.settings.browser.mode

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.linksheet.compose.extension.listHelper
import app.linksheet.compose.util.listState
import fe.android.compose.text.ComposableTextContent.Companion.content
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.component.appbar.SearchTopAppBar
import fe.composekit.component.list.column.SaneLazyColumnLayout
import fe.composekit.component.list.item.ContentPosition
import fe.composekit.component.list.item.type.CheckboxListItem
import fe.composekit.component.page.SaneSettingsScaffold
import fe.linksheet.R
import fe.linksheet.composable.component.appinfo.AppInfoIcon
import fe.linksheet.module.repository.whitelisted.mapBrowserState
import fe.linksheet.module.viewmodel.PreferredBrowserViewModel
import fe.linksheet.module.viewmodel.WhitelistedBrowsersViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

private val FalsePair = false to false

@Composable
fun WhitelistedBrowsersSettingsRoute(
    type: PreferredBrowserViewModel.BrowserType,
    onBackPressed: () -> Unit,
    viewModel: WhitelistedBrowsersViewModel = koinViewModel(parameters = { parametersOf(type) }),
) {
    val items by viewModel.list.appsFiltered.collectAsStateWithLifecycle()
    val searchFilter by viewModel.list.searchQuery.collectAsStateWithLifecycle()
    val whitelisted by viewModel.getAll().collectAsStateWithLifecycle(initialValue = null)

    val listState = remember(items?.size, searchFilter) {
        listState(items, searchFilter)
    }

    SaneSettingsScaffold(
        topBar = {
            SearchTopAppBar(
                titleContent = textContent(R.string.whitelisted),
                placeholderContent = textContent(R.string.settings__title_filter_apps),
                query = searchFilter,
                onQueryChange = viewModel.list::search,
                onBackPressed = onBackPressed
            )
        }
    ) { padding ->
        SaneLazyColumnLayout(padding = padding) {
            listHelper(
                noItems = R.string.no_apps_found,
                notFound = R.string.no_such_app_found,
                listState = listState,
                list = items,
                listKey = { it.flatComponentName }
            ) { item, padding, shape ->
                val (isWhitelisted , isSourcePackageNameOnly) = remember(whitelisted, item) {
                    whitelisted?.mapBrowserState(item) ?: FalsePair
                }
                CheckboxListItem(
                    checked = isWhitelisted,
                    onCheckedChange = {
                        viewModel.save(item, it, isSourcePackageNameOnly)
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

