package fe.linksheet.experiment.ui.overhaul.composable.component.util

import androidx.annotation.StringRes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.structuralEqualityPolicy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow

@Stable
data class TextOptions(
    val maxLines: Int = Int.MAX_VALUE,
    val overflow: TextOverflow = TextOverflow.Clip,
)

val DefaultTextOptions = TextOptions()
val LocalTextOptions = compositionLocalOf(structuralEqualityPolicy()) { DefaultTextOptions }

typealias OptionalTextContent = TextContent?

@Stable
interface TextContent {
    val key: Any
    val content: @Composable () -> Unit
}

@Stable
class Default(text: String) : TextContent {
    override val key = text
    override val content: @Composable () -> Unit = {
        val textOptions = LocalTextOptions.current
        Text(text = text, overflow = textOptions.overflow, maxLines = textOptions.maxLines)
    }

    companion object {
        fun text(text: String): Default {
            return Default(text)
        }

        fun textOrNull(text: String?): Default? {
            return if (text != null) Default(text) else null
        }
    }
}

@Stable
class Resource(@StringRes id: Int, vararg formatArgs: Any) : TextContent {
    override val key = id
    override val content: @Composable () -> Unit = {
        val textOptions = LocalTextOptions.current
        Text(
            text = stringResource(id = id, formatArgs = formatArgs),
            overflow = textOptions.overflow,
            maxLines = LocalTextOptions.current.maxLines
        )
    }

    companion object {
        fun textContent(@StringRes id: Int, vararg formatArgs: Any): Resource {
            return Resource(id, *formatArgs)
        }
    }
}


@Stable
class Annotated(annotatedString: AnnotatedString) : TextContent {
    override val key = annotatedString.text
    override val content: @Composable () -> Unit = {
        val textOptions = LocalTextOptions.current
        Text(text = annotatedString, overflow = textOptions.overflow, maxLines = textOptions.maxLines)
    }

    companion object {
        inline fun buildAnnotatedTextContent(builder: AnnotatedString.Builder.() -> Unit): Annotated {
            return Annotated(buildAnnotatedString(builder))
        }
    }
}


@Stable
class ComposableTextContent(override val content: @Composable () -> Unit, override val key: Any = Unit) :
    TextContent {
    companion object {
        fun content(content: @Composable () -> Unit): ComposableTextContent {
            return ComposableTextContent(content)
        }
    }
}
