@file:OptIn(ExperimentalUuidApi::class, ExperimentalMaterial3Api::class)

package app.linksheet.feature.engine.ui.route

import android.content.res.Resources
import androidx.annotation.StringRes
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material.icons.rounded.SelectAll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.linksheet.compose.extension.collectOnIO
import app.linksheet.compose.extension.listIndexedHelper
import app.linksheet.compose.util.ListState
import app.linksheet.feature.engine.R
import app.linksheet.feature.engine.database.entity.Scenario
import app.linksheet.feature.engine.navigation.ScenarioRoute
import app.linksheet.feature.engine.viewmodel.ScenarioOverviewViewModel
import fe.android.compose.feedback.FeedbackType
import fe.android.compose.feedback.LocalHapticFeedbackInteraction
import fe.android.compose.text.ComposableTextContent.Companion.content
import fe.android.compose.text.DefaultContent.Companion.text
import fe.composekit.component.CommonDefaults
import fe.composekit.component.PreviewThemeNew
import fe.composekit.component.list.column.shape.ClickableShapeListItem
import fe.composekit.component.page.SaneScaffoldSettingsPageInternal
import fe.composekit.component.shape.CustomShapeDefaults
import fe.composekit.route.Route
import org.koin.androidx.compose.koinViewModel
import sh.calvin.reorderable.ReorderableCollectionItemScope
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import kotlin.uuid.ExperimentalUuidApi
import app.linksheet.compose.R as CommonR


@Composable
fun ScenarioOverviewRoute(
    onBackPressed: () -> Unit,
    navigate: (Route) -> Unit,
    viewModel: ScenarioOverviewViewModel = koinViewModel(),
) {
    val scenarios by viewModel.getAll().collectOnIO(emptyList())
    ScenarioOverviewRouteInternal(
        scenarios = scenarios,
        onCreate = {
            viewModel.createScenario(it)
        },
        navigateScenario = {
            navigate(ScenarioRoute(it.id))
        },
        onBackPressed = onBackPressed,
        move = { from, to ->
            viewModel.move(from, to)
            true
        }
    )
}

@Composable
private fun ScenarioOverviewRouteInternal(
    scenarios: List<Scenario>,
    onCreate: (String) -> Unit,
    navigateScenario: (Scenario) -> Unit,
    onBackPressed: () -> Unit,
    move: (from: Scenario, to: Scenario) -> Boolean
) {
    val context = LocalContext.current
    val interaction = LocalHapticFeedbackInteraction.current

    var localScenarios by remember(scenarios) {
        mutableStateOf(scenarios)
    }
    val mapState = remember(scenarios) {
        ListState.Items
    }

    var selectedItems by rememberSaveable { mutableStateOf(setOf<Int>()) }
    val hasSelection = rememberSaveable(selectedItems) { selectedItems.isNotEmpty() }
    var pinnedCount by remember {
        mutableIntStateOf(0)
    }

    val lazyListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        localScenarios = localScenarios.toMutableList().apply {
            add(to.index, removeAt(from.index))
        }
        move(scenarios[from.index], scenarios[to.index])
        interaction.perform(FeedbackType.SegmentFrequentTick)
    }
    val resources = LocalResources.current

    val newScenarioDialog = rememberNewScenarioDialog(onCreate)
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState(),
        canScroll = { true }
    )


    SaneScaffoldSettingsPageInternal(
//        headline = stringResource(id = R.string.settings_scenario__title_scenarios),
//        onBackPressed = onBackPressed,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (hasSelection) {
                            stringResource(id = CommonR.string.generic__n_selected, selectedItems.size)
                        } else {
                            stringResource(id = R.string.settings_scenario__title_scenarios)
                        }
                    )
                },
                actions = {
                    if (hasSelection) {
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.Rounded.SelectAll,
                                contentDescription = null
                            )
                        }
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.Rounded.Delete,
                                contentDescription = null
                            )
                        }
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.padding(paddingValues = WindowInsets.navigationBars.asPaddingValues()),
                onClick = { newScenarioDialog.open() }
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = stringResource(id = CommonR.string.generic__button_text_add)
                )
            }
        },
        state = lazyListState
    ) {
        listIndexedHelper(
            noItems = R.string.settings_scenario__title_scenarios,
            listState = mapState,
            list = localScenarios,
            listKey = { it.hashCode() },
        ) { index, item, padding, shape ->
            var isPinned by remember {
                mutableStateOf(index < pinnedCount)
            }

            ReorderableItem(
                modifier = Modifier.fillMaxWidth(),
                state = reorderableLazyListState,
                key = item.hashCode(),
                enabled = !isPinned
            ) {
                val isSelected = index in selectedItems
                ScenarioListItem(
                    scenario = item,
                    shape = shape,
                    padding = padding,
                    customActions = listOf(
                        createAction(resources, CommonR.string.generic__action_move_up) {
                            if (index > 0) move(item, localScenarios[index - 1]) else false
                        },
                        createAction(resources, CommonR.string.generic__action_move_down) {
                            if (index < localScenarios.size - 1) move(item, localScenarios[index + 1]) else false
                        },
                    ),
                    isSelected = isSelected,
                    onClick = { navigateScenario(item) },
                    onLongClick = {
                        if(isSelected) {
                            selectedItems -= index
                        } else {
                            selectedItems += index
                        }
//                        Log.d("OnLongClick", "onLongClick $selectedIdx $index")
//                        selectedIdx = if (selectedIdx == index) -1 else index
                    },
                    onRemoveRequested = {
//                        selectedIdx = -1
//                        if (isPinned) {
//                            pinnedCount--
//                        }

//                        list.removeAt(index)
                    },
                    isPinned = isPinned,
                    onPinRequested = {
//                        selectedIdx = -1
//                        if (isPinned) {
//                            list.add(--pinnedCount, list.removeAt(index))
//                        } else {
//                            list.add(pinnedCount++, list.removeAt(index))
//                        }

                        isPinned = !isPinned
                    },
                )
            }
        }
    }
}

fun createAction(
    resources: Resources,
    @StringRes id: Int,
    action: () -> Boolean
): CustomAccessibilityAction {
    return CustomAccessibilityAction(
        label = resources.getString(id),
        action = action
    )
}

@Composable
private fun ReorderableCollectionItemScope.ScenarioListItem(
    scenario: Scenario,
    shape: Shape = CustomShapeDefaults.SingleShape,
    padding: PaddingValues = CommonDefaults.EmptyPadding,
    customActions: List<CustomAccessibilityAction>,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onRemoveRequested: () -> Unit,
    isPinned: Boolean,
    onPinRequested: () -> Unit,
) {
    val interaction = LocalHapticFeedbackInteraction.current
    val resources = LocalResources.current
    val interactionSource = remember { MutableInteractionSource() }
    ClickableShapeListItem(
        modifier = Modifier.semantics { this@semantics.customActions = customActions },
        shape = shape,
        padding = padding,
        onClick = onClick,
        onLongClick = onLongClick,
        headlineContent = text(scenario.name),
        supportingContent = content {
            Text(
                text = scenario.id.toString(),
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
        },
        trailingContent = {
//            Row(modifier = Modifier.fillMaxHeight(), verticalAlignment = Alignment.CenterVertically) {
//                IconButton(onClick = onUp) {
//                    Icon(
//                        modifier = Modifier.size(24.dp),
//                        imageVector = Icons.Rounded.KeyboardArrowUp,
//                        contentDescription = null
//                    )
//                }
//                IconButton(onClick = onDown) {
//                    Icon(
//                        modifier = Modifier.size(24.dp),
//                        imageVector = Icons.Rounded.KeyboardArrowDown,
//                        contentDescription = null
//                    )
//                }
//            }
            if (isSelected) {
                Row(modifier = Modifier.fillMaxHeight(), verticalAlignment = Alignment.CenterVertically) {
                    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
                        IconButton(
                            onClick = onRemoveRequested,
                        ) {
                            Icon(imageVector = Icons.Rounded.DeleteOutline, contentDescription = null)
                        }

                        IconButton(onClick = onPinRequested) {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                imageVector = ImageVector.vectorResource(
                                    id = if (isPinned) R.drawable.keep_filled_24px else R.drawable.keep_24px
                                ),
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
        }
    )
}


@OptIn(ExperimentalUuidApi::class)
@Preview
@Composable
private fun ScenarioListItemPreview() {
//    ScenarioListItem(
//        scenario = ScenarioEntity(
//            id = Uuid.NIL,
//            name = "Test scenario",
//            position = 0,
//            referrerApp = null
//        )
//    )
}


@Preview
@Composable
private fun ScenarioRoutePreview() {
    PreviewThemeNew {
        ScenarioOverviewRouteInternal(
            scenarios = listOf(
                Scenario(
                    name = "Test scenario",
                    position = 0,
                    referrerApp = null
                ),
                Scenario(
                    name = "Test scenario 2",
                    position = 1,
                    referrerApp = null
                ),
                Scenario(
                    name = "Test scenario 3",
                    position = 1,
                    referrerApp = null
                )
            ),
            onCreate = {},
            navigateScenario = {},
            onBackPressed = {},
            move = { from, to -> false }
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview
@Composable
fun Test() {
    Column(Modifier.selectableGroup()) {
        HorizontalDivider()

        var selectedIndex: Int? by rememberSaveable { mutableStateOf(null) }
        repeat(3) { idx ->
            val selected = selectedIndex == idx
            ListItem(
                selected = selected,
                onClick = { selectedIndex = if (selected) null else idx },
                leadingContent = { RadioButton(selected = selected, onClick = null) },
                trailingContent = { Icon(Icons.Default.Favorite, contentDescription = null) },
                supportingContent = { Text("Additional info") },
                content = { Text("Item ${idx + 1}") },
            )

            HorizontalDivider()
        }
    }
}
