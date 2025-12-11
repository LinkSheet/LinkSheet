package fe.linksheet.composable.page.settings.apps.verifiedlinkhandlers

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Moving
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fe.android.compose.content.rememberOptionalContent
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.android.compose.text.TextContent
import fe.composekit.component.dialog.DialogDefaults
import fe.composekit.component.list.column.CustomListItemDefaults
import fe.composekit.component.list.column.shape.ShapeListItemDefaults
import fe.composekit.component.list.item.ContentPosition
import fe.composekit.component.list.item.type.RadioButtonListItem
import fe.linksheet.R
import fe.linksheet.module.viewmodel.common.applist.SortByState
import fe.linksheet.module.viewmodel.common.applist.SortType


@Composable
internal fun SortByColumn(
    state: SortByState,
    onChange: (SortByState) -> Unit
) {
    Column {
        Text(
            text = stringResource(R.string.generic__title_sort_by),
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.selectableGroup(),
        ) {
            items(items = SortType.entries, key = { it }) {
                SortByListItem(
                    headlineContent = textContent(it.stringRes),
                    selected = (state.sort == it),
                    onSelect = { onChange(state.copy(sort = it)) }
                )
            }
        }

        FilterChip(
            selected = state.ascending,
            onClick = { onChange(state.copy(ascending = !state.ascending)) },
            label = {
                Text(text = stringResource(id = R.string.settings_verified_link_handlers__text_ascending))
            },
            leadingIcon = rememberOptionalContent(state.ascending) {
                Icon(
                    modifier = Modifier.size(FilterChipDefaults.IconSize),
                    imageVector = Icons.Filled.Moving,
                    contentDescription = null,
                )
            }
        )
    }
}

private val SortType.stringRes: Int
    get() = when (this) {
        SortType.AZ -> R.string.settings_verified_link_handlers__text_sort_by_a_z
        SortType.InstallTime -> R.string.settings_verified_link_handlers__text_sort_by_installation_time
    }

@Composable
private fun SortByListItem(
    headlineContent: TextContent,
    selected: Boolean,
    onSelect: () -> Unit,
) {
    RadioButtonListItem(
        selected = selected,
        onSelect = onSelect,
        position = ContentPosition.Leading,
        headlineContent = headlineContent,
        //            supportingContent = when {
        //                isDeviceLanguage -> textContent(R.string.settings_language__text_system_language)
        //                else -> text(displayItem.currentLocaleName)
        //            },
        otherContent = null,
        width = DialogDefaults.RadioButtonWidth,
        containerHeight = CustomListItemDefaults.containerHeight(oneLine = 12.dp),
        innerPadding = CustomListItemDefaults.padding(
            vertical = 0.dp,
            start = 0.dp,
            leadingContentEnd = 4.dp
        ),
        textOptions = DialogDefaults.ListItemTextOptions,
        colors = ShapeListItemDefaults.colors(
            containerColor = Color.Transparent,
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun SortByColumnPreview() {
    SortByColumn(
        state = SortByState(SortType.AZ, true),
        onChange = {

        }
    )
}
