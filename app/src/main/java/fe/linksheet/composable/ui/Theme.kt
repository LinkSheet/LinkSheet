package fe.linksheet.composable.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Resources
import android.graphics.Color
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import fe.android.compose.feedback.LocalHapticFeedbackInteraction
import fe.android.compose.feedback.rememberHapticFeedbackInteraction
import fe.android.preference.helper.EnumTypeMapper
import fe.android.span.helper.LinkAnnotationStyle
import fe.android.span.helper.LocalLinkAnnotationStyle
import fe.android.span.helper.LocalLinkTags
import fe.composekit.preference.collectAsStateWithLifecycle
import fe.linksheet.activity.BaseComponentActivity
import app.linksheet.compose.debug.DebugPreferenceProvider
import app.linksheet.compose.debug.LocalUiDebug
import app.linksheet.compose.theme.LightColors
import app.linksheet.compose.theme.NewTypography
import fe.linksheet.module.viewmodel.ThemeSettingsViewModel
import fe.linksheet.util.LinkSheetLinkTags
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
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


/**
 * The default light scrim, as defined by androidx and the platform:
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:activity/activity/src/main/java/androidx/activity/EdgeToEdge.kt;l=35-38;drc=27e7d52e8604a080133e8b842db10c89b4482598
 */
private val lightScrim = Color.argb(0xe6, 0xFF, 0xFF, 0xFF)

/**
 * The default dark scrim, as defined by androidx and the platform:
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:activity/activity/src/main/java/androidx/activity/EdgeToEdge.kt;l=40-44;drc=27e7d52e8604a080133e8b842db10c89b4482598
 */
private val darkScrim = Color.argb(0x80, 0x1b, 0x1b, 0x1b)

@Composable
fun BaseComponentActivity.AppTheme(
    systemDarkTheme: Boolean = isSystemInDarkTheme(),
    themeSettingsViewModel: ThemeSettingsViewModel = koinViewModel(),
    debugPreferenceProvider: DebugPreferenceProvider = koinInject(),
    content: @Composable () -> Unit,
) {
    AppTheme(
        edgeToEdge = edgeToEdge,
        systemDarkTheme = systemDarkTheme,
        themeSettingsViewModel = themeSettingsViewModel,
        debugPreferenceProvider = debugPreferenceProvider,
        updateEdgeToEdge = { status, nav -> enableEdgeToEdge(statusBarStyle = status, navigationBarStyle = nav) },
        content = content
    )
}

@OptIn(KoinExperimentalAPI::class)
@Composable
fun AppTheme(
    edgeToEdge: Boolean = true,
    systemDarkTheme: Boolean = isSystemInDarkTheme(),
    themeSettingsViewModel: ThemeSettingsViewModel = koinViewModel(),
    debugPreferenceProvider: DebugPreferenceProvider = koinInject(),
    updateEdgeToEdge: ((SystemBarStyle, SystemBarStyle) -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current

    val themeV2 by themeSettingsViewModel.themeV2.collectAsStateWithLifecycle()
    val materialYou by themeSettingsViewModel.themeMaterialYou.collectAsStateWithLifecycle()
    val amoled by themeSettingsViewModel.themeAmoled.collectAsStateWithLifecycle()

    val colorScheme = remember(themeV2, systemDarkTheme, materialYou, amoled) {
        themeV2.getColorScheme(context, systemDarkTheme, materialYou, amoled)
    }

    if (edgeToEdge && updateEdgeToEdge != null) {
        LaunchedEffect(key1 = themeV2) {
            val isDarkMode: (Resources) -> Boolean = { _ -> themeV2 == ThemeV2.Dark || systemDarkTheme }

            updateEdgeToEdge(
                SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT, detectDarkMode = isDarkMode),
                SystemBarStyle.auto(lightScrim, darkScrim, detectDarkMode = isDarkMode)
            )
        }
    }

    val linkAnnotationStyle = remember(colorScheme) {
        LinkAnnotationStyle(style = SpanStyle(color = colorScheme.primary))
    }

    val hapticFeedbackInteraction = rememberHapticFeedbackInteraction(context = context)

    val linkAssets by themeSettingsViewModel.linkAssets.collectAsStateWithLifecycle()
    KoinAndroidContext {
        CompositionLocalProvider(
            LocalHapticFeedbackInteraction provides hapticFeedbackInteraction,
            LocalLinkAnnotationStyle provides linkAnnotationStyle,
            LocalLinkTags provides LinkSheetLinkTags(urlIds = linkAssets),
            LocalUiDebug provides debugPreferenceProvider
        ) {
            MaterialTheme(
                colorScheme = colorScheme,
                typography = NewTypography,
                content = content
            )
        }
    }
}

@Composable
fun BaseComponentActivity.BoxAppHost(
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
        typography = NewTypography,
        content = content
    )
}

@Deprecated(message = "Use AppTheme", replaceWith = ReplaceWith("AppTheme(content)"))
@Composable
fun BaseComponentActivity.AppHost(content: @Composable () -> Unit) {
    AppTheme(content = content)
}
