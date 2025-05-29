package fe.androidx.compose.material3

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.text.TextStyle
import fe.androidx.compose.material3.tokens.TypographyKeyTokens

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
private fun Typography.fromToken(value: TypographyKeyTokens): TextStyle {
    return when (value) {
        TypographyKeyTokens.DisplayLarge -> displayLarge
        TypographyKeyTokens.DisplayMedium -> displayMedium
        TypographyKeyTokens.DisplaySmall -> displaySmall
        TypographyKeyTokens.HeadlineLarge -> headlineLarge
        TypographyKeyTokens.HeadlineMedium -> headlineMedium
        TypographyKeyTokens.HeadlineSmall -> headlineSmall
        TypographyKeyTokens.TitleLarge -> titleLarge
        TypographyKeyTokens.TitleMedium -> titleMedium
        TypographyKeyTokens.TitleSmall -> titleSmall
        TypographyKeyTokens.BodyLarge -> bodyLarge
        TypographyKeyTokens.BodyMedium -> bodyMedium
        TypographyKeyTokens.BodySmall -> bodySmall
        TypographyKeyTokens.LabelLarge -> labelLarge
        TypographyKeyTokens.LabelMedium -> labelMedium
        TypographyKeyTokens.LabelSmall -> labelSmall
        TypographyKeyTokens.DisplayLargeEmphasized -> displayLargeEmphasized
        TypographyKeyTokens.DisplayMediumEmphasized -> displayMediumEmphasized
        TypographyKeyTokens.DisplaySmallEmphasized -> displaySmallEmphasized
        TypographyKeyTokens.HeadlineLargeEmphasized -> headlineLargeEmphasized
        TypographyKeyTokens.HeadlineMediumEmphasized -> headlineMediumEmphasized
        TypographyKeyTokens.HeadlineSmallEmphasized -> headlineSmallEmphasized
        TypographyKeyTokens.TitleLargeEmphasized -> titleLargeEmphasized
        TypographyKeyTokens.TitleMediumEmphasized -> titleMediumEmphasized
        TypographyKeyTokens.TitleSmallEmphasized -> titleSmallEmphasized
        TypographyKeyTokens.BodyLargeEmphasized -> bodyLargeEmphasized
        TypographyKeyTokens.BodyMediumEmphasized -> bodyMediumEmphasized
        TypographyKeyTokens.BodySmallEmphasized -> bodySmallEmphasized
        TypographyKeyTokens.LabelLargeEmphasized -> labelLargeEmphasized
        TypographyKeyTokens.LabelMediumEmphasized -> labelMediumEmphasized
        TypographyKeyTokens.LabelSmallEmphasized -> labelSmallEmphasized
    }
}

internal val TypographyKeyTokens.value: TextStyle
    @Composable @ReadOnlyComposable get() = MaterialTheme.typography.fromToken(this)
