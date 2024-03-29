package fe.linksheet.experiment.ui.overhaul.ui

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import fe.linksheet.R
import fe.linksheet.ui.HkGroteskFontFamily

//
//val GoogleSansText = FontFamily(
//    Font(R.font.google_sans_text_regular),
//    Font(R.font.google_sans_text_medium, FontWeight.Medium),
//    Font(R.font.google_sans_text_bold, FontWeight.Bold),
//)

val NewDefaultTypography = Typography()

// Set of Material typography styles to start with
val NewTypography = Typography(
//    titleLarge = TextStyle(
//        fontFamily = HkGroteskFontFamily,
//        fontWeight = FontWeight.SemiBold,
//        fontSize = 18.sp,
//        lineHeight = 25.sp
//    ),
    titleLarge = NewDefaultTypography.titleLarge.copy(
        fontFamily = HkGroteskFontFamily,
        fontWeight = FontWeight.SemiBold,
//        fontSize = 18.sp,
//        lineHeight = 25.sp
    ),
    titleMedium = NewDefaultTypography.titleMedium.copy(
        fontFamily = HkGroteskFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp
    ),
    titleSmall = NewDefaultTypography.titleSmall.copy(
        fontFamily = HkGroteskFontFamily,
        fontWeight = FontWeight.SemiBold
    ),
    headlineMedium = NewDefaultTypography.headlineMedium.copy(
        fontFamily = HkGroteskFontFamily,
        fontWeight = FontWeight.SemiBold
    ),
    headlineSmall = NewDefaultTypography.headlineSmall.copy(
        fontFamily = HkGroteskFontFamily,
        fontWeight = FontWeight.SemiBold
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
