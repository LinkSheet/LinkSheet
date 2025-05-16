package fe.linksheet.activity

import android.os.Bundle
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fe.linksheet.R
import fe.linksheet.composable.component.dialog.rememberExportLogDialog
import fe.linksheet.composable.page.settings.debug.log.LogCard
import fe.linksheet.composable.page.settings.debug.log.LogTextPageScaffold
import fe.linksheet.composable.page.settings.debug.log.PrefixMessageCardContent
import fe.linksheet.composable.ui.AppTheme
import fe.linksheet.extension.koin.injectLogger
import fe.linksheet.module.viewmodel.CrashHandlerViewerViewModel
import fe.std.javatime.extension.unixMillisUtc
import fe.std.javatime.time.ISO8601DateTimeFormatter
import fe.std.time.unixMillisOf
import org.koin.androidx.compose.koinViewModel
import org.koin.core.component.KoinComponent

class CrashHandlerActivity : BaseComponentActivity(), KoinComponent {
    companion object {
        const val EXTRA_CRASH_EXCEPTION = "EXTRA_CRASH_EXCEPTION_TEXT"
        const val EXTRA_CRASH_TIMESTAMP = "EXTRA_CRASH_TIMESTAMP"
    }

    private val logger by injectLogger<CrashHandlerActivity>()

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val throwableString = intent.getStringExtra(EXTRA_CRASH_EXCEPTION) ?: return
        logger.fatal(throwableString)
        val timestamp = intent.getLongExtra(EXTRA_CRASH_TIMESTAMP, 0)

        setContent(edgeToEdge = true) {
            AppTheme {
                CrashAppPageWrapper(timestamp = timestamp, throwableString = throwableString)
            }
        }
    }
}

@Composable
fun CrashAppPageWrapper(
    viewModel: CrashHandlerViewerViewModel = koinViewModel(),
    timestamp: Long,
    throwableString: String
) {
    val strTime = timestamp.unixMillisUtc.format(ISO8601DateTimeFormatter.FriendlyFormat)
    val dialog = rememberExportLogDialog(
        logViewCommon = viewModel.logViewCommon,
        name = strTime,
        fnLogEntries = {
            viewModel.logPersistService.readEntries(null)
        }
    )

    CrashAppPage(timestamp = timestamp, throwableString = throwableString, openDialog = dialog::open)
}

@Composable
fun CrashAppPage(timestamp: Long, throwableString: String, openDialog: () -> Unit) {
    LogTextPageScaffold(
        headline = stringResource(id = R.string.app_name),
        onBackPressed = {},
        enableBackButton = false,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                modifier = Modifier.padding(paddingValues = WindowInsets.navigationBars.asPaddingValues()),
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.Share,
                        contentDescription = null
                    )
                },
                text = {
                    Text(text = stringResource(id = R.string.generic__button_text_share))
                },
                onClick = openDialog
            )
        }
    ) {
        divider(id = R.string.crash_viewer__subtitle_app_crashed)

        item {
            LogCard(
                border = BorderStroke(0.dp, Color.Transparent),
                logEntry = PrefixMessageCardContent(
                    type = "F",
                    prefix = "Crash",
                    start = timestamp,
                    messages = mutableListOf(throwableString)
                )
            )
        }
    }
}

@Preview
@Composable
private fun CrashAppPagePreview() {
    CrashAppPage(timestamp = unixMillisOf(2025), throwableString = "Crash") { }
}
