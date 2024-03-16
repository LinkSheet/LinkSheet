package fe.linksheet.composable.settings.debug.log

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import fe.android.compose.route.util.navigate
import fe.kotlin.extension.time.localizedString
import fe.linksheet.LogTextViewerRoute
import fe.linksheet.R
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.composable.util.*
import fe.linksheet.extension.kotlin.collectOnIO
import fe.linksheet.extension.compose.listHelper
import fe.linksheet.logTextViewerSettingsRoute
import fe.linksheet.module.viewmodel.LogSettingsViewModel
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LogSettingsRoute(
    onBackPressed: () -> Unit,
    navController: NavHostController,
    viewModel: LogSettingsViewModel = koinViewModel()
) {
    val files by viewModel.files.collectOnIO()
    val mapState = remember(files) {
        listState(files)
    }

    val startupTime = viewModel.fileAppLogger.startupTime.localizedString()

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

            listHelper(
                noItems = R.string.no_logs_found,
                listState = mapState,
                list = files,
                listKey = { it.millis },
            ) {
                DividedRow(
                    paddingHorizontal = 10.dp,
                    paddingVertical = 5.dp,
                    leftContent = {
                        ClickableRow(
                            paddingHorizontal = 0.dp,
                            paddingVertical = 0.dp,
                            onClick = {
                                navController.navigate(
                                    logTextViewerSettingsRoute,
                                    LogTextViewerRoute(
                                        it.localizedTime,
                                        it.file.name
                                    )
                                )
                            }
                        ) {
                            Texts(
                                headline = it.localizedTime,
                                subtitle = it.file.name
                            )
                        }
                    },
                    rightContent = {
                        IconButton(onClick = {
                            viewModel.deleteFileAsync(it)
                        }) {
                            ColoredIcon(
                                icon = Icons.Default.Delete,
                                descriptionId = R.string.delete,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )
            }
        }
    }
}
