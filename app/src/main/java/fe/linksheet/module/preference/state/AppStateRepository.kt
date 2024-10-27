package fe.linksheet.module.preference.state

import android.content.Context
import fe.android.preference.helper.compose.StatePreferenceRepository

class AppStateRepository(val context: Context) : StatePreferenceRepository(context, "app_state") {
    init {
        AppStatePreferences.runMigrations(this)
    }
}
