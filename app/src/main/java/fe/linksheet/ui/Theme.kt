package fe.linksheet.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.Window
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.*
import androidx.core.view.WindowCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import fe.android.preference.helper.EnumTypeMapper
import fe.linksheet.experiment.ui.overhaul.ui.NewTypography
import fe.linksheet.module.viewmodel.ThemeSettingsViewModel
import fe.linksheet.util.AndroidVersion
import org.koin.androidx.compose.koinViewModel


private val LightColors = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
    onError = md_theme_light_onError,
    errorContainer = md_theme_light_errorContainer,
    onErrorContainer = md_theme_light_onErrorContainer,
    outline = md_theme_light_outline,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    inverseSurface = md_theme_light_inverseSurface,
    inverseOnSurface = md_theme_light_inverseOnSurface,
    inversePrimary = md_theme_light_inversePrimary,
    surfaceTint = md_theme_light_surfaceTint,
    outlineVariant = md_theme_light_outlineVariant,
    scrim = md_theme_light_scrim,
)


private val DarkColors = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    error = md_theme_dark_error,
    onError = md_theme_dark_onError,
    errorContainer = md_theme_dark_errorContainer,
    onErrorContainer = md_theme_dark_onErrorContainer,
    outline = md_theme_dark_outline,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    inverseSurface = md_theme_dark_inverseSurface,
    inverseOnSurface = md_theme_dark_inverseOnSurface,
    inversePrimary = md_theme_dark_inversePrimary,
    surfaceTint = md_theme_dark_surfaceTint,
    outlineVariant = md_theme_dark_outlineVariant,
    scrim = md_theme_dark_scrim,
)

//private val AmoledBlackColors = DarkColors.copy(surface = Color.Black, background = Color.Black)

tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

sealed interface ThemeNew {
    fun getColorScheme(
        context: Context,
        systemDarkTheme: Boolean,
        materialYou: Boolean,
        amoled: Boolean,
    ): Pair<ColorScheme, Boolean>

    data object System : ThemeNew {
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

    data object Light : ThemeNew {
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

    data object Dark : ThemeNew {
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
}

enum class Theme {
    System,
    Light,
    Dark,

    @Deprecated(message = "Use the new property")
    AmoledBlack;

    fun resolve(): ThemeNew {
        return when (this) {
            System -> ThemeNew.System
            Light -> ThemeNew.Light
            Dark, AmoledBlack -> ThemeNew.Dark
        }
    }

    companion object Companion : EnumTypeMapper<Theme>(entries.toTypedArray())

    override fun toString(): String {
        return ordinal.toString()
    }
}

val LocalActivity = staticCompositionLocalOf<Activity> { error("CompositionLocal LocalActivity not present") }

@Composable
fun AppTheme(
    systemDarkTheme: Boolean = isSystemInDarkTheme(),
    themeSettingsViewModel: ThemeSettingsViewModel = koinViewModel(),
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current

    val themeNew = themeSettingsViewModel.theme().resolve()

    // Do not destructure, Compose can't observe destructured vals
    val pair = themeNew.getColorScheme(
        context,
        systemDarkTheme,
        themeSettingsViewModel.themeMaterialYou(),
        themeSettingsViewModel.themeAmoled()
    )

    val colorScheme = pair.first
    val isDark = pair.second

    val view = LocalView.current
    val activity = view.context.findActivity()
    val window = activity?.window

    window?.let {
        WindowCompat.getInsetsController(it, view).isAppearanceLightStatusBars = isDark
    }

    rememberSystemUiController(window).setSystemBarsColor(colorScheme.background, !isDark)

    CompositionLocalProvider(LocalActivity provides activity!!) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = if (themeSettingsViewModel.uiOverhaul()) NewTypography else Typography,
            content = content
        )
    }
}

@Composable
fun PreviewTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = Typography,
        content = content
    )
}

@Composable
fun AppHost(content: @Composable () -> Unit) {
    AppTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            Surface(color = MaterialTheme.colorScheme.surface) {
                Column(modifier = Modifier.fillMaxSize()) {
                    content()
                }
            }
        }
    }
}
