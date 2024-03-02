package fe.linksheet.composable.util

import androidx.annotation.StringRes
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import fe.android.preference.helper.compose.StatePreference


@Composable
fun DividedSwitchRow(
    state: StatePreference<Boolean>,
    @StringRes headline: Int,
    @StringRes subtitle: Int,
    onChange: (Boolean) -> Unit = { state(it) },
    onClick: () -> Unit,
) {
    DividedSwitchRow(
        state = state,
        headline = stringResource(id = headline),
        subtitle = stringResource(id = subtitle),
        onChange = onChange,
        onClick = onClick
    )
}

@Composable
fun DividedSwitchRow(
    state: StatePreference<Boolean>,
    enabled: Boolean = true,
    headline: String,
    subtitle: String? = null,
    subtitleBuilder: @Composable ((Boolean) -> Unit)? = buildEnabledSubtitle(subtitle = subtitle),
    onChange: (Boolean) -> Unit = { state(it) },
    onClick: () -> Unit,
) {
    DividedRow(
        headline = headline,
        subtitle = subtitle,
        subtitleBuilder = subtitleBuilder,
        enabled = enabled,
        onClick = onClick
    ) {
        Switch(
            enabled = enabled,
            checked = state.value,
            onCheckedChange = onChange
        )
    }
}

@Composable
fun DividedSwitchRow(
    state: StatePreference<Boolean>,
    headline: String,
    subtitle: String? = null,
    subtitleBuilder: @Composable (() -> Unit)? = buildSubtitle(subtitle = subtitle),
    onChange: (Boolean) -> Unit = { state(it) },
    onClick: () -> Unit,
) {
    DividedRow(
        headline = headline,
        subtitle = subtitle,
        subtitleBuilder = subtitleBuilder,
        onClick = onClick
    ) {
        Switch(
            checked = state.value,
            onCheckedChange = onChange
        )
    }
}
