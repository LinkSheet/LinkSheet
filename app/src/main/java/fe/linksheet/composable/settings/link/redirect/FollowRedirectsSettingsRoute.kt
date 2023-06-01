package fe.linksheet.composable.settings.link.redirect

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
import fe.linksheet.module.viewmodel.FollowRedirectsSettingsViewModel
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FollowRedirectsSettingsRoute(
    onBackPressed: () -> Unit,
    viewModel: FollowRedirectsSettingsViewModel = koinViewModel()
) {
    SettingsScaffold(
        headline = stringResource(id = R.string.follow_redirects),
        onBackPressed = onBackPressed
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxHeight(),
            contentPadding = PaddingValues(horizontal = 5.dp)
        ) {
            stickyHeader(key = "follow_redirects") {
                SettingEnabledCardColumn(
                    state = viewModel.followRedirects,
                    viewModel = viewModel, headlineId = R.string.follow_redirects,
                    subtitleId = R.string.follow_redirects_explainer,
                    contentTitleId = R.string.options
                )
            }

            item(key = "follow_redirects_local_cache") {
                SwitchRow(
                    state = viewModel.followRedirectsLocalCache,
                    viewModel = viewModel,
                    enabled = viewModel.followRedirects.value,
                    headlineId = R.string.follow_redirects_local_cache,
                    subtitleId = R.string.follow_redirects_local_cache_explainer
                )
            }


            item(key = "follow_only_known_trackers") {
                SwitchRow(
                    state = viewModel.followOnlyKnownTrackers,
                    viewModel = viewModel,
                    enabled = viewModel.followRedirects.value,
                    headlineId = R.string.follow_only_known_trackers,
                    subtitleId = R.string.follow_only_known_trackers_explainer
                )
            }

            item(key = "follow_redirects_external_service") {
                SwitchRow(
                    state = viewModel.followRedirectsExternalService,
                    viewModel = viewModel,
                    enabled = viewModel.followRedirects.value,
                    headline = stringResource(id = R.string.follow_redirects_external_service),
                    subtitleBuilder = { enabled ->
                        LinkableTextView(
                            id = R.string.follow_redirects_external_service_explainer,
                            enabled = enabled,
                            style = LocalTextStyle.current.copy(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 16.sp
                            )
                        )
                    }
                )
            }

            item(key = "follow_redirects_timeout") {
                SliderRow(
                    value = viewModel.followRedirectsTimeout.value.toFloat(),
                    onValueChange = {
                        viewModel.updateState(viewModel.followRedirectsTimeout, it.toInt())
                    },
                    enabled = viewModel.followRedirects.value,
                    valueRange = 0f..30f,
                    valueFormatter = { it.toInt().toString() },
                    headlineId = R.string.request_timeout,
                    subtitleId = R.string.request_timeout_explainer
                )
            }
        }
    }
}