package fe.linksheet.composable.page.settings.link.redirect

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.linksheet.compose.list.item.PreferenceSwitchListItem
import app.linksheet.compose.page.SaneScaffoldSettingsPage
import fe.android.compose.text.AnnotatedStringResourceContent.Companion.annotatedStringResource
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.android.preference.helper.Preference
import fe.composekit.component.ContentType
import fe.composekit.component.PreviewThemeNew
import fe.composekit.component.list.item.toEnabledContentSet
import fe.composekit.component.list.item.type.SliderListItem
import fe.composekit.preference.*
import fe.linksheet.R
import fe.linksheet.composable.component.ConnectedToggleButtonFlowRow
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
    FollowRedirectsSettingsRouteInternal(
        enablePref = viewModel.followRedirects,
        modePref = viewModel.followRedirectsMode,
        localCachePref = viewModel.followRedirectsLocalCache,
        onlyKnownTrackersPref = viewModel.followOnlyKnownTrackers,
        aggressivePref = viewModel.followRedirectsAggressive,
        externalServicePref = viewModel.followRedirectsExternalService,
        allowDarknetsPref = viewModel.followRedirectsAllowsDarknets,
        allowLocalPref = viewModel.followRedirectsAllowLocalNetwork,
        skipBrowserPref = viewModel.followRedirectsSkipBrowser,
        requestTimeoutPref = viewModel.requestTimeout,
        onBackPressed = onBackPressed
    )
}

typealias ModeVmPref = ViewModelStatePreference<FollowRedirectsMode, FollowRedirectsMode, Preference.Mapped<FollowRedirectsMode, String>>

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun FollowRedirectsSettingsRouteInternal(
    enablePref: BooleanVmPref,
    modePref: ModeVmPref,
    localCachePref: BooleanVmPref,
    onlyKnownTrackersPref: BooleanVmPref,
    aggressivePref: BooleanVmPref,
    externalServicePref: BooleanVmPref,
    allowDarknetsPref: BooleanVmPref,
    allowLocalPref: BooleanVmPref,
    skipBrowserPref: BooleanVmPref,
    requestTimeoutPref: IntVmPref,
    onBackPressed: () -> Unit,
) {
    val darknets = remember {
        Darknet.entries.joinToString(separator = ", ") { it.displayName }
    }
    val followRedirects by enablePref.collectAsStateWithLifecycle()
    val contentSet = remember(followRedirects) { followRedirects.toEnabledContentSet() }

    SaneScaffoldSettingsPage(headline = stringResource(id = R.string.follow_redirects), onBackPressed = onBackPressed) {
        item(key = R.string.follow_redirects, contentType = ContentType.SingleGroupItem) {
            PreferenceSwitchListItem(
                statePreference = enablePref,
                headlineContent = textContent(R.string.follow_redirects),
                supportingContent = annotatedStringResource(R.string.follow_redirects_explainer),
            )
        }

        divider(id = R.string.generic__text_mode)

        item(key = -R.string.generic__text_mode, contentType = ContentType.SingleGroupItem) {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                val followRedirectsMode by modePref.collectAsStateWithLifecycle()
                ConnectedToggleButtonFlowRow(
                    current = followRedirectsMode,
                    items = followRedirectsModes,
                    onChange = { modePref(it) },
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

                Text(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    text = stringResource(
                        id = when (followRedirectsMode) {
                            FollowRedirectsMode.Auto -> R.string.settings_follow_redirects__subtitle_mode_auto
                            FollowRedirectsMode.Manual -> R.string.settings_follow_redirects__subtitle_mode_manual
                        }
                    ),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        divider(id = R.string.options)

        group(base = 7, LinkSheetAppConfig.isPro()) {
            item(key = R.string.follow_redirects_local_cache) { padding, shape ->
                PreferenceSwitchListItem(
                    enabled = contentSet,
                    shape = shape,
                    padding = padding,
                    statePreference = localCachePref,
                    headlineContent = textContent(R.string.follow_redirects_local_cache),
                    supportingContent = textContent(R.string.follow_redirects_local_cache_explainer),
                )
            }

            item(key = R.string.follow_only_known_trackers) { padding, shape ->
                val followRedirectsExternalService by externalServicePref.collectAsStateWithLifecycle()
                // TODO: This settings should allow the user to add their own rules in the future, or at least display a _understandable_ list of known tracker domains
                PreferenceSwitchListItem(
                    enabled = (followRedirects && !followRedirectsExternalService).toEnabledContentSet(),
                    shape = shape,
                    padding = padding,
                    statePreference = onlyKnownTrackersPref,
//                    onContentClick = { followRedirectsKnownTrackers.open(Unit) },
                    headlineContent = textContent(R.string.follow_only_known_trackers),
                    supportingContent = annotatedStringResource(R.string.follow_only_known_trackers_explainer),
                )
            }

            item(key = R.string.settings_follow_redirects__title_aggressive) { padding, shape ->
                PreferenceSwitchListItem(
                    enabled = contentSet,
                    shape = shape,
                    padding = padding,
                    statePreference = aggressivePref,
                    headlineContent = textContent(R.string.settings_follow_redirects__title_aggressive),
                    supportingContent = textContent(id = R.string.settings_follow_redirects__subtitle_aggressive),
                )
            }

            item(key = R.string.allow_darknets) { padding, shape ->
                PreferenceSwitchListItem(
                    enabled = contentSet,
                    shape = shape,
                    padding = padding,
                    statePreference = allowDarknetsPref,
                    headlineContent = textContent(R.string.allow_darknets),
                    supportingContent = textContent(id = R.string.follow_redirects_allow_darknets_explainer, darknets),
                )
            }

            item(key = R.string.settings_links_follow_redirects__title_local_network) { padding, shape ->
                PreferenceSwitchListItem(
                    enabled = contentSet,
                    shape = shape,
                    padding = padding,
                    statePreference = allowLocalPref,
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
                        statePreference = externalServicePref,
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
                    statePreference = skipBrowserPref,
                    headlineContent = textContent(R.string.settings_links_follow_redirects__title_skip_browser),
                    supportingContent = textContent(id = R.string.settings_links_follow_redirects__text_skip_browser),
                )
            }

            item(key = R.string.request_timeout) { padding, shape ->
                val requestTimeout by requestTimeoutPref.collectAsStateWithLifecycle()

                SliderListItem(
                    enabled = contentSet,
                    shape = shape,
                    padding = padding,
                    valueRange = 0f..30f,
                    value = requestTimeout.toFloat(),
                    onValueChange = { requestTimeoutPref(it.toInt()) },
                    valueFormatter = { it.toInt().toString() },
                    headlineContent = textContent(R.string.request_timeout),
                    supportingContent = annotatedStringResource(R.string.request_timeout_explainer),
                )
            }
        }
    }
}

@Preview
@Composable
private fun FollowRedirectsSettingsRouteInternalPreview() {
    PreviewThemeNew {
        FollowRedirectsSettingsRouteInternal(
            enablePref = fakeBooleanVM(true),
            modePref = FakePreferences.mapped(FollowRedirectsMode.Auto, FollowRedirectsMode).vm,
            localCachePref = fakeBooleanVM(true),
            onlyKnownTrackersPref = fakeBooleanVM(true),
            aggressivePref = fakeBooleanVM(true),
            externalServicePref = fakeBooleanVM(true),
            allowDarknetsPref = fakeBooleanVM(true),
            allowLocalPref = fakeBooleanVM(true),
            skipBrowserPref = fakeBooleanVM(true),
            requestTimeoutPref = fakeIntVM(10),
            onBackPressed = {}
        )
    }
}
