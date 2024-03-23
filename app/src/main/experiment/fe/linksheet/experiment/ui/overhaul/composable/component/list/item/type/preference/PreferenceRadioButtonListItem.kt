package fe.linksheet.experiment.ui.overhaul.composable.component.list.item.type.preference

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import fe.android.preference.helper.Preference
import fe.android.preference.helper.compose.MutablePreferenceState
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.ShapeListItemDefaults
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.type.RadioButtonListItem


@Composable
fun <P : Preference<T, NT>, T : Any, NT> PreferenceRadioButtonListItem(
    enabled: Boolean = true,
    value: NT,
    preference: MutablePreferenceState<T, NT, P>,
    shape: Shape = ShapeListItemDefaults.SingleShape,
    padding: PaddingValues = PaddingValues(),
    @StringRes headlineContentTextId: Int,
    @StringRes supportingContentTextId: Int? = null,
) {
    RadioButtonListItem(
        enabled = enabled,
        shape = shape,
        padding = padding,
        selected = preference() == value,
        onClick = { preference(value) },
        headlineContentText = stringResource(id = headlineContentTextId),
        supportingContentText = supportingContentTextId?.let { stringResource(id = it) }
    )
}
