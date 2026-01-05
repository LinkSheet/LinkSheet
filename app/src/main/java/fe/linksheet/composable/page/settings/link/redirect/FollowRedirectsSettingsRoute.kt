package fe.linksheet.composable.page.settings.link.redirect

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.linksheet.compose.list.item.PreferenceSwitchListItem
import app.linksheet.compose.page.SaneScaffoldSettingsPage
import fe.android.compose.text.AnnotatedStringResourceContent.Companion.annotatedStringResource
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.component.ContentType
import fe.composekit.component.PreviewThemeNew
import fe.composekit.component.list.item.toEnabledContentSet
import fe.composekit.component.list.item.type.SliderListItem
import fe.composekit.preference.collectAsStateWithLifecycle
import fe.linksheet.R
import fe.linksheet.module.resolver.FollowRedirectsMode
import fe.linksheet.module.viewmodel.FollowRedirectsSettingsViewModel
import fe.linksheet.util.buildconfig.LinkSheetAppConfig
import fe.linksheet.web.Darknet
import org.koin.androidx.compose.koinViewModel

private val followRedirectsModes = listOf(FollowRedirectsMode.Auto, FollowRedirectsMode.Manual)


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FollowRedirectsSettingsRoute(
    onBackPressed: () -> Unit,
    viewModel: FollowRedirectsSettingsViewModel = koinViewModel()
) {
//    val followRedirectsKnownTrackers = dialogHelper<Unit, List<String>, Unit>(
//        fetch = { FastForwardRules.rules["tracker"]?.map { it.toString() } ?: emptyList() },
//        awaitFetchBeforeOpen = true,
//        dynamicHeight = true
//    ) { trackers, close ->
//        FollowRedirectsKnownTrackersDialog(trackers!!, close)
//    }

    val darknets = remember {
        Darknet.entries.joinToString(separator = ", ") { it.displayName }
    }
    val followRedirects by viewModel.followRedirects.collectAsStateWithLifecycle()
    val followRedirectsMode by viewModel.followRedirectsMode.collectAsStateWithLifecycle()
    val contentSet = remember(followRedirects) { followRedirects.toEnabledContentSet() }

    SaneScaffoldSettingsPage(headline = stringResource(id = R.string.follow_redirects), onBackPressed = onBackPressed) {
        item(key = R.string.follow_redirects, contentType = ContentType.SingleGroupItem) {
            PreferenceSwitchListItem(
                statePreference = viewModel.followRedirects,
                headlineContent = textContent(R.string.follow_redirects),
                supportingContent = annotatedStringResource(R.string.follow_redirects_explainer),
            )
        }

        divider(id = R.string.generic__text_mode)

        item(key = -R.string.generic__text_mode, contentType = ContentType.SingleGroupItem) {
            ConnectedToggleButtonFlowRow(
                current = followRedirectsMode,
                items = followRedirectsModes,
                onChange = viewModel.followRedirectsMode,
                itemContent = {
                    Text(
                        text = stringResource(
                            id = when (it) {
                                FollowRedirectsMode.Auto -> R.string.settings_follow_redirects__title_mode_auto
                                FollowRedirectsMode.Manual -> R.string.settings_follow_redirects__title_mode_manual
                            }
                        )
                    )
                }
            )
        }

        divider(id = R.string.options)

        group(base = 6, LinkSheetAppConfig.isPro()) {
            item(key = R.string.follow_redirects_local_cache) { padding, shape ->
                PreferenceSwitchListItem(
                    enabled = contentSet,
                    shape = shape,
                    padding = padding,
                    statePreference = viewModel.followRedirectsLocalCache,
                    headlineContent = textContent(R.string.follow_redirects_local_cache),
                    supportingContent = textContent(R.string.follow_redirects_local_cache_explainer),
                )
            }

            item(key = R.string.follow_only_known_trackers) { padding, shape ->
                val followRedirectsExternalService by viewModel.followRedirectsExternalService.collectAsStateWithLifecycle()
                // TODO: This settings should allow the user to add their own rules in the future, or at least display a _understandable_ list of known tracker domains
                PreferenceSwitchListItem(
                    enabled = (followRedirects && !followRedirectsExternalService).toEnabledContentSet(),
                    shape = shape,
                    padding = padding,
                    statePreference = viewModel.followOnlyKnownTrackers,
//                    onContentClick = { followRedirectsKnownTrackers.open(Unit) },
                    headlineContent = textContent(R.string.follow_only_known_trackers),
                    supportingContent = annotatedStringResource(R.string.follow_only_known_trackers_explainer),
                )
            }

            item(key = R.string.allow_darknets) { padding, shape ->
                PreferenceSwitchListItem(
                    enabled = contentSet,
                    shape = shape,
                    padding = padding,
                    statePreference = viewModel.followRedirectsAllowsDarknets,
                    headlineContent = textContent(R.string.allow_darknets),
                    supportingContent = textContent(id = R.string.follow_redirects_allow_darknets_explainer, darknets),
                )
            }

            item(key = R.string.settings_links_follow_redirects__title_local_network) { padding, shape ->
                PreferenceSwitchListItem(
                    enabled = contentSet,
                    shape = shape,
                    padding = padding,
                    statePreference = viewModel.followRedirectsAllowLocalNetwork,
                    headlineContent = textContent(R.string.settings_links_follow_redirects__title_local_network),
                    supportingContent = textContent(R.string.settings_links_follow_redirects__text_local_network),
                )
            }

            if (LinkSheetAppConfig.isPro()) {
                item(key = R.string.follow_redirects_external_service) { padding, shape ->
                    PreferenceSwitchListItem(
                        enabled = (followRedirects && LinkSheetAppConfig.isPro()).toEnabledContentSet(),
                        shape = shape,
                        padding = padding,
                        statePreference = viewModel.followRedirectsExternalService,
                        headlineContent = textContent(R.string.follow_redirects_external_service),
                        supportingContent = annotatedStringResource(R.string.follow_redirects_external_service_explainer),
                    )
                }
            }

            item(key = R.string.settings_links_follow_redirects__title_skip_browser) { padding, shape ->
                PreferenceSwitchListItem(
                    enabled = contentSet,
                    shape = shape,
                    padding = padding,
                    statePreference = viewModel.followRedirectsSkipBrowser,
                    headlineContent = textContent(R.string.settings_links_follow_redirects__title_skip_browser),
                    supportingContent = textContent(id = R.string.settings_links_follow_redirects__text_skip_browser),
                )
            }

            item(key = R.string.request_timeout) { padding, shape ->
                val requestTimeout by viewModel.requestTimeout.collectAsStateWithLifecycle()

                SliderListItem(
                    enabled = contentSet,
                    shape = shape,
                    padding = padding,
                    valueRange = 0f..30f,
                    value = requestTimeout.toFloat(),
                    onValueChange = { viewModel.requestTimeout(it.toInt()) },
                    valueFormatter = { it.toInt().toString() },
                    headlineContent = textContent(R.string.request_timeout),
                    supportingContent = annotatedStringResource(R.string.request_timeout_explainer),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun <T> ConnectedToggleButtonFlowRow(
    modifier: Modifier = Modifier,
    current: T,
    items: List<T>,
    onChange: (T) -> Unit,
    itemContent: @Composable RowScope.(T) -> Unit
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        for ((index, item) in items.withIndex()) {
            ToggleButton(
                modifier = Modifier.semantics { role = Role.RadioButton },
                checked = item == current,
                onCheckedChange = { onChange(item) },
                shapes = when (index) {
                    0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                    items.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                    else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                },
            ) {
                itemContent(item)
                //                        Icon(
                //                            imageVector = if (type == item) TypeSelector.checkedIcons[index] else TypeSelector.unCheckedIcons[index],
                //                            contentDescription = null,
                //                        )
                //                        Spacer(modifier = Modifier.size(ToggleButtonDefaults.IconSpacing))
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun FollowRedirectsSettingsRoutePreview() {
    PreviewThemeNew {
        ConnectedToggleButtonFlowRow(
            items = followRedirectsModes,
            current = FollowRedirectsMode.Auto,
            onChange = {

            },
            itemContent = {
                Text(it.toString())
            }
        )
    }
}
