package fe.linksheet.module.preference.app

import app.linksheet.api.PreferenceRegistry
import app.linksheet.api.mapped
import fe.linksheet.composable.ui.ThemeV2

class ThemeV2(registry: PreferenceRegistry) {
    val themeV2 = registry.mapped("theme_v2", ThemeV2.System, ThemeV2)
    val materialYou = registry.boolean("theme_material_you", true)
    val amoled = registry.boolean("theme_amoled_enabled")
}
