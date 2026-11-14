package fe.linksheet.composable.page.settings.link

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.res.stringResource
import app.linksheet.compose.list.item.PreferenceDividedSwitchListItem
import app.linksheet.compose.list.item.PreferenceSwitchListItem
import app.linksheet.compose.page.SaneScaffoldSettingsPage
import app.linksheet.feature.libredirect.navigation.LibRedirectRoute
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import fe.android.compose.text.AnnotatedStringResourceContent.Companion.annotatedStringResource
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.route.Route
import fe.linksheet.R
import fe.linksheet.module.viewmodel.LinksSettingsViewModel
import fe.linksheet.navigation.amp2HtmlSettingsRoute
import fe.linksheet.navigation.followRedirectsSettingsRoute
import org.koin.androidx.compose.koinViewModel
import app.linksheet.feature.clearurls.R as ClearUrlsR
import app.linksheet.feature.libredirect.R as LibRedirectR

@Composable
fun LinksSettingsRoute(
    onBackPressed: () -> Unit,
    navigate: (String) -> Unit,
    navigateNew: (Route) -> Unit,
    viewModel: LinksSettingsViewModel = koinViewModel()
) {
    val providers by viewModel.clearUrlsProviders.collectAsStateWithLifecycle()
    if (providers.visible) {
        ClearUrlsProviderDialog(
            state = providers,
            onSearch = viewModel::searchClearUrlsProviders,
            onToggle = viewModel::setRemoveAllQueryParameters,
            onDismiss = { viewModel.showClearUrlsProviders(false) },
        )
    }
    SaneScaffoldSettingsPage(
        headline = stringResource(id = R.string.links),
        onBackPressed = onBackPressed
    ) {
        group(size = 8) {
            item(key = ClearUrlsR.string.use_clear_urls) { padding, shape ->
                PreferenceDividedSwitchListItem(
                    shape = shape,
                    padding = padding,
                    statePreference = viewModel.useClearUrls,
                    onContentClick = { viewModel.showClearUrlsProviders(true) },
                    headlineContent = textContent(ClearUrlsR.string.use_clear_urls),
                    overlineContent = textContent(R.string.clear_urls_providers_hint),
                    supportingContent = annotatedStringResource(ClearUrlsR.string.use_clear_urls_explainer),
                )
            }

            item(key = R.string.settings_links__title_open_without_tracking) { padding, shape ->
                PreferenceSwitchListItem(
                    shape = shape,
                    padding = padding,
                    statePreference = viewModel.openWithoutTrackingButton,
                    headlineContent = textContent(R.string.settings_links__title_open_without_tracking),
                    supportingContent = textContent(R.string.settings_links__subtitle_open_without_tracking),
                )
            }

            item(key = R.string.select_link_parameters_title) { padding, shape ->
                PreferenceSwitchListItem(
                    shape = shape,
                    padding = padding,
                    statePreference = viewModel.selectLinkParameters,
                    headlineContent = textContent(R.string.select_link_parameters_title),
                    supportingContent = textContent(R.string.select_link_parameters_description),
                )
            }

            item(key = R.string.fastfoward_rules) { padding, shape ->
                PreferenceSwitchListItem(
                    shape = shape,
                    padding = padding,
                    statePreference = viewModel.useFastForwardRules,
                    headlineContent = textContent(R.string.fastfoward_rules),
                    supportingContent = annotatedStringResource(R.string.fastfoward_rules_explainer),
                )
            }

            item(key = LibRedirectR.string.enable_libredirect) { padding, shape ->
                PreferenceDividedSwitchListItem(
                    shape = shape,
                    padding = padding,
                    statePreference = viewModel.enableLibRedirect,
                    onContentClick = { navigateNew(LibRedirectRoute) },
                    headlineContent = textContent(LibRedirectR.string.enable_libredirect),
                    supportingContent = annotatedStringResource(LibRedirectR.string.enable_libredirect_explainer),
                )
            }

            item(key = R.string.follow_redirects) { padding, shape ->
                PreferenceDividedSwitchListItem(
                    shape = shape,
                    padding = padding,
                    statePreference = viewModel.followRedirects,
                    onContentClick = { navigate(followRedirectsSettingsRoute) },
                    headlineContent = textContent(R.string.follow_redirects),
                    supportingContent = annotatedStringResource(R.string.follow_redirects_explainer),
                )
            }

            item(key = R.string.enable_amp2html) { padding, shape ->
                PreferenceDividedSwitchListItem(
                    shape = shape,
                    padding = padding,
                    statePreference = viewModel.enableAmp2Html,
                    onContentClick = { navigate(amp2HtmlSettingsRoute) },
                    headlineContent = textContent(R.string.enable_amp2html),
                    supportingContent = annotatedStringResource(R.string.enable_amp2html_explainer),
                )
            }

            item(key = R.string.resolve_embeds) { padding, shape ->
                PreferenceSwitchListItem(
                    shape = shape,
                    padding = padding,
                    statePreference = viewModel.resolveEmbeds,
                    headlineContent = textContent(R.string.resolve_embeds),
                    supportingContent = textContent(R.string.resolve_embeds_explainer),
                )
            }
        }
    }
}
