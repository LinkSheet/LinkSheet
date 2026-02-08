package app.linksheet.feature.libredirect.ui

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.linksheet.compose.extension.collectOnIO
import app.linksheet.compose.page.SaneScaffoldSettingsPage
import app.linksheet.compose.preview.PreviewContainer
import app.linksheet.feature.libredirect.FrontendState
import app.linksheet.feature.libredirect.LibRedirectData
import app.linksheet.feature.libredirect.R
import app.linksheet.feature.libredirect.ServiceSettings
import app.linksheet.feature.libredirect.database.entity.LibRedirectDefault
import app.linksheet.feature.libredirect.viewmodel.LibRedirectServiceSettingsViewModel
import fe.android.compose.content.OptionalContent
import fe.android.compose.extension.enabled
import fe.android.compose.text.DefaultContent.Companion.text
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.android.compose.text.TextContent
import fe.composekit.component.CommonDefaults
import fe.composekit.component.ContentType
import fe.composekit.component.list.item.ContentPosition
import fe.composekit.component.list.item.toEnabledContentSet
import fe.composekit.component.list.item.type.RadioButtonListItem
import fe.composekit.component.list.item.type.SwitchListItem
import fe.composekit.component.shape.CustomShapeDefaults
import fe.linksheet.web.HostUtil
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


@Composable
fun LibRedirectServiceSettingsRoute(
    onBackPressed: () -> Unit,
    serviceKey: String?,
    viewModel: LibRedirectServiceSettingsViewModel = koinViewModel(parameters = {
        parametersOf(serviceKey)
    }),
) {
    LaunchedEffect(key1 = Unit) {
        viewModel.loadSettings()
    }

    val settings by viewModel.settings.collectAsStateWithLifecycle()

    val selectedFrontend by viewModel.selectedFrontend.collectOnIO(null)
    val enabled by viewModel.enabled.collectOnIO(false)

    let2(settings, selectedFrontend) { settings, (default, state) ->
        val userInstances by viewModel.getUserInstances(state.frontendKey).collectAsStateWithLifecycle(
            initialValue = emptyList()
        )
        LibRedirectServiceSettingsRouteInternal(
            enabled = enabled,
            settings = settings,
            selected = default,
            frontendState = state,
            instances = state.instances,
            userInstances = userInstances,
            onBackPressed = onBackPressed,
            customInstancesExperiment = viewModel.customInstancesExperiment(),
            updateState = viewModel::updateState,
            updateInstance = viewModel::updateInstance,
            resetServiceToFrontend = viewModel::resetServiceToFrontend,
            addInstance = {
                viewModel.addInstance(state.frontendKey, it.toString())
            },
            deleteInstance = {
                viewModel.deleteInstance(state.frontendKey, it)
            }
        )
    }
}


@OptIn(ExperimentalContracts::class)
inline fun <T1, T2, R> let2(t1: T1?, t2: T2?, block: (T1, T2) -> R): R? {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    if (t1 != null && t2 != null) {
        return block(t1, t2)
    }
    return null
}

@Composable
private fun LibRedirectServiceSettingsRouteInternal(
    enabled: Boolean,
    settings: ServiceSettings,
    selected: LibRedirectDefault,
    frontendState: FrontendState,
    instances: Set<String>,
    userInstances: List<String>,
    onBackPressed: () -> Unit,
    customInstancesExperiment: Boolean,
    updateState: (Boolean) -> Unit,
    updateInstance: (LibRedirectDefault, String, Boolean) -> Unit,
    resetServiceToFrontend: (FrontendState) -> Unit,
    addInstance: (Uri) -> Unit,
    deleteInstance: (String) -> Unit
) {
    val libRedirectDialog = rememberLibRedirectInstanceDialog(onConfirm = addInstance)

    SaneScaffoldSettingsPage(
        headline = stringResource(R.string.lib_redirect_service, settings.service.name),
        onBackPressed = onBackPressed,
        floatingActionButton = {
            if (customInstancesExperiment) {
                FloatingActionButton(
                    modifier = Modifier.padding(paddingValues = WindowInsets.navigationBars.asPaddingValues()),
                    onClick = {
                        libRedirectDialog.open()
                    }
                ) {
                    Icon(imageVector = Icons.Outlined.Add, contentDescription = null)
                }
            }
        }
    ) {
        item(key = R.string.enabled, contentType = ContentType.SingleGroupItem) {
            SwitchListItem(
                checked = enabled,
                onCheckedChange = { updateState(it) },
                position = ContentPosition.Trailing,
                headlineContent = textContent(R.string.enabled)
            )
        }

        divider(id = R.string.frontend)

        item(key = "dropdown", contentType = ContentType.SingleGroupItem) {
            FrontendDropdown(
                enabled = enabled,
                selected = frontendState,
                frontends = settings.frontends,
                onChange = resetServiceToFrontend
            )
        }

        divider(id = R.string.instance)

        group(size = instances.size + userInstances.size + 1) {
            item(key = R.string.random_instance) { padding, shape ->
                InstanceListItem(
                    instance = LibRedirectDefault.randomInstance,
                    shape = shape,
                    padding = padding,
                    enabled = enabled,
                    headlineContent = textContent(R.string.random_instance),
                    selected = selected.instanceUrl == LibRedirectDefault.randomInstance,
                    onSelect = { updateInstance(selected, LibRedirectDefault.randomInstance, false) },
                )
            }

            for (instance in instances) {
                item(key = instance) { padding, shape ->
                    InstanceListItem(
                        instance = instance,
                        shape = shape,
                        padding = padding,
                        enabled = enabled,
                        selected = selected.instanceUrl == instance,
                        onSelect = { updateInstance(selected, instance, false) }
                    )
                }
            }

            for (instance in userInstances) {
                item(key = instance) { padding, shape ->
                    UserDefinedInstanceListItem(
                        instance = instance,
                        shape = shape,
                        padding = padding,
                        enabled = enabled,
                        selected = selected.instanceUrl == instance,
                        onSelect = { updateInstance(selected, instance, true) },
                        onDelete = { deleteInstance(instance) }
                    )
                }
            }
        }
    }
}

@Composable
private fun InstanceListItem(
    shape: Shape = CustomShapeDefaults.SingleShape,
    padding: PaddingValues = CommonDefaults.EmptyPadding,
    enabled: Boolean,
    selected: Boolean,
    instance: String,
    headlineContent: TextContent? = null,
    otherContent: OptionalContent = null,
    onSelect: () -> Unit,
) {
    RadioButtonListItem(
        shape = shape,
        padding = padding,
        enabled = enabled.toEnabledContentSet(),
        selected = selected,
        onSelect = onSelect,
        position = ContentPosition.Leading,
        headlineContent = headlineContent ?: text(HostUtil.cleanHttpsScheme(instance)),
        otherContent = otherContent
    )
}

@Composable
private fun UserDefinedInstanceListItem(
    shape: Shape = CustomShapeDefaults.SingleShape,
    padding: PaddingValues = CommonDefaults.EmptyPadding,
    enabled: Boolean,
    selected: Boolean,
    instance: String,
    onSelect: () -> Unit,
    onDelete: () -> Unit
) {
    InstanceListItem(
        shape = shape,
        padding = padding,
        enabled = enabled,
        selected = selected,
        onSelect = onSelect,
        instance = instance,
        otherContent = {
            FilledTonalIconButton(onClick = onDelete) {
                Icon(imageVector = Icons.Rounded.DeleteOutline, contentDescription = null)
            }
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FrontendDropdown(
    enabled: Boolean,
    selected: FrontendState,
    frontends: List<FrontendState>,
    onChange: (FrontendState) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (enabled) expanded = !expanded }
    ) {
        TextField(
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled)
                .fillMaxWidth()
                .enabled(enabled),
            value = selected.name,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            for (frontend in frontends) {
                FrontendDropdownItem(
                    frontend = frontend,
                    onClick = {
                        if (selected != frontend) {
                            onChange(frontend)
                        }
//                        if (selected.key != frontend.key) {
//                            onChange(frontend.key)
//                        }

                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun FrontendDropdownItem(
    frontend: FrontendState,
    onClick: () -> Unit,
) {
    DropdownMenuItem(
        text = { Text(text = frontend.name) },
        onClick = onClick
    )
}


@RequiresApi(Build.VERSION_CODES.P)
@Preview
@Composable
private fun LibRedirectServiceSettingsRoutePreview() {
    val libRedirectDefault = LibRedirectDefault("reddit", "libreddit", "https://redlib.catsarch.com")
    val frontendState = FrontendState(
        "reddit", "libreddit", "libreddit",
        LibRedirectData.LibRedditInstance.hosts.toSet(),
        "https://redlib.catsarch.com"
    )
    PreviewContainer {
        LibRedirectServiceSettingsRouteInternal(
            enabled = false,
            settings = ServiceSettings(
                service = LibRedirectData.RedditService,
                fallback = libRedirectDefault,
                frontends = listOf(frontendState),
                defaultFrontend = null
            ),
            selected = libRedirectDefault,
            frontendState = frontendState,
            instances = frontendState.instances,
            userInstances = listOf(),
            onBackPressed = {},
            customInstancesExperiment = false,
            updateState = {},
            updateInstance = { _, _, _ -> },
            resetServiceToFrontend = {},
            addInstance = { _ ->

            },
            deleteInstance = {

            }
        )
    }
}

@Preview
@Composable
private fun InstanceListItemPreview() {
    PreviewContainer {
        InstanceListItem(
            enabled = true,
            selected = false,
            instance = "reddit.linksheet.app",
            onSelect = {}
        )
    }
}
