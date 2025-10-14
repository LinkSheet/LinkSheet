package app.linksheet.compose.theme

import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.sp
import app.linksheet.compose.R
import kotlin.reflect.KProperty0

//
//val GoogleSansText = FontFamily(
//    Font(R.font.google_sans_text_regular),
//    Font(R.font.google_sans_text_medium, FontWeight.Medium),
//    Font(R.font.google_sans_text_bold, FontWeight.Bold),
//)

val HkGroteskFontFamily = FontFamily(
//    Font(resId = R.font.hankengrotesk),
//    Font(resId = R.font.hankengrotesk, weight = FontWeight.Medium),
//    Font(resId = R.font.hankengrotesk, weight = FontWeight.SemiBold),
//    Font(resId = R.font.hankengrotesk, weight = FontWeight.Bold)
    Font(resId = R.font.hkgroteskregular, weight = FontWeight.Normal),
    Font(resId = R.font.hkgroteskmedium, weight = FontWeight.Medium),
    Font(resId = R.font.hkgrotesksemibold, weight = FontWeight.SemiBold),
    Font(resId = R.font.hkgroteskbold, weight = FontWeight.Bold)
)

val PoppinsFontFamily = FontFamily(
    Font(R.font.poppinslight, FontWeight.Thin),
    Font(R.font.poppinslight, FontWeight.Light),
    Font(R.font.poppinsregular, FontWeight.Normal),
    Font(R.font.poppinsmedium, FontWeight.Medium),
    Font(R.font.poppinssemibold, FontWeight.SemiBold),
    Font(R.font.poppinsbold, FontWeight.Bold)
)

val NewDefaultTypography = Typography()

val HkGroteskSemiBold = TextStyle(
    fontFamily = HkGroteskFontFamily,
    fontWeight = FontWeight.SemiBold
)

val DialogTitleStyle = TextStyle(
    fontFamily = HkGroteskFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 18.sp,
)

val BodySmallStyle = TextStyle(
    fontSize = 12.sp,
    fontWeight = FontWeight.Normal,
    letterSpacing = 0.sp,
    lineHeight = 16.sp,
)

val BodyExtraSmallStyle = TextStyle(
    fontSize = 10.sp,
    fontWeight = FontWeight.Normal,
    letterSpacing = 0.sp,
    lineHeight = 12.sp,
)

// ListItem fonts
// TypographyKeyTokens.BodyLarge
// TypographyKeyTokens.BodyMedium
//val test = Typography.fromToken()

// Alertdialog
// headlineSmall
// bodyMedium

// Set of Material typography styles to start with
//val TitleMedium = DefaultTextStyle.copy(
//    fontFamily = TypeScaleTokens.TitleMediumFont,
//    fontWeight = TypeScaleTokens.TitleMediumWeight,
//    fontSize = TypeScaleTokens.TitleMediumSize,
//    lineHeight = TypeScaleTokens.TitleMediumLineHeight,
//    letterSpacing = TypeScaleTokens.TitleMediumTracking,
//)

// TypefaceTokens
//val Plain = FontFamily.SansSerif
// TypeTokens
//val WeightMedium = FontWeight.Medium

//val TitleMediumFont = TypefaceTokens.Plain
//val TitleMediumLineHeight = 24.0.sp
//val TitleMediumSize = 16.sp
//val TitleMediumTracking = 0.2.sp
//val TitleMediumWeight = TypefaceTokens.WeightMedium
val NewTypography = Typography(
    titleLarge = NewDefaultTypography.titleLarge.merge(HkGroteskSemiBold).copy(
//        fontFamily = HkGroteskFontFamily,
//        fontWeight = FontWeight.SemiBold,
//        fontSize = 18.sp,
//        lineHeight = 25.sp
    ),
    titleMedium = NewDefaultTypography.titleMedium.merge(HkGroteskSemiBold).copy(
        fontSize = 16.sp,
        lineHeight = 22.sp
//        lineHeight = 24.sp
    ),
    titleSmall = NewDefaultTypography.titleSmall.merge(HkGroteskSemiBold).copy(
//        fontFamily = HkGroteskFontFamily,
//        fontWeight = FontWeight.SemiBold,
    ),
    headlineMedium = NewDefaultTypography.headlineMedium.merge(HkGroteskSemiBold).copy(
//        fontFamily = HkGroteskFontFamily,
//        fontWeight = FontWeight.SemiBold
    ),
    headlineSmall = NewDefaultTypography.headlineSmall.merge(HkGroteskSemiBold).copy(
//        fontFamily = HkGroteskFontFamily,
//        fontWeight = FontWeight.SemiBold
    ),
    bodyLarge = NewDefaultTypography.bodyLarge.copy(
//        fontFamily = HkGroteskFontFamily,
//        fontWeight = FontWeight.SemiBold
    ),
    labelSmall = NewDefaultTypography.labelSmall.copy(
//        lineHeight = 14.sp
    )
)

@Deprecated(message = "Should no longer be used in new composables")
val LegacyTypography = Typography(
    titleLarge = DialogTitleStyle
)

open class TypographyPreviewState(val style: TextStyle, val text: String) {
    class Property(property: KProperty0<TextStyle>) : TypographyPreviewState(property.get(), property.name)
}

private class SmallTypographyPreviewProvider() : PreviewParameterProvider<TypographyPreviewState> {
    override val values = sequenceOf(
        TypographyPreviewState.Property(NewTypography::headlineSmall),
        TypographyPreviewState.Property(NewTypography::titleSmall),
        TypographyPreviewState.Property(NewTypography::bodySmall),
        TypographyPreviewState.Property(NewTypography::labelSmall),
        TypographyPreviewState(BodySmallStyle, "bodySmall Special"),
        TypographyPreviewState(BodyExtraSmallStyle, "extraSmall")
    )
}

private class MediumTypographyPreviewProvider() : PreviewParameterProvider<TypographyPreviewState> {
    override val values = sequenceOf(
        TypographyPreviewState.Property(NewTypography::headlineMedium),
        TypographyPreviewState.Property(NewTypography::titleMedium),
        TypographyPreviewState.Property(NewTypography::bodyMedium),
        TypographyPreviewState.Property(NewTypography::labelMedium),
    )
}

private class LargeTypographyPreviewProvider() : PreviewParameterProvider<TypographyPreviewState> {
    override val values = sequenceOf(
        TypographyPreviewState.Property(NewTypography::headlineLarge),
        TypographyPreviewState.Property(NewTypography::titleLarge),
        TypographyPreviewState.Property(NewTypography::bodyLarge),
        TypographyPreviewState.Property(NewTypography::labelLarge)
    )
}

@Preview(group = "Small", showBackground = true, widthDp = 600)
@Composable
private fun NewTypographyPreview_Small(@PreviewParameter(SmallTypographyPreviewProvider::class) state: TypographyPreviewState) {
    NewTypographyPreviewBase(state = state)
}

@Preview(group = "Medium", showBackground = true, widthDp = 600)
@Composable
private fun NewTypographyPreview_Medium(@PreviewParameter(MediumTypographyPreviewProvider::class) state: TypographyPreviewState) {
    NewTypographyPreviewBase(state = state)
}

@Preview(group = "Large", showBackground = true, widthDp = 600)
@Composable
private fun NewTypographyPreview_Large(@PreviewParameter(LargeTypographyPreviewProvider::class) state: TypographyPreviewState) {
    NewTypographyPreviewBase(state = state)
}

@Composable
private fun NewTypographyPreviewBase(state: TypographyPreviewState) {
    val size = with(state.style) {
        "(w=${fontWeight?.weight},s=$fontSize,lH=$lineHeight,s=$letterSpacing)"
    }

    Text(
        text = "${state.text} $size",
        style = state.style
    )
}
