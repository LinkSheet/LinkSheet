package fe.linksheet.composable.page.settings.link

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import fe.android.compose.text.AnnotatedStringResourceContent.Companion.annotatedStringResource
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.component.list.item.ContentPosition
import fe.composekit.component.list.item.type.DividedSwitchListItem
import fe.composekit.preference.collectAsStateWithLifecycle
import fe.composekit.route.Route
import fe.linksheet.*
import fe.linksheet.composable.page.settings.link.downloader.downloaderPermissionState
import fe.linksheet.composable.page.settings.link.downloader.requestDownloadPermission
import fe.linksheet.composable.component.list.item.type.PreferenceDividedSwitchListItem
import fe.linksheet.composable.component.list.item.type.PreferenceSwitchListItem
import fe.linksheet.composable.component.page.SaneScaffoldSettingsPage
import fe.linksheet.module.viewmodel.LinksSettingsViewModel
import fe.linksheet.navigation.LibRedirectRoute
import fe.linksheet.navigation.PreviewUrlRoute
import fe.linksheet.navigation.amp2HtmlSettingsRoute
import fe.linksheet.navigation.downloaderSettingsRoute
import fe.linksheet.navigation.followRedirectsSettingsRoute
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LinksSettingsRoute(
    onBackPressed: () -> Unit,
    navigate: (String) -> Unit,
    navigateNew: (Route) -> Unit,
    viewModel: LinksSettingsViewModel = koinViewModel()
) {
    val writeExternalStoragePermissionState = downloaderPermissionState()

    SaneScaffoldSettingsPage(headline = stringResource(id = R.string.links), onBackPressed = onBackPressed) {
        group(size = 8) {
            item(key = R.string.use_clear_urls) { padding, shape ->
                PreferenceSwitchListItem(
                    shape = shape,
                    padding = padding,
                    statePreference = viewModel.useClearUrls,
                    headlineContent = textContent(R.string.use_clear_urls),
                    supportingContent = annotatedStringResource(R.string.use_clear_urls_explainer),
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

            item(key = R.string.enable_libredirect) { padding, shape ->
                PreferenceDividedSwitchListItem(
                    shape = shape,
                    padding = padding,
                    statePreference = viewModel.enableLibRedirect,
                    onContentClick = { navigateNew(LibRedirectRoute) },
                    headlineContent = textContent(R.string.enable_libredirect),
                    supportingContent = annotatedStringResource(R.string.enable_libredirect_explainer),
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

            item(key = R.string.enable_downloader) { padding, shape ->
                val enableDownloader by viewModel.enableDownloader.collectAsStateWithLifecycle()

                DividedSwitchListItem(
                    shape = shape,
                    padding = padding,
                    checked = enableDownloader,
                    onCheckedChange = {
                        requestDownloadPermission(
                            writeExternalStoragePermissionState,
                            viewModel.enableDownloader,
                            it
                        )
                    },
                    onContentClick = { navigate(downloaderSettingsRoute) },
                    position = ContentPosition.Trailing,
                    headlineContent = textContent(R.string.enable_downloader),
                    supportingContent = annotatedStringResource(R.string.enable_downloader_explainer)
                )
            }

            item(key = R.string.settings_links_preview__title_preview) { padding, shape ->
                PreferenceDividedSwitchListItem(
                    shape = shape,
                    padding = padding,
                    statePreference = viewModel.urlPreview,
                    onContentClick = { navigateNew(PreviewUrlRoute) },
                    headlineContent = textContent(R.string.settings_links_preview__title_preview),
                    supportingContent = annotatedStringResource(R.string.settings_links_preview__subtitle_preview),
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
