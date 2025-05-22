package fe.linksheet.composable.util

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import fe.android.preference.helper.Preference
import fe.composekit.preference.ViewModelStatePreference
import fe.composekit.preference.collectAsStateWithLifecycle


@Composable
fun <T : Any, M : Any> RadioButtonRow(
    modifier: Modifier = Modifier,
    value: T,
    statePreference: ViewModelStatePreference<T , T, Preference.Mapped<T, M>>,
    enabled: Boolean = true,
    clickHook: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val state by statePreference.collectAsStateWithLifecycle()

    RadioButtonRow(
        modifier = modifier,
        enabled = enabled,
        onClick = {
            statePreference(value)
            clickHook?.invoke()
        },
        onLongClick = null,
        selected = state == value,
        content = content
    )
}

@Composable
fun RadioButtonRow(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    paddingHorizontal: Dp = defaultHorizontalPadding,
    paddingVertical: Dp = defaultVerticalPadding,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)?,
    selected: Boolean,
    content: @Composable () -> Unit
) {
    ClickableRow(
        modifier = modifier,
        enabled = enabled,
        paddingHorizontal = paddingHorizontal,
        paddingVertical = paddingVertical,
        onClick = onClick,
        onLongClick = onLongClick,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        RadioButton(
            enabled = enabled,
            selected = selected,
            onClick = onClick,
            modifier = Modifier
        )
        Spacer(modifier = Modifier.width(5.dp))
        content()
    }
}
