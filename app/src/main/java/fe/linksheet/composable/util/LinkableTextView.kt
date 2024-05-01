package fe.linksheet.composable.util

import android.content.res.Resources
import android.graphics.Typeface
import android.text.ParcelableSpan
import android.text.Spanned
import android.text.SpannedString
import android.text.style.*
import androidx.annotation.StringRes
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.core.text.HtmlCompat

const val URL_ANNOTATION_KEY = "url"

/**
 * From https://stackoverflow.com/a/72369830
 *
 * Much of this class comes from
 * https://issuetracker.google.com/issues/139320238#comment11
 * which seeks to correct the gap in Jetpack Compose wherein HTML style tags in string resources
 * are not respected.
 */
@Composable
@ReadOnlyComposable
private fun resources(): Resources {
    return LocalContext.current.resources
}

fun Spanned.toHtmlWithoutParagraphs(): String {
    return HtmlCompat.toHtml(this, HtmlCompat.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE)
        .substringAfter("<p dir=\"ltr\">").substringBeforeLast("</p>")
}

fun Resources.getSpanned(@StringRes id: Int, formatArgs: Array<out Any>): Spanned {
    val escapedArgs = formatArgs.map {
        if (it is Spanned) it.toHtmlWithoutParagraphs() else it
    }.toTypedArray()
    val resource = SpannedString(getText(id))
    val htmlResource = resource.toHtmlWithoutParagraphs()
    val formattedHtml = String.format(htmlResource, *escapedArgs)
    return HtmlCompat.fromHtml(formattedHtml, HtmlCompat.FROM_HTML_MODE_LEGACY)
}

fun Resources.getSpanned(@StringRes id: Int): Spanned {
    val resource = SpannedString(getText(id))
    val htmlResource = resource.toHtmlWithoutParagraphs()
    return HtmlCompat.fromHtml(htmlResource, HtmlCompat.FROM_HTML_MODE_LEGACY)
}

@Composable
fun rememberAnnotatedStringResource(
    @StringRes id: Int,
    hyperlinkStyle: SpanStyle = DefaultHyperLinkStyle,
    vararg formatArgs: Any,
): AnnotatedString {
    val resources = resources()
    val density = LocalDensity.current

    return remember(id, formatArgs, hyperlinkStyle) {
        spannableStringToAnnotatedString(resources.getSpanned(id, formatArgs), density, hyperlinkStyle)
    }
}

@Composable
fun rememberAnnotatedStringResource(
    @StringRes id: Int,
    hyperlinkStyle: SpanStyle = DefaultHyperLinkStyle,
): AnnotatedString {
    val resources = resources()
    val density = LocalDensity.current

    return remember(id, hyperlinkStyle) {
        spannableStringToAnnotatedString(resources.getSpanned(id), density, hyperlinkStyle)
    }
}

private val EmptySpanStyle = SpanStyle()

//private val FontFamilySansSerif = SpanStyle(fontFamily = FontFamily.SansSerif)
//private val FontFamilySerif = SpanStyle(fontFamily = FontFamily.Serif)
//private val FontFamilyMonospace = SpanStyle(fontFamily = FontFamily.Monospace)
//private val FontFamilyCursive = SpanStyle(fontFamily = FontFamily.Cursive)
private val FontFamilyDefault = SpanStyle(fontFamily = FontFamily.Default)

private val FontFamilies = arrayOf(
    FontFamily.SansSerif,
    FontFamily.Serif,
    FontFamily.Monospace,
    FontFamily.Cursive
).associate { it.name to SpanStyle(fontFamily = it) }

private val TypefaceNormal = SpanStyle(fontWeight = FontWeight.Normal, fontStyle = FontStyle.Normal)
private val TypefaceBold = SpanStyle(fontWeight = FontWeight.Bold, fontStyle = FontStyle.Normal)
private val TypefaceItalic = SpanStyle(fontWeight = FontWeight.Normal, fontStyle = FontStyle.Italic)
private val TypefaceBoldItalic = SpanStyle(fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic)

private val Typefaces = mapOf(
    Typeface.NORMAL to TypefaceNormal,
    Typeface.BOLD to TypefaceBold,
    Typeface.ITALIC to TypefaceItalic,
    Typeface.BOLD_ITALIC to TypefaceBoldItalic
)

private val TextDecorationLineThrough = SpanStyle(textDecoration = TextDecoration.LineThrough)
private val TextDecorationUnderline = SpanStyle(textDecoration = TextDecoration.Underline)
private val BaselineShiftSuperscript = SpanStyle(baselineShift = BaselineShift.Superscript)
private val BaselineShiftSubscript = SpanStyle(baselineShift = BaselineShift.Subscript)


//is StrikethroughSpan -> SpanStyle(textDecoration = TextDecoration.LineThrough)
//is UnderlineSpan -> SpanStyle(textDecoration = TextDecoration.Underline)
//is SuperscriptSpan -> SpanStyle(baselineShift = BaselineShift.Superscript)
//is SubscriptSpan -> SpanStyle(baselineShift = BaselineShift.Subscript)

fun ParcelableSpan.toSpanStyle(density: Density): SpanStyle {
    return when (this) {
        is StyleSpan -> Typefaces[style] ?: EmptySpanStyle
        is TypefaceSpan -> FontFamilies[family] ?: FontFamilyDefault
        is BulletSpan -> EmptySpanStyle
        is StrikethroughSpan -> TextDecorationLineThrough
        is UnderlineSpan -> TextDecorationUnderline
        is SuperscriptSpan -> BaselineShiftSuperscript
        is SubscriptSpan -> BaselineShiftSubscript
        is ForegroundColorSpan -> SpanStyle(color = Color(foregroundColor))
        is RelativeSizeSpan -> SpanStyle(fontSize = sizeChange.em)
        is AbsoluteSizeSpan -> with(density) {
            SpanStyle(fontSize = if (dip) size.dp.toSp() else size.toSp())
        }

        else -> EmptySpanStyle
    }
}

fun Spanned.getSpan(tag: Any): Pair<Int, Int> {
    return getSpanStart(tag) to getSpanEnd(tag)
}

inline fun Spanned.forEachSpan(fn: (ParcelableSpan, Int, Int) -> Unit) {
    getSpans(0, length, ParcelableSpan::class.java).forEach {
        val (start, end) = getSpan(it)
        fn(it, start, end)
    }

//    for (span in getSpans(0, length, ParcelableSpan::class.java)) {
//        val (start, end) = getSpan(span)
//        fn(span, start, end)
//    }
}



@OptIn(ExperimentalTextApi::class)
private fun spannableStringToAnnotatedString(
    text: CharSequence,
    density: Density,
    hyperlinkStyle: SpanStyle,
): AnnotatedString {
    return buildAnnotatedString {
        append(text)

        if (text is Spanned) {
            text.forEachSpan { span, start, end ->
                addStyle(style = span.toSpanStyle(density), start = start, end = end)
            }
        }
    }
}


val DefaultHyperLinkStyle = SpanStyle(color = Color(0xff3b82f6), textDecoration = TextDecoration.Underline)

@Composable
fun LinkableTextView(
    @StringRes id: Int,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    style: TextStyle = LocalTextStyle.current,
    // TODO: Move to a better place (custom theme?)
    // https://developer.android.com/develop/ui/compose/designsystems/custom#implementing-fully-custom
    hyperlinkStyle: SpanStyle = DefaultHyperLinkStyle,
    parentChecked: Boolean? = null,
    parentClickListener: ((Boolean) -> Unit)? = null,
) {
//    stringResource(id = R)

    val annotatedString = rememberAnnotatedStringResource(id, hyperlinkStyle)
    LinkableTextView(
        modifier = modifier,
        annotatedString = annotatedString,
        enabled = enabled,
        style = style,
        parentChecked = parentChecked,
        parentClickListener = parentClickListener
    )
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun LinkableTextView(
    modifier: Modifier = Modifier,
    annotatedString: AnnotatedString,
    enabled: Boolean = true,
    style: TextStyle = LocalTextStyle.current,
    parentChecked: Boolean? = null,
    parentClickListener: ((Boolean) -> Unit)? = null,
) {
    val uriHandler = LocalUriHandler.current
    val hapticFeedback = LocalHapticFeedback.current

    val textColor = style.color.takeOrElse { LocalContentColor.current }

    ClickableText(
        text = annotatedString,
        style = style.merge(color = textColor),
        onClick = { offset ->
            if (enabled) {
                annotatedString.getUrlAnnotations(start = offset, end = offset).firstOrNull()?.let {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    uriHandler.openUri(it.item.url)
                } ?: run {
                    if (parentChecked != null && parentClickListener != null) {
                        parentClickListener(!parentChecked)
                    }
                }
            }
        },
        modifier = modifier,
    )
}
