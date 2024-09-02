package fe.linksheet.composable.page.settings.app

import android.content.pm.getInstalledPackagesCompat
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.OpenInBrowser
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fe.android.compose.extension.atElevation
import fe.android.compose.feedback.FeedbackType
import fe.android.compose.feedback.LocalHapticFeedbackInteraction
import fe.android.compose.icon.iconPainter
import fe.android.compose.text.ComposableTextContent.Companion.content
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.android.compose.text.TextContent
import fe.composekit.component.card.AlertCardContentLayout
import fe.composekit.component.card.AlertCardDefaults
import fe.composekit.component.icon.AppIconImage
import fe.composekit.component.icon.FilledIcon
import fe.composekit.component.list.column.SaneLazyColumnLayout
import fe.composekit.component.page.SaneSettingsScaffold
import fe.composekit.component.shape.CustomShapeDefaults
import fe.composekit.layout.column.SaneLazyListScope
import fe.linksheet.R
import fe.linksheet.composable.component.appbar.SaneLargeTopAppBar
import fe.linksheet.extension.android.isUserApp
import fe.linksheet.extension.android.toImageBitmap
import sh.calvin.reorderable.ReorderableCollectionItemScope
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppConfigRoute(
    onBackPressed: () -> Unit,
//    viewModel: AppConfigViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val interaction = LocalHapticFeedbackInteraction.current

    val host = "amazon.de"
    val app = context.packageManager.getInstalledPackagesCompat().first { it.applicationInfo.isUserApp() }

    val list = remember {
        mutableStateListOf(
            AppItem(
                label = app.applicationInfo.loadLabel(context.packageManager),
                packageName = app.applicationInfo.packageName,
                icon = app.applicationInfo.loadIcon(context.packageManager).toImageBitmap(),
            ),
            PreferredBrowserItem,
            BrowsersItem,
            NativeAppsItem
        )
    }

    var selectedIdx by remember { mutableIntStateOf(-1) }
    var pinnedCount by remember {
        mutableIntStateOf(0)
    }

    val lazyListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        Log.d("Test", "Drag $from $to")
//        list = list.toMutableList().apply {
//            add(to.index, removeAt(from.index))
//        }
        selectedIdx = -1
        list.add(to.index, list.removeAt(from.index))

        interaction.perform(FeedbackType.SegmentFrequentTick)
    }


    SaneScaffoldSettingsPage2(
        state = lazyListState,
        headline = stringResource(id = R.string.settings_app_config__title_configure_app_behavior, host),
        onBackPressed = onBackPressed,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                modifier = Modifier.padding(paddingValues = WindowInsets.navigationBars.asPaddingValues()),
                text = { Text(text = stringResource(id = R.string.settings_app_config__button_add_item)) },
                icon = { Icon(imageVector = Icons.Outlined.Add, contentDescription = null) },
                onClick = {

                }
            )
        }
    ) {
        itemsIndexed(items = list, key = { _, item -> item.hashCode() }) { index, item ->
            var isPinned by remember {
                mutableStateOf(index < pinnedCount)
            }

            ReorderableItem(
                modifier = Modifier.fillMaxWidth(),
                state = reorderableLazyListState,
                key = item.hashCode(),
                enabled = !isPinned
            ) {
                ItemCard2(
                    item = item,
                    isSelected = selectedIdx == index,
                    onLongClick = {
                        Log.d("OnLongClick", "onLongClick $selectedIdx $index")
                        selectedIdx = if (selectedIdx == index) -1 else index
                    },
                    onRemoveRequested = {
                        selectedIdx = -1
                        if (isPinned) {
                            pinnedCount--
                        }

                        list.removeAt(index)
                    },
                    isPinned = isPinned,
                    onPinRequested = {
                        selectedIdx = -1
                        if (isPinned) {
                            list.add(--pinnedCount, list.removeAt(index))
                        } else {
                            list.add(pinnedCount++, list.removeAt(index))
                        }

                        isPinned = !isPinned
                    }
//                    label = app.applicationInfo.loadLabel(context.packageManager).toString(),
//                    bitmap = app.applicationInfo.loadIcon(context.packageManager).toImageBitmap(),
//                    contentDescription = null
                )
            }
        }

//        divider(id =  R.string.settings_app_config__title_configure_app_behavior)
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun ReorderableCollectionItemScope.ItemCard2(
    modifier: Modifier = AlertCardDefaults.MinHeight,
    innerPadding: PaddingValues = AlertCardDefaults.InnerPadding,
    horizontalArrangement: Arrangement.Horizontal = AlertCardDefaults.HorizontalArrangement,
    item: Item,
    isSelected: Boolean,
    onLongClick: () -> Unit,
    onRemoveRequested: () -> Unit,
    isPinned: Boolean,
    onPinRequested: () -> Unit,
//    contentDescription: String?,
//    label: String,
//    headline: TextContent,
//    subtitle: TextContent
) {
    val interaction = LocalHapticFeedbackInteraction.current
    val interactionSource = remember { MutableInteractionSource() }
//    var pinned by remember { mutableStateOf(false) }

    val cardContainerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
    else MaterialTheme.colorScheme.surfaceContainerHighest

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CustomShapeDefaults.SingleShape)
            .combinedClickable(onClick = {}, onLongClick = onLongClick)
            .semantics { selected = isSelected },
        colors = CardDefaults.cardColors(
            containerColor = cardContainerColor
        )
//        elevation = CardDefaults.cardElevation(defaultElevation = if(isSelected) 2.dp else 0.dp)
//        interactionSource = interactionSource,
//        onClick = {}
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(modifier)
                .padding(innerPadding),
            horizontalArrangement = horizontalArrangement,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (item is AppItem) {
                AppIconImage(bitmap = item.icon, label = item.label.toString())
            } else if (item is IconItem) {
                FilledIconWrapper(item = item, parentContainerColor = cardContainerColor)
            }

            AlertCardContentLayout(
                modifier = Modifier.weight(1.0f),
                title = {
                    CompositionLocalProvider(
                        LocalTextStyle provides MaterialTheme.typography.titleMedium,
                        content = item.title.content
                    )
                },
                subtitle = {
                    CompositionLocalProvider(
                        LocalTextStyle provides MaterialTheme.typography.bodyMedium,
                        content = item.description.content
                    )
                }
            )

//            IconButton(onClick = { /*TODO*/ }) {
//                Icon(
//                    imageVector = ImageVector.vectorResource(id = if (pinned) R.drawable.keep_24px else R.drawable.keep_off_24px),
//                    contentDescription = null
//                )
//            }
            if (isSelected) {
                Row(modifier = Modifier.fillMaxHeight(), verticalAlignment = Alignment.CenterVertically) {
                    CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                        IconButton(
                            onClick = onRemoveRequested,
                        ) {
                            Icon(imageVector = Icons.Rounded.DeleteOutline, contentDescription = null)
                        }

                        IconButton(onClick = onPinRequested) {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                imageVector = ImageVector.vectorResource(id = if (isPinned) R.drawable.keep_filled_24px else R.drawable.keep_24px),
                                contentDescription = null
                            )
                        }
                    }
                }
            } else if (isPinned) {
                IconButton(onClick = onPinRequested) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = ImageVector.vectorResource(id = R.drawable.keep_filled_24px),
                        contentDescription = null
                    )
                }
            } else {
                IconButton(
                    modifier = Modifier.draggableHandle(
                        onDragStarted = { interaction.perform(FeedbackType.DragStart) },
                        onDragStopped = { interaction.perform(FeedbackType.GestureEnd) },
                        interactionSource = interactionSource,
                    ),
                    onClick = {},
                ) {
                    Icon(imageVector = Icons.Rounded.DragHandle, contentDescription = null)
                }
            }


//            AppIconImage(
//                bitmap = item.loadIcon(context),
//                label = item.label
//            )
        }
    }
}

@Composable
private fun FilledIconWrapper(item: IconItem, parentContainerColor: Color) {
    val containerColor = parentContainerColor.atElevation(
        MaterialTheme.colorScheme.surfaceTint, 6.dp
    )

    FilledIcon(
        icon = item.icon.iconPainter,
        iconSize = 20.dp,
        containerSize = 34.dp,
        contentDescription = null,
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = containerColor,
            contentColor = androidx.compose.material3.fix.contentColorFor(backgroundColor = containerColor)
        )
    )
}


private object NativeAppsItem : IconItem(
    title = textContent(R.string.settings_app_config__title_native_apps),
    description = textContent(R.string.settings_app_config__description_native_apps),
    icon = Icons.Outlined.Apps
) {

}

private object BrowsersItem : IconItem(
    title = textContent(R.string.settings_app_config__title_browsers),
    description = textContent(R.string.settings_app_config__description_browsers),
    icon = Icons.Outlined.OpenInBrowser
) {

}

private object PreferredBrowserItem : IconItem(
    title = textContent(R.string.settings_app_config__title_preferred_browser),
    description = textContent(R.string.settings_app_config__description_preferred_browser),
    icon = Icons.Outlined.Public
)

private class AppItem(val label: CharSequence, val packageName: String, val icon: ImageBitmap) : Item(
    title = content {
        Text(text = label.toString(), overflow = TextOverflow.Ellipsis, maxLines = 1)
    },
    description = content {
        Text(text = packageName, overflow = TextOverflow.Ellipsis, maxLines = 1)
    },
)


private open class IconItem(
    title: TextContent, description: TextContent,
    val icon: ImageVector,
) : Item(title, description)

private open class Item(
    val title: TextContent,
    val description: TextContent,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SaneScaffoldSettingsPage2(
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    headline: String,
    onBackPressed: () -> Unit,
    enableBackButton: Boolean = true,
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    content: SaneLazyListScope.() -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState(),
        canScroll = { true }
    )

    SaneSettingsScaffold(
        modifier = modifier.then(Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)),
        topBar = {
            SaneLargeTopAppBar(
                headline = headline,
                enableBackButton = enableBackButton,
                onBackPressed = onBackPressed,
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = floatingActionButtonPosition,
        content = { padding ->
            SaneLazyColumnLayout(
                state = state,
                padding = padding,
                verticalArrangement = Arrangement.spacedBy(5.dp),
                content = content
            )
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SelectableCard() {
    val interactionSource = remember { MutableInteractionSource() }

    val isLongPressed by interactionSource.collectIsPressedAsState()

    Card(
        modifier = Modifier
            .combinedClickable(onClick = {}, onLongClick = { Log.d("Clickable", "Clicked") })
            .fillMaxWidth(),
        interactionSource = interactionSource,
        onClick = {
            Log.d("OnClick", "Onclick")
        }
//        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
//        shape = CardDefaults.shape,
//        elevation = CardDefaults.cardElevation(),
//        border = BorderStroke(1.dp, Color.Black)
    ) {
        Text(
            text = "Selectable Card",
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
private fun SelectableCardPreview() {
    SelectableCard()
}


@Preview
@Composable
private fun AppConfigRoutePreview() {
//    AppConfigViewModel()

    AppConfigRoute(onBackPressed = {})
}
