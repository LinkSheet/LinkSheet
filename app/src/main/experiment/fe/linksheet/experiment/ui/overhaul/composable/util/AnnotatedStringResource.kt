package fe.linksheet.experiment.ui.overhaul.composable.util

import android.text.Annotation
import android.text.Spanned
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.Density
import androidx.core.text.toSpanned
import fe.linksheet.composable.util.forEachSpan
import fe.linksheet.composable.util.toSpanStyle
import fe.linksheet.extension.android.format

const val URL_ANNOTATION_KEY = "url"

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
    val resources = LocalContext.current.resources
    val spanned = resources.getText(id).toSpanned().format(*formatArgs)

    return createLinkAnnotatedString(spanned = spanned)
}

@Composable
fun annotatedStringResource(@StringRes id: Int, vararg formatArgs: Any): AnnotatedString {
    return buildAnnotatedString { annotatedStringResource(id, *formatArgs) }
}

@Composable
private fun AnnotatedString.Builder.createLinkAnnotatedString(
    spanned: Spanned,
    urlLinkStyle: LinkAnnotationStyle = LinkAnnotationStyleDefaults.primary
): AnnotatedString.Builder {
    val density = LocalDensity.current

    return annotatedStringResource(
        text = spanned,
        density = density,
        urlLinkStyle = urlLinkStyle
    )
}
