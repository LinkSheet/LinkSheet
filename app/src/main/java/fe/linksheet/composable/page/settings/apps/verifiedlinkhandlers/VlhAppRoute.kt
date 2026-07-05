package fe.linksheet.composable.page.settings.apps.verifiedlinkhandlers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.linksheet.compose.extension.listHelper
import app.linksheet.compose.preview.PreviewContainer
import app.linksheet.compose.util.drawBitmap
import app.linksheet.compose.util.listState
import app.linksheet.feature.app.core.DomainVerificationAppInfo
import app.linksheet.feature.app.core.LinkHandling
import app.linksheet.testing.fake.PackageInfoFakes
import app.linksheet.testing.fake.toDomainVerificationAppInfo
import fe.android.compose.icon.BitmapIconPainter
import fe.android.compose.icon.IconPainter
import fe.android.compose.text.DefaultContent.Companion.text
import fe.android.compose.text.ProvideContentColorOptionsStyleText
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.android.compose.text.TextContent
import fe.composekit.component.appbar.SearchTopAppBar
import fe.composekit.component.dialog.DialogDefaults
import fe.composekit.component.list.column.SaneLazyColumnLayout
import fe.composekit.component.list.item.ContentPosition
import fe.composekit.component.list.item.type.CheckboxListItem
import fe.composekit.component.page.SaneSettingsScaffold
import fe.linksheet.R
import fe.linksheet.module.viewmodel.VerifiedLinkHandlerViewModel
import fe.linksheet.module.viewmodel.VlhStateHolder
import kotlinx.coroutines.flow.collectLatest
import my.nanihadesuka.compose.InternalLazyColumnScrollbar
import my.nanihadesuka.compose.ScrollbarSettings
import org.koin.androidx.compose.koinViewModel
import app.linksheet.compose.R as CommonR

@Composable
fun VlhAppRoute(
    onBackPressed: () -> Unit,
    viewModel: VerifiedLinkHandlerViewModel = koinViewModel(),
) {
    val filteredHosts by viewModel.filteredHostsFlow.collectAsStateWithLifecycle()
    val preferredHostSet by viewModel.preferredAppHostSetFlow.collectAsStateWithLifecycle()
    val filter by viewModel.searchQuery.collectAsStateWithLifecycle()
    val filterState = rememberTextFieldState(filter)
    LaunchedEffect(filterState) {
        snapshotFlow { filterState.text.toString() }.collectLatest {
            viewModel.search(it)
        }
    }
    val appInfo = viewModel.appInfo
    if (appInfo != null) {
        val vlhStateHolder = remember(appInfo, preferredHostSet) {
            val hostState = createHostState(
                appInfo.hostSet,
                appInfo.linkHandling,
                preferredHostSet
            )
            VlhStateHolder(hostState, hostState.toMap())
        }

        VlhAppRouteInternal(
            title = appInfo.appInfo.label,
            vlhStateHolder = vlhStateHolder,
            filteredHosts = filteredHosts,
            onBackPressed = onBackPressed,
            textFieldState = filterState,
            onSearch = viewModel::search,
            onSave = {
                viewModel.save(vlhStateHolder).invokeOnCompletion {
                    onBackPressed()
                }
            }
        )
    }
}

@Composable
private fun VlhAppRouteInternal(
    title: String,
    vlhStateHolder: VlhStateHolder,
    filteredHosts: List<String>?,
    onBackPressed: () -> Unit,
    textFieldState: TextFieldState,
    onSearch: (String) -> Unit,
    onSave: () -> Unit
) {
    val lazyListState = rememberLazyListState()
    val listState = remember(filteredHosts?.size, textFieldState.text) {
        listState(list = filteredHosts, filter = textFieldState.text)
    }

    SaneSettingsScaffold(
        topBar = {
            SearchTopAppBar(
                titleContent = text(title),
                placeholderContent = textContent(R.string.settings_verified_link_handler__placeholder_filter_hosts),
                state = textFieldState,
                onSearch = onSearch,
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = stringResource(CommonR.string.generic__button_text_cancel),
                        )
                    }
                },
                actions = {
                    ActionDropdown(
                        onSelectAllClick = {
                            filteredHosts?.forEach { host ->
                                vlhStateHolder.put(host, true)
                            }
                        },
                        onUnselectAllClick = {
                            filteredHosts?.forEach { host ->
                                vlhStateHolder.put(host, false)
                            }
                        },
                        onResetClick = {
                            vlhStateHolder.reset()
                        }
                    )
                },
            )
        },
        floatingActionButton = {
            if (vlhStateHolder.hasChanges) {
                ExtendedFloatingActionButton(
                    modifier = Modifier.padding(paddingValues = WindowInsets.navigationBars.asPaddingValues()),
                    text = { Text(text = stringResource(id = CommonR.string.generic__button_text_save)) },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Save,
                            contentDescription = null
                        )
                    },
                    onClick = onSave
                )
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            Box(modifier = Modifier) {
                SaneLazyColumnLayout(
                    state = lazyListState,
                    padding = PaddingValues()
                ) {
                    item(key = "0") {
                        // Works around odd re-order scroll behavior: https://issuetracker.google.com/issues/234223556
                    }
                    listHelper(
                        noItems = R.string.settings_verified_link_handler__text_no_hosts,
                        notFound = R.string.settings_verified_link_handler__text_no_such_host_found,
                        listState = listState,
                        list = filteredHosts,
                        listKey = { it },
                        loaderEnabled = false,
                    ) { host, padding, shape ->
                        CheckboxListItem(
                            padding = padding,
                            shape = shape,
                            checked = vlhStateHolder.get(host) ?: false,
                            onCheckedChange = {
                                vlhStateHolder.put(host, it)
                            },
                            position = ContentPosition.Leading,
                            headlineContent = text(host),
                            otherContent = null,
                            innerPadding = DialogDefaults.ListItemInnerPadding.copy(
                                vertical = 4.dp
                            ),
                            textOptions = DialogDefaults.ListItemTextOptions,
                            colors = DialogDefaults.ListItemColors
                        )
                    }
                }
                InternalLazyColumnScrollbar(
                    modifier = Modifier.padding(paddingValues = WindowInsets.navigationBars.asPaddingValues()),
                    state = lazyListState,
                    settings = ScrollbarSettings.Default.copy(
//                        alwaysShowScrollbar = true,
                        thumbSelectedColor = MaterialTheme.colorScheme.primary,
                        thumbUnselectedColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    }
}

@Composable
private fun ActionDropdown(
    onSelectAllClick: () -> Unit,
    onUnselectAllClick: () -> Unit,
    onResetClick: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val resources = LocalResources.current

    // Icon button should have a tooltip associated with it for a11y.
    TooltipBox(
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
        tooltip = {
            PlainTooltip(
                Modifier.semantics {
                    // TODO(b/496338253): Remove this modifier once bug where tooltip text is
                    //  not announced by a11y screen readers is resolved.
                    liveRegion = LiveRegionMode.Assertive
                    paneTitle = resources.getString(CommonR.string.options)
                }
            ) {
                Text(text = stringResource(id = CommonR.string.options))
            }
        },
        state = rememberTooltipState(),
    ) {
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Default.MoreVert, contentDescription = stringResource(id = CommonR.string.options))
        }
    }
    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
        DropdownMenuItem(
            text = { Text(text = stringResource(id = CommonR.string.generic__button_text_select_all)) },
            onClick = {
                onSelectAllClick()
                expanded = false
            },
//            leadingIcon = { Icon(Icons.Outlined.Edit, contentDescription = null) },
        )
        DropdownMenuItem(
            text = { Text(text = stringResource(id = CommonR.string.generic__button_text_unselect_all)) },
            onClick = {
                onUnselectAllClick()
                expanded = false
            },
//            leadingIcon = { Icon(Icons.Outlined.Settings, contentDescription = null) },
        )
        DropdownMenuItem(
            text = { Text(text = stringResource(id = CommonR.string.generic__button_text_reset)) },
            leadingIcon = { Icon(Icons.Outlined.RestartAlt, contentDescription = null) },
            onClick = {
                onResetClick()
                expanded = false
            },
        )
    }
}

@Composable
private fun RowScope.VlhButton(
    textContent: TextContent,
    iconPainter: IconPainter,
    weight: Float,
    shape: Shape,
    onClick: (() -> Unit)? = null,
) {
    FilledTonalButton(
        modifier = Modifier.weight(weight),
        shape = shape,
        onClick = onClick ?: {}
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                painter = iconPainter.rememberPainter(),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )

            ProvideContentColorOptionsStyleText(
                contentColor = MaterialTheme.colorScheme.primary,
//                textOptions = TextOptions(style = MaterialTheme.typography.bodyMedium),
                content = textContent.content
            )
        }
    }
}


@Preview
@Composable
private fun VlhAppRouteInternalPreview() {
    val icon = drawBitmap(Size(24f, 24f)) { drawCircle(Color.Red) }
    VlhAppRouteInternalPreviewBase(
        app = PackageInfoFakes.Youtube.toDomainVerificationAppInfo(
            linkHandling = LinkHandling.Allowed,
            stateNone = mutableListOf("google.com"),
            stateSelected = mutableListOf("test.com"),
            stateVerified = mutableListOf(),
            icon = BitmapIconPainter.bitmap(icon)
        )
    )
}

@Composable
private fun VlhAppRouteInternalPreviewBase(app: DomainVerificationAppInfo) {
    PreviewContainer {
//        VlhAppRouteInternal(
//            onBackPressed = {},
//            appInfo = app,
//            preferredApps = emptyList(),
//            openSettings = {
//
//            },
//            onSave = {}
//        )
    }
}
