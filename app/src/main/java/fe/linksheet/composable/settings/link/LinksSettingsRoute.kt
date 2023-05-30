package fe.linksheet.composable.settings.link

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import fe.linksheet.R
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.composable.settings.link.downloader.downloaderPermissionState
import fe.linksheet.composable.settings.link.downloader.requestDownloadPermission
import fe.linksheet.composable.util.DividedSwitchRow
import fe.linksheet.composable.util.HeadlineText
import fe.linksheet.composable.util.LinkableTextView
import fe.linksheet.composable.util.SubtitleText
import fe.linksheet.composable.util.SwitchRow
import fe.linksheet.downloaderSettingsRoute
import fe.linksheet.followRedirectsSettingsRoute
import fe.linksheet.libRedirectSettingsRoute
import fe.linksheet.module.viewmodel.LinksSettingsViewModel
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalPermissionsApi::class, ExperimentalFoundationApi::class)
@Composable
fun LinksSettingsRoute(
    onBackPressed: () -> Unit,
    navController: NavController,
    viewModel: LinksSettingsViewModel = koinViewModel()
) {
    val writeExternalStoragePermissionState = downloaderPermissionState()

    val operations = mapOf(
        R.string.fastforward to viewModel.useFastForwardRules,
        R.string.clear_urls to viewModel.useClearUrls,
        R.string.follow_redirects to viewModel.followRedirects,
        R.string.lib_redirect to viewModel.enableLibRedirect,
    )

    SettingsScaffold(R.string.links, onBackPressed = onBackPressed) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxHeight(),
            contentPadding = PaddingValues(horizontal = 5.dp)
        ) {
            stickyHeader(key = "header") {
                if (operations.any { it.value.value }) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(horizontal = 10.dp)
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 80.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Spacer(modifier = Modifier.width(10.dp))
                                Image(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = stringResource(id = R.string.info)
                                )

                                Column(modifier = Modifier.padding(10.dp)) {
                                    HeadlineText(headlineId = R.string.order_of_operation)

                                    operations.filter { (_, enabled) -> enabled.value }
                                        .forEach { (stringId, _) ->
                                            SubtitleText(subtitleId = stringId)
                                        }
                                }
                            }
                        }
                    }
                }
            }

            item(key = "clear_urls") {
                SwitchRow(
                    state = viewModel.useClearUrls,
                    viewModel = viewModel,
                    headline = stringResource(id = R.string.use_clear_urls),
                    subtitleBuilder = {
                        LinkableTextView(
                            id = R.string.use_clear_urls_explainer,
                            style = LocalTextStyle.current.copy(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 16.sp
                            )
                        )
                    }
                )
            }

            item(key = "fastforward_rules") {
                SwitchRow(
                    state = viewModel.useFastForwardRules,
                    viewModel = viewModel,
                    headline = stringResource(id = R.string.fastfoward_rules),
                    subtitleBuilder = {
                        LinkableTextView(
                            id = R.string.fastfoward_rules_explainer,
                            style = LocalTextStyle.current.copy(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 16.sp
                            )
                        )
                    }
                )
            }

            item(key = "libredirect") {
                DividedSwitchRow(
                    state = viewModel.enableLibRedirect,
                    viewModel = viewModel,
                    headline = stringResource(id = R.string.enable_libredirect),
                    subtitleBuilder = {
                        LinkableTextView(
                            id = R.string.enable_libredirect_explainer,
                            style = LocalTextStyle.current.copy(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 16.sp,
                            ),
                            parentChecked = false,
                            parentClickListener = {
                                navController.navigate(libRedirectSettingsRoute)
                            }
                        )
                    },
                    onClick = {
                        navController.navigate(libRedirectSettingsRoute)
                    }
                )
            }

            item(key = "follow_redirects") {
                DividedSwitchRow(
                    state = viewModel.followRedirects,
                    viewModel = viewModel,
                    headline = R.string.follow_redirects,
                    subtitle = R.string.follow_redirects_explainer,
                    onClick = {
                        navController.navigate(followRedirectsSettingsRoute)
                    }
                )
            }

            item(key = "enable_downloader") {
                DividedSwitchRow(
                    state = viewModel.enableDownloader,
                    viewModel = viewModel,
                    headline = R.string.enable_downloader,
                    subtitle = R.string.enable_downloader_explainer,
                    onChange = {
                        requestDownloadPermission(
                            writeExternalStoragePermissionState,
                            viewModel,
                            viewModel.enableDownloader,
                            it
                        )
                    },
                    onClick = {
                        navController.navigate(downloaderSettingsRoute)
                    }
                )
            }
        }
    }
}