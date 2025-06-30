package fe.linksheet.composable.page.settings.apps.verifiedlinkhandlers

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import fe.linksheet.R


@Composable
internal fun <T> BaseStateFilter(
    entries: List<T>,
    allState: T,
    selection: T,
    onSelected: (T) -> Unit,
    stringRes: (T) -> Int,
    icon: (T) -> ImageVector,
) {
    var expanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = stringResource(R.string.generic__label_arrow_rotation)
    )

    // From androidx.compose.material3.tokens#FilterChipTokens
    // val UnselectedLabelTextColor = ColorSchemeKeyTokens.OnSurfaceVariant
    val unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant

    Box {
        FilterChip(
            selected = selection != allState,
            onClick = { expanded = !expanded },
            colors = FilterChipDefaults.filterChipColors(iconColor = unselectedColor),
            label = {
                Text(text = stringResource(id = stringRes(selection)))
            },
            leadingIcon = {
                Icon(
                    modifier = Modifier.size(FilterChipDefaults.IconSize),
                    imageVector = icon(selection),
                    contentDescription = null,
                )
            },
            trailingIcon = {
                Icon(
                    modifier = Modifier.rotate(rotation),
                    imageVector = Icons.Outlined.ArrowDropDown,
                    contentDescription = null
                )
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            for (mode in entries) {
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        onSelected(mode)
                    },
                    text = {
                        Text(text = stringResource(id = stringRes(mode)))
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = icon(mode),
                            contentDescription = null
                        )
                    }
                )
            }
        }
    }
}
