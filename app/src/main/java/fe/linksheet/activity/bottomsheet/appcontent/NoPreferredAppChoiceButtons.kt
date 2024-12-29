package fe.linksheet.activity.bottomsheet.appcontent

import android.widget.Toast
import androidx.compose.runtime.Composable
import fe.linksheet.R
import fe.linksheet.activity.bottomsheet.button.ChoiceButtons
import fe.linksheet.activity.bottomsheet.column.ClickModifier
import fe.linksheet.resolver.DisplayActivityInfo

@Composable
fun NoPreferredAppChoiceButtons(
    info: DisplayActivityInfo?,
    selected: Int,
    launch: (info: DisplayActivityInfo, modifier: ClickModifier) -> Unit,
    showToast: (textId: Int, duration: Int, uiThread: Boolean) -> Unit
) {
    ChoiceButtons(
        enabled = selected != -1,
        choiceClick = { _, modifier ->
            if (info == null) {
                showToast(R.string.something_went_wrong, Toast.LENGTH_SHORT, true)
                return@ChoiceButtons
            }

            launch(info, modifier)
        }
    )
}
