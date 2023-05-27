package fe.linksheet.composable.util

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fe.android.preference.helper.BasePreference
import fe.android.preference.helper.compose.RepositoryState
import fe.linksheet.module.viewmodel.base.BaseViewModel
import fe.linksheet.ui.HkGroteskFontFamily

@Composable
fun DividedSwitchRow(
    state: RepositoryState<Boolean, Boolean, BasePreference.Preference<Boolean>>,
    viewModel: BaseViewModel,
    @StringRes headline: Int,
    @StringRes subtitle: Int,
    onChange: (Boolean) -> Unit = { viewModel.updateState(state, it) },
    onClick: () -> Unit,
) {
    DividedSwitchRow(
        state = state,
        viewModel = viewModel,
        headline = stringResource(id = headline),
        subtitle = stringResource(id = subtitle),
        onChange = onChange,
        onClick = onClick
    )
}

@Composable
fun DividedSwitchRow(
    state: RepositoryState<Boolean, Boolean, BasePreference.Preference<Boolean>>,
    viewModel: BaseViewModel,
    headline: String,
    subtitle: String? = null,
    subtitleBuilder: @Composable (() -> Unit)? = if (subtitle != null) {
        { SubtitleText(subtitle = subtitle) }
    } else null,
    onChange: (Boolean) -> Unit = { viewModel.updateState(state, it) },
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 0.dp, top = 10.dp, end = 10.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .clip(RoundedCornerShape(6.dp))
                .clickable(onClick = onClick)
                .padding(start = 10.dp)

        ) {
            Text(
                text = headline,
                fontFamily = HkGroteskFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            subtitleBuilder?.invoke()
        }

        Divider(
            modifier = Modifier
                .height(32.dp)
                .padding(horizontal = 8.dp)
                .width(1f.dp)
                .align(Alignment.CenterVertically),
            color = MaterialTheme.colorScheme.tertiary
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Switch(
                checked = state.value,
                onCheckedChange = onChange
            )
        }
    }
}