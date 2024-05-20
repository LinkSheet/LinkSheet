package fe.linksheet.composable.util

import androidx.annotation.StringRes
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import fe.android.span.helper.DefaultHyperLinkStyle
import fe.android.span.helper.LinkAnnotationStyle
import fe.android.span.helper.composable.createAnnotatedString


@Deprecated(
    message = "Use new API",
    replaceWith = ReplaceWith("createAnnotatedString(id, linkStyle)", "fe.android.span.helper.composable")
)
@Composable
fun rememberAnnotatedStringResource(
    @StringRes id: Int,
    linkStyle: LinkAnnotationStyle = DefaultHyperLinkStyle,
): AnnotatedString {
    return createAnnotatedString(id, linkStyle)
}

@Deprecated(message = "Use new API")
@Composable
fun LinkableTextView(
    @StringRes id: Int,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    style: TextStyle = LocalTextStyle.current,
    // TODO: Move to a better place (custom theme?)
    // https://developer.android.com/develop/ui/compose/designsystems/custom#implementing-fully-custom
    hyperlinkStyle: SpanStyle = DefaultHyperLinkStyle.style,
    parentChecked: Boolean? = null,
    parentClickListener: ((Boolean) -> Unit)? = null,
) {
    Text(text = createAnnotatedString(id = id))
}
