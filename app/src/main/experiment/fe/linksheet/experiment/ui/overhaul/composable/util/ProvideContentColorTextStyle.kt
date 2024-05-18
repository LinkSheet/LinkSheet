package fe.linksheet.experiment.ui.overhaul.composable.util

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

@Composable
fun ProvideContentColorTextStyle(
    contentColor: Color,
    textStyle: TextStyle,
    textOptions: TextOptions? = null,
    content: @Composable () -> Unit,
) {
    val mergedStyle = LocalTextStyle.current.merge(textStyle)

    ProvideTextOptions(textOptions = textOptions) {
        CompositionLocalProvider(
            LocalContentColor provides contentColor,
            LocalTextStyle provides mergedStyle,
            content = content
        )
    }
}
