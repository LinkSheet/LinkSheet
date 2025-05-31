package fe.linksheet.activity.bottomsheet.content.success.appcontent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.linksheet.preview.PreviewDebugProvider
import fe.linksheet.activity.bottomsheet.Interaction
import fe.linksheet.composable.util.debugBorder
import fe.linksheet.module.app.ActivityAppInfo
import fe.linksheet.module.debug.DebugPreferenceProvider
import fe.linksheet.module.debug.LocalUiDebug
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun AppContent(
    info: ActivityAppInfo?,
    appListSelectedIdx: Int,
    hasPreferredApp: Boolean,
    hideChoiceButtons: Boolean,
    dispatch: (Interaction) -> Unit,
    showToast: (Int, Int, Boolean) -> Unit,
    content: @Composable (Modifier) -> Unit,
) {
    val debug by LocalUiDebug.current.drawBorders.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier.debugBorder(debug, 1.dp, Color.Magenta),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        content(
            Modifier
                .fillMaxWidth()
                .weight(0.1f, fill = false)
        )

        if (!hasPreferredApp && !hideChoiceButtons) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = ButtonDefaults.MinHeight)
//                    .weight(0.1f, fill = true)
            ) {
                NoPreferredAppChoiceButtons(
                    info = info,
                    selected = appListSelectedIdx,
                    dispatch = dispatch,
                    showToast = showToast
                )
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 100)
@Composable
private fun AppContentPreviewSmallerHeight() {
    AppContentPreviewBase()
}

@Preview(showBackground = true)
@Composable
private fun AppContentPreview() {
    AppContentPreviewBase()
}

@Composable
private fun AppContentPreviewBase() {
    CompositionLocalProvider(LocalUiDebug provides PreviewDebugProvider()) {
        AppContent(
            info = null,
            appListSelectedIdx = -1,
            hasPreferredApp = false,
            hideChoiceButtons = false,
            dispatch = {  },
            showToast = { _, _, _ -> }
        ) { modifier ->
            LazyColumn(modifier = modifier) {
                items(count = 25) {
                    Text(text = "Text ${it + 1}")
                }
            }
        }
    }
}
