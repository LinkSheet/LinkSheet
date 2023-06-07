package fe.linksheet.composable.settings.link.amp2html

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fe.linksheet.R
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.composable.util.LinkableTextView
import fe.linksheet.composable.util.SettingEnabledCardColumn
import fe.linksheet.composable.util.SliderRow
import fe.linksheet.composable.util.SwitchRow
import fe.linksheet.module.viewmodel.Amp2HtmlSettingsViewModel
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
                    headlineId = R.string.enable_amp2html,
                    subtitleId = R.string.enable_amp2html_explainer,
                    contentTitleId = R.string.options
                )
            }

            item(key = "enable_amp2html_local_cache") {
                SwitchRow(
                    state = viewModel.enableAmp2HtmlLocalCache,
                    viewModel = viewModel,
                    enabled = viewModel.enableAmp2Html.value,
                    headlineId = R.string.amp2html_local_cache,
                    subtitleId = R.string.amp2html_local_cache_explainer
                )
            }

            item(key = "amp2html_external_service") {
                SwitchRow(
                    state = viewModel.amp2HtmlExternalService,
                    viewModel = viewModel,
                    enabled = viewModel.enableAmp2Html.value,
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
                    }
                )
            }

            item(key = "amp2html_timeout") {
                SliderRow(
                    value = viewModel.amp2HtmlTimeout.value.toFloat(),
                    onValueChange = {
                        viewModel.updateState(viewModel.amp2HtmlTimeout, it.toInt())
                    },
                    enabled = viewModel.enableAmp2Html.value,
                    valueRange = 0f..30f,
                    valueFormatter = { it.toInt().toString() },
                    headlineId = R.string.request_timeout,
                    subtitleId = R.string.request_timeout_explainer
                )
            }
        }
    }
}