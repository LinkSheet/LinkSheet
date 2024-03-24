package fe.linksheet.ui

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import fe.android.preference.helper.OptionTypeMapper
import fe.linksheet.R
import fe.linksheet.experiment.ui.overhaul.composable.component.page.GroupValueProvider
import fe.linksheet.util.AndroidVersion
import fe.linksheet.util.StringResHolder

sealed class ThemeV2(val name: String, @StringRes stringRes: Int) : StringResHolder, GroupValueProvider<Int> {
    override val id: Int = stringRes
    override val key: Int = stringRes

    abstract fun getColorScheme(
        context: Context,
        systemDarkTheme: Boolean,
        materialYou: Boolean,
        amoled: Boolean,
    ): Pair<ColorScheme, Boolean>

    data object System : ThemeV2("system", R.string.system) {
        override fun getColorScheme(
            context: Context,
            systemDarkTheme: Boolean,
            materialYou: Boolean,
            amoled: Boolean,
        ): Pair<ColorScheme, Boolean> {
            val theme = if (systemDarkTheme) Dark else Light
            return theme.getColorScheme(context, systemDarkTheme, materialYou, amoled)
        }
    }

    data object Light : ThemeV2("light", R.string.light) {
        override fun getColorScheme(
            context: Context,
            systemDarkTheme: Boolean,
            materialYou: Boolean,
            amoled: Boolean,
        ): Pair<ColorScheme, Boolean> {
            val colorScheme =
                if (AndroidVersion.AT_LEAST_API_31_S && materialYou) dynamicLightColorScheme(context) else LightColors
            return colorScheme to false
        }
    }

    data object Dark : ThemeV2("dark", R.string.dark) {
        override fun getColorScheme(
            context: Context,
            systemDarkTheme: Boolean,
            materialYou: Boolean,
            amoled: Boolean,
        ): Pair<ColorScheme, Boolean> {
            val scheme =
                if (AndroidVersion.AT_LEAST_API_31_S && materialYou) dynamicDarkColorScheme(context) else DarkColors

            val colorScheme = if (amoled) scheme.copy(surface = Color.Black, background = Color.Black)
            else scheme

            return colorScheme to true
        }
    }

    companion object : OptionTypeMapper<ThemeV2, String>({ it.name }, {
        arrayOf(System, Light, Dark)
    })
}
