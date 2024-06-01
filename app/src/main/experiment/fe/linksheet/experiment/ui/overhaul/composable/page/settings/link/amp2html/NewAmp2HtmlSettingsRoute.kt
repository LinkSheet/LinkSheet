package fe.linksheet.experiment.ui.overhaul.composable.page.settings.link.amp2html

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import fe.linksheet.LinkSheetAppConfig
import fe.linksheet.R
import fe.linksheet.experiment.ui.overhaul.composable.ContentTypeDefaults
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.type.PreferenceSwitchListItem
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.type.SliderListItem
import fe.linksheet.experiment.ui.overhaul.composable.component.page.SaneScaffoldSettingsPage
import fe.linksheet.experiment.ui.overhaul.composable.util.AnnotatedStringResource.Companion.annotated
import fe.linksheet.experiment.ui.overhaul.composable.util.Resource.Companion.textContent
import fe.linksheet.module.viewmodel.Amp2HtmlSettingsViewModel
import fe.linksheet.util.Darknet
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
        item(key = R.string.enable_amp2html, contentType = ContentTypeDefaults.SingleGroupItem) {
            PreferenceSwitchListItem(
                preference = viewModel.enableAmp2Html,
                headlineContent = textContent(R.string.enable_amp2html),
                supportingContent = annotated(R.string.enable_amp2html_explainer),
            )
        }

        divider(stringRes = R.string.options)

        group(size = 3 + if (LinkSheetAppConfig.isPro()) 1 else 0) {
            item(key = R.string.amp2html_local_cache) { padding, shape ->
                PreferenceSwitchListItem(
                    enabled = viewModel.enableAmp2Html(),
                    shape = shape,
                    padding = padding,
                    preference = viewModel.enableAmp2HtmlLocalCache,
                    headlineContent = textContent(R.string.amp2html_local_cache),
                    supportingContent = textContent(R.string.amp2html_local_cache_explainer),
                )
            }

            item(key = R.string.allow_darknets) { padding, shape ->
                PreferenceSwitchListItem(
                    enabled = viewModel.enableAmp2Html(),
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
                        enabled = viewModel.enableAmp2Html() && LinkSheetAppConfig.isPro(),
                        shape = shape,
                        padding = padding,
                        preference = viewModel.amp2HtmlExternalService,
                        headlineContent = textContent(R.string.amp2html_external_service),
                        supportingContent = annotated(R.string.amp2html_external_service_explainer),
                    )
                }
            }

            item(key = R.string.request_timeout) { padding, shape ->
                SliderListItem(
                    enabled = viewModel.enableAmp2Html(),
                    shape = shape,
                    padding = padding,
                    valueRange = 0f..30f,
                    value = viewModel.requestTimeout().toFloat(),
                    onValueChange = { viewModel.requestTimeout(it.toInt()) },
                    valueFormatter = { it.toInt().toString() },
                    headlineContent = textContent(R.string.request_timeout),
                    supportingContent = annotated(R.string.request_timeout_explainer),
                )
            }
        }
    }
}
