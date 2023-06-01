package fe.linksheet.composable.util

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import fe.android.preference.helper.BasePreference
import fe.android.preference.helper.compose.RepositoryState
import fe.linksheet.module.viewmodel.base.BaseViewModel

@Composable
fun SwitchRow(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    state: RepositoryState<Boolean, Boolean, BasePreference.Preference<Boolean>>,
    viewModel: BaseViewModel,
    headline: String,
    subtitle: String? = null,
    subtitleBuilder: @Composable ((enabled: Boolean) -> Unit)? = if (subtitle != null) {
        { SubtitleText(subtitle = subtitle) }
    } else null
) {
    SwitchRow(
        modifier = modifier,
        enabled = enabled,
        checked = state.value,
        onChange = { viewModel.updateState(state, it) },
        headline = headline,
        subtitle = subtitle,
        subtitleBuilder = subtitleBuilder
    )
}

@Composable
fun SwitchRow(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    state: RepositoryState<Boolean, Boolean, BasePreference.Preference<Boolean>>,
    viewModel: BaseViewModel,
    @StringRes headlineId: Int,
    @StringRes subtitleId: Int
) {
    SwitchRow(
        modifier = modifier,
        enabled = enabled,
        checked = state.value,
        onChange = { viewModel.updateState(state, it) },
        headlineId = headlineId,
        subtitleId = subtitleId
    )
}

@Composable
fun SwitchRow(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    checked: Boolean,
    onChange: (Boolean) -> Unit,
    @StringRes headlineId: Int,
    @StringRes subtitleId: Int
) {
    SwitchRow(
        modifier = modifier,
        enabled = enabled,
        checked = checked,
        onChange = onChange,
        headline = stringResource(id = headlineId),
        subtitle = stringResource(id = subtitleId)
    )
}

@Composable
fun SwitchRow(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    checked: Boolean,
    onChange: (Boolean) -> Unit,
    headline: String,
    subtitle: String? = null,
    subtitleBuilder: @Composable ((enabled: Boolean) -> Unit)? = if (subtitle != null) {
        { SubtitleText(subtitle = subtitle) }
    } else null
) {
    ClickableRow(
        modifier = modifier,
        enabled = enabled,
        verticalAlignment = Alignment.CenterVertically,
        onClick = { onChange(!checked) }
    ) {
        Column(modifier = Modifier.fillMaxWidth(0.8f), verticalArrangement = Arrangement.Center) {
            HeadlineText(headline = headline)
            subtitleBuilder?.invoke(enabled)
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Switch(enabled = enabled, checked = checked, onCheckedChange = onChange)
        }
    }
}