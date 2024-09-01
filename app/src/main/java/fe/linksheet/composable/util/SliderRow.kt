package fe.linksheet.composable.util

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fe.android.preference.helper.Preference
import fe.android.preference.helper.compose.StatePreference


@Composable
fun SliderRow(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    valueRangeStep: Float = 1f,
    onValueChange: (Float) -> Unit,
    valueFormatter: (Float) -> String = { it.toString() },
    @StringRes headlineId: Int,
    @StringRes subtitleId: Int,
) {
    SliderRow(
        modifier = modifier,
        enabled = enabled,
        value = value,
        valueRange = valueRange,
        valueRangeStep = valueRangeStep,
        onValueChange = onValueChange,
        valueFormatter = valueFormatter,
        headline = stringResource(id = headlineId),
        subtitle = stringResource(id = subtitleId)
    )
}

@Composable
fun SliderRow(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    valueRangeStep: Float = 1f,
    onValueChange: (Float) -> Unit,
    valueFormatter: (Float) -> String = { it.toString() },
    headline: String,
    subtitle: String? = null,
    subtitleBuilder: @Composable ((enabled: Boolean) -> Unit)? = buildEnabledSubtitle(subtitle = subtitle),
) {
    NonClickableRow(
        modifier = modifier,
        enabled = enabled,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(verticalArrangement = Arrangement.Center) {
            HeadlineText(headline = headline)
            subtitleBuilder?.invoke(enabled)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = valueFormatter(value))
                Spacer(modifier = Modifier.width(10.dp))
                Slider(
                    enabled = enabled,
                    value = value,
                    onValueChange = onValueChange,
                    valueRange = valueRange,
                    steps = (((valueRange.endInclusive - valueRange.start) / valueRangeStep) - 1).toInt()
                )
            }
        }
    }
}
