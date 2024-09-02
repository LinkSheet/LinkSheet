package fe.linksheet.ui

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import fe.linksheet.R

//
//val GoogleSansText = FontFamily(
//    Font(R.font.google_sans_text_regular),
//    Font(R.font.google_sans_text_medium, FontWeight.Medium),
//    Font(R.font.google_sans_text_bold, FontWeight.Bold),
//)

val HkGroteskFontFamily = FontFamily(
    Font(R.font.hkgroteskregular),
    Font(R.font.hkgroteskmedium, FontWeight.Medium),
    Font(R.font.hkgrotesksemibold, FontWeight.SemiBold),
    Font(R.font.hkgroteskbold, FontWeight.Bold)
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

val HkGroteskSemiBold = TextStyle(fontFamily = HkGroteskFontFamily, fontWeight = FontWeight.SemiBold)

// ListItem fonts
// TypographyKeyTokens.BodyLarge
// TypographyKeyTokens.BodyMedium
//val test = Typography.fromToken()

// Alertdialog
// headlineSmall
// bodyMedium

// Set of Material typography styles to start with
val NewTypography = Typography(
//    titleLarge = TextStyle(
//        fontFamily = HkGroteskFontFamily,
//        fontWeight = FontWeight.SemiBold,
//        fontSize = 18.sp,
//        lineHeight = 25.sp
//    ),
    titleLarge = NewDefaultTypography.titleLarge.merge(HkGroteskSemiBold).copy(
//        fontFamily = HkGroteskFontFamily,
//        fontWeight = FontWeight.SemiBold,
//        fontSize = 18.sp,
//        lineHeight = 25.sp
    ),
    titleMedium = NewDefaultTypography.titleMedium.merge(HkGroteskSemiBold).copy(
        fontSize = 16.sp,
        lineHeight = 22.sp
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
    )
//    headlineSmall = TextStyle()
//    headlineSmall =
)
//    bodyLarge = TextStyle(
//        fontFamily = FontFamily.Default,
//        fontWeight = FontWeight.Normal,
//        fontSize = 16.sp,
//        lineHeight = 24.sp,
//        letterSpacing = 0.5.sp
//    )
//    /* Other default text styles to override
//    titleLarge = TextStyle(
//        fontFamily = FontFamily.Default,
//        fontWeight = FontWeight.Normal,
//        fontSize = 22.sp,
//        lineHeight = 28.sp,
//        letterSpacing = 0.sp
//    ),
//    labelSmall = TextStyle(
//        fontFamily = FontFamily.Default,
//        fontWeight = FontWeight.Medium,
//        fontSize = 11.sp,
//        lineHeight = 16.sp,
//        letterSpacing = 0.5.sp
//    )
////)
////
//val Typography = Typography(
//    titleLarge = TextStyle(
//        fontFamily = HkGroteskFontFamily,
//        fontWeight = FontWeight.SemiBold,
//        fontSize = 32.sp,
//        letterSpacing = 0.sp
//    ),
//    titleMedium = TextStyle(
////        fontFamily = HkGroteskFontFamily,
////        fontWeight = FontWeight.,
//        fontSize = 20.sp,
//        letterSpacing = 0.sp
//    ),
//    bodyLarge = TextStyle(
//        fontFamily = FontFamily.Default,
//        fontWeight = FontWeight.Normal,
//        fontSize = 16.sp,
//        lineHeight = 24.sp,
//        letterSpacing = 0.5.sp
//    ),
//    labelSmall = TextStyle(
//        fontFamily = FontFamily.Default,
//        fontWeight = FontWeight.Medium,
//        fontSize = 11.sp,
//        lineHeight = 16.sp,
//        letterSpacing = 0.5.sp
//    )
