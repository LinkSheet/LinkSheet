package fe.linksheet.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
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
import fe.android.preference.helper.OptionTypeMapper
import fe.linksheet.activity.bottomsheet.TapConfig
import fe.linksheet.experiment.ui.overhaul.ui.NewTypography
import fe.linksheet.module.viewmodel.ThemeSettingsViewModel
import fe.linksheet.util.AndroidVersion
import org.koin.androidx.compose.koinViewModel



//private val AmoledBlackColors = DarkColors.copy(surface = Color.Black, background = Color.Black)

tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

enum class Theme {
    System,
    Light,
    Dark,

    @Deprecated(message = "Use the new property")
    AmoledBlack;

    companion object Companion : EnumTypeMapper<Theme>(entries.toTypedArray())

    fun toV2(): ThemeV2 {
        return when (this) {
            System -> ThemeV2.System
            Light -> ThemeV2.Light
            Dark, AmoledBlack -> ThemeV2.Dark
        }
    }

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
    val themeV2 = themeSettingsViewModel.themeV2()

    // Do not destructure, Compose can't observe destructured vals
    val pair = themeV2.getColorScheme(
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
