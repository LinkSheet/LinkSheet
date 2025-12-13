package fe.linksheet.composable.page.settings.link.amp2html

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import fe.android.compose.text.AnnotatedStringResourceContent.Companion.annotatedStringResource
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.component.ContentType
import fe.composekit.component.list.item.toEnabledContentSet
import fe.composekit.component.list.item.type.SliderListItem
import fe.composekit.component.list.item.type.SwitchListItem
import fe.composekit.preference.collectAsStateWithLifecycle
import fe.linksheet.R
import app.linksheet.compose.list.item.PreferenceSwitchListItem
import app.linksheet.compose.page.SaneScaffoldSettingsPage
import fe.linksheet.module.viewmodel.Amp2HtmlSettingsViewModel
import fe.linksheet.util.buildconfig.LinkSheetAppConfig
import fe.linksheet.web.Darknet
import org.koin.androidx.compose.koinViewModel


@Composable
fun Amp2HtmlSettingsRoute(
    onBackPressed: () -> Unit,
    viewModel: Amp2HtmlSettingsViewModel = koinViewModel()
) {
    val darknets = remember {
        Darknet.entries.joinToString(separator = ", ") { it.displayName }
    }

    val enableAmp2Html by viewModel.enableAmp2Html.collectAsStateWithLifecycle()
    val contentSet = remember(enableAmp2Html) { enableAmp2Html.toEnabledContentSet() }

    SaneScaffoldSettingsPage(headline = stringResource(id = R.string.settings_links_amp2html__title_amp2html), onBackPressed = onBackPressed) {
        item(key = R.string.enable_amp2html, contentType = ContentType.SingleGroupItem) {
            SwitchListItem(
                checked = enableAmp2Html,
                onCheckedChange = { viewModel.enableAmp2Html(it) },
                headlineContent = textContent(R.string.enable_amp2html),
                supportingContent = annotatedStringResource(R.string.enable_amp2html_explainer),
            )
        }

        divider(id =  R.string.options)

        group(base = 5, LinkSheetAppConfig.isPro()) {
            item(key = R.string.amp2html_local_cache) { padding, shape ->
                PreferenceSwitchListItem(
                    enabled = contentSet,
                    shape = shape,
                    padding = padding,
                    statePreference = viewModel.enableAmp2HtmlLocalCache,
                    headlineContent = textContent(R.string.amp2html_local_cache),
                    supportingContent = textContent(R.string.amp2html_local_cache_explainer),
                )
            }

            item(key = R.string.allow_darknets) { padding, shape ->
                PreferenceSwitchListItem(
                    enabled = contentSet,
                    shape = shape,
                    padding = padding,
                    statePreference = viewModel.amp2HtmlAllowDarknets,
                    headlineContent = textContent(R.string.allow_darknets),
                    supportingContent = textContent(id = R.string.amp2html_allow_darknets_explainer, darknets),
                )
            }

            item(key = R.string.settings_links_amp2html__title_local_network) { padding, shape ->
                PreferenceSwitchListItem(
                    enabled = contentSet,
                    shape = shape,
                    padding = padding,
                    statePreference = viewModel.amp2HtmlAllowLocalNetwork,
                    headlineContent = textContent(R.string.settings_links_amp2html__title_local_network),
                    supportingContent = textContent(R.string.settings_links_amp2html__text_local_network),
                )
            }

            if (LinkSheetAppConfig.isPro()) {
                item(key = R.string.amp2html_external_service) { padding, shape ->
                    PreferenceSwitchListItem(
                        enabled = (enableAmp2Html && LinkSheetAppConfig.isPro()).toEnabledContentSet(),
                        shape = shape,
                        padding = padding,
                        statePreference = viewModel.amp2HtmlExternalService,
                        headlineContent = textContent(R.string.amp2html_external_service),
                        supportingContent = annotatedStringResource(R.string.amp2html_external_service_explainer),
                    )
                }
            }

            item(key = R.string.settings_links_amp2html__title_skip_browser) { padding, shape ->
                PreferenceSwitchListItem(
                    enabled = contentSet,
                    shape = shape,
                    padding = padding,
                    statePreference = viewModel.amp2HtmlSkipBrowser,
                    headlineContent = textContent(R.string.settings_links_amp2html__title_skip_browser),
                    supportingContent = textContent(id = R.string.settings_links_amp2html__text_skip_browser),
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
