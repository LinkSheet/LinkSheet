package fe.linksheet.composable.util

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fe.linksheet.extension.forEachElementIndex

@Composable
fun <T> FilterChips(
    currentState: T,
    onClick: (T) -> Unit,
    values: List<Pair<T, Int>>
) {
    values.forEachElementIndex { (value, stringRes), _, _, last ->
        FilterChip(
            value = value,
            currentState = currentState,
            onClick = { onClick(value) },
            label = stringRes
        )

        if (!last) {
            Spacer(modifier = Modifier.width(5.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
                Image(
                    imageVector = icon,
                    contentDescription = stringResource(id = label),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
                )
            }
        } else null
    )
}