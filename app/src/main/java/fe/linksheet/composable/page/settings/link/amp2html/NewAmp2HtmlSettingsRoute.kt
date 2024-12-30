package fe.linksheet.composable.page.settings.link.amp2html

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import fe.android.compose.text.AnnotatedStringResourceContent.Companion.annotatedStringResource
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.component.ContentType
import fe.composekit.component.list.item.toEnabledContentSet
import fe.composekit.component.list.item.type.SliderListItem
import fe.linksheet.util.buildconfig.LinkSheetAppConfig
import fe.linksheet.R
import fe.linksheet.composable.component.list.item.type.PreferenceSwitchListItem
import fe.linksheet.composable.component.page.SaneScaffoldSettingsPage
import fe.linksheet.module.viewmodel.Amp2HtmlSettingsViewModel
import fe.linksheet.util.net.Darknet
import org.koin.androidx.compose.koinViewModel


@Composable
fun NewAmp2HtmlSettingsRoute(
    onBackPressed: () -> Unit,
    viewModel: Amp2HtmlSettingsViewModel = koinViewModel()
) {
    val darknets = remember {
        Darknet.entries.joinToString(separator = ", ") { it.displayName }
    }

    SaneScaffoldSettingsPage(headline = stringResource(id = R.string.settings_links_amp2html__title_amp2html), onBackPressed = onBackPressed) {
        item(key = R.string.enable_amp2html, contentType = ContentType.SingleGroupItem) {
            PreferenceSwitchListItem(
                preference = viewModel.enableAmp2Html,
                headlineContent = textContent(R.string.enable_amp2html),
                supportingContent = annotatedStringResource(R.string.enable_amp2html_explainer),
            )
        }

        divider(id =  R.string.options)

        group(size = 4 + if (LinkSheetAppConfig.isPro()) 1 else 0) {
            item(key = R.string.amp2html_local_cache) { padding, shape ->
                PreferenceSwitchListItem(
                    enabled = viewModel.enableAmp2Html().toEnabledContentSet(),
                    shape = shape,
                    padding = padding,
                    preference = viewModel.enableAmp2HtmlLocalCache,
                    headlineContent = textContent(R.string.amp2html_local_cache),
                    supportingContent = textContent(R.string.amp2html_local_cache_explainer),
                )
            }

            item(key = R.string.allow_darknets) { padding, shape ->
                PreferenceSwitchListItem(
                    enabled = viewModel.enableAmp2Html().toEnabledContentSet(),
                    shape = shape,
                    padding = padding,
                    preference = viewModel.amp2HtmlAllowDarknets,
                    headlineContent = textContent(R.string.allow_darknets),
                    supportingContent = textContent(id = R.string.amp2html_allow_darknets_explainer, darknets),
                )
            }

            if (LinkSheetAppConfig.isPro()) {
                item(key = R.string.amp2html_external_service) { padding, shape ->
                    PreferenceSwitchListItem(
                        enabled = (viewModel.enableAmp2Html() && LinkSheetAppConfig.isPro()).toEnabledContentSet(),
                        shape = shape,
                        padding = padding,
                        preference = viewModel.amp2HtmlExternalService,
                        headlineContent = textContent(R.string.amp2html_external_service),
                        supportingContent = annotatedStringResource(R.string.amp2html_external_service_explainer),
                    )
                }
            }

            item(key = R.string.settings_links_amp2html__title_skip_browser) { padding, shape ->
                PreferenceSwitchListItem(
                    enabled = viewModel.enableAmp2Html().toEnabledContentSet(),
                    shape = shape,
                    padding = padding,
                    preference = viewModel.amp2HtmlSkipBrowser,
                    headlineContent = textContent(R.string.settings_links_amp2html__title_skip_browser),
                    supportingContent = textContent(id = R.string.settings_links_amp2html__text_skip_browser),
                )
            }

            item(key = R.string.request_timeout) { padding, shape ->
                SliderListItem(
                    enabled = viewModel.enableAmp2Html().toEnabledContentSet(),
                    shape = shape,
                    padding = padding,
                    valueRange = 0f..30f,
                    value = viewModel.requestTimeout().toFloat(),
                    onValueChange = { viewModel.requestTimeout(it.toInt()) },
                    valueFormatter = { it.toInt().toString() },
                    headlineContent = textContent(R.string.request_timeout),
                    supportingContent = annotatedStringResource(R.string.request_timeout_explainer),
                )
            }
        }
    }
}
