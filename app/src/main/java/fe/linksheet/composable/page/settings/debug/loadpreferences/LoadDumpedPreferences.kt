package fe.linksheet.composable.page.settings.debug.loadpreferences

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.linksheet.compose.page.SaneScaffoldSettingsPage
import fe.linksheet.R
import fe.linksheet.module.viewmodel.LoadDumpedPreferencesViewModel
import org.koin.androidx.compose.koinViewModel


@Composable
fun LoadDumpedPreferences(
    viewModel: LoadDumpedPreferencesViewModel = koinViewModel(),
    onBackPressed: () -> Unit
) {
//    val exportDialog = dialogHelper<Unit, List<LogEntry>, Unit>(
//        fetch = { logEntries!! },
//        awaitFetchBeforeOpen = true,
//        dynamicHeight = true
//    ) { state, close ->
//        ExportLogDialog(
//            logViewCommon = viewModel.logViewCommon,
//            clipboardManager = viewModel.clipboardManager,
//            close = close,
//        ) { sb, redactLog ->
//            state!!.joinTo(
//                sb,
//                separator = lineSeparator,
//                postfix = lineSeparator,
//                transform = items[redactLog]!!
//            )
//        }
//    }

    var text by remember { mutableStateOf("") }

    SaneScaffoldSettingsPage(
        headline = stringResource(id = R.string.import_dumped_preference),
        onBackPressed = onBackPressed
    ) {
        item {
            Column {
                TextField(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 10.dp)
                        .padding(bottom = 56.dp)
                        .background(MaterialTheme.colorScheme.surface)
                        .border(
                            width = 1.dp, color = Color.Black, shape = RoundedCornerShape(8.dp)
                        ), value = text, onValueChange = { text = it })

            }
        }

        item {
            TextButton(onClick = {
                viewModel.importText(text)
            }) {
                Text(text = stringResource(id = R.string.import_and_override))
            }
        }
    }
}

