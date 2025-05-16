package fe.linksheet.activity.bottomsheet

import android.content.Intent
import fe.linksheet.module.app.ActivityAppInfo

sealed interface Interaction
class AppClickInteraction(
    val index: Int,
    val info: ActivityAppInfo,
    val type: ClickType,
    val modifier: ClickModifier,
) : Interaction

class ChoiceButtonInteraction(
    val info: ActivityAppInfo,
    val modifier: ClickModifier,
) : Interaction

class PreferredAppChoiceButtonInteraction(
    val info: ActivityAppInfo,
    val intent: Intent,
    val modifier: ClickModifier,
) : Interaction
