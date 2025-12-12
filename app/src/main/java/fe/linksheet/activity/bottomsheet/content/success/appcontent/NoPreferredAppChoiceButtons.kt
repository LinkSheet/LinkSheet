package fe.linksheet.activity.bottomsheet.content.success.appcontent

import android.widget.Toast
import androidx.compose.runtime.Composable
import fe.linksheet.R
import fe.linksheet.activity.bottomsheet.ChoiceButtonInteraction
import fe.linksheet.activity.bottomsheet.content.success.ChoiceButtons
import fe.linksheet.activity.bottomsheet.Interaction
import app.linksheet.feature.app.core.ActivityAppInfo

@Composable
fun NoPreferredAppChoiceButtons(
    info: ActivityAppInfo?,
    selected: Int,
    dispatch: (Interaction) -> Unit,
    showToast: (textId: Int, duration: Int, uiThread: Boolean) -> Unit
) {
    ChoiceButtons(
        enabled = selected != -1,
        choiceClick = { _, modifier ->
            if (info == null) {
                showToast(R.string.something_went_wrong, Toast.LENGTH_SHORT, true)
                return@ChoiceButtons
            }

            dispatch(ChoiceButtonInteraction(info, modifier))
        }
    )
}
