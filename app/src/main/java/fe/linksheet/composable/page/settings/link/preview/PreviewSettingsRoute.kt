package fe.linksheet.composable.page.settings.link.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import app.linksheet.compose.list.item.PreferenceSwitchListItem
import app.linksheet.compose.page.SaneScaffoldSettingsPage
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.component.ContentType
import fe.linksheet.R
import fe.linksheet.module.viewmodel.PreviewSettingsViewModel
import org.koin.androidx.compose.koinViewModel


@Composable
fun PreviewSettingsRoute(
    onBackPressed: () -> Unit,
    viewModel: PreviewSettingsViewModel = koinViewModel()
) {
    SaneScaffoldSettingsPage(
        headline = stringResource(id = R.string.settings_links_preview__title_preview),
        onBackPressed = onBackPressed
    ) {
        item(key = R.string.settings_links_preview__title_preview, contentType = ContentType.SingleGroupItem) {
            PreferenceSwitchListItem(
                statePreference = viewModel.urlPreview,
                headlineContent = textContent(R.string.settings_links_preview__title_preview),
                supportingContent = textContent(R.string.settings_links_preview__subtitle_preview),
            )
        }

        divider(id = R.string.options)

        group(size = 1) {
            item(key = R.string.settings_links_preview__title_preview_skip_browser) { padding, shape ->
                PreferenceSwitchListItem(
                    shape = shape,
                    padding = padding,
                    statePreference = viewModel.urlPreviewSkipBrowser,
                    headlineContent = textContent(R.string.settings_links_preview__title_preview_skip_browser),
                    supportingContent = textContent(R.string.settings_links_preview__subtitle_preview_skip_browser),
                )
            }
        }
    }
}
