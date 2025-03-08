package fe.linksheet.module.preference.state

import android.content.Context
import fe.composekit.preference.FlowPreferenceRepository

class AppStateRepository(val context: Context) : FlowPreferenceRepository(context, "app_state") {
}
