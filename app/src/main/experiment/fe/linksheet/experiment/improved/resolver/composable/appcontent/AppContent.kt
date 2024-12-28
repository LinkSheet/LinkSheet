package fe.linksheet.experiment.improved.resolver.composable.appcontent

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import fe.linksheet.activity.bottomsheet.column.ClickModifier
import fe.linksheet.resolver.DisplayActivityInfo

@Composable
fun AppContent(
    info: DisplayActivityInfo?,
    appListSelectedIdx: Int,
    hasPreferredApp: Boolean,
    hideChoiceButtons: Boolean,
    launch: (DisplayActivityInfo, ClickModifier) -> Unit,
    showToast: (Int, Int, Boolean) -> Unit,
    content: @Composable ((Modifier) -> Unit),
) {
    Column(modifier = Modifier.wrapContentHeight()) {
        content(Modifier
            .fillMaxWidth()
            .weight(0.9f, fill = false))

        if (!hasPreferredApp && !hideChoiceButtons) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.1f, fill = false)
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
