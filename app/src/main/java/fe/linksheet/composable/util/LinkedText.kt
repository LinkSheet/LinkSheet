package fe.linksheet.composable.util

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.AccessibilityAction
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withAnnotation
import androidx.compose.ui.unit.TextUnit

private const val LinkTag = "Link"


// https://gist.github.com/tadfisher/1745f47d3e4aec471fd58b8b76b44060
val ClickLink = SemanticsPropertyKey<AccessibilityAction<(index: Int) -> Boolean>>(
    name = "ClickLink",
    mergePolicy = { parentValue, childValue ->
        AccessibilityAction(
            parentValue?.label ?: childValue.label,
            parentValue?.action ?: childValue.action
        )
    }
)

fun SemanticsPropertyReceiver.onClickLink(
    label: String? = null,
    action: ((Int) -> Boolean)?
) {
    this[ClickLink] = AccessibilityAction(label, action)
}

@Composable
fun LinkedText(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    color: Color = LocalContentColor.current.copy(LocalContentAlpha.current),
    style: TextStyle = LocalTextStyle.current,
    fontSize: TextUnit = TextUnit.Unspecified,
    softWrap: Boolean = true,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    linkColors: LinkColors = LinkedTextDefaults.linkColors(),
    onClick: (String) -> Unit
) {
    var layoutResult: TextLayoutResult? by remember { mutableStateOf(null) }

    var pressedIndex: Int by remember { mutableStateOf(-1) }

    val annotations = text.getLinkAnnotations(0, text.length)

    val styledText = AnnotatedString.Builder(text).apply {
        for ((i, ann) in annotations.withIndex()) {
            val textColor = linkColors.textColor(enabled, i == pressedIndex).value
            val backgroundColor = linkColors.backgroundColor(enabled, i == pressedIndex).value
            addStyle(
                SpanStyle(color = textColor, background = backgroundColor),
                start = ann.start,
                end = ann.end
            )
        }
    }.toAnnotatedString()

    val pressIndicator = Modifier.pointerInput(onClick) {
        detectTapGestures(
            onPress = { pos ->
                layoutResult?.getOffsetForPosition(pos)?.let { offset ->
                    val index = annotations.indexOfFirst {
                        it.start <= offset && it.end >= offset
                    }
                    pressedIndex = index
                    if (index >= 0) {
                        tryAwaitRelease()
                        pressedIndex = -1
                    }
                }
            },
            onTap = { pos ->
                layoutResult?.getOffsetForPosition(pos)?.let { offset ->
                    annotations.firstOrNull { it.start <= offset && it.end >= offset }
                        ?.item
                        ?.let { url -> onClick(url) }
                }
            }
        )
    }

    val actionSemantics = Modifier.semantics {
        onClickLink { index ->
            annotations.getOrNull(index)?.let {
                onClick(it.item)
            }
            true
        }
    }

    Text(
        text = styledText,
        modifier = modifier
            .then(pressIndicator)
            .then(actionSemantics),
        color = color,
        style = style,
        fontSize = fontSize,
        softWrap = softWrap,
        overflow = overflow,
        maxLines = maxLines,
        onTextLayout = { result ->
            layoutResult = result
            onTextLayout(result)
        }
    )
}

fun AnnotatedString.getLinkAnnotations(start: Int, end: Int): List<AnnotatedString.Range<String>> =
    getStringAnnotations(LinkTag, start, end)

fun AnnotatedString.Builder.addLink(
    url: String,
    start: Int,
    end: Int
) = addStringAnnotation(LinkTag, url, start, end)

@OptIn(ExperimentalComposeApi::class, ExperimentalTextApi::class)
fun <R : Any> AnnotatedString.Builder.withLink(
    url: String,
    block: AnnotatedString.Builder.() -> R
): R = withAnnotation(LinkTag, url, block)

object LinkedTextDefaults {
    @Composable
    fun linkColors(
        textColor: Color = MaterialTheme.colors.primary,
        disabledTextColor: Color = textColor.copy(ContentAlpha.disabled),
        pressedTextColor: Color = textColor,
        backgroundColor: Color = Color.Unspecified,
        disabledBackgroundColor: Color = backgroundColor,
        pressedBackgroundColor: Color = textColor.copy(
            alpha = LocalRippleTheme.current.rippleAlpha().pressedAlpha
        )
    ): LinkColors = DefaultLinkColors(
        textColor = textColor,
        disabledTextColor = disabledTextColor,
        pressedTextColor = pressedTextColor,
        backgroundColor = backgroundColor,
        disabledBackgroundColor = disabledBackgroundColor,
        pressedBackgroundColor = pressedBackgroundColor
    )
}

interface LinkColors {
    @Composable
    fun textColor(enabled: Boolean, isPressed: Boolean): State<Color>

    @Composable
    fun backgroundColor(enabled: Boolean, isPressed: Boolean): State<Color>
}

@Immutable
private data class DefaultLinkColors(
    private val textColor: Color,
    private val disabledTextColor: Color,
    private val pressedTextColor: Color,
    private val backgroundColor: Color,
    private val disabledBackgroundColor: Color,
    private val pressedBackgroundColor: Color
) : LinkColors {

    @Composable
    override fun textColor(enabled: Boolean, isPressed: Boolean): State<Color> =
        animateColorAsState(
            when {
                !enabled -> disabledTextColor
                isPressed -> pressedTextColor
                else -> textColor
            }
        )

    @Composable
    override fun backgroundColor(enabled: Boolean, isPressed: Boolean): State<Color> =
        animateColorAsState(
            when {
                !enabled -> disabledBackgroundColor
                isPressed -> pressedBackgroundColor
                else -> backgroundColor
            }
        )
}
