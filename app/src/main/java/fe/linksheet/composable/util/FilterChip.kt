package fe.linksheet.composable.util

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fe.kotlin.extension.iterator.forEachWithInfo
import fe.kotlin.extension.iterator.withElementInfo

data class FilterChipValue<T>(
    val value: T,
    @StringRes val string: Int,
    val icon: ImageVector? = null
)

@Composable
fun <T> FilterChips(
    currentState: T,
    onClick: (T) -> Unit,
    values: List<FilterChipValue<T>>
) {
    Row {
        for((value, _, _, last) in values.withElementInfo()) {
            FilterChip(
                value = value.value,
                currentState = currentState,
                onClick = { onClick(value.value) },
                label = value.string,
                icon = value.icon
            )

            if (!last) {
                Spacer(modifier = Modifier.width(5.dp))
            }
        }
    }
}

@Composable
fun <T> FilterChip(
    value: T,
    currentState: T,
    onClick: (T) -> Unit,
    @StringRes label: Int,
    icon: ImageVector? = null,
) {
    FilterChip(
        selected = currentState == value,
        onClick = { onClick(value) },
        label = {
            Text(text = stringResource(id = label))
        },
        trailingIcon = if (icon != null) {
            {
                ColoredIcon(icon = icon, descriptionId = label)
            }
        } else null
    )
}
