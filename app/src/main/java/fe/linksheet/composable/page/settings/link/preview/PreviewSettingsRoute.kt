package fe.linksheet.composable.page.settings.link.preview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import app.linksheet.compose.list.item.PreferenceSwitchListItem
import app.linksheet.compose.page.SaneScaffoldSettingsPage
import fe.android.compose.text.AnnotatedStringResourceContent.Companion.annotatedStringResource
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.component.ContentType
import fe.composekit.component.list.item.toEnabledContentSet
import fe.composekit.preference.collectAsStateWithLifecycle
import fe.linksheet.R
import fe.linksheet.module.viewmodel.PreviewSettingsViewModel
import org.koin.androidx.compose.koinViewModel
import app.linksheet.compose.R as CommonR


@Composable
fun PreviewSettingsRoute(
    onBackPressed: () -> Unit,
    viewModel: PreviewSettingsViewModel = koinViewModel()
) {
    val enableActionBar by viewModel.enableActionBar.collectAsStateWithLifecycle()
    val urlPreview by viewModel.urlPreview.collectAsStateWithLifecycle()
    SaneScaffoldSettingsPage(
        headline = stringResource(id = R.string.settings_links_preview__title_preview),
        onBackPressed = onBackPressed
    ) {
        item(key = R.string.settings_links_preview__title_preview, contentType = ContentType.SingleGroupItem) {
            PreferenceSwitchListItem(
                enabled = enableActionBar.toEnabledContentSet(),
                statePreference = viewModel.urlPreview,
                headlineContent = textContent(R.string.settings_links_preview__title_preview),
                supportingContent = annotatedStringResource(R.string.settings_links_preview__subtitle_preview),
            )
        }

        divider(id = CommonR.string.options)

        group(size = 1) {
            item(key = R.string.settings_links_preview__title_preview_skip_browser) { padding, shape ->
                PreferenceSwitchListItem(
                    enabled = (urlPreview && enableActionBar).toEnabledContentSet(),
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
