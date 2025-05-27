package fe.linksheet.activity.bottomsheet

import android.content.Intent
import fe.linksheet.module.app.ActivityAppInfo

sealed interface Interaction {
   val info: ActivityAppInfo
   val modifier: ClickModifier
}
class AppClickInteraction(
    override val info: ActivityAppInfo,
    override val modifier: ClickModifier,
    val index: Int,
    val type: ClickType,
) : Interaction

class ChoiceButtonInteraction(
    override val info: ActivityAppInfo,
    override val modifier: ClickModifier,
) : Interaction

class PreferredAppChoiceButtonInteraction(
    override val info: ActivityAppInfo,
    override val modifier: ClickModifier,
    val intent: Intent,
) : Interaction
