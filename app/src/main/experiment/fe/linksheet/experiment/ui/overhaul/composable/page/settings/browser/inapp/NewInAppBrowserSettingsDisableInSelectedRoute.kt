package fe.linksheet.experiment.ui.overhaul.composable.page.settings.browser.inapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import fe.linksheet.R
import fe.linksheet.composable.util.listState
import fe.linksheet.experiment.ui.overhaul.composable.component.appbar.SearchTopAppBar
import fe.linksheet.experiment.ui.overhaul.composable.component.icon.AppIconImage
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.ContentPosition
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.type.CheckboxListItem
import fe.linksheet.experiment.ui.overhaul.composable.component.page.SaneSettingsScaffold
import fe.linksheet.experiment.ui.overhaul.composable.component.page.layout.SaneLazyColumnPageLayout
import fe.linksheet.experiment.ui.overhaul.composable.component.util.ComposableTextContent.Companion.content
import fe.linksheet.experiment.ui.overhaul.composable.component.util.Resource.Companion.textContent
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
        SaneLazyColumnPageLayout(padding = padding) {
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
                            bitmap = item.loadIcon(context),
                            label = item.label
                        )
                    }
                )
            }
        }
    }
}


