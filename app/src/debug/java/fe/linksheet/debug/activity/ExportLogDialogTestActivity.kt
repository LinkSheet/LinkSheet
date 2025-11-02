package fe.linksheet.debug.activity

import android.os.Bundle
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import fe.linksheet.activity.BaseComponentActivity
import fe.linksheet.composable.component.dialog.rememberExportLogDialog
import fe.linksheet.composable.page.settings.privacy.analytics.rememberAnalyticDialog
import fe.linksheet.module.analytics.TelemetryLevel
import fe.linksheet.module.viewmodel.util.LogViewCommon
import fe.linksheet.composable.ui.AppTheme
import fe.std.javatime.time.ISO8601DateTimeFormatter
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.LocalDateTime

class ExportLogDialogTestActivity : BaseComponentActivity(), KoinComponent {
    private val logViewCommon by inject<LogViewCommon>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val name = LocalDateTime.now().format(ISO8601DateTimeFormatter.FriendlyFormat)

        setContent(edgeToEdge = true) {
            AppTheme {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .systemBarsPadding()
                ) {
                    val dialogState = rememberExportLogDialog(
                        logViewCommon,
                        name
                    ) { listOf() }

                    Button(onClick = { dialogState.open() }) {
                        Text(text = "Open")
                    }

                    val analyticsState = rememberAnalyticDialog(telemetryLevel = TelemetryLevel.Minimal) {

                    }

                    Button(onClick = { analyticsState.open() }) {
                        Text(text = "Open2")
                    }

                }
            }
        }
    }
}
