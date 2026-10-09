package app.linksheet.feature.remoteconfig.preference

import app.linksheet.api.PreferenceRegistry
import fe.android.preference.helper.Preference

interface RemoteConfigPreferences {
    val enable: Preference.Boolean
}

fun remoteConfigPreferences(registry: PreferenceRegistry): RemoteConfigPreferences {
    return object : RemoteConfigPreferences {
        override val enable = registry.boolean("remote_config", false)
    }
}
