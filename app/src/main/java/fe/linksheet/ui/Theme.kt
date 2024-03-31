package fe.linksheet.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import fe.android.preference.helper.EnumTypeMapper
import fe.linksheet.experiment.ui.overhaul.ui.NewTypography
import fe.linksheet.module.viewmodel.ThemeSettingsViewModel
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.androidx.compose.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

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

@OptIn(KoinExperimentalAPI::class)
@Composable
fun AppTheme(
    systemDarkTheme: Boolean = isSystemInDarkTheme(),
    themeSettingsViewModel: ThemeSettingsViewModel = koinViewModel(),
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val themeV2 = themeSettingsViewModel.themeV2()

    val colorScheme = themeV2.getColorScheme(
        context,
        systemDarkTheme,
        themeSettingsViewModel.themeMaterialYou(),
        themeSettingsViewModel.themeAmoled()
    )

    val activity = LocalView.current.context.findActivity()

    KoinAndroidContext {
        CompositionLocalProvider(LocalActivity provides activity!!) {
            MaterialTheme(
                colorScheme = colorScheme,
                typography = if (themeSettingsViewModel.uiOverhaul()) NewTypography else Typography,
                content = content
            )
        }
    }
}

@Composable
fun BoxAppHost(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable BoxScope.() -> Unit,
) {
    AppTheme {
        Box(modifier = modifier, contentAlignment = contentAlignment, content = content)
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

@Deprecated(message = "Use AppTheme", replaceWith = ReplaceWith("AppTheme(content)"))
@Composable
fun AppHost(content: @Composable () -> Unit) {
    AppTheme(content = content)
}
