package fe.linksheet.composable.settings.debug.log

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import fe.android.compose.route.util.navigate
import fe.linksheet.LogTextViewerRoute
import fe.linksheet.R
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.composable.util.ClickableRow
import fe.linksheet.composable.util.HeadlineText
import fe.linksheet.composable.util.SettingEnabledCardColumnCommon
import fe.linksheet.composable.util.SubtitleText
import fe.linksheet.composable.util.Texts
import fe.linksheet.extension.ioState
import fe.linksheet.extension.localizedString
import fe.linksheet.extension.unixMillisToLocalDateTime
import fe.linksheet.logTextViewerSettingsRoute
import fe.linksheet.module.log.AppLogger
import fe.linksheet.module.viewmodel.LogSettingsViewModel
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LogSettingsRoute(
    onBackPressed: () -> Unit,
    navController: NavHostController,
    viewModel: LogSettingsViewModel = koinViewModel()
) {
    val files by viewModel.files.ioState()
    val startupTime = AppLogger.getInstance().startupTime.localizedString()

    SettingsScaffold(R.string.logs, onBackPressed = onBackPressed) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxHeight(),
            contentPadding = PaddingValues(horizontal = 5.dp)
        ) {
            stickyHeader(key = "current") {
                SettingEnabledCardColumnCommon(contentTitle = stringResource(id = R.string.log_files)) {
                    ClickableRow(
                        paddingHorizontal = 10.dp,
                        paddingVertical = 10.dp,
                        onClick = {
                            navController.navigate(
                                logTextViewerSettingsRoute,
                                LogTextViewerRoute(
                                    startupTime,
                                    null
                                )
                            )
                        }
                    ) {
                        Column(verticalArrangement = Arrangement.Center) {
                            HeadlineText(headline = startupTime)
                            SubtitleText(subtitleId = R.string.current_session)
                        }
                    }
                }
            }

            if (files != null) {
                items(items = files!!, key = { it }) { fileName ->
                    ClickableRow(
                        paddingHorizontal = 10.dp,
                        paddingVertical = 5.dp,
                        onClick = {
                            navController.navigate(
                                logTextViewerSettingsRoute,
                                LogTextViewerRoute(
                                    startupTime,
                                    fileName
                                )
                            )
                        }
                    ) {
                        Texts(
                            headline = fileName.toLong().unixMillisToLocalDateTime().localizedString(),
                            subtitle = fileName + AppLogger.fileExt
                        )
                    }
                }
            }
        }
    }
}