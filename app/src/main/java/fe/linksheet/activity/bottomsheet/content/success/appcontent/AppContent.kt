package fe.linksheet.activity.bottomsheet.content.success.appcontent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fe.linksheet.activity.bottomsheet.ClickModifier
import fe.linksheet.module.app.ActivityAppInfo

@Composable
fun AppContent(
    info: ActivityAppInfo?,
    appListSelectedIdx: Int,
    hasPreferredApp: Boolean,
    hideChoiceButtons: Boolean,
    launch: (ActivityAppInfo, ClickModifier) -> Unit,
    showToast: (Int, Int, Boolean) -> Unit,
    content: @Composable (Modifier) -> Unit,
) {
    Column(modifier = Modifier, verticalArrangement = Arrangement.spacedBy(5.dp)) {
        content(
            Modifier
                .fillMaxWidth()
                .weight(1.0f, fill = false),
        )

        if (!hasPreferredApp && !hideChoiceButtons) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .requiredHeight(height = ButtonDefaults.MinHeight)
            ) {
                NoPreferredAppChoiceButtons(
                    info = info,
                    selected = appListSelectedIdx,
                    launch = launch,
                    showToast = showToast
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AppContentPreview() {
    AppContent(
        info = null,
        appListSelectedIdx = -1,
        hasPreferredApp = false,
        hideChoiceButtons = false,
        launch = { _, _ -> },
        showToast = { _, _, _ -> }
    ) {
    }
}
