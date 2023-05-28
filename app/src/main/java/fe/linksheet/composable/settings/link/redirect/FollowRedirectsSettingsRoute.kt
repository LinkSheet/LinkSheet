package fe.linksheet.composable.settings.link.redirect

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fe.linksheet.R
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.composable.util.HeadlineText
import fe.linksheet.composable.util.LinkableTextView
import fe.linksheet.composable.util.SettingEnabledCardColumn
import fe.linksheet.composable.util.SwitchRow
import fe.linksheet.module.viewmodel.FollowRedirectsSettingsViewModel
import fe.linksheet.ui.HkGroteskFontFamily
import fe.linksheet.util.AndroidVersion
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
                    headlineId = R.string.follow_redirects_local_cache,
                    subtitleId = R.string.follow_redirects_local_cache_explainer
                )
            }


            item(key = "follow_only_known_trackers") {
                SwitchRow(
                    state = viewModel.followOnlyKnownTrackers,
                    viewModel = viewModel,
                    headlineId = R.string.follow_only_known_trackers,
                    subtitleId = R.string.follow_only_known_trackers_explainer
                )
            }


            item(key = "follow_redirects_external_service") {
                SwitchRow(
                    state = viewModel.followRedirectsExternalService,
                    viewModel = viewModel,
                    headline = stringResource(id = R.string.follow_redirects_external_service),
                    subtitleBuilder = {
                        LinkableTextView(
                            id = R.string.follow_redirects_external_service_explainer,
                            style = LocalTextStyle.current.copy(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 16.sp
                            )
                        )
                    }
                )
            }
        }
    }
}