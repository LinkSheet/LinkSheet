package fe.linksheet.debug.module.preference

import android.content.Context
import fe.android.preference.helper.PreferenceDefinition
import fe.composekit.preference.FlowPreferenceRepository

class DebugPreferenceRepository(val context: Context) : FlowPreferenceRepository(context, "debug") {
}

object DebugPreferences : PreferenceDefinition() {
    val drawBorders = boolean("draw_borders", default = true)

    init {
        finalize()
    }
}
