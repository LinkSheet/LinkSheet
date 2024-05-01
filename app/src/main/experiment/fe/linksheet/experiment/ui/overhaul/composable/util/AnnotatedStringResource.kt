package fe.linksheet.experiment.ui.overhaul.composable.util

import android.content.res.Resources
import android.text.Annotation
import android.text.Spanned
import android.text.SpannedString
import androidx.annotation.StringRes
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.*
import androidx.compose.ui.unit.Density
import androidx.core.text.HtmlCompat
import fe.linksheet.composable.util.forEachSpan
import fe.linksheet.composable.util.toSpanStyle

fun Spanned.toHtmlWithoutParagraphs(): String {
    return HtmlCompat.toHtml(this, HtmlCompat.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE).substringAfter("<p dir=\"ltr\">")
        .substringBeforeLast("</p>")
}

fun Resources.getSpanned(@StringRes id: Int, formatArgs: Array<out Any>): Spanned {
    val escapedArgs = formatArgs.map { if (it is Spanned) it.toHtmlWithoutParagraphs() else it }.toTypedArray()
    val spanned = SpannedString(getText(id))

    val htmlResource = spanned.toHtmlWithoutParagraphs()
    val formattedHtml = String.format(htmlResource, *escapedArgs)

    return HtmlCompat.fromHtml(formattedHtml, HtmlCompat.FROM_HTML_MODE_LEGACY)
}

const val URL_ANNOTATION_KEY = "url"

@Stable
data class LinkAnnotationStyle(
    val style: SpanStyle,
    val focusedStyle: SpanStyle? = null,
    val hoveredStyle: SpanStyle? = null,
    val pressedStyle: SpanStyle? = null,
    val interactionListener: LinkInteractionListener? = null
) {
    fun toUrlAnnotation(value: String): LinkAnnotation.Url {
        return LinkAnnotation.Url(value, style, focusedStyle, hoveredStyle, pressedStyle, interactionListener)
    }
}

fun AnnotatedString.Builder.addAnnotation(
    annotation: Annotation,
    urlLinkStyle: LinkAnnotationStyle,
    start: Int,
    end: Int
) {
    if (annotation.key == URL_ANNOTATION_KEY) {
        addLink(urlLinkStyle.toUrlAnnotation(annotation.value), start, end)
    }
}

fun AnnotatedString.Builder.annotatedStringResource(
    text: Spanned,
    density: Density,
    urlLinkStyle: LinkAnnotationStyle
): AnnotatedString.Builder {
    append(text)

    text.forEachSpan { span, start, end ->
        if (span is Annotation) {
            addAnnotation(span, urlLinkStyle, start, end)
        } else {
            addStyle(style = span.toSpanStyle(density), start = start, end = end)
        }
    }
    return this
}

@Composable
fun AnnotatedString.Builder.annotatedStringResource(
    @StringRes id: Int,
    vararg formatArgs: Any,
): AnnotatedString.Builder {
    val density = LocalDensity.current
    val resources = LocalContext.current.resources
    val text = resources.getSpanned(id, formatArgs)

    return annotatedStringResource(
        text = text,
        density = density,
        urlLinkStyle = LinkAnnotationStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary))
    )
}

@Composable
fun annotatedStringResource(
    @StringRes id: Int,
    vararg formatArgs: Any,
): AnnotatedString {
    val density = LocalDensity.current
    val resources = LocalContext.current.resources
    val text = resources.getSpanned(id, formatArgs)

    return buildAnnotatedString {
        annotatedStringResource(
            text = text,
            density = density,
            urlLinkStyle = LinkAnnotationStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary))
        )
    }
}



