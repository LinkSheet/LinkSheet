package fe.linksheet.experiment.ui.overhaul.composable.page.settings.link

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import fe.linksheet.*
import fe.linksheet.composable.settings.link.downloader.downloaderPermissionState
import fe.linksheet.composable.settings.link.downloader.requestDownloadPermission
import fe.linksheet.experiment.ui.overhaul.composable.component.list.base.ContentPosition
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.type.DividedSwitchListItem
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.type.PreferenceDividedSwitchListItem
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.type.PreferenceSwitchListItem
import fe.linksheet.experiment.ui.overhaul.composable.component.page.SaneScaffoldSettingsPage
import fe.linksheet.experiment.ui.overhaul.composable.util.AnnotatedStringResource.Companion.annotated
import fe.linksheet.experiment.ui.overhaul.composable.util.Resource.Companion.textContent
import fe.linksheet.module.viewmodel.LinksSettingsViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NewLinksSettingsRoute(
    onBackPressed: () -> Unit,
    navigate: (String) -> Unit,
    viewModel: LinksSettingsViewModel = koinViewModel()
) {
    val writeExternalStoragePermissionState = downloaderPermissionState()

    SaneScaffoldSettingsPage(headline = stringResource(id = R.string.links), onBackPressed = onBackPressed) {
        group(size = 7) {
            item(key = R.string.use_clear_urls) { padding, shape ->
                PreferenceSwitchListItem(
                    shape = shape,
                    padding = padding,
                    preference = viewModel.useClearUrls,
                    headlineContent = textContent(R.string.use_clear_urls),
                    supportingContent = annotated(R.string.use_clear_urls_explainer),
                )
            }

            item(key = R.string.fastfoward_rules) { padding, shape ->
                PreferenceSwitchListItem(
                    shape = shape,
                    padding = padding,
                    preference = viewModel.useFastForwardRules,
                    headlineContent = textContent(R.string.fastfoward_rules),
                    supportingContent = annotated(R.string.fastfoward_rules_explainer),
                )
            }

            item(key = R.string.enable_libredirect) { padding, shape ->
                PreferenceDividedSwitchListItem(
                    shape = shape,
                    padding = padding,
                    preference = viewModel.enableLibRedirect,
                    onContentClick = { navigate(libRedirectSettingsRoute) },
                    headlineContent = textContent(R.string.enable_libredirect),
                    supportingContent = annotated(R.string.enable_libredirect_explainer),
                )
            }

            item(key = R.string.follow_redirects) { padding, shape ->
                PreferenceDividedSwitchListItem(
                    shape = shape,
                    padding = padding,
                    preference = viewModel.followRedirects,
                    onContentClick = { navigate(followRedirectsSettingsRoute) },
                    headlineContent = textContent(R.string.follow_redirects),
                    supportingContent = annotated(R.string.follow_redirects_explainer),
                )
            }

            item(key = R.string.enable_amp2html) { padding, shape ->
                PreferenceDividedSwitchListItem(
                    shape = shape,
                    padding = padding,
                    preference = viewModel.enableAmp2Html,
                    onContentClick = { navigate(amp2HtmlSettingsRoute) },
                    headlineContent = textContent(R.string.enable_amp2html),
                    supportingContent = annotated(R.string.enable_amp2html_explainer),
                )
            }

            item(key = R.string.enable_downloader) { padding, shape ->
                DividedSwitchListItem(
                    shape = shape,
                    padding = padding,
                    checked = viewModel.enableDownloader(),
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
                    supportingContent = annotated(R.string.enable_downloader_explainer)
                )
            }

            item(key = R.string.resolve_embeds) { padding, shape ->
                PreferenceSwitchListItem(
                    shape = shape,
                    padding = padding,
                    preference = viewModel.resolveEmbeds,
                    headlineContent = textContent(R.string.resolve_embeds),
                    supportingContent = textContent(R.string.resolve_embeds_explainer),
                )
            }
        }
    }
}
