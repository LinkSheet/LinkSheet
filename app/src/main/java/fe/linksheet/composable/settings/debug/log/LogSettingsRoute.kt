package fe.linksheet.composable.settings.debug.log

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.junkfood.seal.ui.component.PreferenceSubtitle
import fe.linksheet.R
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.composable.util.ClickableRow
import fe.linksheet.composable.util.dialogHelper
import fe.linksheet.extension.ioState
import fe.linksheet.module.viewmodel.LogSettingsViewModel
import fe.linksheet.ui.HkGroteskFontFamily
import org.koin.androidx.compose.koinViewModel
import java.io.File


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LogSettingsRoute(
    onBackPressed: () -> Unit,
    viewModel: LogSettingsViewModel = koinViewModel()
) {
    val files by viewModel.files.ioState()

    val dialog = dialogHelper<File, String, Unit>(
        fetch = { it.readText() },
        awaitFetchBeforeOpen = true,
        notifyCloseNoState = false,
        dynamicHeight = true
    ) { state, _ ->
        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(5.dp)
            ) {
                stickyHeader(key = "header") {
                    Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                        PreferenceSubtitle(
                            text = stringResource(id = R.string.app_log),
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }


                item("log") {
                    SelectionContainer {
                        Text(
                            text = state!!,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 15.dp)
                    .height(50.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = {

                    }
                ) {
                    Text(text = stringResource(id = R.string.copy_exception))
                }
            }
        }
    }

    SettingsScaffold(R.string.logs, onBackPressed = onBackPressed) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxHeight(),
            contentPadding = PaddingValues(horizontal = 5.dp)
        ) {
            if (files != null) {
                items(items = files!!, key = { it.name }) {
                    ClickableRow(
                        paddingHorizontal = 10.dp,
                        paddingVertical = 5.dp,
                        onClick = { dialog.open(it) }
                    ) {
                        Text(
                            text = it.name,
                            fontFamily = HkGroteskFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}