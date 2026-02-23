package app.linksheet.compose.preview

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import app.linksheet.compose.theme.DarkColors
import app.linksheet.compose.theme.LightColors
import app.linksheet.compose.theme.NewTypography
import fe.android.compose.feedback.LocalHapticFeedbackInteraction
import fe.android.compose.feedback.PreviewHapticFeedbackInteraction

@Composable
fun PreviewTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalHapticFeedbackInteraction provides PreviewHapticFeedbackInteraction) {
        MaterialTheme(
            colorScheme = if(isSystemInDarkTheme()) DarkColors else LightColors,
            typography = NewTypography,
            content = content
        )
    }
}
