package fe.linksheet.experiment.ui.overhaul.composable.page.settings.debug.log

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import fe.linksheet.R
import fe.linksheet.composable.util.ListState
import fe.linksheet.composable.util.listState
import fe.linksheet.experiment.ui.overhaul.composable.component.dialog.rememberNewExportLogDialog
import fe.linksheet.extension.compose.listHelper
import fe.linksheet.extension.kotlin.collectOnIO
import fe.linksheet.module.log.file.entry.LogEntry
import fe.linksheet.module.viewmodel.LogTextSettingsViewModel
import org.koin.androidx.compose.koinViewModel


@Composable
fun NewLogTextSettingsRoute(
    onBackPressed: () -> Unit,
    viewModel: LogTextSettingsViewModel = koinViewModel(),
) {
    val timestamp by viewModel.timestamp.collectOnIO()
    val logEntries by viewModel.logEntries.collectOnIO()
    val listState = remember(logEntries?.size) {
        listState(logEntries)
    }

    val exportDialog = rememberNewExportLogDialog(logViewCommon = viewModel.logViewCommon,
        name = timestamp,
        fnLogEntries = { logEntries!! }
    )

    val text = stringResource(id = R.string.settings_debug_log_viewer__title_log_captured_at, timestamp)

    LogTextPageScaffold(
        headline = stringResource(id = R.string.log_viewer),
        onBackPressed = onBackPressed,
        floatingActionButton = {
            if (listState == ListState.Items) {
                FloatingActionButton(
                    modifier = Modifier.padding(paddingValues = WindowInsets.navigationBars.asPaddingValues()),
                    onClick = exportDialog::open
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = null
                    )
                }
            }
        }
    ) {
        divider(
            key = R.string.settings_debug_log_viewer__title_log_captured_at,
            text = text
        )

        listHelper(noItems = R.string.no_log_entries,
            listState = listState,
            list = logEntries?.let { mergeEntries(it) },
            listKey = { it.hashCode() }
        ) { logEntry, _, _ -> LogCard(logEntry) }
    }
}

private fun mergeEntries(logEntries: List<LogEntry>): MutableList<PrefixMessageCardContent> {
    val merged = mutableListOf<PrefixMessageCardContent>()
    var last: PrefixMessageCardContent? = null
    for (entry in logEntries) {
        if (last != null) {
            if (last.matches(entry)) {
                last.add(entry)
            } else {
                merged.add(last)
                last = null
            }
        } else {
            last = PrefixMessageCardContent(entry.type, entry.prefix, entry.unixMillis)
            last.add(entry)
        }
    }

    if (last != null) {
        merged.add(last)
    }

    return merged
}
