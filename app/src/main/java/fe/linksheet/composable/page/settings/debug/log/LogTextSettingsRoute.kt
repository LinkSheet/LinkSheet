package fe.linksheet.composable.page.settings.debug.log

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
import fe.linksheet.composable.component.dialog.rememberExportLogDialog
import app.linksheet.compose.extension.listHelper
import app.linksheet.compose.util.ListState
import app.linksheet.compose.util.listState
import fe.linksheet.extension.kotlin.collectOnIO
import fe.linksheet.module.viewmodel.LogTextSettingsViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf


@Composable
fun LogTextSettingsRoute(
    onBackPressed: () -> Unit,
    sessionId: String?,
    sessionName: String,
    viewModel: LogTextSettingsViewModel = koinViewModel(parameters = { parametersOf(sessionId) }),
) {
    val logEntries by viewModel.logEntries.collectOnIO()
    val listState = remember(logEntries?.size) {
        listState(logEntries)
    }

    val exportDialog = rememberExportLogDialog(logViewCommon = viewModel.logViewCommon,
        name = sessionName,
        fnLogEntries = { logEntries!! }
    )

    val text = stringResource(id = R.string.settings_debug_log_viewer__title_log_captured_at, sessionName)

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
            list = logEntries?.let { viewModel.merger.mergeEntries(it) },
            listKey = { it.hashCode() }
        ) { logEntry, _, _ -> LogCard(logEntry) }
    }
}
