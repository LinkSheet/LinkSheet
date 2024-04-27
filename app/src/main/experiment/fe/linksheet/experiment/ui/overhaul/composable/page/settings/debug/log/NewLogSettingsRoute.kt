package fe.linksheet.experiment.ui.overhaul.composable.page.settings.debug.log

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.FileUpload
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.navigation.NavHostController
import fe.kotlin.extension.time.localizedString
import fe.linksheet.LogTextViewerRoute
import fe.linksheet.R
import fe.linksheet.composable.util.listState
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.ClickableShapeListItem
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.ShapeListItemDefaults
import fe.linksheet.experiment.ui.overhaul.composable.component.page.SaneScaffoldSettingsPage
import fe.linksheet.experiment.ui.overhaul.composable.component.util.Default.Companion.textOrNull
import fe.linksheet.experiment.ui.overhaul.composable.component.util.Default.Companion.text
import fe.linksheet.experiment.ui.overhaul.composable.component.util.Resource.Companion.textContent
import fe.linksheet.extension.compose.listHelper
import fe.linksheet.extension.kotlin.collectOnIO
import fe.linksheet.logTextViewerSettingsRoute
import fe.linksheet.module.viewmodel.LogSettingsViewModel
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultClickableShapeListItem2(
    enabled: Boolean = true,
    headline: String,
    subtitle: String? = null,
    shape: Shape = ShapeListItemDefaults.SingleShape,
    padding: PaddingValues = PaddingValues(),
    onClick: () -> Unit,
    onExport: () -> Unit,
    onDelete: () -> Unit,
) {
    ClickableShapeListItem(
        enabled = enabled,
        shape = shape,
        padding = padding,
        onClick = onClick,
        role = Role.Button,
        headlineContent = text(headline),
        supportingContent = textOrNull(subtitle),
        trailingContent = {
            Row(modifier = Modifier.fillMaxHeight(), verticalAlignment = Alignment.CenterVertically) {
                CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                    FilledTonalIconButton(onClick = onExport) {
                        Icon(imageVector = Icons.Outlined.FileUpload, contentDescription = headline)
                    }

                    FilledTonalIconButton(onClick = onDelete) {
                        Icon(imageVector = Icons.Outlined.Delete, contentDescription = headline)
                    }
                }
            }
        }
    )
}

@Composable
fun NewLogSettingsRoute(
    onBackPressed: () -> Unit,
    navigate: (String) -> Unit,
    navController: NavHostController,
    viewModel: LogSettingsViewModel = koinViewModel(),
) {
    val files by viewModel.files.collectOnIO()
    val mapState = remember(files) {
        listState(files)
    }

    val startupTime = viewModel.fileAppLogger.startupTime.localizedString()

    SaneScaffoldSettingsPage(headline = stringResource(id = R.string.logs), onBackPressed = onBackPressed) {
        item(key = R.string.reset_app_link_verification_status) {
            ClickableShapeListItem(
                shape = ShapeListItemDefaults.SingleShape,
                role = Role.Button,
                headlineContent = text(startupTime),
                supportingContent = textContent(id = R.string.current_session),
                onClick = {
                    navigate(logTextViewerSettingsRoute.buildNavigation(LogTextViewerRoute(startupTime, null)))
                }
            )
        }

        divider(stringRes = R.string.log_files)

        listHelper(
            noItems = R.string.no_logs_found,
            listState = mapState,
            list = files,
            listKey = { it.millis },
        ) { file, padding, shape ->
            DefaultClickableShapeListItem2(
                shape = shape,
                padding = padding,
                headline = file.localizedTime,
                subtitle = file.file.name,
                onClick = {
                    navigate(
                        logTextViewerSettingsRoute.buildNavigation(
                            LogTextViewerRoute(
                                file.localizedTime,
                                file.file.name
                            )
                        )
                    )
                },
                onExport = {

                },
                onDelete = {
                    viewModel.deleteFileAsync(file)
                }
            )
        }
    }


//    SettingsScaffold(R.string.logs, onBackPressed = onBackPressed) { padding ->
//        LazyColumn(
//            modifier = Modifier
//                .padding(padding)
//                .fillMaxHeight(),
//            contentPadding = PaddingValues(horizontal = 5.dp)
//        ) {
//            stickyHeader(key = "current") {
//                SettingEnabledCardColumnCommon(contentTitle = stringResource(id = R.string.log_files)) {
//                    ClickableRow(
//                        paddingHorizontal = 10.dp,
//                        paddingVertical = 10.dp,
//                        onClick = {
//                            navController.navigate(
//                                logTextViewerSettingsRoute,
//                                LogTextViewerRoute(
//                                    startupTime,
//                                    null
//                                )
//                            )
//                        }
//                    ) {
//                        Column(verticalArrangement = Arrangement.Center) {
//                            HeadlineText(headline = startupTime)
//                            SubtitleText(subtitleId = R.string.current_session)
//                        }
//                    }
//                }
//            }
//
//            listHelper(
//                noItems = R.string.no_logs_found,
//                listState = mapState,
//                list = files,
//                listKey = { it.millis },
//            ) {
//                DividedRow(
//                    paddingHorizontal = 10.dp,
//                    paddingVertical = 5.dp,
//                    leftContent = {
//                        ClickableRow(
//                            paddingHorizontal = 0.dp,
//                            paddingVertical = 0.dp,
//                            onClick = {
//                                navController.navigate(
//                                    logTextViewerSettingsRoute,
//                                    LogTextViewerRoute(
//                                        it.localizedTime,
//                                        it.file.name
//                                    )
//                                )
//                            }
//                        ) {
//                            Texts(
//                                headline = it.localizedTime,
//                                subtitle = it.file.name
//                            )
//                        }
//                    },
//                    rightContent = {
//                        IconButton(onClick = {
//                            viewModel.deleteFileAsync(it)
//                        }) {
//                            ColoredIcon(
//                                icon = Icons.Default.Delete,
//                                descriptionId = R.string.delete,
//                                color = MaterialTheme.colorScheme.primary
//                            )
//                        }
//                    }
//                )
//            }
//        }
//    }
}
