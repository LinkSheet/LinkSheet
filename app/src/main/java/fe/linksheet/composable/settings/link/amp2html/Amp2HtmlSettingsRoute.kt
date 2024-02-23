package fe.linksheet.composable.settings.link.amp2html

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fe.linksheet.LinkSheetAppConfig
import fe.linksheet.R
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.composable.util.LinkableTextView
import fe.linksheet.composable.util.RowInfoCard
import fe.linksheet.composable.util.SettingEnabledCardColumn
import fe.linksheet.composable.util.SliderRow
import fe.linksheet.composable.util.SwitchRow
import fe.linksheet.composable.util.linkableSubtitleBuilder
import fe.linksheet.module.viewmodel.Amp2HtmlSettingsViewModel
import fe.linksheet.util.Darknet
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Amp2HtmlSettingsRoute(
    onBackPressed: () -> Unit,
    viewModel: Amp2HtmlSettingsViewModel = koinViewModel()
) {
    SettingsScaffold(
        headline = stringResource(id = R.string.enable_amp2html),
        onBackPressed = onBackPressed
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxHeight(),
            contentPadding = PaddingValues(horizontal = 5.dp)
        ) {
            stickyHeader(key = "enable_amp2html") {
                SettingEnabledCardColumn(
                    state = viewModel.enableAmp2Html,
                    viewModel = viewModel,
                    headline = stringResource(id = R.string.enable_amp2html),
                    subtitleBuilder = linkableSubtitleBuilder(id = R.string.enable_amp2html_explainer),
                    contentTitle = stringResource(id = R.string.options)
                )
            }

            item(key = "enable_amp2html_local_cache") {
                SwitchRow(
                    state = viewModel.enableAmp2HtmlLocalCache,
                    enabled = viewModel.enableAmp2Html(),
                    headlineId = R.string.amp2html_local_cache,
                    subtitleId = R.string.amp2html_local_cache_explainer
                )
            }

            item(key = "enable_amp2html_builtin_cache") {
                SwitchRow(
                    state = viewModel.amp2HtmlBuiltInCache,
                    enabled = viewModel.enableAmp2Html(),
                    headlineId = R.string.amp2html_builtin_cache,
                    subtitleId = R.string.amp2html_builtin_cache_explainer
                )
            }

            item(key = "amp2html_allow_darknets") {
                SwitchRow(
                    state = viewModel.amp2HtmlAllowDarknets,
                    enabled = viewModel.enableAmp2Html(),
                    headline = stringResource(id = R.string.allow_darknets),
                    subtitle = stringResource(
                        id = R.string.amp2html_allow_darknets_explainer,
                        Darknet.entries.joinToString(separator = ", ") { it.displayName }
                    )
                )
            }

            item(key = "amp2html_external_service") {
                SwitchRow(
                    state = viewModel.amp2HtmlExternalService,
                    enabled = viewModel.enableAmp2Html() && LinkSheetAppConfig.isPro(),
                    headline = stringResource(id = R.string.amp2html_external_service),
                    subtitleBuilder = { enabled ->
                        LinkableTextView(
                            id = R.string.amp2html_external_service_explainer,
                            enabled = enabled,
                            style = LocalTextStyle.current.copy(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 16.sp
                            )
                        )

                        if (!LinkSheetAppConfig.isPro()) {
                            Spacer(modifier = Modifier.height(5.dp))
                            RowInfoCard(text = R.string.pro_feature)
                        }
                    }
                )
            }

            item(key = "amp2html_timeout") {
                SliderRow(
                    value = viewModel.requestTimeout().toFloat(),
                    onValueChange = { viewModel.requestTimeout(it.toInt()) },
                    enabled = viewModel.enableAmp2Html(),
                    valueRange = 0f..30f,
                    valueFormatter = { it.toInt().toString() },
                    headlineId = R.string.request_timeout,
                    subtitleId = R.string.request_timeout_explainer
                )
            }
        }
    }
}
