package fe.linksheet.composable.settings.debug.log

import android.content.ClipboardManager
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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.junkfood.seal.ui.component.PreferenceSubtitle
import fe.android.compose.dialog.helper.OnClose
import fe.android.compose.dialog.helper.dialogHelper
import fe.linksheet.R
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.composable.util.BottomRow
import fe.linksheet.composable.util.DialogColumn
import fe.linksheet.composable.util.DialogSpacer
import fe.linksheet.composable.util.HeadlineText
import fe.linksheet.composable.util.ListState
import fe.linksheet.composable.util.SubtitleText
import fe.linksheet.composable.util.listState
import fe.linksheet.extension.ioState
import fe.linksheet.extension.listHelper
import fe.linksheet.extension.localizedString
import fe.linksheet.extension.setText
import fe.linksheet.extension.unixMillisToLocalDateTime
import fe.linksheet.module.log.LogEntry
import fe.linksheet.module.viewmodel.LogTextSettingsViewModel
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun LogTextSettingsRoute(
    onBackPressed: () -> Unit,
    viewModel: LogTextSettingsViewModel = koinViewModel(),
) {
    val timestamp by viewModel.timestamp.ioState()
    val logEntries by viewModel.logEntries.ioState()
    val listState = remember(logEntries?.size) {
        listState(logEntries)
    }

    val exportDialog = dialogHelper<Unit, List<LogEntry>, Unit>(
        fetch = { logEntries!! },
        awaitFetchBeforeOpen = true,
        dynamicHeight = true
    ) { state, close -> ExportDialog(state!!, close, viewModel.clipboardManager) }

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
                                        text = unixMillis.unixMillisToLocalDateTime()
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportDialog(
    logEntries: List<LogEntry>,
    close: OnClose<Unit>,
    clipboardManager: ClipboardManager
) {
    var expanded by remember { mutableStateOf(false) }
    val items = remember {
        mapOf<Int, (LogEntry) -> String>(
            R.string.export_mode_redacted to { it.messagePrefix + it.redactedMessage },
            R.string.export_mode_default to { it.messagePrefix + it.message }
        )
    }

    var selectedMode by remember { mutableIntStateOf(items.keys.first()) }

    DialogColumn {
        HeadlineText(headlineId = R.string.export_log)
        DialogSpacer()
        SubtitleText(subtitleId = R.string.export_mode)

        Spacer(modifier = Modifier.height(10.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {
            TextField(
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                value = stringResource(id = selectedMode),
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                items.forEach { (textId, _) ->
                    DropdownMenuItem(
                        text = { Text(text = stringResource(id = textId)) },
                        onClick = {
                            selectedMode = textId
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        BottomRow {
            TextButton(
                onClick = {
                    clipboardManager.setText(
                        "Log",
                        logEntries.joinToString(separator = "\n") { items[selectedMode]!!.invoke(it) }
                    )
                    close(Unit)
                }
            ) {
                Text(text = stringResource(id = R.string.export))
            }
        }
    }
}