package fe.linksheet.activity.bottomsheet.content.success.appcontent

import androidx.compose.runtime.Composable
import app.linksheet.feature.app.core.ActivityAppInfo
import fe.linksheet.activity.bottomsheet.AppInteraction
import fe.linksheet.activity.bottomsheet.ChoiceButtonInteraction
import fe.linksheet.activity.bottomsheet.content.success.ChoiceButtons

@Composable
fun NoPreferredAppChoiceButtons(
    info: ActivityAppInfo?,
    selected: Int,
    dispatch: (AppInteraction) -> Unit,
) {
    ChoiceButtons(
        enabled = selected != -1,
        choiceClick = { _, modifier ->
            dispatch(ChoiceButtonInteraction(info, modifier))
        }
    )
}
