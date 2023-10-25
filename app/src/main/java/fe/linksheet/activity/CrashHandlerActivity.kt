package fe.linksheet.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fe.linksheet.composable.util.PreferenceSubtitle
import fe.android.compose.dialog.helper.dialogHelper
import fe.linksheet.R
import fe.linksheet.composable.util.BottomRow
import fe.linksheet.composable.util.ExportLogDialog
import fe.linksheet.extension.koin.injectLogger
import fe.linksheet.module.log.AppLogger
import fe.linksheet.module.log.LogEntry
import fe.linksheet.module.log.Logger
import fe.linksheet.module.viewmodel.CrashHandlerViewerViewModel
import fe.linksheet.ui.AppHost
import fe.linksheet.ui.HkGroteskFontFamily
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent

class CrashHandlerActivity : ComponentActivity(), KoinComponent {
    companion object {
        const val extraCrashException = "EXTRA_CRASH_EXCEPTION_TEXT"
    }

    private val viewModel by viewModel<CrashHandlerViewerViewModel>()
    private val logger by injectLogger(CrashHandlerActivity::class)

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val exception = intent?.getStringExtra(extraCrashException)!!
        logger.print(Logger.Type.Debug, exception)

        setContent {
            AppHost {
                val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
                    rememberTopAppBarState(),
                    canScroll = { true }
                )

                val exportDialog = dialogHelper<Unit, List<LogEntry>, Unit>(
                    fetch = { AppLogger.getInstance().logEntries },
                    awaitFetchBeforeOpen = true,
                    dynamicHeight = true
                ) { state, close ->
                    ExportLogDialog(
                        logViewCommon = viewModel.logViewCommon,
                        clipboardManager = viewModel.clipboardManager,
                        close = close,
                        logEntries = state!!
                    )
                }

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .nestedScroll(scrollBehavior.nestedScrollConnection),
                    topBar = {
                        LargeTopAppBar(
                            colors = TopAppBarDefaults.largeTopAppBarColors(containerColor = Color.Transparent),
                            title = {
                                Text(
                                    text = stringResource(id = R.string.app_name),
                                    fontFamily = HkGroteskFontFamily,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }, scrollBehavior = scrollBehavior
                        )
                    },
                    content = { padding ->
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
                                            text = stringResource(id = R.string.app_crashed),
                                        )

                                        Spacer(modifier = Modifier.height(10.dp))
                                    }
                                }


                                item("exception") {
                                    SelectionContainer {
                                        Text(
                                            text = exception,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }
                            }

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
                )
            }
        }
    }
}