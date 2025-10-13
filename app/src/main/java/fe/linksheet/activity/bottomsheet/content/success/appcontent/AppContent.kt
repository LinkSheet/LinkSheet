package fe.linksheet.activity.bottomsheet.content.success.appcontent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.linksheet.compose.preview.PreviewDebugProvider
import fe.linksheet.activity.bottomsheet.Interaction
import app.linksheet.compose.debugBorder
import fe.linksheet.feature.app.ActivityAppInfo
import app.linksheet.compose.debug.LocalUiDebug

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AppContent(
    info: ActivityAppInfo?,
    appListSelectedIdx: Int,
    hasPreferredApp: Boolean,
    hideChoiceButtons: Boolean,
    dispatch: (Interaction) -> Unit,
    showToast: (Int, Int, Boolean) -> Unit,
    content: @Composable ColumnScope.(Modifier) -> Unit,
) {
    val debug by LocalUiDebug.current.drawBorders.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier.debugBorder(debug, 1.dp, Color.Magenta),
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        content(Modifier.weight(0.9f, fill = false))
        if (!hasPreferredApp && !hideChoiceButtons) {
            NoPreferredAppChoiceButtons(
                info = info,
                selected = appListSelectedIdx,
                dispatch = dispatch,
                showToast = showToast
            )
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
            dispatch = { },
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
