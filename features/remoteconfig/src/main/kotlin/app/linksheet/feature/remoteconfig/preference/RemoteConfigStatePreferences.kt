package app.linksheet.feature.remoteconfig.preference

import app.linksheet.api.PreferenceRegistry
import fe.android.preference.helper.Preference

interface RemoteConfigStatePreferences {
    val dialogDismissed: Preference.Boolean
}

fun remoteConfigStatePreferences(registry: PreferenceRegistry): RemoteConfigStatePreferences {
    return object : RemoteConfigStatePreferences {
        override val dialogDismissed = registry.boolean("remote_config_dialog_dismissed", false)
    }
}
