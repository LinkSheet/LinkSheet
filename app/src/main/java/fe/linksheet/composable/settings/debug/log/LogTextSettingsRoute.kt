package fe.linksheet.composable.settings.debug.log

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fe.linksheet.composable.util.PreferenceSubtitle
import fe.android.compose.dialog.helper.dialogHelper
import fe.kotlin.extension.localizedString
import fe.kotlin.extension.unixMillisUtc
import fe.linksheet.R
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.composable.util.BottomRow
import fe.linksheet.composable.util.ExportLogDialog
import fe.linksheet.composable.util.ListState
import fe.linksheet.composable.util.listState
import fe.linksheet.extension.collectOnIO
import fe.linksheet.extension.compose.listHelper
import fe.linksheet.module.log.LogEntry
import fe.linksheet.module.viewmodel.LogTextSettingsViewModel
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun LogTextSettingsRoute(
    onBackPressed: () -> Unit,
    viewModel: LogTextSettingsViewModel = koinViewModel(),
) {
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

    SettingsScaffold(R.string.log_viewer, onBackPressed = onBackPressed) { padding ->
        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .weight(1f),
                contentPadding = PaddingValues(5.dp)
            ) {
                stickyHeader(key = "header") {
                    Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                        PreferenceSubtitle(
                            text = stringResource(id = R.string.log_viewer_timestamp, timestamp),
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }

                listHelper(
                    noItems = R.string.no_log_entries,
                    listState = listState,
                    list = logEntries,
                    listKey = { it.hashCode() }
                ) { (type, unixMillis, prefix, message) ->
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
                                            text = type
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
                                            ), text = prefix
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(5.dp))

                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = message,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }

                                Spacer(modifier = Modifier.height(2.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    Text(
                                        text = unixMillis.unixMillisUtc.value
                                            .localizedString(),
                                        fontStyle = FontStyle.Italic,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(5.dp))
                }
            }

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

