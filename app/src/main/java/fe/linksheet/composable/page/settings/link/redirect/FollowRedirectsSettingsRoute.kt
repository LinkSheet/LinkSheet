package fe.linksheet.composable.page.settings.link.redirect

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import fe.android.compose.text.AnnotatedStringResourceContent.Companion.annotatedStringResource
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.component.ContentType
import fe.composekit.component.list.item.toEnabledContentSet
import fe.composekit.component.list.item.type.SliderListItem
import fe.composekit.preference.collectAsStateWithLifecycle
import fe.linksheet.util.buildconfig.LinkSheetAppConfig
import fe.linksheet.R
import fe.linksheet.composable.component.list.item.type.PreferenceSwitchListItem
import app.linksheet.compose.page.SaneScaffoldSettingsPage
import fe.linksheet.composable.util.groupSize
import fe.linksheet.module.viewmodel.FollowRedirectsSettingsViewModel
import fe.linksheet.util.web.Darknet
import org.koin.androidx.compose.koinViewModel


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
    val contentSet = remember(followRedirects) { followRedirects.toEnabledContentSet() }

    SaneScaffoldSettingsPage(headline = stringResource(id = R.string.follow_redirects), onBackPressed = onBackPressed) {
        item(key = R.string.follow_redirects, contentType = ContentType.SingleGroupItem) {
            PreferenceSwitchListItem(
                statePreference = viewModel.followRedirects,
                headlineContent = textContent(R.string.follow_redirects),
                supportingContent = annotatedStringResource(R.string.follow_redirects_explainer),
            )
        }

        divider(id =  R.string.options)

        group(size = groupSize(6, LinkSheetAppConfig.isPro())) {
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

            if(LinkSheetAppConfig.isPro()){
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
                    onValueChange = { viewModel.requestTimeout.update(it.toInt()) },
                    valueFormatter = { it.toInt().toString() },
                    headlineContent = textContent(R.string.request_timeout),
                    supportingContent = annotatedStringResource(R.string.request_timeout_explainer),
                )
            }
        }
    }
}
