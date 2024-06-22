package fe.linksheet.component.util

import androidx.annotation.StringRes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import fe.android.span.helper.composable.createAnnotatedString


@Immutable
interface TextContent {
    val key: Any
    val content: @Composable () -> Unit
}

@Immutable
class Default private constructor(text: String) : TextContent {
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

@Immutable
class Resource private constructor(@StringRes id: Int, vararg formatArgs: Any) : TextContent {
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


@Immutable
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

        val AnnotatedString.content: Annotated
            get() = Annotated(this)
    }
}

@Immutable
class AnnotatedStringResource private constructor(@StringRes id: Int, vararg formatArgs: Any) : TextContent {
    override val key = id
    override val content: @Composable () -> Unit = {
        val textOptions = LocalTextOptions.current

        Text(
            text = createAnnotatedString(id = id, *formatArgs),
            overflow = textOptions.overflow,
            maxLines = textOptions.maxLines
        )
    }

    companion object {
        fun annotated(@StringRes id: Int, vararg formatArgs: Any): AnnotatedStringResource {
            return AnnotatedStringResource(id, *formatArgs)
        }
    }
}


@Immutable
class ComposableTextContent(
    override val content: @Composable () -> Unit,
    override val key: Any = Unit
) : TextContent {

    companion object {
        fun content(content: @Composable () -> Unit): ComposableTextContent {
            return ComposableTextContent(content)
        }
    }
}
