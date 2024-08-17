package fe.linksheet.ui

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.ui.graphics.Color
import fe.android.preference.helper.OptionTypeMapper
import fe.composekit.layout.column.GroupValueProvider
import fe.linksheet.R

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
    ): ColorScheme

    data object System : ThemeV2("system", R.string.system) {
        override fun getColorScheme(
            context: Context,
            systemDarkTheme: Boolean,
            materialYou: Boolean,
            amoled: Boolean,
        ): ColorScheme {
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
        ): ColorScheme {
            return if (AndroidVersion.AT_LEAST_API_31_S && materialYou) dynamicLightColorScheme(context) else LightColors
        }
    }

    data object Dark : ThemeV2("dark", R.string.dark) {
        override fun getColorScheme(
            context: Context,
            systemDarkTheme: Boolean,
            materialYou: Boolean,
            amoled: Boolean,
        ): ColorScheme {
            val scheme =
                if (AndroidVersion.AT_LEAST_API_31_S && materialYou) dynamicDarkColorScheme(context) else DarkColors

            val colorScheme = if (amoled) scheme.copy(surface = Color.Black, background = Color.Black)
            else scheme

            return colorScheme
        }
    }

    companion object : OptionTypeMapper<ThemeV2, String>({ it.name }, {
        arrayOf(System, Light, Dark)
    })
}
