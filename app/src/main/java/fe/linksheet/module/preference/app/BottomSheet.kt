package fe.linksheet.module.preference.app

import app.linksheet.api.PreferenceRegistry
import app.linksheet.api.mapped
import fe.linksheet.activity.bottomsheet.TapConfig

class BottomSheet(registry: PreferenceRegistry) {
    val hideAfterCopying = registry.boolean("hide_after_copying", false)
    val usageStatsSorting = registry.boolean("usage_stats_sorting")
    val gridLayout = registry.boolean("grid_layout")
    val dontShowFilteredItem = registry.boolean("dont_show_filtered_item")
    val hideBottomSheetChoiceButtons = registry.boolean("hide_bottom_sheet_choice_buttons")
    val expandOnAppSelect = registry.boolean("expand_on_app_select", true)
    val bottomSheetNativeLabel = registry.boolean("bottom_sheet_native_label", true)
    val hideReferringApp = registry.boolean("hide_referrer_from_sheet")

    val openGraphPreview = OpenGraphPreview(registry)
    val tapConfig = TapConfig(registry)
}

class OpenGraphPreview(registry: PreferenceRegistry) {
    val enable = registry.boolean("url_bar_preview")
    val skipBrowser = registry.boolean("url_bar_preview_skip_browser")
}

class TapConfig(registry: PreferenceRegistry) {
    val single = registry.mapped("tap_config_single", TapConfig.SelectItem, TapConfig)
    val double = registry.mapped("tap_config_double", TapConfig.OpenApp, TapConfig)
    val long = registry.mapped("tap_config_long", TapConfig.OpenSettings, TapConfig)
}
