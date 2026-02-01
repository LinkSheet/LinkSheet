package app.linksheet.feature.libredirect.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
import fe.android.compose.extension.enabled
import fe.android.compose.text.DefaultContent.Companion.text
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.component.ContentType
import fe.composekit.component.list.item.ContentPosition
import fe.composekit.component.list.item.toEnabledContentSet
import fe.composekit.component.list.item.type.RadioButtonListItem
import fe.composekit.component.list.item.type.SwitchListItem
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

    let2(settings, selectedFrontend) { settings, selectedFrontend ->
        LibRedirectServiceSettingsRouteInternal(
            enabled = enabled,
            settings = settings,
            selected = selectedFrontend.first,
            frontendState = selectedFrontend.second,
            onBackPressed = onBackPressed,
            updateState = viewModel::updateState,
            updateInstance = viewModel::updateInstance,
            resetServiceToFrontend = viewModel::resetServiceToFrontend,
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
    onBackPressed: () -> Unit,
    updateState: (Boolean) -> Unit,
    updateInstance: (LibRedirectDefault, String) -> Unit,
    resetServiceToFrontend: (FrontendState) -> Unit
) {
    SaneScaffoldSettingsPage(
        headline = stringResource(R.string.lib_redirect_service, settings.service.name),
        onBackPressed = onBackPressed,
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.padding(paddingValues = WindowInsets.navigationBars.asPaddingValues()),
                onClick = {

                }
            ) {
                Icon(imageVector = Icons.Outlined.Add, contentDescription = null)
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

        group(size = frontendState.instances.size + 1) {
            item(key = R.string.random_instance) { padding, shape ->
                RadioButtonListItem(
                    shape = shape,
                    padding = padding,
                    enabled = enabled.toEnabledContentSet(),
                    selected = selected.instanceUrl == LibRedirectDefault.randomInstance,
                    onSelect = {
                        updateInstance(selected, LibRedirectDefault.randomInstance)
                    },
                    position = ContentPosition.Leading,
                    headlineContent = textContent(R.string.random_instance),
                    otherContent = null
                )
            }

            for (instance in frontendState.instances) {
                item(key = instance) { padding, shape ->
                    RadioButtonListItem(
                        shape = shape,
                        padding = padding,
                        enabled = enabled.toEnabledContentSet(),
                        selected = selected.instanceUrl == instance,
                        onSelect = {
                            updateInstance(selected, instance)
                        },
                        position = ContentPosition.Leading,
                        headlineContent = text(HostUtil.cleanHttpsScheme(instance)),
                        otherContent = null
                    )
                }
            }
        }
    }
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
            onBackPressed = {},
            updateState = {},
            updateInstance = { _, _ -> },
            resetServiceToFrontend = {}
        )
    }
}
