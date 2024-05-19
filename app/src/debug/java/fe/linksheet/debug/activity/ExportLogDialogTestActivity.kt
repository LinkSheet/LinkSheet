package fe.linksheet.debug.activity

import android.os.Bundle
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import fe.kotlin.time.ISO8601DateTimeFormatter
import fe.linksheet.activity.BaseComponentActivity
import fe.linksheet.experiment.ui.overhaul.composable.component.dialog.rememberNewExportLogDialog
import fe.linksheet.module.viewmodel.util.LogViewCommon
import fe.linksheet.ui.AppTheme
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.LocalDateTime

class ExportLogDialogTestActivity : BaseComponentActivity(), KoinComponent {
    private val logViewCommon by inject<LogViewCommon>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val name = LocalDateTime.now().format(ISO8601DateTimeFormatter.DefaultFormat)

        setContent(edgeToEdge = true) {
            AppTheme {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .systemBarsPadding()
                ) {
                    val dialogState = rememberNewExportLogDialog(
                        logViewCommon,
                        name,
                        listOf()
                    )

                    Button(onClick = { dialogState.open() }) {
                        Text(text = "Open")
                    }
                }
            }
        }
    }
}
