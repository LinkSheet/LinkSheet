package fe.linksheet.composable.page.settings.browser.inapp

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import fe.android.compose.icon.BitmapIconPainter
import fe.android.compose.text.ComposableTextContent.Companion.content
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.component.appbar.SearchTopAppBar
import fe.composekit.component.icon.AppIconImage
import fe.composekit.component.list.column.SaneLazyColumnLayout
import fe.composekit.component.list.item.ContentPosition
import fe.composekit.component.list.item.type.CheckboxListItem
import fe.composekit.component.page.SaneSettingsScaffold
import fe.linksheet.R
import fe.linksheet.composable.util.listState
import fe.linksheet.extension.compose.listHelper
import fe.linksheet.extension.kotlin.collectOnIO
import fe.linksheet.module.viewmodel.InAppBrowserSettingsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun NewInAppBrowserSettingsDisableInSelectedRoute(
    onBackPressed: () -> Unit,
    viewModel: InAppBrowserSettingsViewModel = koinViewModel(),
) {
    val items by viewModel._filteredItems.collectOnIO()
    val searchFilter by viewModel.searchFilter.collectOnIO()

    val listState = remember(items?.size, searchFilter) {
        listState(items, searchFilter)
    }

    val context = LocalContext.current

    SaneSettingsScaffold(
        topBar = {
            SearchTopAppBar(
                titleContent = textContent(R.string.disable_in_selected),
                placeholderContent = textContent(R.string.settings__title_filter_apps),
                query = searchFilter,
                onQueryChange = viewModel::search,
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
                listKey = { it.packageName }
            ) { item, padding, shape ->
                CheckboxListItem(
                    checked = item.selected.value,
                    onCheckedChange = {
                        item.update(it)
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
                        AppIconImage(
                            icon = BitmapIconPainter.bitmap(item.loadIcon(context)),
                            label = item.label
                        )
                    }
                )
            }
        }
    }
}


