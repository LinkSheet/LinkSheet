package fe.linksheet.module.remoteconfig

import android.content.Context
import fe.composekit.preference.FlowPreferenceRepository

class RemoteConfigRepository(val context: Context) : FlowPreferenceRepository(context, "remote_config")
