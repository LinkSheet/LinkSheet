package fe.linksheet.experiment.ui.overhaul.composable.page.settings.debug.log

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fe.linksheet.composable.util.PreferenceSubtitle
import fe.android.compose.dialog.helper.dialogHelper
import fe.kotlin.extension.primitive.unixMillisUtc
import fe.kotlin.extension.time.localizedString
import fe.linksheet.R
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.composable.util.BottomRow
import fe.linksheet.composable.util.ExportLogDialog
import fe.linksheet.composable.util.ListState
import fe.linksheet.composable.util.listState
import fe.linksheet.experiment.ui.overhaul.composable.component.page.SaneScaffoldSettingsPage
import fe.linksheet.extension.compose.listHelper
import fe.linksheet.extension.kotlin.collectOnIO
import fe.linksheet.module.log.file.entry.LogEntry
import fe.linksheet.module.viewmodel.LogTextSettingsViewModel
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun NewLogTextSettingsRoute(
    onBackPressed: () -> Unit,
    viewModel: LogTextSettingsViewModel = koinViewModel(),
) {
    val context = LocalContext.current

    val timestamp by viewModel.timestamp.collectOnIO()
    val logEntries by viewModel.logEntries.collectOnIO()
    val listState = remember(logEntries?.size) {
        listState(logEntries)
    }

    val exportDialog = dialogHelper<Unit, List<LogEntry>, Unit>(
        fetch = { logEntries!! },
        awaitFetchBeforeOpen = true,
        dynamicHeight = true
    ) { state, close ->
        ExportLogDialog(
            logViewCommon = viewModel.logViewCommon,
            clipboardManager = viewModel.clipboardManager,
            logEntries = state!!,
            close = close,
        )
    }

    SaneScaffoldSettingsPage(headline = stringResource(id = R.string.log_viewer), onBackPressed = onBackPressed) {
        divider(key = R.string.log_viewer_timestamp, text = context.getString(R.string.log_viewer_timestamp, timestamp))

        listHelper(
            noItems = R.string.no_log_entries,
            listState = listState,
            list = logEntries,
            listKey = { it.hashCode() }
        ) { logEntry, padding, shape ->
            SelectionContainer {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                ) {
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                    ) {
                        Row {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary),
                                shape = RoundedCornerShape(
                                    topStart = 50.dp,
                                    bottomStart = 50.dp,
                                    topEnd = 0.dp,
                                    bottomEnd = 0.dp
                                )
                            ) {
                                Text(
                                    modifier = Modifier.padding(
                                        horizontal = 5.dp,
                                        vertical = 2.dp
                                    ),
                                    fontWeight = FontWeight.SemiBold,
                                    text = logEntry.type
                                )
                            }

                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.inversePrimary),
                                shape = RoundedCornerShape(
                                    topStart = 0.dp,
                                    bottomStart = 0.dp,
                                    topEnd = 50.dp,
                                    bottomEnd = 50.dp
                                )
                            ) {
                                Text(
                                    modifier = Modifier.padding(
                                        horizontal = 5.dp,
                                        vertical = 2.dp
                                    ), text = logEntry.prefix ?: stringResource(id = R.string.app_name)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(5.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = logEntry.message,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = logEntry.unixMillis.unixMillisUtc.value
                                    .localizedString(),
                                fontStyle = FontStyle.Italic,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }


//            Spacer(modifier = Modifier.height(5.dp))
        }

        item {
            if (listState == ListState.Items) {
                BottomRow {
                    TextButton(
                        onClick = {
                            exportDialog.open(Unit)
                        }
                    ) {
                        Text(text = stringResource(id = R.string.export))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LogCard(logEntry: LogEntry) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp)
    ) {
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Row {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary),
                    shape = RoundedCornerShape(
                        topStart = 50.dp,
                        bottomStart = 50.dp,
                        topEnd = 0.dp,
                        bottomEnd = 0.dp
                    )
                ) {
                    Text(
                        modifier = Modifier.padding(
                            horizontal = 5.dp,
                            vertical = 2.dp
                        ),
                        fontWeight = FontWeight.SemiBold,
                        text = logEntry.type
                    )
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.inversePrimary),
                    shape = RoundedCornerShape(
                        topStart = 0.dp,
                        bottomStart = 0.dp,
                        topEnd = 50.dp,
                        bottomEnd = 50.dp
                    )
                ) {
                    Text(
                        modifier = Modifier.padding(
                            horizontal = 5.dp,
                            vertical = 2.dp
                        ), text = logEntry.prefix ?: stringResource(id = R.string.app_name)
                    )
                }
            }

            Spacer(modifier = Modifier.height(5.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = logEntry.message,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = logEntry.unixMillis.unixMillisUtc.value
                        .localizedString(),
                    fontStyle = FontStyle.Italic,
                    fontSize = 12.sp
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun LogCardPreview() {
    LogCard(
        LogEntry.DefaultLogEntry(
            type = "D",
            prefix = "AnalyticsClient",
            message = "Trying to send events (attemptNo: 1)"
        )
    )
}
