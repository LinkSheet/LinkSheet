package app.linksheet.api.preference

import android.content.Context
import fe.composekit.preference.FlowPreferenceRepository

abstract class RemoteConfigRepository(val context: Context) : FlowPreferenceRepository(context, "remote_config") {

}

