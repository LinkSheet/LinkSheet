package fe.linksheet.module.preference.app

import app.linksheet.api.PreferenceRegistry
import app.linksheet.api.mapped
import fe.linksheet.activity.bottomsheet.TapConfig

class TapConfig(registry: PreferenceRegistry) {
    val single = registry.mapped("tap_config_single", TapConfig.SelectItem, TapConfig)
    val double = registry.mapped("tap_config_double", TapConfig.OpenApp, TapConfig)
    val long = registry.mapped("tap_config_long", TapConfig.OpenSettings, TapConfig)
}
