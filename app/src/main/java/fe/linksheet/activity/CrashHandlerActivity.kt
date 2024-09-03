package fe.linksheet.activity

import android.os.Bundle
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fe.kotlin.time.ISO8601DateTimeFormatter
import fe.linksheet.R
import fe.linksheet.composable.component.dialog.createExportLogDialog
import fe.linksheet.composable.page.settings.debug.log.LogCard
import fe.linksheet.composable.page.settings.debug.log.LogTextPageScaffold
import fe.linksheet.composable.page.settings.debug.log.PrefixMessageCardContent
import fe.linksheet.extension.koin.injectLogger
import fe.linksheet.module.log.file.LogFileService
import fe.linksheet.module.viewmodel.CrashHandlerViewerViewModel
import fe.linksheet.composable.ui.AppTheme
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.LocalDateTime

class CrashHandlerActivity : BaseComponentActivity(), KoinComponent {
    companion object {
        const val EXTRA_CRASH_EXCEPTION = "EXTRA_CRASH_EXCEPTION_TEXT"
    }

    private val viewModel by viewModel<CrashHandlerViewerViewModel>()
    private val logger by injectLogger<CrashHandlerActivity>()
    private val logFileService by inject<LogFileService>()

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val throwableString = intent.getStringExtra(EXTRA_CRASH_EXCEPTION) ?: return
        logger.fatal(throwableString)

        val timestamp = LocalDateTime.now().format(ISO8601DateTimeFormatter.DefaultFormat)

        setContent(edgeToEdge = true) {
            AppTheme {
                val openDialog = createExportLogDialog(
                    uiOverhaul = true,
                    name = timestamp,
                    logViewCommon = viewModel.logViewCommon,
                    clipboardManager = viewModel.clipboardManager
                ) { logFileService.logEntries }

                LogTextPageScaffold(
                    headline = stringResource(id = R.string.app_name),
                    onBackPressed = {},
                    enableBackButton = false,
                    floatingActionButton = {
                        FloatingActionButton(
                            modifier = Modifier.padding(paddingValues = WindowInsets.navigationBars.asPaddingValues()),
                            onClick = { openDialog() }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Share,
                                contentDescription = null
                            )
                        }
                    }
                ) {
                    divider(id =  R.string.crash_viewer__subtitle_app_crashed)

                    item {
                        LogCard(
                            border = BorderStroke(0.dp, Color.Transparent),
                            logEntry = PrefixMessageCardContent(
                                type = "F",
                                prefix = "Crash",
                                start = System.currentTimeMillis(),
                                messages = mutableListOf(throwableString)
                            )
                        )
                    }
                }
            }
        }
    }
}
