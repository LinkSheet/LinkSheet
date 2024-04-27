package fe.linksheet.experiment.ui.overhaul.composable.page.settings.browser.inapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import fe.linksheet.R
import fe.linksheet.composable.util.listState
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.ContentPosition
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.type.CheckboxListItem
import fe.linksheet.experiment.ui.overhaul.composable.component.page.SaneSettingsScaffold
import fe.linksheet.experiment.ui.overhaul.composable.component.page.layout.SaneLazyColumnPageDefaults
import fe.linksheet.experiment.ui.overhaul.composable.component.page.layout.SaneLazyColumnPageLayout
import fe.linksheet.experiment.ui.overhaul.composable.component.util.ComposableTextContent.Companion.content
import fe.linksheet.extension.compose.listHelper
import fe.linksheet.extension.kotlin.collectOnIO
import fe.linksheet.module.viewmodel.InAppBrowserSettingsViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
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
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    SaneSettingsScaffold(
        topBar = {
            Column(
                modifier = Modifier.padding(bottom = SaneLazyColumnPageDefaults.BottomSpacing),
                verticalArrangement = Arrangement.spacedBy((-1).dp)
            ) {
                TopAppBar(
                    title = { Text(text = stringResource(id = R.string.disable_in_selected)) },
                    navigationIcon = {
                        IconButton(onClick = onBackPressed) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = null
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior
                )

                DockedSearchBar(
                    modifier = Modifier.padding(horizontal = SaneLazyColumnPageDefaults.HorizontalSpacing),
                    inputField = {
                        SearchBarDefaults.InputField(
                            query = searchFilter,
                            onQueryChange = { viewModel.search(it) },
                            onSearch = {},
                            expanded = false,
                            onExpandedChange = {},
                            placeholder = { Text(text = stringResource(id = R.string.settings__title_filter_apps)) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Rounded.Search,
                                    contentDescription = null
                                )
                            },
                            trailingIcon = {
                                if (searchFilter.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.search(null) }) {
                                        Icon(
                                            imageVector = Icons.Rounded.Clear,
                                            contentDescription = null
                                        )
                                    }
                                }
                            }
                        )
                    },
                    expanded = false,
                    onExpandedChange = {},
                    content = {}
                )
            }
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
                        Image(
                            bitmap = item.loadIcon(context),
                            contentDescription = item.label,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                )
            }
        }
    }
}


